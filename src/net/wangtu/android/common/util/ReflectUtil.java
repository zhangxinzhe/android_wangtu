package net.wangtu.android.common.util;

import android.annotation.SuppressLint;

public class ReflectUtil {

	// 获取参数类型Class[]的方法
	@SuppressWarnings("rawtypes")
	public static Class[] getMethodClass(String[] type) {
		Class[] cs = new Class[type.length];
		for (int i = 0; i < cs.length; i++) {
			if (!type[i].trim().equals("") || type[i] != null) {
				if (type[i].equals("int") || type[i].equals("Integer")) {
					cs[i] = Integer.TYPE;
				} else if (type[i].equals("float") || type[i].equals("Float")) {
					cs[i] = Float.TYPE;
				} else if (type[i].equals("double") || type[i].equals("Double")) {
					cs[i] = Double.TYPE;
				} else if (type[i].equals("boolean") || type[i].equals("Boolean")) {
					cs[i] = Boolean.TYPE;
				} else {
					cs[i] = String.class;
				}
			}
		}
		return cs;
	}

	// 获取参数Object[]的方法
	@SuppressLint("UseValueOf")
	public static Object[] getMethodObject(String[] type, String[] param) {
		Object[] obj = new Object[param.length];
		for (int i = 0; i < obj.length; i++) {
			if (!param[i].trim().equals("") || param[i] != null) {
				if (type[i].equals("int") || type[i].equals("Integer")) {
					obj[i] = new Integer(param[i]);
				} else if (type[i].equals("float") || type[i].equals("Float")) {
					obj[i] = new Float(param[i]);
				} else if (type[i].equals("double") || type[i].equals("Double")) {
					obj[i] = new Double(param[i]);
				} else if (type[i].equals("boolean") || type[i].equals("Boolean")) {
					obj[i] = new Boolean(param[i]);
				} else {
					obj[i] = param[i];
				}
			}
		}
		return obj;
	}
}
