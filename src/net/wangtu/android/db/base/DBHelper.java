package net.wangtu.android.db.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import net.wangtu.android.util.LogUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DBHelper extends SQLiteOpenHelper {
	private static DBHelper instance;
	private static int databaseVersion = -1;
	public static String databaseName = "netstudy";
	private final Context context;

	/**
	 * 初始化数据库
	 * @param paramInt
	 * @param paramString
	 * @param paramContext
	 */
	public static void init(int paramInt, String paramString,Context paramContext) {
		databaseVersion = paramInt;
		databaseName = paramString;
		instance = new DBHelper(paramContext);
	}

	public DBHelper(Context context) {
		super(context, databaseName, null, databaseVersion);
		this.context = context;
	}

	public static DBHelper getInstance() {
		return instance;
	}

	public void onCreate(SQLiteDatabase database) {
		if (databaseVersion <= 0){
			throw new RuntimeException("DBHelper.DATABASE_VERSION必须初始化，请调用init方法初始化");
		}
		LogUtil.debug("开始初始化数据库...");
		executeSqlFromFile(database, getSqlName(1));
		if (databaseVersion > 1){
			onUpgrade(database, 1, databaseVersion);
		}
	}

	/**
	 * 根据版本号依次升级
	 * currentTableVersion:当前版本号
	 * lastTableVersion ： 最新版本号
	 */
	public void onUpgrade(SQLiteDatabase database, int currentTableVersion,int lastTableVersion) {
		if (databaseVersion <= 0){
			throw new RuntimeException("DBHelper.DATABASE_VERSION必须初始化，请调用init方法");
		}
		if (lastTableVersion <= currentTableVersion){
			return;
		}
		LogUtil.info(new StringBuilder().append("updating dababase from version ").append(currentTableVersion).append("to version ").append(lastTableVersion).toString());
		for (int i = currentTableVersion + 1; i <= lastTableVersion; ++i){
			executeSqlFromFile(database,getSqlName(i));
		}
	}

	/**
	 * 执行文件中的语法
	 * 格式：--表示注释，go表示分行，--和go之前的行是语法
	 * @param database
	 * @param path
	 */
	private void executeSqlFromFile(SQLiteDatabase database,String path) {
		LogUtil.info(new StringBuilder().append("开始执行在assets中的数据库文件：").append(path).toString());
		try {
			BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(this.context.getAssets().open(path)));
			String str = null;
			StringBuilder localStringBuilder = new StringBuilder();
			a:while (true) {
				while (true) {
					while (true) {
						if ((str = localBufferedReader.readLine()) == null){
							break a;
						}
						if (!(str.startsWith("--"))){
							break;
						}
					}
					if (!(str.trim().equalsIgnoreCase("go"))){
						break;
					}
					if (!(TextUtils.isEmpty(localStringBuilder.toString()))){
						database.execSQL(localStringBuilder.toString());
					}
					localStringBuilder = new StringBuilder();
				}
				localStringBuilder.append(str);
			}
			
			if (!(TextUtils.isEmpty(localStringBuilder.toString()))){
				database.execSQL(localStringBuilder.toString());
			}
		} catch (Exception localException) {
			LogUtil.error(localException, DBHelper.class);
			throw new RuntimeException(localException);
		}
		LogUtil.debug(new StringBuilder().append("成功执行在assets中的数据库文件：").append(path).toString());
	}
	
	private java.text.DecimalFormat format = new java.text.DecimalFormat("000");
	private String getSqlName(int version){
		return "db/db_${db.version}.sql".replace("${db.version}",format.format(version));
	}
}
