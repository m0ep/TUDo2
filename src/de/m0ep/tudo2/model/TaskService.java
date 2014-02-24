package de.m0ep.tudo2.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.m0ep.tudo2.provider.TaskContract;

public class TaskService {
	private static final String TAG = TaskService.class.getName();
	private static final String FORMAT_STRING_DATETIME = "yyyy-MM-dd'T'HH:mm:ssz";
	private static final SimpleDateFormat FORMAT_DATETIME =
	        new SimpleDateFormat( FORMAT_STRING_DATETIME, Locale.getDefault() );
	private static final String FORMAT_STRING_DATE = "yyyy-MM-dd";
	private static final SimpleDateFormat FORMAT_DATE =
	        new SimpleDateFormat( FORMAT_STRING_DATE, Locale.getDefault() );

	/**
	 * Creates a new {@link TaskModel} from the values of a
	 * {@link ContentValues} object.
	 * 
	 * @param values
	 * @return {@link TaskModel} object.
	 */
	static public TaskModel createFrom( ContentValues values ) {
		TaskModel result = new TaskModel();

		if ( values.containsKey( TaskContract.TaskEntry.COMPLETED ) ) {
			Calendar completed = readDateTimeCalendar( TaskContract.TaskEntry.COMPLETED, values );
			result.setCompleted( completed );
		}

		if ( values.containsKey( TaskContract.TaskEntry.DELETED ) ) {
			result.setDeleted( values.getAsBoolean( TaskContract.TaskEntry.DELETED ) );
		}

		if ( values.containsKey( TaskContract.TaskEntry.DUE ) ) {
			Calendar due = readDateCalendar( TaskContract.TaskEntry.DUE, values );
			result.setDue( due );
		}

		if ( values.containsKey( TaskContract.TaskEntry._ID ) ) {
			result.setId( values.getAsLong( TaskContract.TaskEntry._ID ) );
		}

		if ( values.containsKey( TaskContract.TaskEntry.NOTE ) ) {
			result.setNote( values.getAsString( TaskContract.TaskEntry.NOTE ) );
		}

		if ( values.containsKey( TaskContract.TaskEntry.PRIORITY ) ) {
			result.setPriority( values.getAsString( TaskContract.TaskEntry.STATUS ) );
		}

		if ( values.containsKey( TaskContract.TaskEntry.STATUS ) ) {
			result.setStatus( values.getAsString( TaskContract.TaskEntry.STATUS ) );
		}

		if ( values.containsKey( TaskContract.TaskEntry.TITLE ) ) {
			result.setTitle( values.getAsString( TaskContract.TaskEntry.TITLE ) );
		}

		if ( values.containsKey( TaskContract.TaskEntry.UPDATED ) ) {
			Calendar updated = readDateTimeCalendar( TaskContract.TaskEntry.UPDATED, values );
			result.setUpdated( updated );
		}

		return result;
	}

	static public ContentValues convertToContentValues( TaskModel entry ) {
		ContentValues result = new ContentValues();

		result.put( TaskContract.TaskEntry._ID,
		        entry.getId() );

		if ( null != entry.getCompleted() ) {
			result.put( TaskContract.TaskEntry.COMPLETED,
			        formatDateTimeISO8601( entry.getCompleted().getTime() ) );
		}

		result.put( TaskContract.TaskEntry.DELETED,
		        entry.isDeleted() );

		if ( null != entry.getDue() ) {
			result.put( TaskContract.TaskEntry.DUE,
			        formatDateISO8601( entry.getDue().getTime() ) );
		}

		result.put( TaskContract.TaskEntry.NOTE,
		        entry.getNote() );

		result.put( TaskContract.TaskEntry.PRIORITY,
		        entry.getPriority() );

		result.put( TaskContract.TaskEntry.STATUS,
		        entry.getStatus() );

		result.put( TaskContract.TaskEntry.TITLE,
		        entry.getTitle() );

		if ( null != entry.getUpdated() ) {
			result.put( TaskContract.TaskEntry.UPDATED,
			        formatDateTimeISO8601( entry.getUpdated().getTime() ) );
		}

		return result;
	}

	static public long insert( SQLiteDatabase db, TaskModel entry ) {
		ContentValues values = convertToContentValues( entry );
		values.remove( TaskContract.TaskEntry._ID );

		return db.insert(
		        TaskContract.TaskEntry.TABLENAME,
		        null,
		        values );
	}

	public static int update(
	        SQLiteDatabase db,
	        TaskModel entry ) {
		return db.update(
		        TaskContract.TaskEntry.TABLENAME,
		        convertToContentValues( entry ),
		        "_id = ?",
		        new String[] { Long.toString( entry.getId() ) } );
	}

	protected static Calendar readDateTimeCalendar( String key, ContentValues values ) {
		Object obj = values.get( key );
		Calendar result = null;

		if ( obj instanceof Long ) {
			result = Calendar.getInstance();
			result.setTimeInMillis( (Long) obj );
		} else if ( obj instanceof String ) {
			result = Calendar.getInstance();
			Date date = parseDateTimeISO8601( obj.toString() );
			result.setTime( date );
		}

		return result;
	}

	protected static Calendar readDateCalendar( String key, ContentValues values ) {
		Object obj = values.get( key );
		Calendar result = null;

		if ( obj instanceof Long ) {
			result = Calendar.getInstance();
			result.setTimeInMillis( (Long) obj );
		} else if ( obj instanceof String ) {
			result = Calendar.getInstance();
			Date date = parseDateISO8601( obj.toString() );
			result.setTime( date );
		}

		return result;
	}

	public static Date parseDateTimeISO8601( String string ) {
		try {
			return FORMAT_DATETIME.parse( string );
		} catch ( ParseException e ) {
			Log.w( TAG, e );
			return null;
		}
	}

	public static String formatDateTimeISO8601( Date date ) {
		return FORMAT_DATETIME.format( date );
	}

	public static Date parseDateISO8601( String string ) {
		try {
			return FORMAT_DATE.parse( string );
		} catch ( ParseException e ) {
			Log.w( TAG, e );
			return null;
		}
	}

	public static String formatDateISO8601( Date date ) {
		return FORMAT_DATE.format( date );
	}
}
