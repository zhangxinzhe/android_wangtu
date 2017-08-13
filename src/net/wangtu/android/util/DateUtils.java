package net.wangtu.android.util;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateUtils {
	public static String date2String(Date paramDate) {
		return date2String(paramDate, "yyyy-MM-dd HH:mm:ss.SSS");
	}

	public static String date2String(Date paramDate, String paramString) {
		if (paramDate == null) {
			return null;
		}
		return new SimpleDateFormat(paramString).format(paramDate);
	}

	public static String date2StringByDay(Date paramDate) {
		return date2String(paramDate, "yyyy-MM-dd");
	}

	public static String date2StringByMinute(Date paramDate) {
		return date2String(paramDate, "yyyy-MM-dd HH:mm");
	}

	public static String date2StringBySecond(Date paramDate) {
		return date2String(paramDate, "yyyy-MM-dd HH:mm:ss");
	}
}
