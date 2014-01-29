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
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.m0ep.tudo2.provider.TaskContract.TaskEntry;

public class ListDayTaskFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	private static final String TAG = ListDayTaskFragment.class.getName();

	private final Calendar calendar = Calendar.getInstance( Locale.getDefault() );;
	private SimpleCursorAdapter cursorAdapter;

	private static final String[] PROJECTION = {
	        TaskEntry._ID,
	        TaskEntry.DATE,
	        TaskEntry.DESCRIPTION
	};

	private static final String SELECTION = TaskEntry.DATE + " = ?";

	public ListDayTaskFragment() {
		// TODO Auto-generated constructor stub
	}

	public Date getDate() {
		return calendar.getTime();
	}

	public void setDate( final Date date ) {
		calendar.setTime( date );
		//getLoaderManager().restartLoader( 0, null, this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState ) {

		String[] from = { TaskEntry.DESCRIPTION };
		int[] to = { android.R.id.text1 };

		cursorAdapter = new SimpleCursorAdapter(
		        getActivity(),
		        android.R.layout.simple_list_item_checked, null, from, to, 0 );

		setListAdapter( cursorAdapter );
		getLoaderManager().initLoader( 0, null, this );

		return super.onCreateView( inflater, container, savedInstanceState );
	}

	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		try {
			String formatDate = "2014-01-29";
			String[] selectionArgs = { formatDate };

			return new CursorLoader(
			        getActivity(),
			        TaskEntry.CONTENT_URI,
			        PROJECTION,
			        SELECTION,
			        selectionArgs,
			        "" );
		} catch ( Exception e ) {
			Log.e( TAG, "error", e );
			return null;
		}
	}

	@Override
	public void onLoadFinished( Loader<Cursor> loader, Cursor data ) {
		cursorAdapter.swapCursor( data );
	}

	@Override
	public void onLoaderReset( Loader<Cursor> loader ) {
		cursorAdapter.swapCursor( null );
	}
}
