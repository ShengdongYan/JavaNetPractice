package com.bjsxt.net.tcp.chat.demo04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 创建客户端: 发送数据+接收数据
 * 写出数据：输出流
 * 读取数据：输入流
 * 
    输入流 与输出流 在同一个线程内 应该 独立处理，彼此独立
    
    加入名称
    
    
 * 
 * 
 * 
 * @author Administrator
 *
 */
public class Client {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		System.out.println("请输入名称:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String name = br.readLine();
		if(name.equals("")){
			return;
		}
		
		Socket client = new Socket("localhost",9999);
		new Thread(new Send(client,name)).start(); //一条路径
		new Thread(new Receive(client)).start(); //一条路径
		
	}

}
