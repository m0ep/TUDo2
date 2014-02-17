package de.m0ep.tudo2;

import java.text.DateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import de.m0ep.tudo2.model.TaskEntry;
import de.m0ep.tudo2.model.TaskService;
import de.m0ep.tudo2.provider.TaskContract;
import de.m0ep.tudo2.provider.TaskProvider.TaskSQLiteHelper;

public class TaskActivity extends Activity {
	public static final String TAG = TaskActivity.class.getName();

	private static final String STATE_KEY_TASK_ID_TO_EDIT = "task_to_edit_id";
	private static final String STATE_KEY_TASKACTIVITY_MODE = "taskactivity_mode";
	private static final String STATE_KEY_DUE_DATE = "due_date";
	private static final String STATE_KEY_PRIORITY = "priority";
	private static final String STATE_KEY_NOTE = "note";
	private static final String STATE_KEY_TITLE = "title";

	public static final String EXTRA_TASK_ACTIVITY_MODE = "extra_taskactivity_mode";
	public static final String EXTRA_TASK_ID = "extra_task_id";
	public static final String EXTRA_TASK_URI = "extra_task_uri";

	public static final int MODE_ADD_TASK = 0;
	public static final int MODE_EDIT_TASK = 1;

	private EditText editTitle;
	private Spinner spinnerPriority;
	private Button buttonDueDate;
	private EditText editNote;

	private Button buttonPositive;
	private Button buttonNegative;

	private DateFormat dateFormat;

	private int activityMode = MODE_ADD_TASK;
	private long taskToEditId = -1;

