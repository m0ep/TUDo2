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
import de.m0ep.tudo2.provider.TaskContract.TaskEntry;

public class TaskProvider extends ContentProvider {
	private static final String TAG = TaskProvider.class.getName();

	public static final String AUTHORITY = "de.m0ep.tudo2";

	private static final String DBNAME = "tudo2db";

	private static final UriMatcher URI_MATCHER = new UriMatcher( UriMatcher.NO_MATCH );

	private static final int TASKS = 1;
	private static final int TASKS_ID = 2;

	static {
		URI_MATCHER.addURI( AUTHORITY, TaskEntry.TABLE_NAME, TASKS );
		URI_MATCHER.addURI( AUTHORITY, TaskEntry.TABLE_NAME + "/#", TASKS_ID );
	}

	private TaskSqliteHelper sqliteHelper;

	public static CharSequence formatDate( Date date ) {
		return DateFormat.format( "yyyy-MM-dd", date );
	}

	@Override
	public boolean onCreate() {
		sqliteHelper = new TaskSqliteHelper( getContext() );
		return true;
	}

	@Override
	public String getType( Uri uri ) {
		switch ( URI_MATCHER.match( uri ) ) {
			case TASKS:
				return "android.cursor.dir/vnd." + AUTHORITY + "."
				        + TaskContract.TaskEntry.TABLE_NAME;
			case TASKS_ID:
				return "android.cursor.item/vnd." + AUTHORITY + "."
				        + TaskContract.TaskEntry.TABLE_NAME;
			default:
				return null;
		}
	}

	@Override
	public Uri insert( Uri uri, ContentValues values ) {
		if ( TASKS == URI_MATCHER.match( uri ) ) {
			if ( values.containsKey( TaskEntry._ID ) ) {
				values.remove( TaskEntry._ID );
				Log.d( TAG, "Remove _id from ContentValues" );
			}

			if ( values.containsKey( TaskEntry.DURATION ) ) {
				int duration = values.getAsInteger( TaskEntry.DURATION );
				values.put( TaskEntry.DURATION, Math.max( 0, duration ) );
			} else {
				throw new IllegalArgumentException( "Missing duration" );
			}

			if ( !values.containsKey( TaskEntry.DESCRIPTION ) ) {
				throw new IllegalArgumentException( "Missing description" );
			}

			SQLiteDatabase db = sqliteHelper.getWritableDatabase();

			setDefaultContentValues( values );
			long insertedId = db.insert( TaskEntry.TABLE_NAME, null, values );
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
					sortOrder = TaskEntry._ID + " ASC";
				}
				break;
			case TASKS_ID:
				selection += " " + TaskEntry._ID + " = " + uri.getLastPathSegment();
				break;
			default:
				throw new IllegalArgumentException( "Unknown uri: " + uri );
		}

		SQLiteDatabase db = sqliteHelper.getReadableDatabase();
		return db.query( TaskEntry.TABLE_NAME, projection, selection, selectionArgs,
		        "", "", sortOrder );
	}

	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {
		if ( TASKS_ID == URI_MATCHER.match( uri ) ) {
			if ( TextUtils.isEmpty( selection ) ) {
				return 0;
			}

			if ( values.containsKey( TaskEntry._ID ) ) {
				values.remove( TaskEntry._ID );
				Log.d( TAG, "Remove _id from ContentValues" );
			}

			if ( values.containsKey( TaskEntry.DATE ) ) {
				Object obj = values.get( TaskEntry.DATE );

				if ( obj instanceof Date ) {
					values.put( TaskEntry.DATE, (String) formatDate( (Date) obj ) );
				} else if ( obj instanceof Long ) {
					values.put( TaskEntry.DATE, (String) formatDate( new Date( (Long) obj ) ) );
				}
			}

			if ( values.containsKey( TaskEntry.DURATION ) ) {
				int duration = values.getAsInteger( TaskEntry.DURATION );
				values.put( TaskEntry.DURATION, Math.max( 0, duration ) );
			}

			SQLiteDatabase db = sqliteHelper.getWritableDatabase();
			return db.update( TaskEntry.TABLE_NAME, values, selection, selectionArgs );
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
			db.delete( TaskEntry.TABLE_NAME, selection, selectionArgs );
		}

		throw new IllegalArgumentException( "Unknown uri: " + uri );
	}

	private void setDefaultContentValues( ContentValues values ) {
		if ( !values.containsKey( TaskEntry.STATE ) ) {
			values.put( TaskEntry.STATE, 0 );
		}

		if ( !values.containsKey( TaskEntry.PRIORITY ) ) {
			values.put( TaskEntry.PRIORITY, 0 );
		}

		if ( !values.containsKey( TaskEntry.DATE ) ) {
			values.put( TaskEntry.DATE, (String) formatDate( new Date() ) );
		}
	}

	private static final class TaskSqliteHelper extends SQLiteOpenHelper {
		private static final String SQL_CREATE_TABLE_TASKS = "CREATE TABLE " +
		        TaskEntry.TABLE_NAME + " ( "
		        + TaskEntry._ID + " INTEGER PRIMARY KEY, "
		        + TaskEntry.STATE + " INTEGER, "
		        + TaskEntry.PRIORITY + " INTEGER, "
		        + TaskEntry.DATE + " TEXT, "
		        + TaskEntry.DURATION + " INTEGER, "
		        + TaskEntry.DESCRIPTION + " TEXT );";

		public TaskSqliteHelper( Context context ) {
			super( context, DBNAME, null, 1 );
		}

		@Override
		public void onCreate( SQLiteDatabase db ) {
			db.execSQL( SQL_CREATE_TABLE_TASKS );
		}

		@Override
		public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
			db.execSQL( "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME );
			db.execSQL( SQL_CREATE_TABLE_TASKS );
		}

	}
}
