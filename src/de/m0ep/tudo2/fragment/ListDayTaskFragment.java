package de.m0ep.tudo2.fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import de.m0ep.tudo2.R;
import de.m0ep.tudo2.provider.TaskContract.TaskEntry;
import de.m0ep.tudo2.provider.TaskProvider;

public class ListDayTaskFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	private static final int TASK_LOADER = 0;

	private static final String TAG = ListDayTaskFragment.class.getName();

	private final Calendar calendar = Calendar.getInstance( Locale.getDefault() );;

	private static final String[] PROJECTION = {
	        TaskEntry._ID,
	        TaskEntry.STATE,
	        TaskEntry.PRIORITY,
	        TaskEntry.DATE,
	        TaskEntry.DURATION,
	        TaskEntry.DESCRIPTION
	};

	String[] mappingFrom = {
	        TaskEntry.PRIORITY,
	        TaskEntry.DURATION,
	        TaskEntry.DESCRIPTION
	};

	int[] mappingTo = {
	        R.id.text_priority,
	        R.id.text_duration,
	        R.id.text_description
	};

	String[] values = { "one", "two", "three", "four" };
	SimpleCursorAdapter cursorAdapter;

	private static final String SELECTION = TaskEntry.DATE + " = ?";

	public ListDayTaskFragment() {
		// TODO Auto-generated constructor stub
	}

	public Date getDate() {
		return calendar.getTime();
	}

	public void setDate( final Date date ) {
		calendar.setTime( date );

		if ( isAdded() ) {
			getLoaderManager().restartLoader( TASK_LOADER, null, this );
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState ) {

		cursorAdapter = new SimpleCursorAdapter(
		        getActivity(),
		        R.layout.list_tasl_item,
		        null,
		        mappingFrom,
		        mappingTo,
		        SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );
		setListAdapter( cursorAdapter );

		getLoaderManager().initLoader( TASK_LOADER, null, this );

		return super.onCreateView( inflater, container, savedInstanceState );
	}

	@Override
	public Loader<Cursor> onCreateLoader( int loaderId, Bundle args ) {
		String[] selectionArgs = { (String) TaskProvider.formatDate( calendar.getTime() ) };

		try {
			switch ( loaderId ) {
				case TASK_LOADER:
					return new CursorLoader(
					        getActivity(),
					        TaskEntry.CONTENT_URI,
					        PROJECTION,
					        SELECTION,
					        selectionArgs,
					        null );
				default:
					Log.e( TAG, "Invalid loader with id=" + loaderId );
					return null;
			}
		} catch ( Exception e ) {
			Log.e( TAG, "Error while creating a loader", e );
			return null;
		}
	}

	@Override
	public void onLoadFinished( Loader<Cursor> loader, Cursor returnCursor ) {
		cursorAdapter.swapCursor( returnCursor );
	}

	@Override
	public void onLoaderReset( Loader<Cursor> loader ) {
		cursorAdapter.swapCursor( null );
	}
}
