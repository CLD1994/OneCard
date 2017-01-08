package com.zhuoli.onecard.data.utils;

import com.orhanobut.logger.Logger;
import com.zhuoli.onecard.utils.CryptoUtils;
import com.zhuoli.onecard.utils.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import okio.ByteString;

/**
 * author: CLD
 * created on: 2016/10/24 0024 下午 2:59
 * description:
 */

public class OneCardSocket{
	/** 地址 */
	private final String ip = "10.10.100.254";
	/** 端口 */
	private final int port = 8899;
	/** 超时时间(ms) */
	private final int timeout = 5000;
	/** 套接字 */
	private Socket socket = null;
	/** 输出流 */
	private OutputStream output = null;
	/** 输入流 */
	private InputStream input = null;

	/**
	 * 连接函数
	 */
	public void connect() throws IOException{

		// 连接网络
		socket = new Socket(ip, port);
		socket.setSoTimeout(timeout);
		socket.setKeepAlive(true);
		output = socket.getOutputStream();
		input = socket.getInputStream();
	}

	/**
	 * 关闭连接
	 */
	public void close() {
		IOUtils.closeQuietly(output);
		IOUtils.closeQuietly(input);
		IOUtils.closeQuietly(socket);
	}

	public void write(byte[] data) throws IOException {
		connect();
		output.write(data);
	}

	public byte[] read() throws IOException {
		byte[] bytes = new byte[3];
		if (input.read(bytes) != -1){
			ByteString temp = ByteString.of(bytes);
			String hex = CryptoUtils.HEX.hexToDecString(temp.substring(1,3).hex());
			int length = Integer.valueOf(hex);
			byte[] bytes1 = new byte[length];
			if (input.read(bytes1,3,length-3) != -1){
				bytes1[0] = bytes[0];
				bytes1[1] = bytes[1];
				bytes1[2] = bytes[2];
				Logger.d(ByteString.of(bytes1).hex());
				close();
				return bytes1;
			}else {
				close();
				return null;
			}
		}else {
			close();
			return null;
		}

	}
}
