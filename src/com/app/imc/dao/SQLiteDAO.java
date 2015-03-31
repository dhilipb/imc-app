package com.app.imc.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.app.imc.Database;

public abstract class SQLiteDAO<K> {

	public abstract long insert(K k);
	
	protected SQLiteDatabase getDatabase() {
		return Database.getDatabase();
	}

	public List<K> retrieveAll() {
		List<K> result = cursorToModel(getDatabase().query(getTableName(),
				getColumns(), null, null, null, null, null));
		
		return result;
	}
	
	protected abstract K getCursor(Cursor cursor);
	
	private List<K> cursorToModel(Cursor cursor) {
		List<K> result = new ArrayList<K>();
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			K item = getCursor(cursor);
			result.add(item);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		
		return result;
	}
	
	/*
	 * Select all by a column
	 */
	public List<K> retrieveByColumn(String column, String value) {
		List<K> result = cursorToModel(getDatabase().rawQuery("SELECT * FROM " + getTableName() +
				" WHERE " + column + " = ?", new String[] {value}));
		
		return result;
	}
	
	private String[] getColumns() {
		List<String> columns = new ArrayList<String>();
		columns.add("id");
		
		try {
			Field f = Class.forName(this.getClass().getName()).getField("SCHEMA");
			String[] schema = (String[]) f.get(null);
			for (String row : schema) {
				columns.add(row.split(" ")[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return columns.toArray(new String[columns.size()]);
	}
	
	protected String getTableName() {
		String tableName = "";
		
		try {
			Field f = Class.forName(this.getClass().getName()).getField("TABLE_NAME");
			tableName = (String) f.get(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return tableName;
	}
	
	protected void getInsertSql(ContentValues values) {
		
		String sql = "INSERT INTO " + getTableName() + "(";
		for (String key : values.keySet()) {
			sql += "'" + key + "',";
		}
		sql = sql.substring(0, sql.length() - 1) + ") VALUES (";
		
		for (String key : values.keySet()) {
			sql += "" + DatabaseUtils.sqlEscapeString(values.getAsString(key)) + ",";
		}
		sql = sql.substring(0, sql.length() - 1) + ")";
		
//		Log.d("SQL", sql);
	}
	
}
