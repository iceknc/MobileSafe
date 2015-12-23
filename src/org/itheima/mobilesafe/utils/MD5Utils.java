package org.itheima.mobilesafe.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	public static String encode(String password) {
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			byte[] digest = digester.digest(password.getBytes());

			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				int c = b & 0xFF;
				String s = Integer.toHexString(c);
				if (s.length() == 1) {
					s = 0 + s;
				}
				sb.append(s);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encode(InputStream in) {
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			byte[] bys = new byte[8192];

			int len = -1;
			while ((len = in.read(bys)) !=-1) {
				digester.update(bys, 0, len);
			}
			byte[] digest = digester.digest();

			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				int c = b & 0xFF;
				String s = Integer.toHexString(c);

				if (s.length() == 1) {
					s = 0 + s;
				}

				sb.append(s);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			in = null;
		}
		return null;
	}
}
