package de.m0ep.tudo2;

import java.util.Calendar;
import java.util.Date;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.m0ep.tudo2.TaskListViewOnTouchListener.TaskTouchActionCallback;
import de.m0ep.tudo2.model.TaskService;
import de.m0ep.tudo2.provider.TaskContract;
import de.m0ep.tudo2.provider.TaskProvider.TaskSQLiteHelper;

public class MainActivity
        extends ListActivity
        implements MultiChoiceModeListener, TaskTouchActionCallback {
	private static final String TAG = MainActivity.class.getName();

	private java.text.DateFormat dateFormat;
	private Calendar mSelectedDate;

	private Button buttonPrevDate;
	private Button buttonNextDate;
	private TextView textCurDate;
	private TaskCursorAdapter cursorAdapter;

	private TaskSQLiteHelper dbHelper;

	boolean isItemPressed;
	boolean isSwiping;

	int numSelectedItems = 0;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		buttonPrevDate = (Button) findViewById( R.id.button_prev_date );
		buttonNextDate = (Button) findViewById( R.id.button_next_date );
		textCurDate = (TextView) findViewById( R.id.text_cur_date );
		listView = getListView();

		dateFormat = DateFormat.getDateFormat( this );
		mSelectedDate = Calendar.getInstance();
		textCurDate.setText( dateFormat.format( mSelectedDate.getTime() ) );

		buttonPrevDate.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				mSelectedDate.add( Calendar.DAY_OF_YEAR, -1 );
				textCurDate.setText( dateFormat.format( mSelectedDate.getTime() ) );
				cursorAdapter.swapCursor( getTaskCursor() );
			}
		} );

		buttonNextDate.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				mSelectedDate.add( Calendar.DAY_OF_YEAR, 1 );
				textCurDate.setText( dateFormat.format( mSelectedDate.getTime() ) );
				cursorAdapter.swapCursor( getTaskCursor() );
			}
		} );

		dbHelper = new TaskSQLiteHelper( this );
		Cursor cursor = getTaskCursor();
		cursorAdapter = new TaskCursorAdapter( this, cursor );
		setListAdapter( cursorAdapter );

		lvOnTouchListener = new TaskListViewOnTouchListener( listView, this );
		listView.setOnTouchListener( lvOnTouchListener );
		listView.setOnScrollListener( lvOnTouchListener.getScrollListener() );

		listView.setChoiceMode( ListView.CHOICE_MODE_MULTIPLE_MODAL );
		listView.setMultiChoiceModeListener( this );
		listView.setSelector( R.drawable.task_item_bg_selector );

	}

	static final String query = "SELECT " + TaskContract.TaskEntry._ID + ", "
	        + TaskContract.TaskEntry.STATUS + ", "
	        + TaskContract.TaskEntry.PRIORITY + ", "
	        + TaskContract.TaskEntry.TITLE
	        + " FROM " + TaskContract.TaskEntry.TABLENAME
	        + " WHERE " + TaskContract.TaskEntry.DELETED + " = 0 AND "
	        + TaskContract.TaskEntry.DUE + " = ? "
	        + " ORDER BY " + TaskContract.TaskEntry._ID + " ASC;";

	private ListView listView;

	private TaskListViewOnTouchListener lvOnTouchListener;

	private Cursor getTaskCursor() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = db.rawQuery(
		        query,
		        new String[] { TaskService.formatDateISO8601( mSelectedDate.getTime() ) }
		        );
		Log.v( TAG, "" + cursor.getCount() );

		//		Cursor cursor = db.query(
		//		        TaskContract.TaskEntry.TABLENAME,
		//		        new String[] {
		//		                TaskContract.TaskEntry._ID,
		//		                TaskContract.TaskEntry.STATUS,
		//		                TaskContract.TaskEntry.PRIORITY,
		//		                TaskContract.TaskEntry.TITLE,
		//		        },
		//		        null,
		//		        null,
		//		        null,
		//		        null,
		//		        TaskContract.TaskEntry._ID + " ASC" );
		return cursor;
	}

	@Override
	protected void onResume() {
		cursorAdapter.swapCursor( getTaskCursor() );
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		int id = item.getItemId();

		if ( R.id.action_add == id ) {
			Intent intent = new Intent( this, TaskActivity.class );
			startActivity( intent );
		}

		return super.onOptionsItemSelected( item );
	}

	@Override
	public boolean onActionItemClicked( ActionMode mode, MenuItem item ) {
		switch ( item.getItemId() ) {
			case R.id.menu_delete:

				for ( long id : listView.getCheckedItemIds() ) {
					SQLiteDatabase db = dbHelper.getWritableDatabase();

					ContentValues values = new ContentValues();
					values.put( TaskContract.TaskEntry.DELETED, 1 );

					db.update( TaskContract.TaskEntry.TABLENAME,
					        values,
					        TaskContract.TaskEntry._ID + " = ?",
					        new String[] { Long.toString( id ) } );

					cursorAdapter.swapCursor( getTaskCursor() );
				}

				mode.finish();
				return true;
		}

		return false;
	}

	@Override
	public boolean onCreateActionMode( ActionMode mode, Menu menu ) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate( R.menu.menu_cab_tasklist, menu );
		lvOnTouchListener.setSwipeEnable( false );
		return true;
	}

	@Override
	public void onDestroyActionMode( ActionMode mode ) {
		lvOnTouchListener.setSwipeEnable( true );
	}

	@Override
	public boolean onPrepareActionMode( ActionMode mode, Menu menu ) {
		return false;
	}

	@Override
	public void onItemCheckedStateChanged( ActionMode mode, int position, long id, boolean checked ) {
		mode.setTitle( listView.getCheckedItemCount() + " Selected" );
	}

	@Override
	public void onMoveTask( int postition ) {
		long id = listView.getItemIdAtPosition( postition );

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query( TaskContract.TaskEntry.TABLENAME,
		        new String[] {
		                TaskContract.TaskEntry._ID,
		                TaskContract.TaskEntry.DUE,
		                TaskContract.TaskEntry.TITLE },
		        TaskContract.TaskEntry._ID + " = ?",
		        new String[] { Long.toString( id ) },
		        null, null, null );

		if ( cursor.moveToFirst() ) {
			int dueIndex = cursor.getColumnIndex( TaskContract.TaskEntry.DUE );
			int titleIndex = cursor.getColumnIndex( TaskContract.TaskEntry.TITLE );
			String dueString = cursor.getString( dueIndex );
			String titleString = cursor.getString( titleIndex );

			Date date = TaskService.parseDateISO8601( dueString );
			Calendar cal = Calendar.getInstance();
			cal.setTime( date );

			cal.add( Calendar.DAY_OF_YEAR, 1 );

			String newDueString = TaskService.formatDateISO8601( cal.getTime() );
			ContentValues values = new ContentValues();
			values.put( TaskContract.TaskEntry.DUE, newDueString );

			int rowsAffected = db.update( TaskContract.TaskEntry.TABLENAME,
			        values,
			        TaskContract.TaskEntry._ID + " = ?",
			        new String[] { Long.toString( id ) } );

			if ( 0 < rowsAffected ) {
				Toast.makeText(
				        this,
				        "Moved '" + titleString + "' to the next day",
				        Toast.LENGTH_SHORT ).show();

				cursorAdapter.swapCursor( getTaskCursor() );
			}
		}
	}

	@Override
	public void onSelectTask( int position ) {
		Log.v( TAG, Integer.toString( position ) );
		listView.setItemChecked( position, !listView.isItemChecked( position ) );
		listView.performHapticFeedback( HapticFeedbackConstants.LONG_PRESS );
	}
}
