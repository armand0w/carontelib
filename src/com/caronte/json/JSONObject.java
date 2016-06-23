package com.caronte.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;

public class JSONObject 
{
	protected JSONObject parent;
	protected ArrayList<JSONPair> members;
	private ArrayList<Object> array;

	public JSONObject(JSONObject jsonObject) 
	{
		members = jsonObject.members;
		parent = jsonObject.parent;
	}

	public JSONObject() 
	{
		members = new ArrayList<JSONPair>();
	}
	
	public Enumeration<String> elements()
	{
		Enumeration<String> result = new Enumeration<String>() {
			private Integer index = 0;
			private Integer total = members.size();
			
			@Override
			public String nextElement() 
			{
				JSONPair pair = members.get(index++);
				return pair.getString();
			}
			
			@Override
			public boolean hasMoreElements() 
			{
				return index < total;
			}
		};
		
		return result;
	}
	
	public void addPair(String string, Object value) throws Exception 
	{
		JSONPair jsonPair = new JSONPair();
		
		if (value instanceof JSONObject)
		{
			((JSONObject)value).parent = this;
		}
		
		jsonPair.setString(string);
		jsonPair.setValue(new JSONValue(value));
		
		if (members.contains(jsonPair))
		{
			throw new Exception("Element \"" + string + "\" already exist in JSON");
		}

		members.add(jsonPair);
	}

	public void resetArray() 
	{
		array = new ArrayList<Object>();
	}

	public void addToArray(Object value) 
	{
		if (value instanceof JSONObject)
		{
			((JSONObject)value).parent = this;
		}

		array.add(value);
	}

	public void saveArray(String string) throws Exception 
	{
		addPair(string, array);
	}
	
	@SuppressWarnings("unchecked")
	private String arrayToString(Integer indentation, Object array)
	{
		StringBuffer buffer = new StringBuffer();
		ArrayList<Object> arrayList = (ArrayList<Object>)array; 
		
		for (Object object : arrayList) 
		{
			JSONValueType type = JSONValue.getTypeFromValue(object);
			
			for (int i = 0; i < indentation; buffer.append("\t"), i++);
			
			if (type == JSONValueType.BOOL || type == JSONValueType.INTEGER || type == JSONValueType.NULL)
			{
				buffer.append(object);
			}
			
			if (type == JSONValueType.DECIMAL)
			{
				if (object instanceof BigDecimal)
				{
					buffer.append(((BigDecimal)object).toPlainString());
				}
				else
				{
					buffer.append(object);
				}
			}
						
			if (type == JSONValueType.STRING)
			{
				buffer.append("\"" + object + "\"");
			}
			
			if (type == JSONValueType.OBJECT)
			{
				buffer.append(((JSONObject)object).toString(indentation));
			}

			if (type == JSONValueType.ARRAY)
			{
				buffer.append("[\n");
				buffer.append(arrayToString(indentation + 1, object));
				buffer.append("\n");

				for (int i = 0; i < indentation; buffer.append("\t"), i++);
				
				buffer.append("]");
			}

			if (arrayList.get(arrayList.size() - 1) != object)
			{
				buffer.append(",\n");
			}
		}

		return buffer.toString();
	}
	
	private String toString(Integer indentation, ArrayList<JSONPair> members)
	{
		StringBuffer buffer = new StringBuffer();
		
		if (members != null)
		{
			if (members.size() > 0)
			{
				buffer.append("{");

				for (JSONPair member : members) 
				{
					buffer.append("\n");
					String key = member.getString();
					JSONValue value = member.getValue();
					JSONValueType type = value.getType();
					
					for (int i = 0; i < (indentation + 1); buffer.append("\t"), i++);
					
					buffer.append("\"" + key + "\" : ");

					if (type == JSONValueType.NULL)
					{
						buffer.append("null");
					}

					if (type == JSONValueType.BOOL || type == JSONValueType.INTEGER || type == JSONValueType.DECIMAL)
					{
						buffer.append(value.getValue());
					}
					
					if (type == JSONValueType.STRING)
					{
						buffer.append("\"" + value.getValue() + "\"");
					}
					
					if (type == JSONValueType.ARRAY)
					{
						buffer.append("[\n");
						buffer.append(arrayToString(indentation + 2, value.getValue()));
						buffer.append("\n");

						for (int i = 0; i < (indentation + 1); buffer.append("\t"), i++);
						
						buffer.append("]");
					}

					if (type == JSONValueType.OBJECT)
					{
						buffer.append(((JSONObject)value.getValue()).toString(indentation + 1));
					}
					
					if (!members.get(members.size() - 1).equals(member))
					{
						buffer.append(",");
					}
				}

				buffer.append("\n");				

				for (int i = 0; i < indentation; buffer.append("\t"), i++);
				
				buffer.append("}");				
			}
		}
		
		return buffer.toString();
	}
	
	public String toString(Integer indentation) 
	{
		return toString(indentation, members);
	}

	@Override
	public String toString() 
	{
		return toString(0, members);
	}
}
