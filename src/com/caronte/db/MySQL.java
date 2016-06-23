package com.caronte.db;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import com.caronte.db.model.SPParameter;
import com.caronte.db.pool.DataBasePool;
import com.caronte.jpath.JPATH;
import com.caronte.json.JSONObject;
import com.caronte.json.JSONValue;
import com.caronte.json.JSONValueType;

public class MySQL 
{
	private String ip;
	private String port;
	private String user;
	private String password;
	private String schema;
	private Integer blockSize;
	private Integer maxPoolSize;
	
	private static HashMap<String, DataBasePool> schemaPoolMap;
	
	static
	{
		schemaPoolMap = new HashMap<String, DataBasePool>();
	}
	
	public MySQL(String ip, String port, String user, String password, String schema, Integer blockSize, Integer maxPoolSize) 
	{
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.password = password;
		this.schema = schema;
		this.maxPoolSize = maxPoolSize;
		this.blockSize = blockSize;
	}
	
	private Connection openConnection() throws Exception
	{
		DataBasePool pool = null;
		String id = ip + ":" + port + "/" + schema;
		
		synchronized(schemaPoolMap)
		{
			if (!schemaPoolMap.containsKey(id))
			{
				pool = new DataBasePool();
				pool.setIP(ip);
				pool.setPort(port);
				pool.setUser(user);
				pool.setPassword(password);
				pool.setSchema(schema);

				pool.setBlockSize(blockSize);
				pool.setMaxSize(maxPoolSize);
				
				schemaPoolMap.put(id, pool);
			}
			
			pool = schemaPoolMap.get(id);
		}
		
		return pool.openConnection();
	}
	
	private void closeConnection(Connection connection) throws Exception
	{
		DataBasePool pool = null;
		String id = ip + ":" + port + "/" + schema;
		
		synchronized(schemaPoolMap)
		{
			if (!schemaPoolMap.containsKey(id))
			{
				pool = new DataBasePool();
				pool.setIP(ip);
				pool.setPort(port);
				pool.setUser(user);
				pool.setPassword(password);
				pool.setSchema(schema);

				pool.setBlockSize(blockSize);
				pool.setMaxSize(maxPoolSize);
				
				schemaPoolMap.put(schema, pool);
			}
			
			pool = schemaPoolMap.get(id);
			pool.closeConnection(connection);
		}		
	}

	private void evalute(String label, JSONValue value, JSONValueType type) throws Exception
	{
		if (value == null)
		{
			throw new Exception("Element " + label + " cannot be null");
		}

		if (value == null || value.getType() != type)
		{
			throw new Exception("Invalid data type for element " + label);
		}
	}
		
	@SuppressWarnings("unchecked")
	private String createDynamicFilter(JSONValue filters) throws Exception
	{
		StringBuffer dynamicFilter = new StringBuffer();

		if (filters != null && filters.getType() == JSONValueType.ARRAY)
		{
			ArrayList<Object> array = (ArrayList<Object>)filters.getValue();
			
			for (Object object : array) 
			{
				JSONValue field = JPATH.find((JSONObject)object, "/field");
				JSONValue operator = JPATH.find((JSONObject)object, "/operator");
				Integer total = JPATH.count((JSONObject)object, "/values");
				
				if (field != null && field.getType() == JSONValueType.STRING && operator != null && operator.getType() == JSONValueType.STRING && total > 0)
				{
					String separator = "";
					String init = "";
					String end = "";

					if (dynamicFilter.length() == 0)
					{
						dynamicFilter.append(" WHERE ");
					}
					else
					{
						dynamicFilter.append(" AND ");
					}

					dynamicFilter.append((String)field.getValue());
					dynamicFilter.append(" ");
					dynamicFilter.append((String)operator.getValue());
					dynamicFilter.append(" ");

					switch (((String)operator.getValue()).toUpperCase()) 
					{
						case "BETWEEN" : 
							separator = " AND "; 
						break;
						case "IN" :
							init = "(";
							separator = ","; 
							end = ")";
						break;
					}
					
					dynamicFilter.append(init);
					
					for (int i = 0; i < total; i++)
					{
						JSONValue valueType = JPATH.find((JSONObject)object, "/values[" + i + "]/type");
						JSONValue valueValue = JPATH.find((JSONObject)object, "/values[" + i + "]/value");
						
						if (valueType == null || valueType.getType() != JSONValueType.STRING)
						{
							throw new Exception("Invalid value type in filter");
						}

						if (valueValue == null || valueValue.getType() != JSONValueType.STRING)
						{
							throw new Exception("Invalid value in filter");
						}

						if (!((String)valueType.getValue()).equalsIgnoreCase("string") && !((String)valueType.getValue()).equalsIgnoreCase("int"))
						{
							throw new Exception("Invalid value type '" + (String)valueType.getValue() + "' in filter");
						}
						
						if (i > 0)
						{
							dynamicFilter.append(separator);
						}
						
						if (((String)valueType.getValue()).equalsIgnoreCase("string"))
						{
							dynamicFilter.append("'" + ((String)valueValue.getValue()) + "'");
						}
						if (((String)valueType.getValue()).equalsIgnoreCase("int"))
						{
							dynamicFilter.append(((String)valueValue.getValue()));
						}
					}
					
					dynamicFilter.append(end);					
				}
			}
		}
		
		return dynamicFilter.toString();
	}
	
