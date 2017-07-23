package net.wangtu.android.common.util.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import net.wangtu.android.common.util.ValidateUtil;

/**
 * 网络连接
 * @author Administrator
 *
 */
public class FormUtil {
	protected static final String boundary = "------------cH2ei4Ij5cH2gL6KM7Ef1Ij5ei4gL6";
	
	/**
	 * 表单提交
	 * @param uri
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public static String post(String uri,Map<String, String> params) throws Exception{
		String result = "";
		BufferedReader reader = null;
		HttpURLConnection conn = null;
		OutputStream os = null;
		try{
			//连接属性设置
			URL url = new URL(uri);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(30000);// 10s内连不上就断开
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Accept", "*/*");
			conn.connect();
			
			//发送的信息
			if(params != null && params.size() > 0){
				StringBuffer msg = new StringBuffer();
				Set<String> keys = params.keySet();
				for (String key : keys) {
					msg.append(URLEncoder.encode(key, "UTF-8"));
					msg.append("=");
					if(params.get(key) != null){
						msg.append(URLEncoder.encode(params.get(key), "UTF-8"));
					}
					msg.append("&");
				}
				msg.deleteCharAt(msg.length() -1);
				os = conn.getOutputStream();
				byte[] data =msg.toString().getBytes();
				os.write(data, 0, data.length);
			}
			
			//验证状态
            int responseCode = conn.getResponseCode();
			if(responseCode >= 400){
				throw new IOException(responseCode + "报错！");
			}
			
            //返回的信息
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);// 读字符串用的
			String inputLine = null;
			result = reader.readLine();
	        while (((inputLine = reader.readLine()) != null)) {
	            result += "\n" + inputLine;
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
			if(os != null){
				try {
					os.close();
				} catch (IOException e1) {
				}
			}
			if(conn != null){
				conn.disconnect();
			}
		}
		
		return result;
	}
	
	/**
	 * 上传文件
	 * @param uri
	 * @param file
	 * @throws IOException 
	 */
	public static boolean upload(String uri,File file,String cookies) throws IOException{
		InputStream is = null;
		OutputStream os = null;
        HttpURLConnection conn = null;

        try {
            // 获取链接
            URL url = new URL(uri);
            conn = (HttpURLConnection) url.openConnection();

            // 设置必要参数
            if(!ValidateUtil.isBlank(cookies)){
				conn.setRequestProperty("Cookie", cookies); 
			}
            conn.setConnectTimeout(30000);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setRequestProperty("Accept", "*/*");
            conn.connect();
            os = conn.getOutputStream();
            byte[] data = null;
            
            // 文件上传前
            StringBuffer contentBuffer = new StringBuffer();
            contentBuffer.append("--" + boundary + "\r\n");
            contentBuffer.append("Content-Disposition: form-data; name=\"file\"; filename=\"apk.log\"\r\n");
            contentBuffer.append("Content-Type:application/octet-stream\r\n");
            contentBuffer.append("\r\n");
            data = contentBuffer.toString().getBytes();
            os.write(data, 0, data.length);

            // 文件上传中
            is = new FileInputStream(file);
            data = new byte[4096];
            int temp = 0;
            while ((temp = is.read(data)) != -1) {
                os.write(data, 0, temp);
            }

            // 文件上传后
            contentBuffer = new StringBuffer();
            contentBuffer.append("\r\n");
            contentBuffer.append("--" + boundary + "--" + "\r\n");
            contentBuffer.append("\r\n");
            data = contentBuffer.toString().getBytes();
            os.write(data, 0, data.length);
            os.flush();
            
            // 返回信息的处理
            StringBuilder text = new StringBuilder();
            InputStream is1 = null;
            InputStreamReader sr = null;
            BufferedReader br = null;
            int code = conn.getResponseCode();
            try {
                is1 = code >= 400 ? conn.getErrorStream() : conn.getInputStream();

                sr = new InputStreamReader(is1);
                br = new BufferedReader(sr);

                char[] chars = new char[4096];
                int length = 0;

                while ((length = br.read(chars)) != -1) {
                    text.append(chars, 0, length);
                }
            }
            finally {
                if (br != null) {
                    br.close();
                    br = null;
                }
                if (sr != null) {
                    sr.close();
                    sr = null;
                }
                if (is1 != null) {
                    is1.close();
                    is1 = null;
                }
            }
            return true;

        }
        catch (IOException e) {
        	throw e;
        }  finally {

            if (os != null) {
                os.close();
                os = null;
            }
            if (is != null) {
                is.close();
            }
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
	}
	
	/**
	 * 上传文件
	 * @param uri
	 * @param file
	 * @throws IOException 
	 */
	public static boolean upload(String uri,File file) throws IOException{
		return upload(uri, file, null);
	}
}
