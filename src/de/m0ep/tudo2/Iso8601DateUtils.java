package de.m0ep.tudo2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Iso8601DateUtils {
	private static final String FORMAT_STRING_DATETIME = "yyyy-MM-dd'T'HH:mm:ssZ";
	private static final SimpleDateFormat FORMAT_DATETIME =
	        new SimpleDateFormat( FORMAT_STRING_DATETIME, Locale.getDefault() );
	private static final String FORMAT_STRING_DATE = "yyyy-MM-dd";
	private static final SimpleDateFormat FORMAT_DATE =
	        new SimpleDateFormat( FORMAT_STRING_DATE, Locale.getDefault() );

	public static Date parseDateTimeIso8601( String string ) throws ParseException {
		return FORMAT_DATETIME.parse( string );
	}

	public static String formatDateTimeIso8601( Date date ) {
		return FORMAT_DATETIME.format( date );
	}

	public static Date parseDateIso8601( String string ) throws ParseException {
		return FORMAT_DATE.parse( string );
	}

	public static String formatDateIso8601( Date date ) {
		return FORMAT_DATE.format( date );
	}
}
