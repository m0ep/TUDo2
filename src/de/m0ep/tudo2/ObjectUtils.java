package de.m0ep.tudo2;

import java.util.Arrays;

public final class ObjectUtils {
	public static boolean equals( final Object a, final Object b ) {
		return a == b || ( a != null && a.equals( b ) );
	}

	public static int hashCode( Object... objects ) {
		return Arrays.hashCode( objects );
	}
}
