package net.wangtu.android.db.base.helper;

import net.wangtu.android.util.DateUtils;

import java.util.Date;

public abstract class SqlUtils {
	public static String getSQL(String paramString, Object[] paramArrayOfObject) {
		if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0))
			return paramString;
		StringBuilder localStringBuilder = new StringBuilder();
		int i = 0;
		int j = 0;
		while ((i = paramString.indexOf(63)) > 0) {
			localStringBuilder.append(paramString.substring(0, i));
			paramString = paramString.substring(i + 1);
			Object localObject = paramArrayOfObject[(j++)];
			if (localObject == null) {
				localStringBuilder.append("null");
			} else if (localObject instanceof String) {
				localStringBuilder.append("'");
				localStringBuilder.append(localObject);
				localStringBuilder.append("'");
			} else if (localObject instanceof Date) {
				localStringBuilder.append("'");
				localStringBuilder.append(DateUtils
						.date2String((Date) localObject));
				localStringBuilder.append("'");
			} else {
				localStringBuilder.append(localObject);
			}
		}
		localStringBuilder.append(paramString);
		return localStringBuilder.toString();
	}

	public static String getInSQL(int paramInt) {
		StringBuilder localStringBuilder = new StringBuilder();
		localStringBuilder.append("(");
		for (int i = 0; i < paramInt; ++i)
			if (i == 0)
				localStringBuilder.append("?");
			else
				localStringBuilder.append(",?");
		localStringBuilder.append(")");
		return localStringBuilder.toString();
	}
}
