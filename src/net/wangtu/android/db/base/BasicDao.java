package net.wangtu.android.db.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.db.base.callback.MapRowMapper;
import net.wangtu.android.db.base.callback.MultiRowMapper;
import net.wangtu.android.db.base.callback.SingleRowMapper;
import net.wangtu.android.db.base.helper.DbUtils;
import net.wangtu.android.db.base.helper.SqlUtils;
import net.wangtu.android.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class BasicDao {
	public static boolean DEBUG = false;
	protected static final ReentrantLock lock = new ReentrantLock();

	public SQLiteDatabase openSQLiteDatabase() {
		return DBHelper.getInstance().getWritableDatabase();
	}

	public void closeSQLiteDatabase() {
		DBHelper.getInstance().close();
	}

	protected Object execute(DoGrammarInterface paramDoGrammarInterface) {
		if (null == paramDoGrammarInterface){
			return null;
		}
		Object localObject1 = null;
		lock.lock();
		try {
			SQLiteDatabase localSQLiteDatabase = openSQLiteDatabase();
			localObject1 = paramDoGrammarInterface.doGrammar(localSQLiteDatabase);
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			closeSQLiteDatabase();
			lock.unlock();
		}
		return localObject1;
	}

	protected long insert(String table, String nullColumns, ContentValues paramContentValues) {
		long l = -1914775121537531904L;
		lock.lock();
		try {
			l = openSQLiteDatabase().insert(table, nullColumns,paramContentValues);
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			closeSQLiteDatabase();
			lock.unlock();
		}
		return l;
	}

	/**
	 * 根据字段保存
	 * @param table
	 * @param nullColumns 为空的字段
	 * @param paramList 不为空的字段
	 * @return
	 */
	protected long insertBatch(String table, String nullColumns, List<ContentValues> paramList) {
		long l1 = -1914775121537531904L;
		lock.lock();
		SQLiteDatabase localSQLiteDatabase = null;
		try {
			localSQLiteDatabase = openSQLiteDatabase();
			localSQLiteDatabase.beginTransaction();
			int i = 0;
			int j = paramList.size();
			while (i < j) {
				long l2 = localSQLiteDatabase.insert(table,nullColumns, (ContentValues) paramList.get(i));
				l1 += l2;
				++i;
			}
			localSQLiteDatabase.setTransactionSuccessful();
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			if (null != localSQLiteDatabase)
				localSQLiteDatabase.endTransaction();
			closeSQLiteDatabase();
			lock.unlock();
		}
		return l1;
	}

	/**
	 * 根据表名更新
	 * @param table
	 * @param params
	 * @param where
	 * @param whereArgs
	 * @return
	 */
	protected int update(String table, ContentValues params,String where, String[] whereArgs) {
		int i = 0;
		lock.lock();
		try {
			i = openSQLiteDatabase().update(table, params,
					where, whereArgs);
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			closeSQLiteDatabase();
			lock.unlock();
		}
		return i;
	}

	/**
	 * 批量更新
	 * @param table
	 * @param paramList
	 * @param where
	 * @param whereArgs
	 * @return
	 */
	protected int updateBatch(String table, List<ContentValues> paramList, String where,List<String[]> whereArgs) {
		int i = 0;
		lock.lock();
		SQLiteDatabase localSQLiteDatabase = null;
		try {
			localSQLiteDatabase = openSQLiteDatabase();
			localSQLiteDatabase.beginTransaction();
			int j = 0;
			int k = paramList.size();
			while (j < k) {
				int l = localSQLiteDatabase.update(table,(ContentValues) paramList.get(j), where, (String[]) whereArgs.get(j));
				i += l;
				++j;
			}
			localSQLiteDatabase.setTransactionSuccessful();
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			if (null != localSQLiteDatabase)
				localSQLiteDatabase.endTransaction();
			closeSQLiteDatabase();
			lock.unlock();
		}
		return i;
	}

	/**
	 * 删除行
	 * @param table
	 * @param where
	 * @param whereArgs
	 * @return
	 */
	protected int delete(String table, String where,String[] whereArgs) {
		int i = 0;
		lock.lock();
		try {
			i = openSQLiteDatabase().delete(table, where,whereArgs);
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			closeSQLiteDatabase();
			lock.unlock();
		}
		return i;
	}

	/**
	 * 批量删除
	 * @param table 表名
	 * @param whereClause 删除条件字段
	 * @param paramList 删除条件字段值数组
	 * @return
	 */
	protected int deleteBatch(String table, String whereClause, List<String[]> paramList) {
		int i = 0;
		lock.lock();
		SQLiteDatabase localSQLiteDatabase = null;
		try {
			localSQLiteDatabase = openSQLiteDatabase();
			localSQLiteDatabase.beginTransaction();
			int j = 0;
			int k = paramList.size();
			while (j < k) {
				int l = localSQLiteDatabase.delete(table, whereClause,(String[]) paramList.get(j));
				i += l;
				++j;
			}
			localSQLiteDatabase.setTransactionSuccessful();
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			if (null != localSQLiteDatabase)
				localSQLiteDatabase.endTransaction();
			closeSQLiteDatabase();
			lock.unlock();
		}
		return i;
	}

	/**
	 * 保存
	 * @param sql
	 */
	protected void execSQL(String sql) {
		lock.lock();
		try {
			debugSql(sql, null);
			openSQLiteDatabase().execSQL(sql);
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			closeSQLiteDatabase();
			lock.unlock();
		}
	}

	/**
	 * 保存
	 * @param sql
	 * @param params
	 */
	protected void execSQL(String sql, Object[] params) {
		lock.lock();
		try {
			debugSql(sql, null);
			openSQLiteDatabase().execSQL(sql, params);
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			closeSQLiteDatabase();
			lock.unlock();
		}
	}

	/**
	 * 批量保存
	 * @param sql
	 * @param paramList
	 */
	protected void execSQLBatch(String sql, List<Object[]> paramList) {
		lock.lock();
		SQLiteDatabase localSQLiteDatabase = null;
		try {
			localSQLiteDatabase = openSQLiteDatabase();
			localSQLiteDatabase.beginTransaction();
			int i = 0;
			int j = paramList.size();
			while (i < j) {
				Object[] params = (Object[]) paramList.get(i);
				debugSql(sql, params);
				localSQLiteDatabase.execSQL(sql, params);
				++i;
			}
			localSQLiteDatabase.setTransactionSuccessful();
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			if (null != localSQLiteDatabase)
				localSQLiteDatabase.endTransaction();
			closeSQLiteDatabase();
			lock.unlock();
		}
	}

	/**
	 * 查询数组
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param paramMultiRowMapper
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T> List<T> query(String table,String[] columns, String selection,String[] selectionArgs, String groupBy,String having, String orderBy,MultiRowMapper<T> paramMultiRowMapper) {
		ArrayList localArrayList = new ArrayList();
		lock.lock();
		Cursor localCursor = null;
		try {
			localCursor = openSQLiteDatabase().query(table,columns, selection, selectionArgs, groupBy, having, orderBy);
			for (int i = 0; localCursor.moveToNext(); ++i) {
				Object localObject1 = paramMultiRowMapper.mapRow(localCursor, i);
				localArrayList.add(localObject1);
			}
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			closeCursor(localCursor);
			closeSQLiteDatabase();
			lock.unlock();
		}
		return localArrayList;
	}

	/**
	 * 查询数组
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T> List<T> query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,String having, String orderBy, String limit,MultiRowMapper<T> rowMapper) {
		ArrayList localArrayList = new ArrayList();
		lock.lock();
		Cursor localCursor = null;
		try {
			localCursor = openSQLiteDatabase().query(table,columns, selection, selectionArgs,groupBy, having, orderBy, limit);
			for (int i = 0; localCursor.moveToNext(); ++i) {
				Object localObject1 = rowMapper.mapRow(localCursor, i);
				localArrayList.add(localObject1);
			}
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			closeCursor(localCursor);
			closeSQLiteDatabase();
			lock.unlock();
		}
		return localArrayList;
	}

	/**
	 * 
	 * @param distinct 是否distinct
	 * @param table 表名
	 * @param columns 要查询的列
	 * @param selection 查询条件
	 * @param selectionArgs 查询条件参数
	 * @param groupBy 分组
	 * @param having 分组条件
	 * @param orderBy 排序
	 * @param limit 显示行
	 * @param rowMapper
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T> List<T> query(boolean distinct,String table, String[] columns, String selection, String[] selectionArgs, String groupBy,String having, String orderBy, String limit, MultiRowMapper<T> rowMapper) {
		ArrayList arrayList = new ArrayList();
		lock.lock();
		Cursor localCursor = null;
		try {
			localCursor = openSQLiteDatabase().query(distinct,table, columns, selection,selectionArgs, groupBy, having,orderBy, limit);
			for (int i = 0; localCursor.moveToNext(); ++i) {
				Object bean = rowMapper.mapRow(localCursor, i);
				arrayList.add(bean);
			}
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			closeCursor(localCursor);
			closeSQLiteDatabase();
			lock.unlock();
		}
		return arrayList;
	}

	/**
	 * 更新
	 * @param sql
	 */
	protected void update(String sql) {
		lock.lock();
		try {
			debugSql(sql, null);
			openSQLiteDatabase().execSQL(sql);
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			closeSQLiteDatabase();
			lock.unlock();
		}
	}

	/**
	 * 更新
	 * @param sql
	 * @param params
	 */
	protected void update(String sql, Object[] params) {
		if (null == params) {
			update(sql);
		} else {
			lock.lock();
			try {
				debugSql(sql, params);
				openSQLiteDatabase().execSQL(sql, params);
			} catch (Exception localException) {
				LogUtil.error(localException, BasicDao.class);
			} finally {
				closeSQLiteDatabase();
				lock.unlock();
			}
		}
	}

	/**
	 * 批量更新
	 * @param sql
	 * @param paramList
	 */
	@SuppressWarnings("rawtypes")
	protected void updateBatch(String sql, List<Object[]> paramList) {
		if (null == paramList){
			return;
		}
		lock.lock();
		SQLiteDatabase localSQLiteDatabase = null;
		try {
			localSQLiteDatabase = openSQLiteDatabase();
			localSQLiteDatabase.beginTransaction();
			Iterator localIterator = paramList.iterator();
			while (localIterator.hasNext()) {
				Object[] arrayOfObject = (Object[]) localIterator.next();
				debugSql(sql, arrayOfObject);
				localSQLiteDatabase.execSQL(sql, arrayOfObject);
			}
			localSQLiteDatabase.setTransactionSuccessful();
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			if (null != localSQLiteDatabase){
				localSQLiteDatabase.endTransaction();
			}
			closeSQLiteDatabase();
			lock.unlock();
		}
	}

	/**
	 * 查数组
	 * @param sql
	 * @param params
	 * @param paramMultiRowMapper
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T> List<T> query(String sql, String[] params, MultiRowMapper<T> paramMultiRowMapper) {
		ArrayList beanList = new ArrayList();
		lock.lock();
		debugSql(sql, params);
		Cursor localCursor = null;
		try {
			localCursor = openSQLiteDatabase().rawQuery(sql,params);
			for (int i = 0; localCursor.moveToNext(); ++i) {
				Object bean = paramMultiRowMapper.mapRow(localCursor, i);
				beanList.add(bean);
			}
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			closeCursor(localCursor);
			closeSQLiteDatabase();
			lock.unlock();
		}
		return beanList;
	}

	/**
	 * 查对象
	 * @param sql
	 * @param params
	 * @param paramSingleRowMapper
	 * @return
	 */
	protected <T> T query(String sql, String[] params, SingleRowMapper<T> paramSingleRowMapper) {
		T value = null;
		lock.lock();
		debugSql(sql, params);
		Cursor localCursor = null;
		try {
			localCursor = openSQLiteDatabase().rawQuery(sql,params);
			if (localCursor.moveToNext()){
				value = paramSingleRowMapper.mapRow(localCursor);
			}
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			closeCursor(localCursor);
			closeSQLiteDatabase();
			lock.unlock();
		}
		return value;
	}

	/**
	 * 返回map
	 * @param sql
	 * @param params
	 * @param paramMapRowMapper
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <K, V> Map<K, V> query(String sql, String[] params, MapRowMapper<K, V> paramMapRowMapper) {
		HashMap localHashMap = new HashMap();
		lock.lock();
		debugSql(sql, params);
		Cursor localCursor = null;
		try {
			localCursor = openSQLiteDatabase().rawQuery(sql,params);
			for (int i = 0; localCursor.moveToNext(); ++i) {
				Object key = paramMapRowMapper.mapRowKey(localCursor,i);
				Object value = paramMapRowMapper.mapRowValue(localCursor, i);
				localHashMap.put(key, value);
			}
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			closeCursor(localCursor);
			closeSQLiteDatabase();
			lock.unlock();
		}
		return localHashMap;
	}

	/**
	 * in查询，返回数组
	 * @param prefixSql
	 * @param prefixParams
	 * @param inParams
	 * @param endSql
	 * @param paramMultiRowMapper
	 * @return
	 */
	protected <T> List<T> queryForInSQL(String prefixSql,String[] prefixParams, String[] inParams,String endSql, MultiRowMapper<T> paramMultiRowMapper) {
		if (null == prefixParams){
			prefixParams = new String[0];
		}
		StringBuilder sqlStringBuilder = new StringBuilder();
		sqlStringBuilder.append(prefixSql).append(SqlUtils.getInSQL(inParams.length));
		if (!(TextUtils.isEmpty(endSql))){
			sqlStringBuilder.append(endSql);
		}
		String[] params = new String[inParams.length + prefixParams.length];
		System.arraycopy(prefixParams, 0, params, 0,prefixParams.length);
		System.arraycopy(inParams, 0, params,prefixParams.length, inParams.length);
		return query(sqlStringBuilder.toString(), params, paramMultiRowMapper);
	}

	/**
	 * in查询返回map
	 * @param prefixSql
	 * @param prefixParams
	 * @param inParams
	 * @param endSql
	 * @param rowMapper
	 * @return
	 */
	protected <K, V> Map<K, V> queryForInSQL(String prefixSql,String[] prefixParams, String[] inParams, String endSql, MapRowMapper<K, V> rowMapper) {
		if (null == prefixParams){
			prefixParams = new String[0];
		}
		StringBuilder sqlStringBuilder = new StringBuilder();
		sqlStringBuilder.append(prefixSql).append(SqlUtils.getInSQL(inParams.length));
		if (!(TextUtils.isEmpty(endSql))){
			sqlStringBuilder.append(endSql);
		}
		String[] params = new String[inParams.length + prefixParams.length];
		System.arraycopy(prefixParams, 0, params, 0,prefixParams.length);
		System.arraycopy(inParams, 0, params,prefixParams.length, inParams.length);
		return query(sqlStringBuilder.toString(), params,rowMapper);
	}

	/**
	 * 开发模式下打印sql
	 * @param paramString
	 * @param paramArrayOfObject
	 */
	private void debugSql(String paramString, Object[] paramArrayOfObject) {
		if (DEBUG){
			LogUtil.debug(SqlUtils.getSQL(paramString, paramArrayOfObject));
		}
	}

	/**
	 * 关闭游标
	 * @param paramCursor
	 */
	private void closeCursor(Cursor paramCursor) {
		if ((null != paramCursor) && (!(paramCursor.isClosed())))
			paramCursor.close();
	}

	/**
	 * 根据param的field自动映射table，并保存 
	 * @param table 表名
	 * @param params 类对象数组
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public long insert(String table, Object[] params) {
		if (ValidateUtil.isBlank(table) || ValidateUtil.isEmpty(params)){
			return -1914779828821688320L;
		}
		lock.lock();
		SQLiteDatabase localSQLiteDatabase = null;
		long l = -1914775121537531904L;
		try {
			localSQLiteDatabase = openSQLiteDatabase();
			Set tableColumnsSet = DbUtils.getTableAllColumns(localSQLiteDatabase,table);
			localSQLiteDatabase.beginTransaction();
			for (int j = 0; j < params.length; ++j) {
				Object param = params[j];
				ContentValues localContentValues = DbUtils.getWantToInsertValues(param, tableColumnsSet);
				l = localSQLiteDatabase.insert(table, null, localContentValues);
			}
			localSQLiteDatabase.setTransactionSuccessful();
		} catch (Exception localException) {
			LogUtil.error(localException, BasicDao.class);
		} finally {
			if (null != localSQLiteDatabase){
				localSQLiteDatabase.endTransaction();
			}
			closeSQLiteDatabase();
			lock.unlock();
		}
		return l;
	}

	public static abstract interface DoGrammarInterface {
		public abstract Object doGrammar(SQLiteDatabase paramSQLiteDatabase);
	}
}
