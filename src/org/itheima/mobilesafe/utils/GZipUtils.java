package org.itheima.mobilesafe.utils;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {

	/**
	 * 压缩文件
	 * 
	 * @param in
	 *            要压缩的文件路径
	 * @param out
	 *            输出的文件路径
	 * @throws Exception
	 */
	public static void zip(String in, String out) throws Exception {
		zip(new File(in), new File(out));
	}

	/**
	 * 压缩文件
	 * 
	 * @param in
	 *            要压缩的文件
	 * @param out
	 *            输出的zip文件
	 * @throws Exception
	 */
	public static void zip(File in, File out) throws Exception {
		zip(new FileInputStream(in), new FileOutputStream(out));
	}

	/**
	 * 压缩流
	 * 
	 * @param in
	 *            要压缩的输入流
	 * @param out
	 *            压缩后的输出流
	 * @throws IOException
	 */
	public static void zip(InputStream in, OutputStream out) throws IOException {
		GZIPOutputStream gzos = new GZIPOutputStream(out);

		byte[] bys = new byte[1024];
		int len = -1;
		try {
			while ((len = in.read(bys)) != -1) {
				gzos.write(bys, 0, len);
				gzos.flush();
			}
		} finally {
			close(in);
			close(gzos);
		}
	}

	/**
	 * 解压缩zip文件
	 * 
	 * @param in
	 *            输入的文件路径
	 * @param out
	 *            输出的文件路径
	 * @throws IOException
	 */
	public static void unZip(String in, String out) throws IOException {
		unZip(new File(in), new File(out));
	}

	/**
	 * 解压缩zip文件
	 * 
	 * @param in
	 *            输入的zip文件
	 * @param out
	 *            输出的文件
	 * @throws IOException
	 */
	public static void unZip(File in, File out) throws IOException {
		unZip(new FileInputStream(in), new FileOutputStream(out));
	}

	/**
	 * 解压缩zip文件
	 * 
	 * @param in
	 *            输入的zip文件流
	 * @param out
	 *            输出的标准文件
	 * @throws IOException
	 */
	public static void unZip(InputStream in, OutputStream out)
			throws IOException {
		GZIPInputStream gzis = new GZIPInputStream(in);

		byte[] bys = new byte[1024];
		int len = -1;
		try {
			while ((len = gzis.read(bys)) != -1) {
				out.write(bys, 0, len);
				out.flush();
			}
		} finally {
			close(out);
			close(gzis);
		}
	}

	private static void close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			io = null;
		}
	}
}
