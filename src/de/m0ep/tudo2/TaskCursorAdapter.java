package de.m0ep.tudo2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.m0ep.tudo2.provider.TaskContract;

public class TaskCursorAdapter extends CursorAdapter {

	private final LayoutInflater inflater;
	private final HashSet<Integer> selectedPositionsSet;

	public TaskCursorAdapter( Context context, Cursor c ) {
		super( context, c, true );
		this.inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		this.selectedPositionsSet = new HashSet<Integer>();
	}

	public void addSelection( int position ) {
		selectedPositionsSet.add( position );
		notifyDataSetChanged();
	}

	public void removeSelection( int position ) {
		selectedPositionsSet.remove( position );
		notifyDataSetChanged();
	}

	public boolean isSelected( int position ) {
		return selectedPositionsSet.contains( position );
	}

	public Set<Integer> getSelectedPositions() {
		return Collections.unmodifiableSet( selectedPositionsSet );
	}

	public void clearSelection() {
		selectedPositionsSet.clear();
		notifyDataSetChanged();
	}

	@Override
	public void bindView( View view, Context context, Cursor cursor ) {
		int statusIndex = cursor.getColumnIndex( TaskContract.TaskEntry.STATUS );
		int priorityIndex = cursor.getColumnIndex( TaskContract.TaskEntry.PRIORITY );
		int titleIndex = cursor.getColumnIndex( TaskContract.TaskEntry.TITLE );

		String status = cursor.getString( statusIndex );
		String priority = cursor.getString( priorityIndex );
		String title = cursor.getString( titleIndex );

		ViewHolder vh = (ViewHolder) view.getTag();
		vh.toggleStatus.setChecked( "completed".equals( status ) ); // TODO: magic string
		vh.textPriority.setText( priority );
		vh.textTitle.setText( title );
	}

	@Override
	public View newView( final Context context, Cursor cursor, ViewGroup parent ) {
		View view = inflater.inflate( R.layout.list_task_item, parent, false );
		ViewHolder vh = new ViewHolder();

		vh.toggleStatus = (ToggleButton) view.findViewById( R.id.toggleStatus );
		vh.textPriority = (TextView) view.findViewById( R.id.text_priority );
		vh.textTitle = (TextView) view.findViewById( R.id.text_title );

		view.setTag( vh );

		return view;
	}

	private static class ViewHolder {
		ToggleButton toggleStatus;
		TextView textPriority;
		TextView textTitle;
	}
}
