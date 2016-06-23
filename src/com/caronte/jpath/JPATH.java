package com.caronte.jpath;

import java.util.ArrayList;

import com.caronte.json.JSONObject;
import com.caronte.json.JSONPair;
import com.caronte.json.JSONValue;
import com.caronte.json.JSONValueType;

class JPATHObject extends JSONObject
{
	public JPATHObject(JSONObject jsonObject) 
	{
		super(jsonObject);
	}

	public void removeValue(String objectName, String elementName)
	{
		JSONPair jsonPair = new JSONPair();
		
		if (elementName.contains("["))
		{
			int idx1 = elementName.indexOf("[");
			int idx2 = elementName.indexOf("]");
			int pathIndex = -1;
			
			if (idx1 != -1 && idx2 != -1)
			{
				String strPathIndex = elementName.substring(idx1 + 1, idx2);
				pathIndex = Integer.valueOf(strPathIndex); 
			}
			
			if (pathIndex != -1)
			{
				jsonPair.setString(elementName.substring(0, elementName.indexOf("[")));
				jsonPair.setValue(null);
				
				jsonPair = members.get(members.indexOf(jsonPair));
				
				if (jsonPair.getValue() != null && jsonPair.getValue().getType() == JSONValueType.ARRAY)
				{
					@SuppressWarnings("unchecked")
					ArrayList<Object> array = (ArrayList<Object>)jsonPair.getValue().getValue();
					
					if (pathIndex < array.size())
					{
						array.remove(pathIndex);
					}
				}
			}
		}
		else
		{
			jsonPair.setString(elementName);
			jsonPair.setValue(null);
			
			members.remove(jsonPair);
			
			if (members.size() == 0 && parent != null && objectName != null)
			{
				JPATHObject jpathObject = new JPATHObject(parent);
				
				for (JSONPair member : jpathObject.members) 
				{
					if (member.getString().equals(objectName))
					{
						member.setValue(new JSONValue(null));
					}
				}
			}
		}
	}
	
	public JSONValue findValue(String path)
	{
		JSONValue value = null;
		
		path = path.trim();
		
		if (path.startsWith("/"))
		{
			path = path.substring(1);
		}
		
		String[] pathElements = path.split("/");
		
		if (pathElements != null && pathElements.length > 0)
		{
			JSONPair jsonPair = new JSONPair();
			String pathElement = pathElements[0];
			int pathIndex = -1;

			if (pathElement.contains("["))
			{
				int idx1 = pathElement.indexOf("[");
				int idx2 = pathElement.indexOf("]");
				
				if (idx1 != -1 && idx2 != -1)
				{
					String strPathIndex = pathElement.substring(idx1 + 1, idx2);
					pathIndex = Integer.valueOf(strPathIndex); 
				}
				
				pathElement = pathElement.substring(0, pathElement.indexOf("["));
			}

			jsonPair.setString(pathElement);
			jsonPair.setValue(null);

			int index = members.indexOf(jsonPair);
				
			if (index != -1)
			{
				value = members.get(index).getValue();
				
				if (pathIndex != -1)
				{
					if (value.getType() != JSONValueType.ARRAY)
					{
						return null;
					}
					
					@SuppressWarnings("unchecked")
					ArrayList<Object> array = (ArrayList<Object>)value.getValue();
					
					if (array == null || array.size() < pathIndex)
					{
						return null;
					}
					
					value = new JSONValue(((JSONObject)array.get(pathIndex)));
				}
				
				if (pathElements.length > 1)
				{					
					if (value.getType() != JSONValueType.OBJECT)
					{
						return null;
					}

					JPATHObject jpathObject = new JPATHObject(((JSONObject)value.getValue()));
					value = jpathObject.findValue(path.substring(pathElements[0].length()));
				}
			}
		}
		
		return value;
	}
}

public class JPATH 
{
	public static JSONValue find(JSONObject jsonObject, String path)
	{
		JPATHObject jpathObject = new JPATHObject(jsonObject);
		return jpathObject.findValue(path);
	}
	
	public static void remove(JSONObject jsonObject, String path)
	{
		String pathParent = "";
		JSONObject parent = null;
		
		path = path.trim();
		
		if (path.startsWith("/"))
		{
			path = path.substring(1);
		}
		
		String[] pathElements = path.split("/");
		
		for (int i = 0; i < pathElements.length - 1; pathParent += "/" + pathElements[i++]);
		
		if (pathParent.equals(""))
		{
			parent = jsonObject;
		}
		else
		{
			JSONValue jsonValue = find(jsonObject, pathParent);
			
			if (jsonValue != null && jsonValue.getType() == JSONValueType.OBJECT)
			{
				parent = (JSONObject)jsonValue.getValue();
			}
		}
		
		if (parent != null)
		{
			JPATHObject jpathObject = new JPATHObject(parent);
			jpathObject.removeValue(pathElements.length > 2 ? pathElements[pathElements.length - 2] : null, pathElements[pathElements.length - 1]);
		}
		
		
	}

	public static Integer count(JSONObject jsonObject, String path)
	{
		JPATHObject jpathObject = new JPATHObject(jsonObject);
		JSONValue jsonValue = jpathObject.findValue(path);
		
		if (jsonValue.getType() == JSONValueType.ARRAY)
		{
			@SuppressWarnings("unchecked")
			ArrayList<Object> array = (ArrayList<Object>)jsonValue.getValue();
			
			if (array != null)
			{
				return array.size();
			}
		}
		
		return 0;
	}
}
