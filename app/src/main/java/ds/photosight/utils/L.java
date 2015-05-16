package ds.photosight.utils;

import android.text.TextUtils;
import ds.photosight.BuildConfig;


public final class L {

	public static final boolean DEBUG = BuildConfig.DEBUG;
	private static final String POSFIX_STRING = ")";
	private static final String PREFIX_STRING = "# (";
	private static final String PREFIX_MAIN_STRING = "# ";


	public static void v(String msg) {
		if (DEBUG)
			android.util.Log.v(getLocation(), msg);
	}


	public static void d(String msg) {
		if (DEBUG)
			android.util.Log.d(getLocation(), msg);
	}


	public static void i(String msg) {
		if (DEBUG)
			android.util.Log.i(getLocation(), msg);
	}


	public static void w(String msg) {
		if (DEBUG)
			android.util.Log.w(getLocation(), msg);
	}


	public static void e(String msg) {
		if (DEBUG)
			android.util.Log.e(getLocation(), msg);
	}


	// ==========================================================


	public static void v(String msg, Exception e) {
		if (DEBUG)
			android.util.Log.v(getLocation(), msg, e);
	}


	public static void d(String msg, Exception e) {
		if (DEBUG)
			android.util.Log.d(getLocation(), msg, e);
	}


	public static void i(String msg, Exception e) {
		if (DEBUG)
			android.util.Log.i(getLocation(), msg, e);
	}


	public static void w(String msg, Exception e) {
		if (DEBUG)
			android.util.Log.w(getLocation(), msg, e);
	}


	public static void e(String msg, Exception e) {
		if (DEBUG)
			android.util.Log.e(getLocation(), msg, e);
	}


	// ==========================================================


	public static void v(Exception e) {
		if (DEBUG)
			android.util.Log.v(getLocation(), "", e);
	}


	public static void d(Exception e) {
		if (DEBUG)
			android.util.Log.d(getLocation(), "", e);
	}


	public static void i(Exception e) {
		if (DEBUG)
			android.util.Log.i(getLocation(), "", e);
	}


	public static void w(Exception e) {
		if (DEBUG)
			android.util.Log.w(getLocation(), "", e);
	}


	public static void e(Exception e) {
		if (DEBUG)
			android.util.Log.e(getLocation(), "", e);
	}


	// ==========================================================


	/*public static void v(Object obj, String msg) {
		if (DEBUG)
			android.util.Log.v(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg);
	}


	public static void d(Object obj, String msg) {
		if (DEBUG)
			android.util.Log.d(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg);
	}


	public static void i(Object obj, String msg) {
		if (DEBUG)
			android.util.Log.i(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg);
	}


	public static void w(Object obj, String msg) {
		if (DEBUG)
			android.util.Log.w(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg);
	}


	public static void e(Object obj, String msg) {
		if (DEBUG)
			android.util.Log.e(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg);
	}*/


	// ==========================================================


	public static void v(Object obj, String msg, Exception e) {
		if (DEBUG)
			android.util.Log.v(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg, e);
	}


	public static void d(Object obj, String msg, Exception e) {
		if (DEBUG)
			android.util.Log.d(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg, e);
	}


	public static void i(Object obj, String msg, Exception e) {
		if (DEBUG)
			android.util.Log.i(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg, e);
	}


	public static void w(Object obj, String msg, Exception e) {
		if (DEBUG)
			android.util.Log.w(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg, e);
	}


	public static void e(Object obj, String msg, Exception e) {
		if (DEBUG)
			android.util.Log.e(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg, e);
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void v(String msg, Object... formatArgs) {
		if (DEBUG)
			android.util.Log.v(getLocation(), String.format(msg, formatArgs));
	}

	public static void d(String msg, Object... formatArgs) {
		if (DEBUG)
			android.util.Log.d(getLocation(), String.format(msg, formatArgs));
	}

	public static void w(String msg, Object... formatArgs) {
		if (DEBUG)
			android.util.Log.w(getLocation(), String.format(msg, formatArgs));
	}

	public static void i(String msg, Object... formatArgs) {
		if (DEBUG)
			android.util.Log.i(getLocation(), String.format(msg, formatArgs));
	}

	public static void e(String msg, Object... formatArgs) {
		if (DEBUG)
			android.util.Log.e(getLocation(), String.format(msg, formatArgs));
	}




	private static String getLocation() {
		final String className = L.class.getName();
		final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
		boolean found = false;
		for (int i = 0; i < traces.length; i++) {
			StackTraceElement trace = traces[i];

			try {
				if (found) {
					if (!trace.getClassName().startsWith(className)) {
						Class<?> clazz = Class.forName(trace.getClassName());
						return PREFIX_MAIN_STRING + getClassName(clazz) + ":" + trace.getMethodName() + ":" + trace.getLineNumber();
					}
				} else if (trace.getClassName().startsWith(className)) {
					found = true;
					continue;
				}
			} catch (ClassNotFoundException e) {
			}
		}
		return "[]: ";
	}


	private static String getClassName(Class<?> clazz) {
		if (clazz != null) {
			if (!TextUtils.isEmpty(clazz.getSimpleName())) {
				return clazz.getSimpleName();
			}

			return getClassName(clazz.getEnclosingClass());
		}

		return "";
	}
}