	public JSONObject executePagedQuery(String query, JSONObject jsonObject) throws Exception
	{
		JSONObject result = new JSONObject();
		JSONValue filters = JPATH.find(jsonObject, "/filters");
		JSONValue fieldOrder = JPATH.find(jsonObject, "/fieldOrder");
		JSONValue typeOrder = JPATH.find(jsonObject, "/typeOrder");
		JSONValue currentPage = JPATH.find(jsonObject, "/currentPage");
		JSONValue pageSize = JPATH.find(jsonObject, "/pageSize");
		JSONValue maxPageScrollElements = JPATH.find(jsonObject, "/maxPageScrollElements");
		
		evalute("currentPage", currentPage, JSONValueType.INTEGER);
		evalute("pageSize", pageSize, JSONValueType.INTEGER);
		evalute("maxPageScrollElements", maxPageScrollElements, JSONValueType.INTEGER);
		evalute("fieldOrder", fieldOrder, JSONValueType.INTEGER);
		evalute("typeOrder", typeOrder, JSONValueType.STRING);
					
		String dynamicFilter = createDynamicFilter(filters);
		
		String countQuery = "SELECT count(1) AS dataSize FROM (" + query + ") AS T" + dynamicFilter;
		String pagedQuery = "SELECT * FROM (" + query + ") AS T" + dynamicFilter + " ORDER BY " + (Integer)fieldOrder.getValue() +  " " + (String)typeOrder.getValue() + " LIMIT " + (((Integer)currentPage.getValue() - 1) * ((Integer)pageSize.getValue())) + "," + ((Integer)pageSize.getValue());
		
		JSONObject resultCount = executeQuery(countQuery, jsonObject);
		JSONObject resultPaged = executeQuery(pagedQuery, jsonObject);
		
		JSONValue dataSize = JPATH.find(resultCount, "/data[0]/dataSize");
		
		evalute("dataSize", dataSize, JSONValueType.INTEGER);
		
		ArrayList<Integer> pageScroller = PageScroller.create((Integer)maxPageScrollElements.getValue(), (Integer)dataSize.getValue(), (Integer)pageSize.getValue(), (Integer)currentPage.getValue());
		
		result.addPair("currentPage", currentPage.getValue());
		result.addPair("pageSize", pageSize.getValue());
		result.addPair("totalRows", dataSize.getValue());
		result.addPair("table", resultPaged);
		
		result.resetArray();		
		for (Integer pageScroll : pageScroller) 
		{
			JSONObject page = new JSONObject();
			page.addPair("label", pageScroll == null ? "..." : pageScroll.toString());
			page.addPair("page", pageScroll);
			result.addToArray(page);
		}		
		result.saveArray("pageScroller");
				
		return result;
	}

