package net.wangtu.android.common.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

/**
 * File操作工具
 * 
 * @author zhangxz
 * 
 */
public class FileUtil {
	public static final String BASE_DIR = Environment.getExternalStorageDirectory() + "/netstudy";

	/**
	 * 添加文件
	 * 
	 * @param path
	 * @throws Exception
	 */
	public static boolean saveFile(String content, String path)
			throws IOException {
		if (!canFile()) {
			return false;
		}

		File file = new File(BASE_DIR + path);
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
		}

		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			os.write(content.getBytes());
			return true;
		} catch (IOException e) {
			throw e;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	/**
	 * 获取文件
	 * 
	 * @param path
	 * @return
	 */
	public static File getFile(String path) {
		return new File(BASE_DIR + path);
	}

	/**
	 * 获取文件
	 * 
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	public static InputStream getInputStream(String path)
			throws FileNotFoundException {
		return new FileInputStream(BASE_DIR + path);
	}

	/**
	 * 删除文件夹
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {
		if (!canFile()) {
			return false;
		}
		File file = new File(BASE_DIR + path);
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteFile(path + "/" + children[i]);
				if (!success) {
					return false;
				}
			}
		}
		
		if (file.exists()) {
			return file.delete();
		}
		return true;
	}

	/**
	 * 创建文件夹
	 * 
	 * @param path
	 */
	public static boolean mkdir(String path) {
		if (!canFile()) {
			return false;
		}
		File file = new File(BASE_DIR + path);
		if (!file.exists()) {
			file.mkdirs();
		}

		return true;
	}

	/**
	 * 追加文件
	 * 
	 * @param data
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Boolean writeFileEnd(byte[] data, String path)
			throws IOException {
		if (!canFile()) {
			return false;
		}

		File file = new File(BASE_DIR + path);
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
		}

		// 写入文件
		RandomAccessFile randomFile = null;
		try {
			randomFile = new RandomAccessFile(BASE_DIR + path, "rw");
			randomFile.seek(randomFile.length());
			randomFile.write(data);
			return true;
		} catch (IOException e) {
			throw e;
		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				} catch (Exception e2) {

				}
			}
		}
	}

	/**
	 * 修改文件路径
	 * 
	 * @param file
	 * @param path
	 * @return
	 */
	public static File rename(File file, String path) {
		if (!canFile()) {
			return null;
		}

		File newFile = new File(BASE_DIR + path);
		if (newFile.exists()) {
			newFile.delete();
		}
		// 创建父文件夹
		newFile.getParentFile().mkdirs();
		file.renameTo(newFile);
		return newFile;
	}

	/**
	 * 获取问价大小
	 * 
	 * @param path
	 * @return
	 */
	public static long getFileSize(String path) {
		return new File(BASE_DIR + path).length();
	}
	
	/**
	 * 验证文件是否存在
	 * @param path
	 * @return
	 */
	public static boolean isExists(String path){
		return new File(BASE_DIR + path).exists();
	}

	/**
	 * 是否有读写权限
	 * 
	 * @return
	 */
	public static boolean canFile() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取文件日期
	 * 
	 * @param path
	 * @return
	 */
	public static long getFileDate(String path) {
		return new File(BASE_DIR + path).lastModified();
	}

	/**
	 * 获取目录下所有的文件
	 * 
	 * @param dir
	 * @return
	 */
	public static File[] getFiles(String dir) {
		return new File(BASE_DIR + dir).listFiles();
	}

	/**
	 * 获取目录下所有的文件大小
	 * 
	 * @param dir
	 * @return
	 */
	public static long getFilesSize(String dir) {
		File[] files = getFiles(dir);
		if (files == null) {
			return 0;
		}
		int dirSize = 0;
		for (int i = 0; i < files.length; i++) {
			dirSize += files[i].length();
		}
		return dirSize;
	}

	/**
	 * 以行为单位读取文件
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 * @throws IOException
	 */
	public static String readFileByLines(String path) throws IOException {
		if (!canFile()) {
			return null;
		}
		File file = new File(BASE_DIR + path);
		if (!file.exists()) {
			return null;
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			StringBuffer sb = new StringBuffer();
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString);
			}
			return sb.toString();
		} catch (IOException e) {
			throw e;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}

	/**
	 * 获取sd卡可用空间
	 * 
	 * @return
	 */
	@SuppressLint("NewApi") 
	@SuppressWarnings("deprecation")
	public static long getSdAvailableSpace() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
			return stat.getAvailableBlocks() * stat.getBlockSize();
		}
		return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
	}

	/**
	 * 计算存储目录下的文件大小，当文件总大小大于规定的缓存大小或者sdcard剩余空间小于缓存大小的时候删除40%最近没有被使用的文件
	 * 
	 * @param dirPath
	 */
	public static void calAndRemoveCache(String dirPath, long cacheSize) {
		File[] files = getFiles(dirPath);
		if (files == null || files.length <= 0) {
			return;
		}
		long dirSize = getFilesSize(dirPath);
		if (dirSize > cacheSize || cacheSize > getSdAvailableSpace()) {
			int removeFactor = (int) ((0.4 * files.length) + 1);

			// 根据文件的最后修改时间进行排序
			Arrays.sort(files, new Comparator<File>() {
				@Override
				public int compare(File lhs, File rhs) {
					if (lhs.lastModified() > rhs.lastModified()) {
						return 1;
					} else if (lhs.lastModified() == rhs.lastModified()) {
						return 0;
					} else {
						return -1;
					}
				}
			});

			// 删除40%最近没有被使用的文件
			for (int i = 0; i < removeFactor; i++) {
				files[i].delete();
			}
		}
	}

	/**
	 * 保存bitmap到本地
	 * @param bitmap
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String saveBitmap(Bitmap bitmap, String path) throws IOException {
		if (!canFile()) {
			return null;
		}

		if (ValidateUtil.isBlank(path)) {
			path = BASE_DIR + "/cache" + "/bit_" + System.currentTimeMillis() + ".jpeg";
		}
		File file = new File(path);
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
		}

		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			return file.getAbsolutePath();
		} catch (IOException e) {
			throw e;
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e2) {
				}
			}
		}
	}
}
