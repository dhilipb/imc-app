package com.app.imc.model;

public abstract class Model {

	protected long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public abstract String toString();
	
}
