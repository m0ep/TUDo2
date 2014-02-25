package de.m0ep.tudo2;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.m0ep.tudo2.TaskListOnTouchListener.TaskTouchActionCallback;
import de.m0ep.tudo2.data.TaskContract.Task;
import de.m0ep.tudo2.data.TaskContract.TaskColumns;

public class DailyTaskListFragment
        extends ListFragment
        implements OnClickListener, TaskTouchActionCallback, MultiChoiceModeListener {
	private static final String TAG = DailyTaskListFragment.class.getName();

	private static final String STATE_KEY_SELECTED_DATE = "selected_date";

	private static final String[] QUERY_PROJECTION = new String[] {
	        Task._ID,
	        Task.IS_DELETED,
	        Task.DUE,
	        Task.PRIORITY,
	        Task.STATUS,
	        Task.TITLE
	};
	private static final String QUERY_SELECTION = Task.IS_DELETED + " = 0 AND " + Task.DUE + " = ?";
	private static final String QUERY_ORDER_BY = Task._ID + " ASC";

	private Calendar selectedDate;
	private java.text.DateFormat dateFormat;

	private Button btnPrevDate;
	private Button btnNextDate;
	private TextView txtCurDate;
	private ListView listView;

	private TaskCursorAdapter dailyTaskAdapter;
	private TaskListOnTouchListener taskListOnTouchListener;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		dateFormat = DateFormat.getDateFormat( getActivity() );
		selectedDate = Calendar.getInstance();
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState ) {

		View view = inflater.inflate( R.layout.daily_task_layout, container, false );
		btnNextDate = (Button) view.findViewById( R.id.button_next_date );
		btnPrevDate = (Button) view.findViewById( R.id.button_prev_date );
		txtCurDate = (TextView) view.findViewById( R.id.text_selected_date );

		return view;
	}

	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );

		btnNextDate.setOnClickListener( this );
		btnPrevDate.setOnClickListener( this );

		listView = getListView();
		dailyTaskAdapter = new TaskCursorAdapter( getActivity(), null );
		setListAdapter( dailyTaskAdapter );

		taskListOnTouchListener = new TaskListOnTouchListener( listView, this );
		listView.setOnTouchListener( taskListOnTouchListener );
		listView.setOnScrollListener( taskListOnTouchListener.getScrollListener() );
		listView.setChoiceMode( ListView.CHOICE_MODE_MULTIPLE_MODAL );
		listView.setMultiChoiceModeListener( this );
		listView.setSelector( R.drawable.task_item_bg_selector );

		if ( null != savedInstanceState ) {
			selectedDate = (Calendar) savedInstanceState.getSerializable( STATE_KEY_SELECTED_DATE );
		}

		txtCurDate.setText( dateFormat.format( selectedDate.getTime() ) );

		refreshListView( selectedDate );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		if ( null != outState ) {
			outState.putSerializable( STATE_KEY_SELECTED_DATE, selectedDate );
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshListView( selectedDate );
	}

	@Override
	public void onClick( View v ) {
		if ( v == btnNextDate ) {
			selectedDate.add( Calendar.DAY_OF_YEAR, 1 );
			txtCurDate.setText( dateFormat.format( selectedDate.getTime() ) );
			refreshListView( selectedDate );
		} else if ( v == btnPrevDate ) {
			selectedDate.add( Calendar.DAY_OF_YEAR, -1 );
			txtCurDate.setText( dateFormat.format( selectedDate.getTime() ) );
			refreshListView( selectedDate );
		}
	}

	/******* Task Callbacks *******/

	@Override
	public void onMoveTask( int postition ) {
		long id = listView.getItemIdAtPosition( postition );
		Uri uri = ContentUris.withAppendedId( Task.CONTENT_URI, id );
		ContentResolver contentResolver = getActivity().getContentResolver();
		Cursor cursor = contentResolver.query( uri,
		        new String[] { Task._ID, TaskColumns.DUE, TaskColumns.TITLE },
		        null, null, null );

		if ( cursor.moveToFirst() ) {
			int dueIndex = cursor.getColumnIndex( TaskColumns.DUE );
			int titleIndex = cursor.getColumnIndex( TaskColumns.TITLE );
			String dueString = cursor.getString( dueIndex );
			String titleString = cursor.getString( titleIndex );

			Date date = null;
			try {
				date = Iso8601DateUtils.parseDateIso8601( dueString );
			} catch ( ParseException e ) {
				Log.v( TAG, "Failed to parse ISO8601 date '" + dueString + "'" );
				Toast.makeText(
				        getActivity(),
				        R.string.error_task_moving_failed,
				        Toast.LENGTH_SHORT ).show();
				return;
			}

			Calendar cal = Calendar.getInstance();
			cal.setTime( date );
			cal.add( Calendar.DAY_OF_YEAR, 1 );

			String newDueString = Iso8601DateUtils.formatDateIso8601( cal.getTime() );
			ContentValues values = new ContentValues();
			values.put( TaskColumns.DUE, newDueString );

			int updatedRows = contentResolver.update( uri, values, null, null );
			if ( 0 < updatedRows ) {
				Toast.makeText(
				        getActivity(),
				        getString( R.string.fmt_move_task, titleString ),
				        Toast.LENGTH_SHORT ).show();

				refreshListView( selectedDate );
			}
		}
	}

	@Override
	public void onSelectTask( int position ) {
		listView.setItemChecked( position, !listView.isItemChecked( position ) );
		listView.performHapticFeedback( HapticFeedbackConstants.LONG_PRESS );
	}

	/******* Multiple Selection Callbacks *******/

	@Override
	public boolean onCreateActionMode( ActionMode mode, Menu menu ) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate( R.menu.menu_cab_tasklist, menu );
		taskListOnTouchListener.setSwipeEnable( false );
		return true;
	}

	@Override
	public boolean onPrepareActionMode( ActionMode mode, Menu menu ) {
		return false;
	}

	@Override
	public boolean onActionItemClicked( ActionMode mode, MenuItem item ) {
		long[] checkedItemIds = listView.getCheckedItemIds();
		switch ( item.getItemId() ) {
			case R.id.menu_delete: {
				ContentResolver contentResolver = getActivity().getContentResolver();
				for ( long id : checkedItemIds ) {
					Uri uri = ContentUris.withAppendedId( Task.CONTENT_URI, id );
					contentResolver.delete( uri, null, null );
				}

				refreshListView( selectedDate );

				mode.finish();
				return true;
			}
			case R.id.menu_edit: {
				if ( 1 == checkedItemIds.length ) {
					final long id = checkedItemIds[0];
					mode.finish();

					Intent intent = new Intent( getActivity(), TaskActivity.class );
					intent.putExtra( TaskActivity.EXTRA_TASK_ACTIVITY_MODE,
					        TaskActivity.MODE_EDIT_TASK );
					intent.putExtra( TaskActivity.EXTRA_TASK_ID,
					        id );
					startActivity( intent );

					return true;
				} else {
					Toast.makeText(
					        getActivity(),
					        R.string.error_task_loading_failed,
					        Toast.LENGTH_SHORT ).show();
				}
			}
		}

		return false;
	}

	@Override
	public void onDestroyActionMode( ActionMode mode ) {
		taskListOnTouchListener.setSwipeEnable( true );
	}

	@Override
	public void onItemCheckedStateChanged( ActionMode mode, int position, long id, boolean checked ) {
		mode.setTitle( listView.getCheckedItemCount() + " Task(s) selected" );
		final boolean canEdit = ( 1 == listView.getCheckedItemCount() );
		final MenuItem menuItemEdit = mode.getMenu().findItem( R.id.menu_edit );
		menuItemEdit.setVisible( canEdit );
		menuItemEdit.setEnabled( canEdit );
	}

	/******* Helper Methods *******/

	private void refreshListView( Calendar selectedDate ) {
		ContentResolver contentResolver = getActivity().getContentResolver();
		Cursor cursor = contentResolver.query( Task.CONTENT_URI,
		        QUERY_PROJECTION,
		        QUERY_SELECTION,
		        new String[] { Iso8601DateUtils.formatDateIso8601( selectedDate.getTime() ) },
		        QUERY_ORDER_BY );

		cursor.moveToFirst();
		dailyTaskAdapter.swapCursor( cursor );
	}
}
