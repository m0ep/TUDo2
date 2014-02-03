package de.m0ep.tudo2;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import de.m0ep.tudo2.provider.TaskContract;

public class TaskActivity extends Activity {

	public static final String EXTRA_MODE = "mode";
	public static final String EXTRA_TASK_ID = "task_id";

	public static final int MODE_ADD_TASK = 0;
	public static final int MODE_EDIT_TASK = 1;

	private Button buttonDate;
	private EditText editDuration;
	private EditText editDescription;
	private Spinner spinnerPriority;
	private Button buttonPositive;
	private Button buttonNegative;

	private DateFormat dateFormat;

	private int activityMode = MODE_ADD_TASK;
	private int taskId = -1;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_add_task );
		// Show the Up button in the action bar.
		setupActionBar();

		dateFormat = android.text.format.DateFormat.getDateFormat( this );

		spinnerPriority = (Spinner) findViewById( R.id.spinner_priorities );
		buttonDate = (Button) findViewById( R.id.button_date );
		editDuration = (EditText) findViewById( R.id.edit_duration );
		editDescription = (EditText) findViewById( R.id.edit_description );
		buttonPositive = (Button) findViewById( R.id.button_positive );
		buttonPositive.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				doOk();
			}
		} );

		buttonNegative = (Button) findViewById( R.id.button_negative );
		buttonNegative.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				doCancel();
			}
		} );

		Date date = new Date();
		buttonDate.setText( dateFormat.format( date ) );
		buttonDate.setTag( date );
		buttonDate.setOnClickListener( new OnClickListener() {
			@SuppressLint( "SimpleDateFormat" )
			@Override
			public void onClick( View v ) {

				Calendar cal = Calendar.getInstance();
				try {
					cal.setTime( dateFormat.parse( buttonDate.getText().toString() ) );
				} catch ( ParseException e ) {
					cal.setTime( new Date() );
				}

				DatePickerDialog datePickerDialog = new DatePickerDialog(
				        TaskActivity.this, new OnDateSetListener() {
					        @Override
					        public void onDateSet( DatePicker view, int year, int monthOfYear,
					                int dayOfMonth ) {
						        Calendar cal = Calendar.getInstance();
						        cal.set( year, monthOfYear, dayOfMonth );
						        buttonDate.setText( dateFormat.format( cal.getTime() ) );
						        buttonDate.setTag( cal.getTime() );
					        }
				        },
				        cal.get( Calendar.YEAR ),
				        cal.get( Calendar.MONTH ),
				        cal.get( Calendar.DAY_OF_MONTH ) );
				datePickerDialog.setTitle( "Select a Date" );
				datePickerDialog.show();
			}
		} );

		Bundle extras = getIntent().getExtras();
		if ( null != extras ) {
			activityMode = extras.getInt( EXTRA_MODE, MODE_ADD_TASK );

			if ( MODE_EDIT_TASK == activityMode ) {
				taskId = extras.getInt( EXTRA_TASK_ID, -1 );

				if ( -1 == taskId ) {
					Toast.makeText(
					        getBaseContext(),
					        R.string.error_task_edit_invalid_taskid,
					        Toast.LENGTH_SHORT ).show();
					finish();
				}
			}
		}
	}

	protected void doOk() {
		int priority = spinnerPriority.getSelectedItemPosition();
		Date date = (Date) buttonDate.getTag();
		int duration = Integer.parseInt( editDuration.getText().toString() ); // TODO: parse 12h, 12m....
		String description = editDescription.getText().toString();

		ContentValues values = new ContentValues();
		values.put( TaskContract.TaskEntry.PRIORITY, priority );
		values.put( TaskContract.TaskEntry.DATE, date.getTime() );
		values.put( TaskContract.TaskEntry.DURATION, duration );
		values.put( TaskContract.TaskEntry.DESCRIPTION, description );

		ContentResolver resolver = getContentResolver();
		resolver.insert( TaskContract.TaskEntry.CONTENT_URI, values );
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
