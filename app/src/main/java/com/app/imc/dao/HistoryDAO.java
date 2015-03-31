package com.app.imc.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.app.imc.model.History;

public class HistoryDAO extends SQLiteDAO<History> {

	public static String TABLE_NAME = History.class.getSimpleName();
	public static String[] SCHEMA = {"time datetime default CURRENT_TIMESTAMP",
									"course integer NOT NULL",
									"correct integer NOT NULL"};

	@Override
	public long insert(History history) {
		ContentValues values = new ContentValues();
		values.put("course", history.getCourseId());
		values.put("correct", history.getCorrect());
		
		long id = getDatabase().insert(TABLE_NAME, null, values);
		history.setId(id);
		getInsertSql(values);
		
		return id;
	}

	@Override
	protected History getCursor(Cursor cursor) {
		History history = new History(cursor.getLong(2), cursor.getInt(3));
		history.setId(cursor.getLong(0));
		history.setDate(cursor.getString(1));
		return history;
	}

}
