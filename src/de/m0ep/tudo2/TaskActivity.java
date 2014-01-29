package de.m0ep.tudo2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import de.m0ep.tudo2.provider.TaskProvider;

public class TaskActivity extends Activity {

	EditText editDate;
	EditText editDuration;
	EditText editDescription;
	Spinner spinnerPriority;
	Button buttonPositive;
	Button buttonNegative;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_add_task );
		// Show the Up button in the action bar.
		setupActionBar();

		DateFormat dateFormat = new DateFormat();

		spinnerPriority = (Spinner) findViewById( R.id.spinner_priorities );
		editDate = (EditText) findViewById( R.id.edit_date );
		editDuration = (EditText) findViewById( R.id.edit_duration );
		editDescription = (EditText) findViewById( R.id.edit_description );
		buttonPositive = (Button) findViewById( R.id.button_positive );
		buttonNegative = (Button) findViewById( R.id.button_negative );

		editDate.setText( TaskProvider.formatDate( new Date() ) );
		editDate.setOnClickListener( new OnClickListener() {
			@SuppressLint( "SimpleDateFormat" )
			@Override
			public void onClick( View v ) {

				Calendar cal = Calendar.getInstance();
				try {
					SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );
					cal.setTime( formatter.parse( editDate.getText().toString() ) );
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
						        editDate.setText( TaskProvider.formatDate( cal.getTime() ) );
					        }
				        },
				        cal.get( Calendar.YEAR ),
				        cal.get( Calendar.MONTH ),
				        cal.get( Calendar.DAY_OF_MONTH ) );
				datePickerDialog.setTitle( "Select a Date" );
				datePickerDialog.show();
			}
		} );

	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled( true );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.task, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		switch ( item.getItemId() ) {
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask( this );
				return true;
		}
		return super.onOptionsItemSelected( item );
	}

}
