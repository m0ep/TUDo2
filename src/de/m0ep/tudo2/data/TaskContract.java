package de.m0ep.tudo2.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class TaskContract {
	public static final String AUTHORITY = "de.m0ep.tudo2.taskprovider";

	public static final Uri AUTHORITY_URI = Uri.parse( "content://" + AUTHORITY );

	public interface TaskColumns extends BaseColumns {
		public static final String TABLENAME = "task";
		public static final String COMPLETED = "completed";
		public static final String IS_DELETED = "is_deleted";
		public static final String DUE = "due";
		public static final String NOTE = "note";
		public static final String PRIORITY = "priority";
		public static final String STATUS = "status";
		public static final String TITLE = "title";
		public static final String UPDATED = "updated";

	}

	public static abstract class Task implements TaskColumns {
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
		        AUTHORITY_URI,
		        TABLENAME );

		public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/" + TABLENAME;
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + TABLENAME;
	}
}
