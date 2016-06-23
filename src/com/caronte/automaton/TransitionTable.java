package com.caronte.automaton;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class TransitionTable 
{
	private State firstState;
	private HashMap<String, State> table;
	
	public TransitionTable() 
	{
		table = new HashMap<String, State>();
	}

	public Boolean loadFromBase64Input(String base64Input) throws IOException
	{
		byte[] bytes = Base64.getDecoder().decode(base64Input);
		String input = new String(bytes, "UTF-8");
		String[] lines = input.split("\r\n");
		List<String> list = new ArrayList<String>();

		for (String string : lines) 
		{
			list.add(string);
		}
		
		return loadFromList(list);
	}

	public Boolean loadFromFile(String path) throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get(path), Charset.forName("UTF-8"));
		return loadFromList(lines);
	}
	
	private Boolean loadFromList(List<String> list)
	{
		ArrayList<String> actions;

		if (list.size() > 1)
		{
			String lastState = list.get(list.size() - 1).split("\\|")[0];

			for (int l = 1; l < list.size(); l++) 
			{
				if (list.get(l).startsWith("#") || list.get(l).trim().length() == 0)
				{
					continue;
				}

				String[] data = list.get(l).split("\\|");
				State state;
				
				actions = null;
				
				if (data.length > 3)
				{
					String[] aActions = data[3].split(",");
					actions = new ArrayList<String>();
					
					for (int ia = 0; ia < aActions.length; ia++) 
					{
						actions.add(aActions[ia]);
					}
				}
				
				
				if (!table.containsKey(data[0]))
				{
					state = new State(data[0], l == 1, data[0].equals(lastState));
					table.put(data[0], state);
					
					if (l == 1)
					{
						firstState = state;
					}
				}
				else
				{
					state = table.get(data[0]);
				}
				
				state.addTransition(data[1], data[2], actions);
			}
			
			return true;
		}
		
		return false;
	}

	public State getFirstState() 
	{
		return firstState;
	}

	public State nextState(State currentState, String transitionValue, IActionExecutor actionExecutor) throws Exception
	{
		State epsilonState = null;
		
		if (currentState == null)
		{
			return null;
		}
		
		currentState = table.get(currentState.nextState(transitionValue, actionExecutor));
		
		while (currentState != null)
		{
			epsilonState = table.get(currentState.nextState("epsilon", actionExecutor));

			if (epsilonState == null)
			{
				break;
			}

			currentState = epsilonState;
		}
		
		return currentState;
	}
}
