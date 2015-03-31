package com.app.imc.model;

import java.util.List;
import java.util.Locale;

public class Course extends Model  {
	
	private final String slug; // derived
	private final String courseName;
	private final String longName;
	private List<Question> questions;
	
	public Course(String courseName, String longName) {
		this.courseName = courseName;
		this.longName = longName;
		slug = courseName.toLowerCase(Locale.UK).replaceAll("[\\s\\.]", "");
	}
	
	public String getCourseName() {
		return courseName;
	}
	public String getLongName() {
		return longName;
	}
	public List<Question> getQuestions() {
		return questions;
	}
	public String getSlug() {
		return slug;
	}
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	@Override
	public String toString() {
		return "Course [name='" + courseName + "']";
	}
	
}
