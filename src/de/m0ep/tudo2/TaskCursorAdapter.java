package de.m0ep.tudo2;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.m0ep.tudo2.provider.TaskContract;

public class TaskCursorAdapter extends CursorAdapter {

	private final LayoutInflater inflater;
	private final OnTouchListener touchListener;

	public TaskCursorAdapter( Context context, Cursor c, OnTouchListener touchListener ) {
		super( context, c, true );
		this.touchListener = touchListener;
		this.inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
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
	public View newView( Context context, Cursor cursor, ViewGroup parent ) {
		View view = inflater.inflate( R.layout.list_task_item, parent, false );
		ViewHolder vh = new ViewHolder();
		vh.toggleStatus = (ToggleButton) view.findViewById( R.id.toggleStatus );
		vh.textPriority = (TextView) view.findViewById( R.id.text_priority );
		vh.textTitle = (TextView) view.findViewById( R.id.text_title );
		view.setTag( vh );
		view.setOnTouchListener( touchListener );

		return view;
	}

	private static class ViewHolder {
		ToggleButton toggleStatus;
		TextView textPriority;
		TextView textTitle;
	}
}