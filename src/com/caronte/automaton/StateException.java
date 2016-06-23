package com.caronte.automaton;

@SuppressWarnings("serial")
public class StateException extends Exception 
{
	private Boolean rollback;
	
	public StateException(String message, Boolean rollback) 
	{ 
		super(message); 
		this.rollback = rollback;
	}
	
	public Boolean getRollback() 
	{
		return rollback;
	}	
}
