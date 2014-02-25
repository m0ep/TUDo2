package de.m0ep.tudo2;

import java.util.Date;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.m0ep.tudo2.data.TaskConstants;
import de.m0ep.tudo2.data.TaskContract.Task;

public class TaskCursorAdapter extends CursorAdapter {
	private final LayoutInflater inflater;

	public TaskCursorAdapter( Context context, Cursor c ) {
		super( context, c, 0 );
		this.inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	}

	@Override
	public void bindView( View view, Context context, Cursor cursor ) {
		int statusIndex = cursor.getColumnIndex( Task.STATUS );
		int priorityIndex = cursor.getColumnIndex( Task.PRIORITY );
		int titleIndex = cursor.getColumnIndex( Task.TITLE );
		int noteIndex = cursor.getColumnIndex( Task.NOTE );

		String status = cursor.getString( statusIndex );
		String priority = cursor.getString( priorityIndex );
		String title = cursor.getString( titleIndex );
		String note = cursor.getString( noteIndex );

		boolean completed = TaskConstants.STATUS_COMPLETED.equals( status );

		final int position = cursor.getPosition();
		final ViewHolder vh = (ViewHolder) view.getTag();
		vh.toggleStatus.setChecked( completed );
		vh.textPriority.setText( priority );
		vh.textTitle.setText( title );

		if ( !TextUtils.isEmpty( note ) ) {
			vh.textNote.setVisibility( View.VISIBLE );
			vh.textNote.setText( note );

			LinearLayout.LayoutParams layoutParams = (LayoutParams) vh.textTitle.getLayoutParams();
			layoutParams.weight = 0;
			vh.textTitle.setLayoutParams( layoutParams );
		} else {
			vh.textNote.setVisibility( View.GONE );
			vh.textNote.setText( "" );

			LinearLayout.LayoutParams layoutParams = (LayoutParams) vh.textTitle.getLayoutParams();
			layoutParams.weight = 1;
			vh.textTitle.setLayoutParams( layoutParams );
		}

		updateVisualCheckStatus( vh.textPriority, completed );
		updateVisualCheckStatus( vh.textTitle, completed );
		updateVisualCheckStatus( vh.textNote, completed );

		vh.toggleStatus.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
				long id = getItemId( position );

				ContentValues values = new ContentValues();
				values.put( Task.STATUS, isChecked
				        ? TaskConstants.STATUS_COMPLETED
				        : TaskConstants.STATUS_NOT_COMPLETED );
				values.put( Task.COMPLETED, isChecked
				        ? Iso8601DateUtils.formatDateTimeIso8601( new Date() )
				        : "" );

				Uri uri = ContentUris.withAppendedId( Task.CONTENT_URI, id );
				int rowsUpdated = mContext.getContentResolver().update( uri, values, null, null );

				if ( 0 < rowsUpdated ) {
					updateVisualCheckStatus( vh.textPriority, isChecked );
					updateVisualCheckStatus( vh.textTitle, isChecked );
					updateVisualCheckStatus( vh.textNote, isChecked );
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

		vh.toggleStatus = (ToggleButton) view.findViewById( R.id.status );
		vh.textPriority = (TextView) view.findViewById( R.id.priority );
		vh.textTitle = (TextView) view.findViewById( R.id.title );
		vh.textNote = (TextView) view.findViewById( R.id.note );

		view.setTag( vh );

		return view;
	}

	private static class ViewHolder {
		ToggleButton toggleStatus;
		TextView textPriority;
		TextView textTitle;
		TextView textNote;
	}
}
