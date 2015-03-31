package com.app.imc.model;

import java.util.ArrayList;
import java.util.List;

public class Question extends Model  {

	private final String question;
	private List<Option> options;
	private String explanation;
	private String correctAnswer;
	private String answered;
    
	public Question(String question) {
		this.question = question;
		options = new ArrayList<Option>();
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}
	
	public void addOption(Option option) {
		options.add(option);
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getQuestion() {
		return question;
	}
	
	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public String getAnswered() {
		return answered;
	}

	public void setAnswered(String answered) {
		this.answered = answered;
	}


	@Override
	public String toString() {
		return "Question [name='" + question + "']";
	}
	
}
