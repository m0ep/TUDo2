package de.m0ep.tudo2.provider;

import java.util.Date;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import de.m0ep.tudo2.model.TaskEntry;

public class TaskProvider extends ContentProvider {
	private static final String TAG = TaskProvider.class.getName();

	private static final String DBNAME = "tudo2.db";

	private static final UriMatcher URI_MATCHER = new UriMatcher( UriMatcher.NO_MATCH );

	private static final int TASKS = 1;
	private static final int TASKS_ID = 2;

	static {
		URI_MATCHER.addURI( TaskContract.AUTHORITY, TaskContract.TaskEntry.TABLENAME, TASKS );
		URI_MATCHER.addURI( TaskContract.AUTHORITY, TaskContract.TaskEntry.TABLENAME + "/#",
		        TASKS_ID );
	}

	private TaskSQLiteHelper sqliteHelper;

	public static CharSequence formatDate( Date date ) {
		return DateFormat.format( "yyyy-MM-dd", date );
	}

	@Override
	public boolean onCreate() {
		sqliteHelper = new TaskSQLiteHelper( getContext() );
		return true;
	}

	@Override
	public String getType( Uri uri ) {
		switch ( URI_MATCHER.match( uri ) ) {
			case TASKS:
				return "android.cursor.dir/vnd." + TaskContract.AUTHORITY + "."
				        + TaskContract.TaskEntry.TABLENAME;
			case TASKS_ID:
				return "android.cursor.item/vnd." + TaskContract.AUTHORITY + "."
				        + TaskContract.TaskEntry.TABLENAME;
			default:
				return null;
		}
	}

	@Override
	public Uri insert( Uri uri, ContentValues values ) {
		if ( TASKS == URI_MATCHER.match( uri ) ) {
			SQLiteDatabase db = sqliteHelper.getWritableDatabase();

			long insertedId = db.insert( TaskContract.TaskEntry.TABLENAME, null, values );
			return ContentUris.withAppendedId( uri, insertedId );
		}

		throw new IllegalArgumentException( "Unknown uri: " + uri );
	}

	@Override
	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs,
	        String sortOrder ) {
		switch ( URI_MATCHER.match( uri ) ) {
			case TASKS:
				if ( TextUtils.isEmpty( sortOrder ) ) {
					sortOrder = TaskContract.TaskEntry._ID + " ASC";
				}
				break;
			case TASKS_ID:
				selection += " " + TaskContract.TaskEntry._ID + " = " + uri.getLastPathSegment();
				break;
			default:
				throw new IllegalArgumentException( "Unknown uri: " + uri );
		}

		SQLiteDatabase db = sqliteHelper.getReadableDatabase();
		return db.query( TaskContract.TaskEntry.TABLENAME, projection, selection, selectionArgs,
		        "", "", sortOrder );
	}

	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {
		if ( TASKS_ID == URI_MATCHER.match( uri ) ) {
			if ( TextUtils.isEmpty( selection ) ) {
				return 0;
			}

			if ( values.containsKey( TaskContract.TaskEntry._ID ) ) {
				values.remove( TaskContract.TaskEntry._ID );
				Log.d( TAG, "Remove _id from ContentValues" );
			}

			SQLiteDatabase db = sqliteHelper.getWritableDatabase();
			return db.update( TaskContract.TaskEntry.TABLENAME, values, selection, selectionArgs );
		}

		throw new IllegalArgumentException( "Unknown uri: " + uri );
	}

	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs ) {
		if ( TASKS_ID == URI_MATCHER.match( uri ) ) {
			if ( TextUtils.isEmpty( selection ) ) {
				return 0;
			}

			SQLiteDatabase db = sqliteHelper.getWritableDatabase();
			db.delete( TaskContract.TaskEntry.TABLENAME, selection, selectionArgs );
		}

		throw new IllegalArgumentException( "Unknown uri: " + uri );
	}

	public static final class TaskSQLiteHelper extends SQLiteOpenHelper {
		private static final String SQL_CREATE_TABLE_TASKS = "CREATE TABLE IF NOT EXISTS " +
		        TaskContract.TaskEntry.TABLENAME + " ( "
		        + TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
		        + TaskContract.TaskEntry.COMPLETED + " TEXT, "
		        + TaskContract.TaskEntry.DELETED + " INTEGER DEFAULT 0, "
		        + TaskContract.TaskEntry.DUE + " TEXT NOT NULL, "
		        + TaskContract.TaskEntry.NOTE + " TEXT, "
		        + TaskContract.TaskEntry.PRIORITY + " TEXT NOT NULL, "
		        + TaskContract.TaskEntry.STATUS + " TEXT DEFAULT '"
		        + TaskEntry.STATUS_NOT_COMPLETED + "', "
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
}
