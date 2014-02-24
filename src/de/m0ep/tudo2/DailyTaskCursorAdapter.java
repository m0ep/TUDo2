package de.m0ep.tudo2;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.m0ep.tudo2.model.TaskModel;
import de.m0ep.tudo2.model.TaskService;
import de.m0ep.tudo2.provider.TaskContract.TaskEntry;
import de.m0ep.tudo2.provider.TaskSQLiteHelper;

public class DailyTaskCursorAdapter extends CursorAdapter {

	private final LayoutInflater inflater;
	private final TaskSQLiteHelper taskSQLiteHelper;

	public DailyTaskCursorAdapter( Context context, Cursor c ) {
		super( context, c, 0 );
		this.inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		this.taskSQLiteHelper = new TaskSQLiteHelper( context );
	}

	@Override
	public void bindView( View view, Context context, Cursor cursor ) {
		int statusIndex = cursor.getColumnIndex( TaskEntry.STATUS );
		int priorityIndex = cursor.getColumnIndex( TaskEntry.PRIORITY );
		int titleIndex = cursor.getColumnIndex( TaskEntry.TITLE );

		String status = cursor.getString( statusIndex );
		String priority = cursor.getString( priorityIndex );
		String title = cursor.getString( titleIndex );

		boolean completed = TaskModel.STATUS_COMPLETED.equals( status );

		final int position = cursor.getPosition();
		final ViewHolder vh = (ViewHolder) view.getTag();
		vh.toggleStatus.setChecked( completed );
		vh.textPriority.setText( priority );
		vh.textTitle.setText( title );

		updateVisualCheckStatus( vh.textPriority, completed );
		updateVisualCheckStatus( vh.textTitle, completed );

		vh.toggleStatus.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
				long id = getItemId( position );

				ContentValues values = new ContentValues();
				values.put( TaskEntry.STATUS, isChecked
				        ? TaskModel.STATUS_COMPLETED
				        : TaskModel.STATUS_NOT_COMPLETED );
				values.put( TaskEntry.COMPLETED, isChecked
				        ? TaskService.formatDateTimeISO8601( new Date() )
				        : "" );

				SQLiteDatabase db = taskSQLiteHelper.getWritableDatabase();
				int res = db.update( TaskEntry.TABLENAME,
				        values,
				        TaskEntry._ID + " = ?",
				        new String[] { Long.toString( id ) } );

				if ( 0 < res ) {
					updateVisualCheckStatus( vh.textPriority, isChecked );
					updateVisualCheckStatus( vh.textTitle, isChecked );
				}
			}
		} );
	}

	private void updateVisualCheckStatus( final TextView view, boolean isChecked ) {
		view.setEnabled( !isChecked );

		if ( isChecked ) {
			view.setPaintFlags( view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
		} else {
			view.setPaintFlags( view.getPaintFlags() & ( ~Paint.STRIKE_THRU_TEXT_FLAG ) );
		}
	}

	@Override
	public View newView( final Context context, Cursor cursor, ViewGroup parent ) {
		View view = inflater.inflate( R.layout.daily_task_list_item, parent, false );
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
