package net.wangtu.android.db.base.helper;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.wangtu.android.util.DateUtils;
import net.wangtu.android.util.LogUtil;

public class DbUtils {
	private static Map<String, Set<String>> table2ColumSetCache = new HashMap<String, Set<String>>();

	public static Set<String> getTableAllColumns(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		if (table2ColumSetCache.containsKey(paramString))
			return ((Set) table2ColumSetCache.get(paramString));
		HashSet localHashSet = new HashSet();
		Cursor localCursor = null;
		try {
			localCursor = paramSQLiteDatabase.rawQuery("SELECT * FROM "
					+ paramString + " LIMIT 0", null);
			String[] arrayOfString1 = localCursor.getColumnNames();
			String[] arrayOfString2 = arrayOfString1;
			int i = arrayOfString2.length;
			for (int j = 0; j < i; ++j) {
				String str = arrayOfString2[j];
				localHashSet.add(str);
			}
		} catch (Exception localException) {
			LogUtil.error(localException, DbUtils.class);
		} finally {
			if ((null != localCursor) && (!(localCursor.isClosed())))
				localCursor.close();
		}
		table2ColumSetCache.put(paramString, localHashSet);
		return localHashSet;
	}

	public static ContentValues getWantToInsertValues(Object obj, Set<String> paramSet) {
		ContentValues localContentValues = new ContentValues();
		try {
			Field[] arrayOfField1 = obj.getClass().getDeclaredFields();
			Field[] arrayOfField2 = arrayOfField1;
			int i = arrayOfField2.length;
			for (int j = 0; j < i; ++j) {
				Field localField = arrayOfField2[j];
				localField.setAccessible(true);
				String str = localField.getName();
				if (paramSet.contains(str)){
					putValueToContentValues(localField.get(obj),localContentValues, str);
				}
			}
		} catch (Exception localException) {
			LogUtil.error(localException, DbUtils.class);
		}
		return localContentValues;
	}

	private static void putValueToContentValues(Object paramObject,
			ContentValues paramContentValues, String paramString) {
		if (paramObject instanceof Integer)
			paramContentValues.put(paramString, (Integer) paramObject);
		else if (paramObject instanceof Long)
			paramContentValues.put(paramString, (Long) paramObject);
		else if (paramObject instanceof Byte)
			paramContentValues.put(paramString, (Byte) paramObject);
		else if (paramObject instanceof byte[])
			paramContentValues.put(paramString, (byte[]) (byte[]) paramObject);
		else if (paramObject instanceof Boolean)
			paramContentValues.put(paramString, (Boolean) paramObject);
		else if (paramObject instanceof Short)
			paramContentValues.put(paramString, (Short) paramObject);
		else if (paramObject instanceof Float)
			paramContentValues.put(paramString, (Float) paramObject);
		else if (paramObject instanceof Double)
			paramContentValues.put(paramString, (Double) paramObject);
		else if (paramObject instanceof String)
			paramContentValues.put(paramString, (String) paramObject);
		else if (paramObject instanceof Date)
			paramContentValues.put(paramString,
					DateUtils.date2StringBySecond((Date) paramObject));
		else
			paramContentValues.put(paramString, paramObject.toString());
	}
}
