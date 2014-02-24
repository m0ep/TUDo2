package de.m0ep.tudo2.provider;

import java.util.Date;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

public class TaskProvider extends ContentProvider {
	private static final String TAG = TaskProvider.class.getName();

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
}
