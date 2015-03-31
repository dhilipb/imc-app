package com.app.imc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.imc.dao.CourseDAO;
import com.app.imc.dao.OptionDAO;
import com.app.imc.dao.QuestionDAO;

public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "imc.db";
	private static final int DATABASE_VERSION = 1;

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTable(CourseDAO.TABLE_NAME, CourseDAO.SCHEMA));
		db.execSQL(createTable(QuestionDAO.TABLE_NAME, QuestionDAO.SCHEMA));
		db.execSQL(createTable(OptionDAO.TABLE_NAME, OptionDAO.SCHEMA));
	}

	private String createTable(String tableName, String[] cols) {
		String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
		sql += "id INTEGER PRIMARY KEY AUTOINCREMENT,";
		for (String column : cols) {
			sql += column + ",";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ");";
		Log.d("SQL", sql);
		return sql;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteHelper.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + CourseDAO.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + QuestionDAO.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + OptionDAO.TABLE_NAME);
		onCreate(db);
	}

}
