package com.caronte.db.pool;

import java.sql.Connection;

import com.caronte.db.enums.Status;

public class DataBaseConnection
{
	private Connection connection;
	private Status status;

	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
}