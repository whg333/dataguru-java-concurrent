package cn.dataguru.week8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HomeWorkNIO {

	private static final int PORT = 8090;
	private static final int BUFF_SIZE = 8;
	
	private static final String EMPTY = "";
	private static final String ENTER = "\r\n";
	private static final String QUIT = "quit";
	
	//根据客户端地址来选择合适的处理器Handler的映射Map
	private static final Map<SocketAddress, Handler> clientAddressMap = new HashMap<SocketAddress, Handler>();
	
	public static void main(String[] args) throws IOException {
		startServer(PORT);
	}
	
	private static void startServer(int port) throws IOException{
		Selector selector = Selector.open();
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.socket().bind(new InetSocketAddress(port));
		serverChannel.configureBlocking(false);
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("NIO Server start on port="+port);
		
		while(true){
			selector.select();
			Iterator<SelectionKey> keyIt = selector.selectedKeys().iterator();
			while(keyIt.hasNext()){
				final SelectionKey key = keyIt.next();
				keyIt.remove();
				
				if(key.isAcceptable()){
					Acceptor.handleAccept(key);
				}else if(key.isValid()){
					clientAddressMap.get(clientAddress(key)).handle(key);
				}
			}
		}
	}
	
	private static class Acceptor{
		public static void handleAccept(SelectionKey key) throws IOException {
			SocketChannel clientChannel = ((ServerSocketChannel)key.channel()).accept();
			clientChannel.configureBlocking(false);
			clientChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(BUFF_SIZE));
			
			recordClientAddress(clientChannel);
		}
	}
	
	private static class Handler{
		
		private final StringBuilder readData = new StringBuilder();
		
		public void handle(final SelectionKey key){
			if(key.isReadable()){
				handleRead(key);
			}else if(key.isWritable()){
				handleWrite(key);
			}
		}
		
		public void handleRead(SelectionKey key) {
			readData(key);
			if(isReadEnd()){
				interestWrite(key);
			}
		}
		
		/** 不停的从客户端通道读取数据，并保存至readData */
		private void readData(SelectionKey key){
			SocketChannel clientChannel = (SocketChannel) key.channel();
			ByteBuffer readBuf = (ByteBuffer)key.attachment();
			int readSize;
			try {
				while ((readSize = clientChannel.read(readBuf)) > 0) {
					readData.append(new String(Arrays.copyOfRange(readBuf.array(), 0, readSize)));
					readBuf.clear();
				}
				if (readSize == -1) { //客户端关闭连接
					disconnect(key);
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
				disconnect(key);
				return;
			}
			//System.out.println("received from client:"+readData+", "+readData.length());
		}
		
		/** 以回车结尾代表客户端数据发送完毕，则服务器端也读取数据完毕，接着等待可写入客户端数据 */
		private boolean isReadEnd(){
			return readData.lastIndexOf(ENTER) != -1;
		}
		
		/** 对可写事件感兴趣，并清空readData准备下次接受客户端的数据 */
		private void interestWrite(SelectionKey key){
			key.interestOps(SelectionKey.OP_WRITE);
			key.attach(ByteBuffer.wrap(readData.toString().trim().getBytes()));
			readData.delete(0, readData.length());
		}
		
		public void handleWrite(SelectionKey key) {
			String data = handleReadedData(key);
			if(hasData(data)){
				if(isQuit(data)){
					disconnect(key);
					return;
				}
				write(key, data);
			}
			interestRead(key);
		}
		
		/** 处理从客户端通道读取完毕的数据 */
		private String handleReadedData(SelectionKey key) {
			ByteBuffer readedBuf = (ByteBuffer)key.attachment();
			byte[] readedBytes = readedBuf.array();
			if(readedBytes.length == 0){
				return EMPTY;
			}
			String readedData = new String(readedBytes);
			return isQuit(readedData) ? QUIT : CalculatorUtil.calculate(readedData) + ENTER + ENTER;
		}
		
		private boolean isQuit(String data) {
			return QUIT.equals(data);
		}
		
		private boolean hasData(String data) {
			return data.length() > 0;
		}
		
		/** 写数据data给客户端通道 */
		private void write(SelectionKey key, String data){
			SocketChannel clientChannel = (SocketChannel) key.channel();
			ByteBuffer writeBuf = ByteBuffer.wrap(data.getBytes());
			try {
				clientChannel.write(writeBuf);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//System.out.println("writed to client:"+data+", "+data.length());
		}
		
		/** 对可读事件感兴趣，等待可读入客户端的数据 */
		private void interestRead(SelectionKey key){
			key.interestOps(SelectionKey.OP_READ);
			//感兴趣key变化后必须重置附件？！否则连续2次回车后，read事件读取原来的附件读取不到（readSize==0）
			key.attach(ByteBuffer.allocate(BUFF_SIZE));
		}
	}
	
	private static void recordClientAddress(SocketChannel clientChannel){
		SocketAddress clientAddress = clientChannel.socket().getRemoteSocketAddress();
		clientAddressMap.put(clientAddress, new Handler());
		System.out.println("clientAddress=【"+clientAddress+"】 already accept!!!");
	}
	
	private static void disconnect(SelectionKey key){
		SocketChannel clientChannel = (SocketChannel) key.channel();
		SocketAddress clientAddress = clientChannel.socket().getRemoteSocketAddress();
		try {
			clientChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		clientAddressMap.remove(clientAddress);
		System.out.println("clientAddress=【"+clientAddress+"】 already closed so server close!!!");
	}
	
	private static SocketAddress clientAddress(SelectionKey key){
		return ((SocketChannel)key.channel()).socket().getRemoteSocketAddress();
	}
	
}
