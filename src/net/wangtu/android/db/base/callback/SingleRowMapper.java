package net.wangtu.android.db.base.callback;

import java.sql.SQLException;

import android.database.Cursor;

public abstract interface SingleRowMapper<T> {
	  public abstract T mapRow(Cursor paramCursor) throws SQLException;
}
