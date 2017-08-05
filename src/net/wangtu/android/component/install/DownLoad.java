package net.wangtu.android.component.install;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import android.os.Environment;

import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.util.ContextUtil;
import net.wangtu.android.util.LogUtil;

/**
 * apk下载
 * @author zhangxz
 *
 */
public class DownLoad {
	
	/**
	 * 下载apk
	 * @param uri
	 * @param event
	 * @return
	 */
	public static String downLoadApk(String uri,DownLoadEvent event){
		if(ValidateUtil.isBlank(uri)){
			return null;
		}
		
		InputStream is = null;
		FileOutputStream fos = null;
		try{
            // 判断SD卡是否存在，并且是否具有读写权限
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				event.error(DownLoadEvent.NO_SD_AUTHORIZATION, "没有读写权限！");
				return null;
			}
			
			// 开始下载
            if(event != null){
            	event.start();
            }
			
            // 下载路径
            URL url = new URL(uri);
            // 创建连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            
            // 404报错捕获
            int responseCode = conn.getResponseCode();
			if(responseCode >= 400){
				event.error(DownLoadEvent.ERROR, "下载失败," + responseCode + "报错！");
				return null;
			}
            
            //获取文件名称
            String fileName = conn.getURL().getFile();
            if(fileName != null && fileName.indexOf(".apk") > -1){
            	int begin = Math.max(fileName.lastIndexOf("/"), fileName.lastIndexOf("\\"));
            	fileName = fileName.substring(begin);
            }else{
            	fileName = UUID.randomUUID().toString() + ".apk";
            }
            
            // 获取文件大小
            int size = conn.getContentLength();
            // 创建输入流
            is = conn.getInputStream();
            
            //获取缓存目录
            File cacheFile = ContextUtil.getContext().getExternalCacheDir();
            if(cacheFile ==null){
            	cacheFile = ContextUtil.getContext().getExternalFilesDir(null);
            	if(cacheFile == null){
            		cacheFile = Environment.getExternalStorageDirectory();
            		if(cacheFile == null){
            			event.error(DownLoadEvent.ERROR, "下载目录不存在！");
            			return null;
            		}
            	}
            	// 判断文件目录是否存在
                if (!cacheFile.exists()){
                    cacheFile.mkdir();
                }
            }
            File apkFile = new File(cacheFile, fileName);
            fos = new FileOutputStream(apkFile);
            int index = 0;
            int numread = 0;
            byte buf[] = new byte[1024];
            
            // 写入到文件中
            do{
                numread = is.read(buf);
                index += numread;
                
                if(event != null){
                	// 下载中
                	event.loading((int) (((float) index / size) * 100));
                	
                	// 下载完成
                	if (numread <= 0){
                		event.success(apkFile.getAbsolutePath());
                		break;
                	}
                }
                
                if (numread <= 0){
                	break;
                }
                
                // 写入文件
            	fos.write(buf, 0, numread);
            } while (true);
            
            return apkFile.getAbsolutePath();
        }catch (IOException e){
        	LogUtil.error(e,DownLoad.class);
        	event.error(DownLoadEvent.ERROR, "下载失败！");
        }finally{
        	if(fos != null){
        		try{
        			fos.close();
        		}catch(Exception e){
        			
        		}
        	}
        	
        	if(is != null){
        		try{
        			is.close();
        		}catch(Exception e){
        			
        		}
        	}
        }
        
		return null;
	}
	
	/**
	 * 下载事件
	 * @author zhangxz
	 *
	 */
	public interface DownLoadEvent{
		
		/**
		 * 程序报错
		 */
		public final static int ERROR = 0;
		
		/**
		 * 没有sd卡读写权限
		 */
		public final static int NO_SD_AUTHORIZATION = 1;
		
		/**
		 * 开始下载
		 */
		public void start();
		
		/**
		 * 下载中
		 * @param progress
		 */
		public void loading(int progress);
		
		/**
		 * 下载结束
		 * @param path
		 */
		public void success(String path);
		
		/**
		 * 下载出错
		 * @param status
		 * @param msg
		 */
		public void error(int status, String msg);
	}
}
