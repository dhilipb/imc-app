package com.app.imc.model;

public class History extends Model {

	private final Long courseId;
	private final Integer correct;
	private String date;
	
	public History(Long courseId, Integer correct) {
		this.courseId = courseId;
		this.correct = correct;
	}
	
	public Long getCourseId() {
		return courseId;
	}
	
	public Integer getCorrect() {
		return correct;
	}

	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		return "History [courseId="+courseId+", correct="+correct+", date="+date+"]";
	}
	
}
