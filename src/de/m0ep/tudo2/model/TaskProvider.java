package de.m0ep.tudo2.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class TaskProvider extends ContentProvider {
	public static final String AUTHORITY = "de.m0ep.tudo2";

	private static final String TABLE_TASKS = "tasks";
	private static final String DBNAME = "tudo2db";

	private static final UriMatcher URI_MATCHER = new UriMatcher( UriMatcher.NO_MATCH );

	private static final int TASKS = 1;
	private static final int TASKS_ID = 2;

	@SuppressLint( "SimpleDateFormat" )
	private final SimpleDateFormat dateFormat = new SimpleDateFormat( "YYYY-MM-dd" );

	static {
		URI_MATCHER.addURI( AUTHORITY, TABLE_TASKS, TASKS );
		URI_MATCHER.addURI( AUTHORITY, TABLE_TASKS + "/#", TASKS_ID );
	}

	private TaskSqliteHelper sqliteHelper;

	@Override
	public boolean onCreate() {
		sqliteHelper = new TaskSqliteHelper( getContext() );
		return true;
	}

	@Override
	public String getType( Uri uri ) {
		switch ( URI_MATCHER.match( uri ) ) {
			case TASKS:
				return "android.cursor.dir/vnd." + AUTHORITY + "." + TABLE_TASKS;
			case TASKS_ID:
				return "android.cursor.item/vnd." + AUTHORITY + "." + TABLE_TASKS;
			default:
				return null;
		}
	}

	@Override
	public Uri insert( Uri uri, ContentValues values ) {
		if ( TASKS == URI_MATCHER.match( uri ) ) {
			SQLiteDatabase db = sqliteHelper.getWritableDatabase();
			checkContentValues( values );
			long insertedId = db.insert( TABLE_TASKS, null, values );
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
					sortOrder = TaskContract._ID + " ASC";
				}
				break;
			case TASKS_ID:
				selection += " " + TaskContract._ID + " = " + uri.getLastPathSegment();
				break;
			default:
				throw new IllegalArgumentException( "Unknown uri: " + uri );
		}

		SQLiteDatabase db = sqliteHelper.getReadableDatabase();
		return db.query( TABLE_TASKS, projection, selection, selectionArgs, "", "", sortOrder );
	}

	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs ) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void checkContentValues( ContentValues values ) {
		if ( values.containsKey( TaskContract._ID ) ) {
			values.remove( TaskContract._ID );
		}

		if ( !values.containsKey( TaskContract.STATE ) ) {
			values.put( TaskContract.STATE, 0 );
		}

		if ( !values.containsKey( TaskContract.PRIORITY ) ) {
			values.put( TaskContract.PRIORITY, 0 );
		}

		if ( !values.containsKey( TaskContract.DATE ) ) {
			values.put( TaskContract.DATE, dateFormat.format( new Date() ) );
		}

		if ( values.containsKey( TaskContract.DURATION ) ) {
			int duration = values.getAsInteger( TaskContract.DURATION );
			values.put( TaskContract.DURATION, Math.max( 0, duration ) );
		} else {
			throw new IllegalArgumentException( "Missing duration" );
		}

		if ( !values.containsKey( TaskContract.DESCRIPTION ) ) {
			throw new IllegalArgumentException( "Missing description" );
		}
	}

	public static final class TaskContract implements BaseColumns {
		public static final String STATE = "state";
		public static final String PRIORITY = "priority";
		public static final String DATE = "date";
		public static final String DURATION = "duration";
		public static final String DESCRIPTION = "description";
	}

	private static final class TaskSqliteHelper extends SQLiteOpenHelper {
		private static final String SQL_CREATE_TABLE_TASKS = "CREATE TABLE " +
		        TABLE_TASKS + " ( "
		        + TaskContract._ID + " INTEGER PRIMARY KEY, "
		        + TaskContract.STATE + " INTEGER, "
		        + TaskContract.PRIORITY + " INTEGER, "
		        + TaskContract.DATE + " TEXT, "
		        + TaskContract.DURATION + " INTEGER, "
		        + TaskContract.DESCRIPTION + " TEXT );";

		public TaskSqliteHelper( Context context ) {
			super( context, DBNAME, null, 1 );
		}

		@Override
		public void onCreate( SQLiteDatabase db ) {
			db.execSQL( SQL_CREATE_TABLE_TASKS );
		}

		@Override
		public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
			db.execSQL( "DROP TABLE " + TABLE_TASKS );
			db.execSQL( SQL_CREATE_TABLE_TASKS );
		}

	}
}
