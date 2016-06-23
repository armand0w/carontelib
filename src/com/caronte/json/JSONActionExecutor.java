package com.caronte.json;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.caronte.automaton.IActionExecutor;
import com.caronte.automaton.StateException;

public class JSONActionExecutor implements IActionExecutor 
{
	private Integer lineNumber;
	private StringBuffer lineFragment;
	private StringBuffer token;
	private String string;
	private Boolean tokenScannerEnabled;
	private Boolean pushLastValueAfterAction;
	private Integer subJSONLevels;
	private JSONObject jsonObject;
	private ParserParameter parserParameter;
	
	public JSONActionExecutor(Integer lineNumer, JSONObject jsonObject, ParserParameter parserParameter) 
	{
		this.lineNumber = lineNumer;
		this.jsonObject = jsonObject;
		this.parserParameter = parserParameter;
		
		lineFragment = new StringBuffer();
		token = new StringBuffer();
		tokenScannerEnabled = false;
		pushLastValueAfterAction = false;		
	}
	
	public Integer getLineNumber() 
	{
		return lineNumber;
	}
	
	public String getLineFragment() 
	{
		return lineFragment.toString();
	}
	
	@Override
	public void execute(String action) throws Exception, StateException
	{
		if (action != null)
		{		
			if (action.equalsIgnoreCase("SkipAlphabet"))
			{
				String skipped = parserParameter.SkipAlphabet();
				
				if (skipped.length() > 0)
				{
					token.append(skipped);
				}
			}
			if (action.equalsIgnoreCase("SkipNumber"))
			{
				String skipped = parserParameter.SkipNumber();
				
				if (skipped.length() > 0)
				{
					token.append(skipped);
				}
			}
			if (action.equalsIgnoreCase("IncLineCounter"))
			{
				lineNumber++;
				lineFragment = new StringBuffer();
			}
			if (action.equalsIgnoreCase("UnicodeToChar"))
			{
				if (tokenScannerEnabled)
				{
					String tokenAux = token.toString();
					String unicode = tokenAux.substring(tokenAux.length() - 4);		
					int hexadecimalValue = Integer.parseInt(unicode, 16);
					String stringValue = Character.toString((char)hexadecimalValue);
					tokenAux = tokenAux.substring(0, tokenAux.length() - 6) + stringValue;
					token = new StringBuffer();
					token.append(tokenAux);
				}
			}
			if (action.equalsIgnoreCase("StartStringScanner"))
			{
				tokenScannerEnabled = true;
				pushLastValueAfterAction = false;
			}			
			if (action.equalsIgnoreCase("StartNumericScanner"))
			{
				tokenScannerEnabled = true;
				pushLastValueAfterAction = true;
			}			
			if (action.equalsIgnoreCase("StartReservedWordScanner"))
			{
				tokenScannerEnabled = true;
				pushLastValueAfterAction = true;
			}			
			if (action.equalsIgnoreCase("StartArrayScanner"))
			{
				jsonObject.resetArray();
			}
			if (action.equalsIgnoreCase("StartJSONScanner"))
			{
				subJSONLevels = 0;
				tokenScannerEnabled = true;
				pushLastValueAfterAction = true;
			}
			if (action.equalsIgnoreCase("PushSubJSONMarker"))
			{
				subJSONLevels++;
			}
			if (action.equalsIgnoreCase("PushElementLabel"))
			{
				string =  token.toString().substring(0, token.length() - 1);
				token = new StringBuffer();
				tokenScannerEnabled = false;
			}			
			if (action.equalsIgnoreCase("PushStringValue") || action.equalsIgnoreCase("PushStringValueToArray"))
			{
				String value = token.toString().substring(0, token.length() - 1);
				
				if (action.equalsIgnoreCase("PushStringValue"))
				{
					jsonObject.addPair(string, value);
				}
				
				if (action.equalsIgnoreCase("PushStringValueToArray"))
				{
					jsonObject.addToArray(value);
				}
				
				token = new StringBuffer();
				tokenScannerEnabled = false;
			}			
			if (action.equalsIgnoreCase("PushIntegerValue") || action.equalsIgnoreCase("PushIntegerValueToArray"))
			{
				String value = token.toString().substring(0, token.length() - 1);
				
				if (action.equalsIgnoreCase("PushIntegerValue"))
				{
					jsonObject.addPair(string, new BigInteger(value));
				}
				
				if (action.equalsIgnoreCase("PushIntegerValueToArray"))
				{
					jsonObject.addToArray(new BigInteger(value));
				}

				token = new StringBuffer();
				tokenScannerEnabled = false;
			}			
			if (action.equalsIgnoreCase("PushDecimalValue") || action.equalsIgnoreCase("PushDecimalValueToArray"))
			{
				String value = token.toString().substring(0, token.length() - 1);
				
				if (action.equalsIgnoreCase("PushDecimalValue"))
				{
					jsonObject.addPair(string, new BigDecimal(value));
				}
				
				if (action.equalsIgnoreCase("PushDecimalValueToArray"))
				{
					jsonObject.addToArray(new BigDecimal(value));
				}
				
				token = new StringBuffer();
				tokenScannerEnabled = false;
			}			
			if (action.equalsIgnoreCase("PushReservedWordValue") || action.equalsIgnoreCase("PushReservedWordValueToArray"))
			{
				String value = token.toString().substring(0, token.length() - 1);
				
				if (!value.equals("true") && !value.equals("false") && !value.equals("null"))
				{
					String error;
					String fragment = getLineFragment();
					
					fragment = fragment.replaceAll("\t", "    ");
					
					if (fragment.length() > 50)
					{
						fragment = "... " + fragment.substring(fragment.length() - 50);
					}
					
					error = "Parse error on line " + getLineNumber() + ":\n\n" + fragment + "\n";
					for (int ie = 2; ie < fragment.length(); error +="-", ie++);
					error += "^\nUnexpected value : " + value + "\n";
					throw new Exception(error);
				}
				else
				{
					if (value.equals("null"))
					{
						if (action.equalsIgnoreCase("PushReservedWordValue"))
						{
							jsonObject.addPair(string, null);
						}

						if (action.equalsIgnoreCase("PushReservedWordValueToArray"))
						{
							jsonObject.addToArray(null);
						}						
					}
					else
					{
						if (action.equalsIgnoreCase("PushReservedWordValue"))
						{
							jsonObject.addPair(string, new Boolean(value));
						}

						if (action.equalsIgnoreCase("PushReservedWordValueToArray"))
						{
							jsonObject.addToArray(new Boolean(value));
						}						
					}
				}
				
				token = new StringBuffer();
				tokenScannerEnabled = false;
			}
			if (action.equalsIgnoreCase("PushArrayValue"))
			{
				jsonObject.saveArray(string);
			}
			if (action.equalsIgnoreCase("TryPushJSONValue") || action.equalsIgnoreCase("TryPushJSONValueToArray"))
			{				
				subJSONLevels--;
			
				if (subJSONLevels == 0)
				{
					String value = token.toString().substring(0, token.length());
					
					JSONObject jsonObjectAux = JSON.parse(value);
					
					if (action.equalsIgnoreCase("TryPushJSONValue"))
					{
						jsonObject.addPair(string, jsonObjectAux);
					}
					
					if (action.equalsIgnoreCase("TryPushJSONValueToArray"))
					{
						jsonObject.addToArray(jsonObjectAux);
					}
					
					token = new StringBuffer();
					tokenScannerEnabled = false;
				}
				else
				{
					throw new StateException("End of complex element not found", true);
				}
			}			
		}
	}
	
	@Override
	public void beforeTransition(String value) 
	{
		lineFragment.append(value);

		if (tokenScannerEnabled)
		{
			token.append(value);
		}
	}

	@Override
	public void afterTransition(String value)
	{
		if (pushLastValueAfterAction)
		{
			token.append(value);
			pushLastValueAfterAction = false;
		}		
	}
}