	public JSONObject executeQuery(String query, JSONObject jsonObject) throws Exception
	{
		Connection connection;
		ResultSet resultSet;
		ResultSetMetaData resultSetMetaData;
		PreparedStatement preparedStatement;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		connection = null;
		resultSet = null;
		resultSetMetaData = null;
		preparedStatement = null;
		
		try
		{
			connection = openConnection();
			
			preparedStatement = connection.prepareStatement(query);

			if (jsonObject != null)
			{
				JSONValue jsonValue = JPATH.find(jsonObject, "/parameters");
				
				if (jsonValue != null && jsonValue.getType() == JSONValueType.ARRAY)
				{
					int total = JPATH.count(jsonObject, "/parameters");
					
					for (int i = 0; i < total; i++)
					{
						String type = "";
						String value = null;
						
						if ((jsonValue = JPATH.find(jsonObject, "/parameters[" + i + "]/type")) != null && jsonValue.getType() == JSONValueType.STRING)
						{
							type = (String)jsonValue.getValue();
						}
						if ((jsonValue = JPATH.find(jsonObject, "/parameters[" + i + "]/value")) != null && jsonValue.getType() == JSONValueType.STRING)
						{
							value = (String)jsonValue.getValue();
						}
						
						switch(type)
						{
							case "int":
								preparedStatement.setInt(i + 1, Integer.parseInt(value));
							break;
							case "long":
								preparedStatement.setLong(i + 1, Long.parseLong(value));
							break;
							case "decimal":
								preparedStatement.setBigDecimal(i + 1, new BigDecimal(value));
							break;
							case "date":
								Date date = new Date(simpleDateFormat.parse(value).getTime());
								preparedStatement.setDate(i + 1, date);
							break;
							default:
								preparedStatement.setString(i + 1, value);
							break;
						}
					}
				}
			}
						
			resultSet = preparedStatement.executeQuery();			
			resultSetMetaData = resultSet.getMetaData();
			
			jsonObject = new JSONObject();
			jsonObject.resetArray();
			
			for (int i = 0; i < resultSetMetaData.getColumnCount(); i++)
			{
				JSONObject column = new JSONObject();
				
				column.addPair("position", i);
				column.addPair("label", resultSetMetaData.getColumnLabel(i + 1));
				column.addPair("name", resultSetMetaData.getColumnName(i + 1));
				
				switch (resultSetMetaData.getColumnType(i + 1)) 
				{
					case Types.CHAR:
						column.addPair("size", resultSetMetaData.getPrecision(i + 1));
						column.addPair("type", "CHAR");
						break;
					case Types.VARCHAR:
						column.addPair("size", resultSetMetaData.getPrecision(i + 1));
						column.addPair("type", "VARCHAR");
						break;
					case Types.LONGVARCHAR:
						column.addPair("size", resultSetMetaData.getPrecision(i + 1));
						column.addPair("type", "LONGVARCHAR");
						break;
					case Types.LONGVARBINARY:
						column.addPair("size", resultSetMetaData.getPrecision(i + 1));
						column.addPair("type", "LONGVARBINARY");
						break;
					case Types.DATE:
						column.addPair("type", "DATE");
						break;
					case Types.DECIMAL:
						column.addPair("type", "DECIMAL");
						column.addPair("precision", resultSetMetaData.getPrecision(i + 1));
						column.addPair("scale", resultSetMetaData.getScale(i + 1));
						break;
					case Types.DOUBLE:
						column.addPair("type", "DOUBLE");
						column.addPair("precision", resultSetMetaData.getPrecision(i + 1));
						column.addPair("scale", resultSetMetaData.getScale(i + 1));
						break;
					case Types.FLOAT:
						column.addPair("type", "FLOAT");
						column.addPair("precision", resultSetMetaData.getPrecision(i + 1));
						column.addPair("scale", resultSetMetaData.getScale(i + 1));
						break;
					case Types.INTEGER:
					case Types.BIGINT:
						column.addPair("type", "INTEGER");
						break;
					default:
						column.addPair("type", "UNKNOWN");
						break;
				}
				
				jsonObject.addToArray(column);
			}

			jsonObject.saveArray("columns");

			jsonObject.resetArray();
			
			while(resultSet.next())
			{
				JSONObject element = new JSONObject();
				
				for (int i = 0; i < resultSetMetaData.getColumnCount(); i++)
				{
					switch (resultSetMetaData.getColumnType(i + 1)) 
					{
						case Types.CHAR:
						case Types.VARCHAR:
							element.addPair(resultSetMetaData.getColumnLabel(i + 1), resultSet.getString(i + 1));
							break;
						case Types.LONGVARCHAR:							
							element.addPair(resultSetMetaData.getColumnLabel(i + 1), resultSet.getString(i + 1));
							break;
						case Types.LONGVARBINARY:							
							element.addPair(resultSetMetaData.getColumnLabel(i + 1), new String (Base64.getEncoder().encode(resultSet.getBytes(i + 1)),"UTF-8"));
							break;
						case Types.DATE:
							element.addPair(resultSetMetaData.getColumnLabel(i + 1), simpleDateFormat.format(resultSet.getDate(i + 1)));
							break;
						case Types.DECIMAL:
							element.addPair(resultSetMetaData.getColumnLabel(i + 1), resultSet.getBigDecimal(i + 1));
							break;
						case Types.DOUBLE:
							element.addPair(resultSetMetaData.getColumnLabel(i + 1), resultSet.getDouble(i + 1));
							break;
						case Types.FLOAT:
							element.addPair(resultSetMetaData.getColumnLabel(i + 1), resultSet.getFloat(i + 1));
							break;
						case Types.INTEGER:
						case Types.BIGINT:
							element.addPair(resultSetMetaData.getColumnLabel(i + 1), resultSet.getInt(i + 1));
							break;
						default:
							break;
					}
				}
				
				jsonObject.addToArray(element);
			}
			
			jsonObject.saveArray("data");
						
			resultSet.close();
			preparedStatement.close();
			
			return jsonObject;
		}
		catch(Exception e)
		{
			throw new Exception(e);
		}
		finally 
		{
			if (resultSet != null)
			{
				try 
				{ 
					resultSet.close(); 
				} 
				catch(Exception e) 
				{
				}
			}
			
			if (preparedStatement != null && !preparedStatement.isClosed())
			{
				try 
				{ 
					preparedStatement.close();
				} 
				catch(Exception e) 
				{
				}
			}
			
			if (connection != null)
			{
				closeConnection(connection);
			}
		}
	}
	
