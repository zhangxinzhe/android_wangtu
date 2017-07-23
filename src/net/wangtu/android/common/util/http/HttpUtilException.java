package net.wangtu.android.common.util.http;

import java.io.IOException;

/**
 * 网络请求出错
 * @author Administrator
 *
 */
public class HttpUtilException extends IOException{
	private static final long serialVersionUID = 7971625901042593160L;
	
	public static final int HttpUtilStatus_200 = 200;                //访问成功
	public static final int HttpUtilStatus_302 = 302;                //重定向
	public static final int HttpUtilStatus_404 = 404;                //地址不存在
	public static final int HttpUtilStatus_500 = 500;                //服务器报错
	public static final int HttpUtilStatus_502 = 502;                //网关错误
	public static final int HttpUtilStatus_503 = 503;                //服务器不可用（正在维护或者暂停）
	public static final int HttpUtilStatus_Timeout = 1001;           //访问超时
	public static final int HttpUtilStatus_ConnectionLost= 1002;     //不能上网
	public static final int HttpUtilStatus_DomainFail= 1003;         //不能上网
	public static final int HttpUtilStatus_JsonError = 1004;         //json解析报错
	public static final int HttpUtilStatus_NoWifi = 1005;            //网络未打开
	public static final int HttpUtilStatus_Undefined = 1006;          //未知错误
	
	
	private int code;//http状态码
	
	public HttpUtilException(String msg,int code){
		super(msg);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