	private TaskSQLiteHelper dbHelper;
	private String[] prioritiesArray;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_add_task );
		getActionBar().setDisplayHomeAsUpEnabled( true );

		prioritiesArray = getResources().getStringArray( R.array.array_priorities );
		dateFormat = android.text.format.DateFormat.getDateFormat( this );
		dbHelper = new TaskSQLiteHelper( this );

		editTitle = (EditText) findViewById( R.id.edit_title );
		spinnerPriority = (Spinner) findViewById( R.id.spinner_priority );
		editNote = (EditText) findViewById( R.id.edit_note );
		buttonDueDate = (Button) findViewById( R.id.edit_due_date );
		buttonPositive = (Button) findViewById( R.id.button_positive );
		buttonNegative = (Button) findViewById( R.id.button_negative );

		buttonDueDate.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				DatePickerDialog datePickerDialog = createDatePickerDialog();
				datePickerDialog.show();
			}
		} );

		buttonPositive.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				storeData();
				finish();
			}
		} );

		buttonNegative.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				setInstanceStateToDefault();
				finish();
			}
		} );

		boolean dataLoadedToViews = false;
		if ( null != savedInstanceState ) {
			restoreInstanceState( savedInstanceState );
			dataLoadedToViews = true;
		} else if ( null != getIntent() ) {
			Intent startIntent = getIntent();
			Bundle extras = startIntent.getExtras();

			if ( null != extras
			        && extras.containsKey( EXTRA_TASK_ACTIVITY_MODE )
			        && extras.containsKey( EXTRA_TASK_ID ) ) {
				activityMode = extras.getInt( EXTRA_TASK_ACTIVITY_MODE );
				taskToEditId = extras.getLong( EXTRA_TASK_ID );

				dataLoadedToViews = loadTaskToEdit( taskToEditId );
			}
		}

		if ( !dataLoadedToViews ) {
			setInstanceStateToDefault();
		}
	}

	@Override
	protected void onSaveInstanceState( Bundle outState ) {
		if ( null != outState ) {
			outState.putString( STATE_KEY_TITLE, editTitle.getText().toString() );
			outState.putString( STATE_KEY_NOTE, editNote.getText().toString() );
			outState.putInt( STATE_KEY_PRIORITY, spinnerPriority.getSelectedItemPosition() );
			outState.putSerializable( STATE_KEY_DUE_DATE, (Calendar) buttonDueDate.getTag() );
			outState.putInt( STATE_KEY_TASKACTIVITY_MODE, activityMode );
			outState.putLong( STATE_KEY_TASK_ID_TO_EDIT, taskToEditId );
		}

		super.onSaveInstanceState( outState );
	}

	@Override
	protected void onRestoreInstanceState( Bundle savedInstanceState ) {
		restoreInstanceState( savedInstanceState );
		super.onRestoreInstanceState( savedInstanceState );
	}

	private boolean loadTaskToEdit( long id ) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query( TaskContract.TaskEntry.TABLENAME,
		        new String[] {
		                TaskContract.TaskEntry._ID,
		                TaskContract.TaskEntry.TITLE,
		                TaskContract.TaskEntry.NOTE,
		                TaskContract.TaskEntry.PRIORITY,
		                TaskContract.TaskEntry.DUE
		        },
		        TaskContract.TaskEntry._ID + " = ?",
		        new String[] { Long.toString( id ) },
		        null, null, null );

		if ( cursor.moveToFirst() ) {
			int titleIndex = cursor.getColumnIndex( TaskContract.TaskEntry.TITLE );
			int noteIndex = cursor.getColumnIndex( TaskContract.TaskEntry.NOTE );
			int priorityIndex = cursor.getColumnIndex( TaskContract.TaskEntry.PRIORITY );
			int dueIndex = cursor.getColumnIndex( TaskContract.TaskEntry.DUE );

			String title = cursor.getString( titleIndex );
			String note = cursor.getString( noteIndex );
			String priority = cursor.getString( priorityIndex );
			String due = cursor.getString( dueIndex );

			editTitle.setText( title );
			editNote.setText( note );

			for ( int i = 0; i < prioritiesArray.length; i++ ) {
				if ( prioritiesArray[i].equals( priority ) ) {
					spinnerPriority.setSelection( i );
					break;
				}
			}

			Calendar dueCalendar = Calendar.getInstance();
			dueCalendar.setTime( TaskService.parseDateISO8601( due ) );
			buttonDueDate.setText( dateFormat.format( dueCalendar.getTime() ) );
			buttonDueDate.setTag( dueCalendar );

			return true;
		}

		return false;
	}

	private void setInstanceStateToDefault() {
		editTitle.setText( "" );
		editNote.setText( "" );
		spinnerPriority.setSelection( 0 );

		Calendar currentDate = Calendar.getInstance();
		buttonDueDate.setText( dateFormat.format( currentDate.getTime() ) );
		buttonDueDate.setTag( currentDate );

		activityMode = MODE_ADD_TASK;
		taskToEditId = -1;
	}

	private void restoreInstanceState( Bundle savedInstanceState ) {
		if ( null != savedInstanceState ) {
			editTitle.setText( savedInstanceState.getString( STATE_KEY_TITLE, "" ) );
			editNote.setText( savedInstanceState.getString( STATE_KEY_NOTE, "" ) );
			spinnerPriority.setSelection( savedInstanceState.getInt( STATE_KEY_PRIORITY, 0 ) );

			Calendar dueDate = (Calendar) savedInstanceState.getSerializable( STATE_KEY_DUE_DATE );
			if ( null == dueDate ) {
				dueDate = Calendar.getInstance();
			}

			buttonDueDate.setText( dateFormat.format( dueDate.getTime() ) );
			buttonDueDate.setTag( dueDate );

			activityMode = savedInstanceState.getInt( STATE_KEY_TASKACTIVITY_MODE, MODE_ADD_TASK );
			taskToEditId = savedInstanceState.getLong( STATE_KEY_TASK_ID_TO_EDIT, -1 );
		}
	}

	private DatePickerDialog createDatePickerDialog() {
		Calendar currentDate = Calendar.getInstance();
		DatePickerDialog dpd = new DatePickerDialog(
		        TaskActivity.this,
		        new OnDateSetListener() {
			        @Override
			        public void onDateSet( DatePicker view, int year, int monthOfYear,
			                int dayOfMonth ) {
				        final Calendar dueDate = Calendar.getInstance();
				        dueDate.set( year, monthOfYear, dayOfMonth );

				        buttonDueDate.setText( dateFormat.format( dueDate.getTime() ) );
				        buttonDueDate.setTag( dueDate );
			        }

		        },
		        currentDate.get( Calendar.YEAR ),
		        currentDate.get( Calendar.MONTH ),
		        currentDate.get( Calendar.DAY_OF_MONTH ) );
		return dpd;
	}

	private void storeData() {
		TaskEntry entry = new TaskEntry();
		String title = editTitle.getText().toString();

		if ( TextUtils.isEmpty( title ) ) {
			Toast.makeText( this, R.string.error_task_title_is_empty, Toast.LENGTH_SHORT ).show();
			return;
		}

		entry.setTitle( title );
		entry.setPriority( prioritiesArray[(int) spinnerPriority.getSelectedItemId()] );
		entry.setDue( (Calendar) buttonDueDate.getTag() );

		String note = editNote.getText().toString();
		if ( !TextUtils.isEmpty( note ) ) {
			entry.setNote( note );
		}

		entry.setUpdated( Calendar.getInstance() );

		long returnId = -1;

		if ( MODE_EDIT_TASK == activityMode ) {
			entry.setId( taskToEditId );
			returnId = TaskService.update( dbHelper.getWritableDatabase(), entry );
		} else {
			returnId = TaskService.insert( dbHelper.getWritableDatabase(), entry );
		}

		Intent resultIntent = new Intent();
		resultIntent.putExtra( EXTRA_TASK_URI,
		        ContentUris.withAppendedId(
		                TaskContract.TaskEntry.CONTENT_URI,
		                returnId ) );
		setResult( RESULT_OK, resultIntent );
	}
}