	private JSONObject getOutputResult(ArrayList<SPParameter> spParameterList, CallableStatement callableStatement) throws SQLException, UnsupportedEncodingException, Exception
	{
		Blob blob;
		Clob clob;
		JSONObject result;
		
		result = new JSONObject();
		
		for (SPParameter spParameter : spParameterList) 
		{
			Object outParameter = null;				
			
			if (spParameter.getParameterMode().trim().toUpperCase().equals("OUT"))
			{
				switch (spParameter.getParameterDataType().toLowerCase()) 
				{
				case "int":
					outParameter = callableStatement.getInt(spParameter.getParameterPosition());
					break;
				case "numeric":
				case "decimal":
					outParameter = callableStatement.getDouble(spParameter.getParameterPosition());
					break;
				case "char":
					outParameter = callableStatement.getString(spParameter.getParameterPosition());
					break;
				case "varchar":
					outParameter = callableStatement.getString(spParameter.getParameterPosition());
					break;
				case "datetime":
					outParameter = callableStatement.getDate(spParameter.getParameterPosition());
					break;
				case "tinytext":
				case "text":
				case "mediumtext":
				case "longtext":
					clob = callableStatement.getClob(spParameter.getParameterPosition());
					outParameter = Base64.getEncoder().encode(clob.toString().getBytes("UTF-8"));
					break;
				case "tinyblob":
				case "blob":
				case "mediumblob":
				case "longblob":
					blob = callableStatement.getBlob(spParameter.getParameterPosition());
					outParameter = Base64.getEncoder().encode(blob.getBytes(0, (int)blob.length()));
					break;
				default:
					break;
				}

				result.addPair(spParameter.getParameterName(), outParameter);
			}
		}
		
		return result;
	}
	
