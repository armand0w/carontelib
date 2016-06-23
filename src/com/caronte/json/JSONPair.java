package com.caronte.json;

public class JSONPair 
{
	private String string;
	private JSONValue value;
	
	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	}
	public JSONValue getValue() {
		return value;
	}
	public void setValue(JSONValue value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof JSONPair)
		{
			return ((JSONPair) obj).getString().equalsIgnoreCase(this.string);					
		}
		
		return super.equals(obj);
	}
}
