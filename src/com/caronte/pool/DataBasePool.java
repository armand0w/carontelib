package com.caronte.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Stack;

import com.caronte.enums.Status;

public class DataBasePool 
{
	private String ip;
	private String port;
	private String password;
	private String user;
	private String schema;
	
	private int maxSize;
	private int blockSize;
	private int pivot;

	private Stack<DataBaseConnection> stackConnections;
	
	public DataBasePool() {
		this.pivot = 0;
	}
	
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setIP(String ip) {
		this.ip = ip;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public synchronized void closeConnection(Connection connection) 
	{
		int i;
		
		if (stackConnections == null)
			return;
		
		for (i = 0; i < stackConnections.size(); i++)
		{
			DataBaseConnection dataBaseConnection = stackConnections.get(i);
			
			if (dataBaseConnection.getConnection() == connection)
			{
				dataBaseConnection.setStatus(Status.AVAILABLE);
				break;
			}
		}
	}

	public synchronized Connection openConnection() 
	{
		int i;
		int delta = 0;
		Boolean createNewConnections = true;
		DataBaseConnection dataBaseConnection = null;
		
		if (stackConnections == null)
		{
			stackConnections = new Stack<DataBaseConnection>();
		}

		for (i = 0; i < stackConnections.size(); i++)
		{
			dataBaseConnection = stackConnections.get((i + this.pivot) % stackConnections.size());
			
			if (dataBaseConnection.getStatus() == Status.AVAILABLE)
			{
				dataBaseConnection.setStatus(Status.BUSSY);
				createNewConnections = false;
				break;
			}
			else
			{
				delta++;
			}
		}
		
		if (createNewConnections)
		{
			delta = 0;
			
			for (i = 0; i < blockSize; i++)
			{
				if (stackConnections.size() >= maxSize)
				{
					break;
				}
				
				try
				{
					DataBaseConnection dataBaseConnectionAux = new DataBaseConnection();	
					dataBaseConnectionAux.setConnection(DriverManager.getConnection("jdbc:oracle:thin:@" + ip + ":" + port + ":" + schema, user, password));
					dataBaseConnectionAux.setStatus(Status.AVAILABLE);
					
					if (dataBaseConnection == null)
					{	
						pivot = stackConnections.size();
						dataBaseConnection = dataBaseConnectionAux;
						dataBaseConnection.setStatus(Status.BUSSY);
					}
					
					stackConnections.add(dataBaseConnectionAux);
				}
				catch(Exception e)
				{
				}
			}
		}
		
		try
		{
			if (dataBaseConnection != null && dataBaseConnection.getConnection() != null && dataBaseConnection.getConnection().isClosed())
			{
				dataBaseConnection.setConnection(DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + schema, user, password));
			}

			pivot = (pivot + delta + 1) % stackConnections.size();
			
			return dataBaseConnection.getConnection();
		}
		catch(Exception e)
		{
			return null;
		}
	}

}