	private void setInputParameter(SPParameter spParameter, CallableStatement callableStatement, Connection connection) throws SQLException, ParseException, UnsupportedEncodingException
	{
		Blob blob;
		Clob clob;
		byte[] bytes;
		SimpleDateFormat simpleDateFormat;

		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		switch (spParameter.getParameterDataType().toLowerCase()) 
		{
		case "int":
			if (spParameter.getValue() == null)
			{
				callableStatement.setNull(spParameter.getParameterPosition(), Types.INTEGER);
			}
			else
			{
				if (spParameter.getValue() instanceof BigInteger)
				{
					callableStatement.setLong(spParameter.getParameterPosition(), ((BigInteger)spParameter.getValue()).longValue());						
				}
				if (spParameter.getValue() instanceof Integer)
				{
					callableStatement.setInt(spParameter.getParameterPosition(), (Integer)spParameter.getValue());						
				}
				if (spParameter.getValue() instanceof Long)
				{
					callableStatement.setLong(spParameter.getParameterPosition(), (Long)spParameter.getValue());						
				}
				if (spParameter.getValue() instanceof String)
				{
					callableStatement.setLong(spParameter.getParameterPosition(), Long.parseLong((String)spParameter.getValue()));						
				}
			}
			break;
		case "numeric":
		case "decimal":
			if (spParameter.getValue() == null)
			{
				callableStatement.setNull(spParameter.getParameterPosition(), Types.NUMERIC);
			}
			else
			{
				if (spParameter.getValue() instanceof BigDecimal)
				{
					callableStatement.setBigDecimal(spParameter.getParameterPosition(), (BigDecimal)spParameter.getValue());						
				}
				if (spParameter.getValue() instanceof Float)
				{
					callableStatement.setBigDecimal(spParameter.getParameterPosition(), new BigDecimal((Float)spParameter.getValue()));						
				}
				if (spParameter.getValue() instanceof Double)
				{
					callableStatement.setBigDecimal(spParameter.getParameterPosition(), new BigDecimal((Double)spParameter.getValue()));						
				}
				if (spParameter.getValue() instanceof String)
				{
					callableStatement.setBigDecimal(spParameter.getParameterPosition(), new BigDecimal((String)spParameter.getValue()));						
				}
			}
			break;
		case "char":
			if (spParameter.getValue() == null)
			{
				callableStatement.setNull(spParameter.getParameterPosition(), Types.CHAR);
			}
			else
			{
				if (((String)spParameter.getValue()).length() > spParameter.getParameterMaxLenth())
				{
					throw new SQLException("Data length too big for SP parameter " + spParameter.getParameterName(), "42000", 1074);
				}
				
				callableStatement.setString(spParameter.getParameterPosition(), (String)spParameter.getValue());
			}
			break;
		case "varchar":
			if (spParameter.getValue() == null)
			{
				callableStatement.setNull(spParameter.getParameterPosition(), Types.VARCHAR);
			}
			else
			{
				if (((String)spParameter.getValue()).length() > spParameter.getParameterMaxLenth())
				{
					throw new SQLException("Data length too big for SP parameter " + spParameter.getParameterName(), "42000", 1074);
				}

				callableStatement.setString(spParameter.getParameterPosition(), (String)spParameter.getValue());
			}
			break;
		case "datetime":
			if (spParameter.getValue() == null)
			{
				callableStatement.setNull(spParameter.getParameterPosition(), Types.DATE);
			}
			else
			{
				Timestamp date = new Timestamp(simpleDateFormat.parse((String)spParameter.getValue()).getTime());
				callableStatement.setTimestamp(spParameter.getParameterPosition(), date);
			}
			break;
		case "tinytext":
		case "text":
		case "mediumtext":
		case "longtext":
			if (spParameter.getValue() == null)
			{
				callableStatement.setNull(spParameter.getParameterPosition(), Types.CLOB);
			}
			else
			{
				clob = connection.createClob();
				bytes = Base64.getDecoder().decode((String)spParameter.getValue());
				clob.setString(1, new String(bytes, "UTF-8"));
				callableStatement.setClob(spParameter.getParameterPosition(), clob);
			}
			break;
		case "tinyblob":
		case "blob":
		case "mediumblob":
		case "longblob":
			if (spParameter.getValue() == null)
			{
				callableStatement.setNull(spParameter.getParameterPosition(), Types.BLOB);
			}
			else
			{
				blob = connection.createBlob();
				bytes = Base64.getDecoder().decode((String)spParameter.getValue());
				blob.setBytes(1, bytes);
				callableStatement.setBlob(spParameter.getParameterPosition(), blob);
			}
			break;
		default:
			break;
		}
	}
	
	private void setOutputParameter(SPParameter spParameter, CallableStatement callableStatement) throws SQLException
	{
		switch (spParameter.getParameterDataType().toLowerCase()) 
		{
		case "int":
			callableStatement.registerOutParameter(spParameter.getParameterPosition(), Types.INTEGER);						
			break;
		case "numeric":
			callableStatement.registerOutParameter(spParameter.getParameterPosition(), Types.NUMERIC);						
			break;
		case "char":
			callableStatement.registerOutParameter(spParameter.getParameterPosition(), Types.CHAR);						
			break;
		case "varchar":
			callableStatement.registerOutParameter(spParameter.getParameterPosition(), Types.VARCHAR);						
			break;
		case "datetime":
			callableStatement.registerOutParameter(spParameter.getParameterPosition(), Types.DATE);						
			break;
		case "tinytext":
		case "text":
		case "mediumtext":
		case "longtext":
			callableStatement.registerOutParameter(spParameter.getParameterPosition(), Types.CLOB);						
			break;
		case "tinyblob":
		case "blob":
		case "mediumblob":
		case "longblob":
			callableStatement.registerOutParameter(spParameter.getParameterPosition(), Types.BLOB);						
			break;
		default:
			break;
		}
	}
	
