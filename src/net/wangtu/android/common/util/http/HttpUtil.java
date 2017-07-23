package net.wangtu.android.common.util.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import net.wangtu.android.common.util.FileUtil;
import net.wangtu.android.common.util.JsonUtil;
import net.wangtu.android.common.util.UrlUtil;
import net.wangtu.android.common.util.ValidateUtil;

import org.json.JSONObject;
import android.os.Environment;

/**
 * 网络连接
 * @author Administrator
 *
 */
public class HttpUtil {
	/**
	 * 获取json对象
	 * @param url
	 * @return
	 * @throws Exception 
	 */
	public static JSONObject getJson(String url) throws Exception{
		String c = getString(url);
		if(ValidateUtil.isBlank(c)){
			return null;
		}
		
		try {
			return JsonUtil.parseJson(c);
		} catch (Exception e) {
			String msg = "url:" + url + "\n";
			msg += "content:" + c + "\n";
			msg += "error:" + e.getMessage();
			throw e;
		}
	}
	
	/**
	 * 获取字符串
	 * @param uri
	 * @return
	 * @throws Exception 
	 */
	public static String getString(String uri) throws Exception{
		return getString(uri,null);
	}
	
	public static String getString(String uri,String cookie) throws Exception{
		return getString(uri,cookie,null,null);
	}
	
	/**
	 * 获取字符串
	 * @param uri
	 * @param cookie
	 * @return
	 * @throws Exception 
	 */
	public static String getString(String uri,String cookie,Map<String, String> returnCookies,String userAgent) throws Exception{
		String result = "";
		BufferedReader reader = null;
		HttpURLConnection conn = null;
		String location = null;
		try{
			uri = UrlUtil.addParams(uri, "__t=" + new Date().getTime());
			URL url = new URL(uri);
			conn = (HttpURLConnection) url.openConnection();
			
			// 设置SSLSocketFoactory
			if (conn instanceof HttpsURLConnection) { // 是Https请求
			    SSLContext sslContext = SSLContextUtil.getSSLContext();
			    if (sslContext != null) {
			        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			        ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
			        ((HttpsURLConnection) conn).setHostnameVerifier(SSLContextUtil.hostnameVerifier);
			    }
			}
			
			if(!ValidateUtil.isBlank(cookie)){
				conn.setRequestProperty("Cookie", cookie); 
			}
			//conn.setChunkedStreamingMode(0);  
			//conn.setConnectTimeout(10000);// 10s内连不上就断开
			//conn.setRequestMethod("POST"); 
			conn.setInstanceFollowRedirects(true);
			conn.setUseCaches(false);
			if(!ValidateUtil.isBlank(userAgent)){
				conn.setRequestProperty("User-Agent","Android " + userAgent);
			}
			conn.connect();
			
			//验证连接状态
			int responseCode = conn.getResponseCode();
			if(responseCode >= 400){
				throw new HttpUtilException(responseCode + "报错！",responseCode);
			}
			if(responseCode == 301){
				location = conn.getHeaderField("Location");
			}
		    //conn.getHeaderField("Location");

			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);// 读字符串用的
			String inputLine = null;
			result = reader.readLine();
	        while (((inputLine = reader.readLine()) != null)) {
	            result += "\n" + inputLine;
	        }
	        
	        if(returnCookies != null){
	        	// 取到所用的Cookie
	        	Map<String, List<String>> headers = conn.getHeaderFields();
	        	if(headers != null && headers.size() > 0){
	        		List<String> cookies = headers.get("Set-Cookie");
	        		if(cookies != null && cookies.size() > 0){
	        			String[] c = null;
	        			int index = -1;
	        			for (String keyValue : cookies) {
	        				index =  keyValue.indexOf(";");
	        				if(index > -1){
	        					keyValue = keyValue.substring(0,index);
	        					c = keyValue.split("=");
	        					if(c != null && c.length > 1){
	        						returnCookies.put(c[0].trim(), c[1].trim());
	        					}
	        				}
	        			}
	        		}
	        	}
	        }
		}catch(Exception e){
			throw e;
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
			if(conn != null){
				conn.disconnect();
			}
		}
		
		if(!ValidateUtil.isBlank(location)){
			return getString(location,cookie,returnCookies,userAgent);
		}
		
		return result;
	}
	
	/**
	 * 下载文件
	 * @param uri
	 * @param relativePath “/”开头
	 * @return 下载后文件的路径
	 * @throws IOException 
	 */
	public static String downLoadFile(String uri,String relativePath) throws IOException{
		InputStream is = null;
		FileOutputStream fos = null;
		try{
            // 判断SD卡是否存在，并且是否具有读写权限
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                // 获得存储卡的路径
                String path = FileUtil.BASE_DIR + relativePath;
                uri = UrlUtil.addParams(uri, "__t=" + new Date().getTime());
                URL url = new URL(uri);
                // 创建连接
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                
                // 设置SSLSocketFoactory
    			if (conn instanceof HttpsURLConnection) { // 是Https请求
    			    SSLContext sslContext = SSLContextUtil.getSSLContext();
    			    if (sslContext != null) {
    			        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
    			        ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
    			        ((HttpsURLConnection) conn).setHostnameVerifier(SSLContextUtil.hostnameVerifier);
    			    }
    			}
                
                conn.connect();
                
                //验证状态
                int responseCode = conn.getResponseCode();
    			if(responseCode >= 400){
    				throw new HttpUtilException(responseCode + "报错！",responseCode);
    			}
    			
                // 创建输入流
                is = conn.getInputStream();

                File apkFile = new File(path);
                if(apkFile.exists()){
                	apkFile.delete();
                }
                File dir = apkFile.getParentFile();
                if (!dir.exists()){
                	dir.mkdirs();
                }
                fos = new FileOutputStream(apkFile);
                int numread = 0;
                // 缓存
                byte buf[] = new byte[1024];
                // 写入到文件中
                do{
                    numread = is.read(buf);
                    if (numread <= 0){
                        break;
                    }

                    // 写入文件
                    fos.write(buf, 0, numread);
                } while (true);
                return apkFile.getAbsolutePath();
            }
        }  catch (IOException e){
        	throw e;
        }finally{
        	if(fos!=null){
        		try {
        			fos.close();
				} catch (Exception e2) {
				}
        	}
        	if(is!=null){
        		try {
        			is.close();
				} catch (Exception e2) {
				}
        	}
        }
        
		return null;
	}
}
