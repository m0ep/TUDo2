package de.m0ep.tudo2;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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

	public static final String EXTRA_MODE = "mode";
	public static final String EXTRA_TASK_ID = "task_id";

	public static final int MODE_ADD_TASK = 0;
	public static final int MODE_EDIT_TASK = 1;

	private static final String EXTRA_ADDED_TASK_URI = "extra_added_task_uri";

	private EditText editTitle;
	private Spinner spinnerPriority;
	private Button buttonDueDate;
	private EditText editNote;

	private Button buttonPositive;
	private Button buttonNegative;

	private DateFormat dateFormat;

	private final int activityMode = MODE_ADD_TASK;
	private final int taskId = -1;

	private TaskSQLiteHelper dbHelper;
	private Calendar currentDate;
	private String[] prioritiesArrays;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_add_task );
		// Show the Up button in the action bar.
		setupActionBar();

		prioritiesArrays = getResources().getStringArray( R.array.array_priorities );
		dateFormat = android.text.format.DateFormat.getDateFormat( this );
		dbHelper = new TaskSQLiteHelper( this );

		currentDate = Calendar.getInstance();
		currentDate.setTime( new Date() );

		editTitle = (EditText) findViewById( R.id.edit_title );
		spinnerPriority = (Spinner) findViewById( R.id.spinner_priority );
		buttonDueDate = (Button) findViewById( R.id.edit_due_date );
		editNote = (EditText) findViewById( R.id.edit_note );
		buttonPositive = (Button) findViewById( R.id.button_positive );
		buttonNegative = (Button) findViewById( R.id.button_negative );

		initView();
	}

	protected void initView() {

		buttonDueDate.setText( dateFormat.format( currentDate.getTime() ) );
		buttonDueDate.setTag( currentDate );
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
				doOk();
			}
		} );

		buttonNegative.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				doCancel();
			}
		} );
	}

	protected DatePickerDialog createDatePickerDialog() {
		DatePickerDialog dpd = new DatePickerDialog(
		        TaskActivity.this,
		        new OnDateSetListener() {
			        @Override
			        public void onDateSet( DatePicker view, int year, int monthOfYear,
			                int dayOfMonth ) {
				        setDueDate( year, monthOfYear, dayOfMonth );
			        }

		        },
		        currentDate.get( Calendar.YEAR ),
		        currentDate.get( Calendar.MONTH ),
		        currentDate.get( Calendar.DAY_OF_MONTH ) );
		return dpd;
	}

	private void setDueDate( int year, int monthOfYear, int dayOfMonth ) {
		final Calendar dueDate = Calendar.getInstance();
		dueDate.set( year, monthOfYear, dayOfMonth );

		final Calendar currentDate = Calendar.getInstance();
		currentDate.setTime( new Date() );

		if ( !dueDate.before( currentDate ) ) {
			buttonDueDate.setText( dateFormat.format( dueDate.getTime() ) );
			buttonDueDate.setTag( dueDate );
		} else {
			Toast.makeText( this, R.string.error_task_due_date_in_past, Toast.LENGTH_SHORT ).show();
		}
	}

	protected void doOk() {
		TaskEntry entry = new TaskEntry();
		String title = editTitle.getText().toString();

		if ( TextUtils.isEmpty( title ) ) {
			Toast.makeText( this, R.string.error_task_title_is_empty, Toast.LENGTH_SHORT ).show();
			return;
		}

		entry.setTitle( title );

		entry.setPriority( prioritiesArrays[(int) spinnerPriority.getSelectedItemId()] );

		entry.setDue( (Calendar) buttonDueDate.getTag() );

		String note = editNote.getText().toString();
		if ( !TextUtils.isEmpty( note ) ) {
			entry.setNote( note );
		}

		entry.setUpdated( Calendar.getInstance() );

		long insertedId = TaskService.insert( dbHelper.getWritableDatabase(), entry );

		Intent resultIntent = new Intent();
		resultIntent.putExtra(
		        EXTRA_ADDED_TASK_URI,
		        ContentUris.withAppendedId(
		                TaskContract.TaskEntry.CONTENT_URI,
		                insertedId ) );
		setResult( RESULT_OK, resultIntent );
		finish();
	}

	protected void doCancel() {
		finish();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled( true );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate( R.menu.task, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		switch ( item.getItemId() ) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask( this );
				return true;
		}
		return super.onOptionsItemSelected( item );
	}

}