	public JSONObject executeSP(String procedure, JSONObject parameters) throws Exception
	{
		Connection connection;
		PreparedStatement preparedStatement;
		CallableStatement callableStatement; 
		ResultSet resultSet;
		ArrayList<SPParameter> spParameterList;
		int cParameters;
		JSONObject result;
		
		connection = null;
		preparedStatement = null;
		callableStatement = null; 
		resultSet = null;
		result = null;

		try
		{
			connection = openConnection();
			
			preparedStatement = connection.prepareStatement("SELECT ORDINAL_POSITION, PARAMETER_MODE, PARAMETER_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH FROM information_schema.parameters WHERE SPECIFIC_SCHEMA = ? AND SPECIFIC_NAME = ? ORDER BY ORDINAL_POSITION;");
			preparedStatement.setString(1, schema);
			preparedStatement.setString(2, procedure);
			resultSet = preparedStatement.executeQuery();
						
			if (resultSet.next())
			{
				cParameters = 0;
				
				spParameterList = new ArrayList<SPParameter>();
				
				do
				{
					SPParameter spParameter = new SPParameter();
					
					spParameter.setParameterPosition(resultSet.getInt("ORDINAL_POSITION"));
					spParameter.setParameterMode(resultSet.getString("ORDINAL_POSITION"));
					spParameter.setParameterMode(resultSet.getString("PARAMETER_MODE"));
					spParameter.setParameterName(resultSet.getString("PARAMETER_NAME"));
					spParameter.setParameterDataType(resultSet.getString("DATA_TYPE"));
					spParameter.setParameterMaxLenth(resultSet.getInt("CHARACTER_MAXIMUM_LENGTH"));

					if (spParameter.getParameterMode().trim().toUpperCase().equals("IN"))
					{
						JSONValue jsonValue = JPATH.find(parameters, "/" + spParameter.getParameterName());
						
						if (jsonValue != null)
						{
							if (jsonValue.getType() == JSONValueType.ARRAY || jsonValue.getType() == JSONValueType.UNKNOWN)
							{
								resultSet.close();
								preparedStatement.close();
								resultSet = null;
								preparedStatement = null;
								closeConnection(connection);
								
								throw new Exception("Unsupported data type for parameter " + spParameter.getParameterName());								
							}
							
							spParameter.setValue(jsonValue.getValue());
						}
					}
					
					spParameterList.add(spParameter);
										
					cParameters++;
				} while(resultSet.next());

				StringBuffer call = new StringBuffer();
				
				call.append("{ call " + procedure + " (");

				for (int cP = 0; cP < cParameters; cP++)
				{
					if (cP == 0)
					{
						call.append("?");
					}
					else
					{
						call.append(",?");
					}
				}
				
				call.append(") }");
				
				callableStatement = connection.prepareCall(call.toString());
			
				for (SPParameter spParameter : spParameterList) 
				{
					if (spParameter.getParameterMode().trim().toUpperCase().equals("IN"))
					{
						setInputParameter(spParameter, callableStatement, connection);
					}

					if (spParameter.getParameterMode().trim().toUpperCase().equals("OUT"))
					{
						setOutputParameter(spParameter, callableStatement);
					}
				}

				callableStatement.execute();
				
				result = getOutputResult(spParameterList, callableStatement);
			}
		}
		catch(Exception e)
		{
			throw new Exception(e);
		}
		finally 
		{
			if (resultSet != null)
			{
				try 
				{ 
					resultSet.close(); 
				} 
				catch(Exception e) 
				{
				}
			}
			
			if (preparedStatement != null && !preparedStatement.isClosed())
			{
				try 
				{ 
					preparedStatement.close();
				} 
				catch(Exception e) 
				{
				}
			}
			
			if (callableStatement != null && !callableStatement.isClosed())
			{
				try 
				{ 
					callableStatement.close();
				} 
				catch(Exception e) 
				{
				}
			}
			
			if (connection != null)
			{
				closeConnection(connection);
			}
		}
		
		return result;
	}
}
