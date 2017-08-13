package net.wangtu.android.db.base.callback;

import java.sql.SQLException;

import android.database.Cursor;

public abstract interface MapRowMapper<K, V> {
	public abstract K mapRowKey(Cursor paramCursor, int paramInt) throws SQLException;
	public abstract V mapRowValue(Cursor paramCursor, int paramInt) throws SQLException;
}
