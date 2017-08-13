package net.wangtu.android.db.base.callback;

import java.sql.SQLException;

import android.database.Cursor;

public abstract interface MultiRowMapper<T> {
	  public abstract T mapRow(Cursor paramCursor, int paramInt) throws SQLException;
}
