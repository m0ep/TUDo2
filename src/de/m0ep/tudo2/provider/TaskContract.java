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
		        TaskContract.TaskEntry.TABLENAME );

		public static final String TABLENAME = "tasks";

		public static final String COMPLETED = "completed";
		public static final String DELETED = "deleted";
		public static final String DUE = "due";
		public static final String NOTE = "note";
		public static final String PRIORITY = "priority";
		public static final String STATUS = "status";
		public static final String TITLE = "title";
		public static final String UPDATED = "updated";
	}
}
