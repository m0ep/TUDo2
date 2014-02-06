package de.m0ep.tudo2.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class TaskContract {

	public static final String AUTHORITY = "de.m0ep.tudo2.taskprovider";

	public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY );

	public TaskContract() {
	}

	public static abstract class TaskEntry implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
		        TaskContract.CONTENT_URI,
		        TaskContract.TaskEntry.TABLE_NAME );

		public static final String TABLE_NAME = "tasks";

		public static final String STATE = "state";
		public static final String PRIORITY = "priority";
		public static final String DATE = "date";
		public static final String DURATION = "duration";
		public static final String DESCRIPTION = "description";

	}
}
