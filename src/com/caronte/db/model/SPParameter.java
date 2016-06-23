package com.caronte.db.model;

public class SPParameter 
{
	private Integer parameterPosition;
	private String parameterMode;
	private String parameterName;
	private String parameterDataType;
	private Integer parameterMaxLenth;
	private Object value;
		
	public Integer getParameterPosition() 
	{
		return parameterPosition;
	}
	
	public void setParameterPosition(Integer parameterPosition) 
	{
		this.parameterPosition = parameterPosition;
	}
	
	public String getParameterMode() 
	{
		return parameterMode;
	}
	
	public void setParameterMode(String parameterMode) 
	{
		this.parameterMode = parameterMode;
	}
	
	public String getParameterName() 
	{
		return parameterName;
	}
	
	public void setParameterName(String parameterName) 
	{
		this.parameterName = parameterName;
	}
	
	public String getParameterDataType() 
	{
		return parameterDataType;
	}
	
	public void setParameterDataType(String parameterDataType) 
	{
		this.parameterDataType = parameterDataType;
	}
	
	public Integer getParameterMaxLenth() 
	{
		return parameterMaxLenth;
	}
	
	public void setParameterMaxLenth(Integer parameterMaxLenth) 
	{
		this.parameterMaxLenth = parameterMaxLenth;
	}
	
	public Object getValue() 
	{
		return value;
	}
	
	public void setValue(Object value) 
	{
		this.value = value;
	}	
}
