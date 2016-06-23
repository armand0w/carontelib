package com.caronte.automaton;

import java.util.ArrayList;

public class Transition 
{
	private String newState;
	private ArrayList<String> actions;
	
	public Transition(String newState, ArrayList<String> actions) 
	{
		this.actions = actions;
		this.newState = newState;
	}
	
	public String getNewState() 
	{
		return newState;
	}

	public ArrayList<String> getActions() 
	{
		return actions;
	}
}
