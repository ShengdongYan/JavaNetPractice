package com.bjsxt.net.tcp.chat.demo04;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建服务器
 * 写出数据：输出流
 * 读取数据：输入流
 * @author Administrator
 *
 */
public class Server {
	private List<MyChannel> all = new ArrayList<MyChannel>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new Server().start();	
		
	}
	
	public void start() throws IOException{
		ServerSocket server =new ServerSocket(9999);
		while(true){
			Socket client =server.accept();		
			MyChannel channel = new MyChannel(client);
			all.add(channel);//统一管理
			new Thread(channel).start(); //一条道路
		}
	}
	
	
	/**
	 * 一个客户端 一条道路
	 * 1、输入流
	 * 2、输出流
	 * 3、接收数据
	 * 4、发送数据
	 * @author Administrator
	 *
	 */
	private class MyChannel implements Runnable{
		private DataInputStream dis ;
		private DataOutputStream dos ;
		private boolean isRunning =true;
		private String name; 
		public MyChannel(Socket client ) {
			try {
				dis = new DataInputStream(client.getInputStream());
				dos = new DataOutputStream(client.getOutputStream());				
				this.name =dis.readUTF();				
				this.send("欢迎您进入聊天室");
				sendOthers(this.name+"进入了聊天室",true);
			} catch (IOException e) {
				//e.printStackTrace();
				CloseUtil.closeAll(dis,dos);
				isRunning =false;
			}
		}
		/**
		 * 读取数据
		 * @return
		 */
		private String receive(){
			String msg ="";
			try {
				msg=dis.readUTF();
			} catch (IOException e) {
				//e.printStackTrace();
				CloseUtil.closeAll(dis);
				isRunning =false;
				all.remove(this); //移除自身
			}
			return msg;
		}
		
		/**
		 * 发送数据
		 */
		private void send(String msg){
			if(null==msg ||msg.equals("")){
				return ;
			}
			try {
				dos.writeUTF(msg);
				dos.flush();
			} catch (IOException e) {
				//e.printStackTrace();
				CloseUtil.closeAll(dos);
				isRunning =false;
				all.remove(this); //移除自身
			}
		}
		
		/**
		 * 发送给其他客户端
		 */
		private void sendOthers(String msg,boolean sys){
			//是否为私聊 约定
			if(msg.startsWith("@")&& msg.indexOf(":")>-1 ){ //私聊
				//获取name
				String name =msg.substring(1,msg.indexOf(":"));
				String content = msg.substring(msg.indexOf(":")+1);
				for(MyChannel other:all){
					if(other.name.equals(name)){
						other.send(this.name+"对您悄悄地说:"+content);
					}
				}
			}else{		
				//遍历容器
				for(MyChannel other:all){
					if(other ==this){
						continue;
					}
					if(sys){ //系统信息
						other.send("系统信息:"+msg);
					}else{
						//发送其他客户端
						other.send(this.name+"对所有人说:"+msg);
					}
				}
			}
		}
		
		
		@Override
		public void run() {
			while(isRunning){
				sendOthers(receive(),false);
			}
		}
	}
	

}
