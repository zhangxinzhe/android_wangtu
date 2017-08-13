package net.wangtu.android.db.base.helper;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public class SqlCreator {
	private final StringBuilder sql;
	private final List<String> args;
	private boolean hasOrderBy;
	private boolean hasWhere;
	private boolean isFirst;

	public SqlCreator(String paramString) {
		this(paramString, true);
	}

	public SqlCreator(String paramString, boolean paramBoolean) {
		this.hasOrderBy = false;
		this.hasWhere = true;
		this.isFirst = true;
		if (TextUtils.isEmpty(paramString))
			throw new IllegalArgumentException("baseSQL can't be null");
		this.args = new ArrayList();
		this.sql = new StringBuilder();
		this.sql.append(paramString.trim());
		this.hasWhere = paramBoolean;
	}

	public void addExpression(String paramString1, String paramString2,
			boolean paramBoolean) {
		addExpression(paramString1, paramString2, null, paramBoolean);
	}

	public void addExpression(String paramString1, String paramString2,
			String paramString3, boolean paramBoolean) {
		if (paramBoolean) {
			if (this.isFirst) {
				if (this.hasWhere)
					if (!(this.sql.toString().toLowerCase().endsWith("where")))
						this.sql.append(" " + paramString1);
					else
						this.sql.append(" WHERE");
				this.isFirst = false;
			} else {
				this.sql.append(" " + paramString1);
			}
			this.sql.append(" " + paramString2);
			if (paramString3 != null)
				this.args.add(paramString3);
		}
	}

	public void and(String paramString, boolean paramBoolean) {
		addExpression("AND", paramString, paramBoolean);
	}

	public void and(String paramString1, String paramString2,
			boolean paramBoolean) {
		addExpression("AND", paramString1, paramString2, paramBoolean);
	}

	public void or(String paramString, boolean paramBoolean) {
		addExpression("OR", paramString, paramBoolean);
	}

	public void or(String paramString1, String paramString2,
			boolean paramBoolean) {
		addExpression("OR", paramString1, paramString2, paramBoolean);
	}

	public void groupBy(String[] paramArrayOfString) {
		if ((paramArrayOfString == null) || (paramArrayOfString.length == 0))
			return;
		this.sql.append(" GROUP BY ");
		String[] arrayOfString = paramArrayOfString;
		int i = arrayOfString.length;
		for (int j = 0; j < i; ++j) {
			String str = arrayOfString[j];
			this.sql.append(str).append(", ");
		}
		this.sql.delete(this.sql.length() - 2, this.sql.length() - 1);
	}

	public void orderBy(String paramString) {
		orderBy(paramString, false);
	}

	public void orderByDesc(String paramString) {
		orderBy(paramString, true);
	}

	public void orderBy(String paramString, boolean paramBoolean) {
		if (!(this.hasOrderBy))
			this.sql.append(" ORDER BY ");
		else
			this.sql.append(", ");
		this.sql.append(paramString);
		if (paramBoolean)
			this.sql.append(" DESC");
		this.hasOrderBy = true;
	}

	public String[] getArgs() {
		return ((String[]) this.args.toArray(new String[this.args.size()]));
	}

	public String getSQL() {
		return this.sql.toString();
	}
}
