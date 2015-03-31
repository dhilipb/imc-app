package com.app.imc.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.app.imc.model.Option;
import com.app.imc.model.Question;

public class OptionDAO extends SQLiteDAO<Option> {

	public static String TABLE_NAME = Option.class.getSimpleName();
	public static String[] SCHEMA = {"text TEXT NOT NULL",
									"question INTEGER NOT NULL"};
	private Question question;
	
	public void setQuestion(Question question) {
		this.question = question;
	}
	
	@Override
	public long insert(Option option) {
		if (question == null) return -1;
		
		ContentValues values = new ContentValues();
		values.put("text", option.getText());
		values.put("question", question.getId());
		
		long id = getDatabase().insert(TABLE_NAME, null, values);
		option.setId(id);
		getInsertSql(values);

		return id;
	}

	@Override
	protected Option getCursor(Cursor cursor) {
		Option option = new Option(cursor.getString(1));
		option.setId(cursor.getLong(0));
		return option;
	}

}
