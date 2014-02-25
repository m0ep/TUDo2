package de.m0ep.tudo2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.m0ep.tudo2.data.TaskContract.Task;

public final class TaskSQLiteHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "tudo2.db";

	private static final String SQL_CREATE_TABLE_TASKS = "CREATE TABLE IF NOT EXISTS " +
	        Task.TABLENAME + " ( "
	        + Task._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
	        + Task.COMPLETED + " TEXT, "
	        + Task.IS_DELETED + " INTEGER DEFAULT 0, "
	        + Task.DUE + " TEXT NOT NULL, "
	        + Task.NOTE + " TEXT, "
	        + Task.PRIORITY + " TEXT NOT NULL, "
	        + Task.STATUS + " TEXT DEFAULT '"
	        + TaskConstants.STATUS_NOT_COMPLETED + "', "
	        + Task.TITLE + " TEXT NOT NULL, "
	        + Task.UPDATED + " TEXT NOT NULL"
	        + ");";

	public TaskSQLiteHelper( Context context ) {
		super( context, DBNAME, null, 1 );
	}

	@Override
	public void onCreate( SQLiteDatabase db ) {
		db.execSQL( SQL_CREATE_TABLE_TASKS );
	}

	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		db.execSQL( "DROP TABLE IF EXISTS " + Task.TABLENAME );
		db.execSQL( SQL_CREATE_TABLE_TASKS );
	}

}