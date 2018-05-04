package com.tg.coloursteward.net;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DES {
	public static final String KEY_STR = "71D54718B0F6FFF07CE9";
	public static final String APP_ID = "ICEXCGJ0-5F89-4E17-BC44-7A0DB101B245";
	public static final String TOKEN = "AXPHrD48LRa8xYVkgV4c";
	public static final String APP_UUID = "33f09c7ca5e6491fbcdfd363cf58851e";
	public static final String APP_KEY = "01a2b3c4d5e6f7a8b9c0";
	public static final String APP_SECRET = "1a2b3c4d5e6f7a8b9c0d1a2b3c4d5e6f";
	/**
	 * 3DES解密
	 * @param message
	 * @return
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 * @throws InvalidKeyException 
	 * @throws InvalidKeySpecException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws Exception
	 */
	public static String Decrypt(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] bytesrc = convertHexString(message);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(KEY_STR.getBytes("GBK"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(KEY_STR.getBytes("GBK"));
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte);
	}

	/**
	 * 3DES加密
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static String Encrypt(String message) throws Exception {
		
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(KEY_STR.getBytes("GBK"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(KEY_STR.getBytes("GBK"));
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
		return toHexString(cipher.doFinal(message.getBytes("GBK"))).toUpperCase(Locale.ENGLISH);
	}

	/**
	 * 字符串转Byte数组
	 * @param ss
	 * @return
	 */
	private static byte[] convertHexString(String ss) {
		byte digest[] = new byte[ss.length() / 2];
		for (int i = 0; i < digest.length; i++) {
			String byteString = ss.substring(2 * i, 2 * i + 2);
			int byteValue = Integer.parseInt(byteString, 16);
			digest[i] = (byte) byteValue;
		}
		return digest;
	}

	/**
	 * Byte数组转字符串
	 * @param b
	 * @return
	 */
	private static String toHexString(byte b[]) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String plainText = Integer.toHexString(0xff & b[i]);
			if (plainText.length() < 2)
				plainText = "0" + plainText;
			hexString.append(plainText);
		}
		return hexString.toString();
	}
	
}
