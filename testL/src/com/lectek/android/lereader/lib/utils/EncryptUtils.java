package com.lectek.android.lereader.lib.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密工具类
 * 
 * @author limj
 * 
 */
public class EncryptUtils {
	private final static String AES_IV = "0102030405060708";
	// 向量
	private final static String iv = "01234567";
	// 加解密统一使用的编码方式
	private final static String encoding = "utf-8";
	/**
	 * 对字符串加密(32位)
	 * 
	 * @param str
	 * @return
	 */
	public static final String encodeByMd5(String str) {
		if (str == null) {
			return "";
		} else {
			String newstr = "";
			MessageDigest md5;
			try {
				md5 = MessageDigest.getInstance("MD5");
				md5.update(str.getBytes("utf-8"));
				byte resultData[] = md5.digest();
				newstr = convertToHexString(resultData);
				// BASE64Encoder base64en = new BASE64Encoder();
				// 加密后的字符串
				// newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return newstr;
		}
	}

	/**
	 * 3DES加密后Base64
	 * 
	 * @param source
	 * @param key
	 * @return
	 */
	public static String encryptBase643DES(String source, String key) {
		try {
			Key deskey = null;
			DESedeKeySpec spec = new DESedeKeySpec(key.getBytes());
			SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
			deskey = keyfactory.generateSecret(spec);

			Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
			IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
			byte[] encryptData = cipher.doFinal(source.getBytes(encoding));
			return Base64Util.encodeB64(encryptData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * byte[]转String
	 * 
	 * @param data
	 * @return
	 */
	public static String convertToHexString(byte data[]) {
		StringBuffer strBuffer = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			if ((0xff & data[i]) < 0x10) {
				strBuffer.append("0" + Integer.toHexString((0xFF & data[i])));
			} else {
				strBuffer.append(Integer.toHexString(0xFF & data[i]));
			}
		}
		return strBuffer.toString();
	}

	/**
	 * 流转byte[]
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] inputStreamToByte(InputStream is) throws IOException {
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		int ch;
		while ((ch = is.read()) != -1) {
			bytestream.write(ch);
		}
		byte data[] = bytestream.toByteArray();
		bytestream.close();
		return data;
	}

	public static byte[] decryptByAES(byte[] content, String password) {
		try {
			byte[] enCodeFormat = password.getBytes();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(AES_IV.getBytes()));
			byte[] result = cipher.doFinal(content);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream decryptByAES(InputStream content, String password) {
		try {
			byte[] enCodeFormat = password.getBytes();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(AES_IV.getBytes()));
			CipherInputStream cin = new CipherInputStream(content, cipher);
			return cin;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * AES加密字节数组
	 * @param byteContent 字节数组
	 * @param password 加密密码
	 * @return
	 */
	public static byte[] encryptByAES(byte[] byteContent, String password) {
		try {
			byte[] enCodeFormat = password.getBytes();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器 
            cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(AES_IV.getBytes()));;// 初始化 
			byte[] result = cipher.doFinal(byteContent);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		// md5
		/*
		 * String md5 = EncryptUtils.encodeByMd5("123456");
		 * System.out.println(md5);// e10adc3949ba59abbe56e057f20f883e // 3DES
		 * String encryByDES =
		 * EncryptUtils.encryptBase643DES("1.010000000011000000001",
		 * "lereaderV1.0secretkey123");// 24 System.out.println("digest:" +
		 * encryByDES);// digest:4kUGCk1X8zAZsoKe5oSvUfiaDcC3gK6H String
		 * decryByDES = EncryptUtils.decryptBase643DES(encryByDES,
		 * "lereaderV1.0secretkey123"); System.out.println(decryByDES); // REK
		 * String rek = EncryptUtils.encodeByMd5("1.010000000011000000001" +
		 * "lereaderV1.0secretkey123"); System.out.println("rek:" + rek);//
		 * rek:263edb7c516354a7b756deb74eb37d30 // secretKey String byteArr =
		 * EncryptUtils.encryptBase64AES("b8403b65f815",
		 * "263edb7c516354a7b756deb74eb37d30"); System.out.println("byteArr:" +
		 * byteArr);// ceiUxvcjxJI2IhI3Ez2XVQ== String secretKey = new
		 * String(EncryptUtils.decryptByAES((new
		 * BASE64Decoder()).decodeBuffer(byteArr),
		 * "263edb7c516354a7b756deb74eb37d30"), "utf-8");
		 * System.out.println("secretKey:" + secretKey);
		 */// b8403b65f815
		String key = "lereaderV1.0secretkey123";
		String encryString = EncryptUtils.encryptBase643DES("hello", key);
		System.out.println("encry== " + encryString);
//		System.out.println(EncryptUtils.decryptBase643DES(encryString, key));
		/*
		 * String fileName = "D:/social/test1.epub"; InputStream in = new
		 * FileInputStream(fileName); byte[] byteContent =
		 * FileUtils.InputStreamToByte(in); byte[] decryptContent =
		 * EncryptUtils.decryptByAES(byteContent, "b8403b65f815");
		 * FileUtils.getFile(decryptContent, "D:/social", "test11.epub");
		 */
		
		byte[] content = encryptByAES("1234".getBytes(), "1234567890abcdef");
    	content = decryptByAES(content, "1234567890abcdef");
    	System.out.println(new String(content));
	}
}
