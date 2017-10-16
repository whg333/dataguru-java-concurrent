package cn.dataguru.week8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class HomeWorkAIO {

	private static final int PORT = 8091;
	private static final int BUFF_SIZE = 8;
	
	private static final String EMPTY = "";
	private static final String ENTER = "\r\n";
	private static final String QUIT = "quit";
	
	//记录客户端地址对应的客户端通道的映射Map
	private static final Map<SocketAddress, AsynchronousSocketChannel> clientAddressMap = new HashMap<SocketAddress, AsynchronousSocketChannel>();

	//关闭服务器的阀门，当accept时的failed的失败异常错误达到指定的次数后就关闭服务器 
	private static final CountDownLatch stopServerLatch = new CountDownLatch(2);
	
	public static void main(String[] args) throws IOException {
		startServer(PORT);
	}
	
	private static void startServer(int port) throws IOException{
		AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
		serverChannel.bind(new InetSocketAddress(port));
		serverChannel.accept(ByteBuffer.allocate(BUFF_SIZE), new AcceptCompletionHandler(serverChannel));
		System.out.println("AIO Server start on port="+port);
		
		try {
			stopServerLatch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	private static class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, ByteBuffer>{
		
		private final AsynchronousServerSocketChannel serverChannel;
		
		public AcceptCompletionHandler(AsynchronousServerSocketChannel serverChannel) {
			this.serverChannel = serverChannel;
		}

		@Override
		public void completed(AsynchronousSocketChannel clientChannel, ByteBuffer readBuf) {
			//必须再次调用accept方法令服务器可接受后续新的客户端连接
			serverChannel.accept(ByteBuffer.allocate(BUFF_SIZE), this);
			
			clientChannel.read(readBuf, readBuf, new ReadCompletionHandler(clientChannel));
			recordClientAddress(clientChannel);
		}
		
		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			try{
				serverChannel.close();
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				stopServerLatch.countDown();
			}
		}
	}
	
	private static class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer>{

		private final StringBuilder readData = new StringBuilder();
		private final AsynchronousSocketChannel clientChannel;
		
		public ReadCompletionHandler(AsynchronousSocketChannel clientChannel) {
			this.clientChannel = clientChannel;
		}

		@Override
		public void completed(Integer readSize, ByteBuffer readBuf) {
			readData(readSize, readBuf);
			if(isReadEnd()){
				handleWrite(readBuf);
			}
			continueRead(readBuf);
		}
		
		private void readData(Integer readSize, ByteBuffer readBuf){
			if(readSize > 0){
				readData.append(new String(Arrays.copyOfRange(readBuf.array(), 0, readSize)));
				readBuf.clear();
			}else if(readSize == -1){ //客户端关闭连接
				disconnect(clientChannel);
				return;
			}
			//System.out.println("received from client:"+readData+", "+readData.length());
		}
		
		private boolean isReadEnd(){
			return readData.lastIndexOf(ENTER) != -1;
		}
		
		private void handleWrite(ByteBuffer readBuf){
			String data = handleReadedData();
			if(hasData(data)){
				if(isQuit(data)){
					disconnect(clientChannel);
					return;
				}
				write(readBuf, data);
			}
		}
		
		/** 处理从客户端通道读取完毕的数据 */
		private String handleReadedData() {
			String readedData = readData.toString().trim();
			readData.delete(0, readData.length());
			if(readedData.length() == 0){
				return EMPTY;
			}
			return isQuit(readedData) ? QUIT : CalculatorUtil.calculate(readedData) + ENTER + ENTER;
		}
		
		private boolean isQuit(String data) {
			return QUIT.equals(data);
		}
		
		private boolean hasData(String data) {
			return data.length() > 0;
		}
		
		/** 写数据data给客户端通道 */
		private void write(ByteBuffer readBuf, String data){
			ByteBuffer writeBuf = ByteBuffer.wrap(data.getBytes());
			clientChannel.write(writeBuf);
			//System.out.println("writed to client:"+data+", "+data.length());
		}
		
		private void continueRead(ByteBuffer readBuf){
			clientChannel.read(readBuf, readBuf, this);
		}

		@Override
		public void failed(Throwable exc, ByteBuffer readBuf) {
			disconnect(clientChannel);
		}
		
	}
	
	private static void recordClientAddress(AsynchronousSocketChannel clientChannel){
		try {
			SocketAddress clientAddress = clientChannel.getRemoteAddress();
			clientAddressMap.put(clientAddress, clientChannel);
			System.out.println("clientAddress=【"+clientAddress+"】 already accept!!!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void disconnect(AsynchronousSocketChannel clientChannel){
		if(!clientChannel.isOpen()){
			return;
		}
		try{
			SocketAddress clientAddress = clientChannel.getRemoteAddress();
			clientChannel.close();
			clientAddressMap.remove(clientAddress);
			System.out.println("clientAddress=【"+clientAddress+"】 already closed so server close!!!");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
}
