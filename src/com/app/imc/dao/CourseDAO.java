package com.app.imc.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.app.imc.model.Course;

public class CourseDAO extends SQLiteDAO<Course> {

	public static String TABLE_NAME = Course.class.getSimpleName();
	public static String[] SCHEMA = {"courseName TEXT NOT NULL",
									"longName TEXT"};
	
	@Override
	public long insert(Course course) {
		ContentValues values = new ContentValues();
		values.put("courseName", course.getCourseName());
		values.put("longName", course.getLongName());
		
		long id = getDatabase().insert(TABLE_NAME, null, values);
		course.setId(id);
		getInsertSql(values);
		
		return id;
	}

	@Override
	protected Course getCursor(Cursor cursor) {
		Course course = new Course(cursor.getString(1), cursor.getString(2));
		course.setId(cursor.getLong(0));
		return course;
	}

}
