package de.m0ep.tudo2.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.m0ep.tudo2.model.TaskModel;

public final class TaskSQLiteHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "tudo2.db";

	private static final String SQL_CREATE_TABLE_TASKS = "CREATE TABLE IF NOT EXISTS " +
	        TaskContract.TaskEntry.TABLENAME + " ( "
	        + TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
	        + TaskContract.TaskEntry.COMPLETED + " TEXT, "
	        + TaskContract.TaskEntry.DELETED + " INTEGER DEFAULT 0, "
	        + TaskContract.TaskEntry.DUE + " TEXT NOT NULL, "
	        + TaskContract.TaskEntry.NOTE + " TEXT, "
	        + TaskContract.TaskEntry.PRIORITY + " TEXT NOT NULL, "
	        + TaskContract.TaskEntry.STATUS + " TEXT DEFAULT '"
	        + TaskModel.STATUS_NOT_COMPLETED + "', "
	        + TaskContract.TaskEntry.TITLE + " TEXT NOT NULL, "
	        + TaskContract.TaskEntry.UPDATED + " TEXT NOT NULL"
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
		db.execSQL( "DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLENAME );
		db.execSQL( SQL_CREATE_TABLE_TASKS );
	}

}