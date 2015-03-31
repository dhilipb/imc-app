package com.app.imc.model;


public class Option extends Model  {
	private final String text;
    
	public Option(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return "Option [text='" + text + "']";
	}
}
