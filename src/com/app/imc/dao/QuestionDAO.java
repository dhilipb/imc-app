package com.app.imc.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.app.imc.model.Course;
import com.app.imc.model.Question;

public class QuestionDAO extends SQLiteDAO<Question> {

	public static String TABLE_NAME = Question.class.getSimpleName();
	public static String[] SCHEMA = {"question TEXT NOT NULL",
									"explanation TEXT",
									"correctAnswer TEXT",
									"course INTEGER NOT NULL"};
	
	private Course course;
	
	public void setCourse(Course course) {
		this.course = course;
	}
	
	@Override
	public long insert(Question question) {
		if (course == null) return -1;
		
		ContentValues values = new ContentValues();
		values.put("question", question.getQuestion());
		values.put("explanation", question.getExplanation());
		values.put("correctAnswer", question.getCorrectAnswer());
		values.put("course", course.getId());
		
		long id = getDatabase().insert(TABLE_NAME, null, values);
		question.setId(id);
		getInsertSql(values);

		return id;
	}

	@Override
	protected Question getCursor(Cursor cursor) {
		Question question = new Question(cursor.getString(1));
		question.setId(cursor.getLong(0));
		question.setExplanation(cursor.getString(2));
		question.setCorrectAnswer(cursor.getString(3));
		
		return question;
	}

}
