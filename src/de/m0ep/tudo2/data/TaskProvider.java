package de.m0ep.tudo2.data;

import java.util.Date;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import de.m0ep.tudo2.Iso8601DateUtils;
import de.m0ep.tudo2.data.TaskContract.Task;

public class TaskProvider extends ContentProvider {
	private static final String TAG = TaskProvider.class.getName();

	private static final UriMatcher URI_MATCHER = new UriMatcher( UriMatcher.NO_MATCH );

	private static final int TASKS = 1;
	private static final int TASKS_ID = 2;

	static {
		URI_MATCHER.addURI( TaskContract.AUTHORITY, Task.TABLENAME, TASKS );
		URI_MATCHER.addURI( TaskContract.AUTHORITY, Task.TABLENAME + "/#",
		        TASKS_ID );
	}

	private TaskSQLiteHelper sqliteHelper;

	@Override
	public boolean onCreate() {
		sqliteHelper = new TaskSQLiteHelper( getContext() );
		return true;
	}

	@Override
	public String getType( Uri uri ) {
		switch ( URI_MATCHER.match( uri ) ) {
			case TASKS:
				return Task.CONTENT_DIR_TYPE;
			case TASKS_ID:
				return Task.CONTENT_ITEM_TYPE;
			default:
				return null;
		}
	}

	@Override
	public Uri insert( Uri uri, ContentValues values ) {
		if ( TASKS == URI_MATCHER.match( uri ) ) {
			SQLiteDatabase db = sqliteHelper.getWritableDatabase();
			values.put( Task.UPDATED, Iso8601DateUtils.formatDateTimeIso8601( new Date() ) );
			long insertedId = db.insert( Task.TABLENAME, null, values );
			getContext().getContentResolver().notifyChange( Task.CONTENT_URI, null );
			return ContentUris.withAppendedId( uri, insertedId );
		}

		throw new IllegalArgumentException( "Unknown URI: " + uri );
	}

	@Override
	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs,
	        String sortOrder ) {
		if ( TextUtils.isEmpty( sortOrder ) ) {
			sortOrder = Task._ID + " ASC";
		}

		int matchResult = URI_MATCHER.match( uri );
		if ( TASKS_ID == matchResult ) {
			String id = uri.getLastPathSegment();

			if ( TextUtils.isEmpty( selection ) ) {
				selection = Task._ID + "=" + id;
			} else {
				selection = Task._ID + "=" + id + " AND " + selection;
			}
		} else if ( TASKS != matchResult ) {
			throw new IllegalArgumentException( "Unknown URI: " + uri );
		}

		SQLiteDatabase db = sqliteHelper.getReadableDatabase();
		Cursor resultCursor = db.query( Task.TABLENAME,
		        projection,
		        selection,
		        selectionArgs,
		        "",
		        "",
		        sortOrder );
		resultCursor.setNotificationUri( getContext().getContentResolver(), Task.CONTENT_URI );
		return resultCursor;
	}

	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {
		if ( values.containsKey( Task._ID ) ) {
			values.remove( Task._ID );
			Log.d( TAG, "Remove _id from ContentValues" );
		}

		int matchResult = URI_MATCHER.match( uri );
		if ( TASKS_ID == matchResult ) {
			String id = uri.getLastPathSegment();

			if ( TextUtils.isEmpty( selection ) ) {
				selection = Task._ID + "=" + id;
			} else {
				selection = Task._ID + "=" + id + " AND " + selection;
			}
		} else if ( TASKS != matchResult ) {
			throw new IllegalArgumentException( "Unknown URI: " + uri );
		}

		// modify updated datetime
		values.put( Task.UPDATED, Iso8601DateUtils.formatDateTimeIso8601( new Date() ) );

		SQLiteDatabase db = sqliteHelper.getWritableDatabase();
		int rowsUpdated = db.update( Task.TABLENAME, values, selection, selectionArgs );
		getContext().getContentResolver().notifyChange( Task.CONTENT_URI, null );
		return rowsUpdated;
	}

	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs ) {
		int matchResult = URI_MATCHER.match( uri );
		if ( TASKS_ID == matchResult ) {
			String id = uri.getLastPathSegment();

			if ( TextUtils.isEmpty( selection ) ) {
				selection = Task._ID + "=" + id;
			} else {
				selection = Task._ID + "=" + id + " AND " + selection;
			}
		} else if ( TASKS != matchResult ) {
			throw new IllegalArgumentException( "Unknown URI: " + uri );
		}

		// don't delete it really, just set a deleted-flag.
		ContentValues values = new ContentValues();
		values.put( Task.IS_DELETED, 1 );
		values.put( Task.UPDATED, Iso8601DateUtils.formatDateTimeIso8601( new Date() ) );

		SQLiteDatabase db = sqliteHelper.getWritableDatabase();
		int rowsUpdated = db.update( Task.TABLENAME, values, selection, selectionArgs );
		getContext().getContentResolver().notifyChange( Task.CONTENT_URI, null );
		return rowsUpdated;
	}
}
