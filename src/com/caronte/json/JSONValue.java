package com.caronte.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class JSONValue 
{
	private JSONValueType type;
	private Object value;
	
	public static JSONValueType getTypeFromValue(Object value)
	{
		if (value == null)
			return JSONValueType.NULL;
		
		if (value instanceof Boolean)
			return JSONValueType.BOOL;

		if (value instanceof BigInteger || value instanceof Integer || value instanceof Long)
			return JSONValueType.INTEGER;
	
		if (value instanceof BigDecimal || value instanceof Float || value instanceof Double)
			return JSONValueType.DECIMAL;

		if (value instanceof String)
			return JSONValueType.STRING;

		if (value instanceof JSONObject)
			return JSONValueType.OBJECT;

		if (value instanceof ArrayList)
			return JSONValueType.ARRAY;

		return JSONValueType.UNKNOWN;
	}
	
	public JSONValue(Object value) 
	{
		this.type = getTypeFromValue(value);
		this.value = value;
	}
	
	public JSONValueType getType() {
		return type;
	}
	
	public Object getValue() {
		return value;
	}
}
