package net.wangtu.android.db.base.exception;

public class DbException extends Exception{
	private static final long serialVersionUID = 6212173608468273017L;

	public DbException() {
	}

	public DbException(String paramString) {
		super(paramString);
	}

	public DbException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public DbException(Throwable paramThrowable) {
		super(paramThrowable);
	}
}
