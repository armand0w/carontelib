package com.caronte.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashMap;

import com.caronte.jpath.JPATH;
import com.caronte.json.JSONObject;
import com.caronte.json.JSONValue;
import com.caronte.json.JSONValueType;
import com.caronte.pool.DataBasePool;

public class Oracle 
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
	
	public Oracle(String ip, String port, String user, String password, String schema, Integer blockSize, Integer maxPoolSize) 
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
					case Types.CLOB:
						column.addPair("size", resultSetMetaData.getPrecision(i + 1));
						column.addPair("type", "CLOB");
						break;
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
						column.addPair("type", "INTEGER");
						break;
					case Types.NUMERIC:
						column.addPair("type", "NUMERIC");
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
						case Types.CLOB:
							element.addPair(resultSetMetaData.getColumnLabel(i + 1), new String(Base64.getEncoder().encode(resultSet.getString(i + 1).getBytes("UTF-8")),"UTF-8"));
							break;
						case Types.LONGVARCHAR:							
							element.addPair(resultSetMetaData.getColumnLabel(i + 1), resultSet.getString(i + 1));
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
						case Types.NUMERIC:
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
}
