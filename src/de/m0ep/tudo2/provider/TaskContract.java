package de.m0ep.tudo2.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class TaskContract {

	public TaskContract() {
	}

	public static abstract class TaskEntry implements BaseColumns {

		public static final String TABLE_NAME = "tasks";

		public static final String STATE = "state";
		public static final String PRIORITY = "priority";
		public static final String DATE = "date";
		public static final String DURATION = "duration";
		public static final String DESCRIPTION = "description";

		public static final Uri CONTENT_URI = Uri.parse(
		        "content://" + TaskProvider.AUTHORITY + "/" + TABLE_NAME );
	}
}
