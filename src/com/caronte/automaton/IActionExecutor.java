package com.caronte.automaton;


public interface IActionExecutor 
{	
	public void execute(String action) throws Exception, StateException;
	public void beforeTransition(String value);
	public void afterTransition(String value);
}
