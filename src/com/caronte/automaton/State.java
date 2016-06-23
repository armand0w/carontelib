package com.caronte.automaton;

import java.util.ArrayList;
import java.util.HashMap;

public class State 
{
	private Integer type;
	private String name;
	private HashMap<String, Transition> transitions;
	
	public State(String name, Boolean initialState, Boolean endState) 
	{
		this.name = name;
		this.type = StateType.NORMAL;
		this.type = initialState ? StateType.INITIAL : endState ? StateType.END : StateType.NORMAL;
		transitions = new HashMap<String, Transition>();
	}

	public String getName() 
	{
		return name;
	}
			
	public Boolean isInitialState()
	{
		return type == StateType.INITIAL;
	}

	public Boolean isEndState()
	{
		return type == StateType.END;
	}
	
	public Boolean addTransition(String value, String newState, ArrayList<String> actions)
	{
		if (transitions.containsKey(value))
		{
			return false;
		}
		
		if (value.isEmpty())
		{
			value = "epsilon";
		}
		
		value = value.replaceAll("\\\\t", "\t");
		value = value.replaceAll("\\\\n", "\n");
		value = value.replaceAll("\\\\r", "\r");
		
		transitions.put(value, new Transition(newState, actions));
		
		return true;
	}
	
	public String nextState(String value, IActionExecutor actionExecutor) throws Exception
	{
		if (!value.equalsIgnoreCase("epsilon"))
		{
			actionExecutor.beforeTransition(value);
		}

		if (transitions.containsKey(value))
		{
			Transition transition = transitions.get(value);
			ArrayList<String> actions = transition.getActions();


			if (actions != null)
			{
				for (String action : actions) 
				{
					try
					{
						actionExecutor.execute(action);					
					}
					catch(StateException se)
					{
						if (se.getRollback())
						{
							return this.name;
						}
					}
				}
			}
			
			if (!value.equalsIgnoreCase("epsilon"))
			{
				actionExecutor.afterTransition(value);
			}

			return transition.getNewState();
		}
		
		return null;
	}
}
