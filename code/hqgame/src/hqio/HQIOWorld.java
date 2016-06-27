package hqio;

import hqexceptions.HQManageExceptions;
import hqio.hqrequest.HQRequest;
import hqio.hqrequest.HQResponse;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * 与world通信的类
 * 该类主要提供两个功能
 * 1 向world服发送hqrequest
 * 2 从world接收hqrequest
 * 
 * 该类对引擎使用者是不透明的
 * ioplayer通过调用这两个方法，对player进行通信
 * ***/

public class HQIOWorld extends IoHandlerAdapter implements Runnable{
	
	private static final HQIOWorld ioWorld=new HQIOWorld();
	private IoSession session;
	private final Queue<HQRequest> requestQuery=new ConcurrentLinkedQueue<HQRequest>();
	
	public static HQIOWorld getInstance(){
		return ioWorld;
	}
	public boolean init(){
		NioSocketConnector socket = new NioSocketConnector();
		
		
		socket.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ProtocolCodec()));
		// 参考：http://blog.sina.com.cn/s/blog_3f77ac270100p3yf.html
		OrderedThreadPoolExecutor threadpool=new OrderedThreadPoolExecutor(500);
		socket.getFilterChain().addLast("threadPool", new ExecutorFilter(threadpool));
		// 数值太小报java.nio.BufferUnderflowException
		int recsize = 16*512 * 1024;
		int sendsize = 16*1024 * 1024;
		SocketSessionConfig sc = socket.getSessionConfig();
		sc.setReceiveBufferSize(recsize);// 设置输入缓冲区的大小
		sc.setSendBufferSize(sendsize);// 设置输出缓冲区的大小
		sc.setSoLinger(0);
		
		//初始化端口
		socket.setHandler(this);
		//连接世界服务器
        ConnectFuture connect = socket.connect(new InetSocketAddress("127.0.0.1",22222 ));	
		connect.awaitUninterruptibly(60 * 1000);
		if(!connect.isConnected()){
			try{
				Thread.sleep(5000);
			}catch (Exception e) {
				//log.error(e, e);
				HQManageExceptions.getInstance().manageExceptions(e);
				return false;
			}
		}
		try{
			Thread.sleep(1000);
		}catch (Exception e) {
			//log.error(e, e);
			HQManageExceptions.getInstance().manageExceptions(e);
			return false;
		}
		this.session = connect.getSession();
		new Thread(this).start();
		return true;
	}
	private HQIOWorld(){
		
	}
	/**
	 * 发送消息的方法，
	 * ioplayer调用此方法发送hqrequest对象
	 * 该方法将hqrequest对象放入队列，并唤醒发送线程
	 * 让game中的线程就像单机操作一样
	 * **/
	public HQResponse sendHQRequest(final HQRequest request){
		return sendHQRequest(request, 10000);
	}
	/**
	 * 发送HQRequest
	 * **/
	public HQResponse sendHQRequest(final HQRequest request,int timeOut){
		session.setAttribute(request.requestId+"response", null);
		session.setAttribute(request.requestId+"thread", Thread.currentThread());
		requestQuery.add(request);
		synchronized(this){
			this.notify();
		}
		synchronized(Thread.currentThread()){
			try {
				Thread.currentThread().wait(timeOut);
			} catch (InterruptedException e) {
				//e.printStackTrace();
				HQManageExceptions.getInstance().manageExceptions(e);
			}
		}
		Object object=session.getAttribute(request.requestId+"response");
		session.removeAttribute(request.requestId+"response");
		session.removeAttribute(request.requestId+"thread");
		return (HQResponse)object;
	}
	
	@Override
	public void sessionOpened(IoSession session)throws Exception {
		//System.out.println("incoming client:"+session.getRemoteAddress());
	}
	/**
	 * 当客户端发送消息到达时HQResponse
	 * 收到world回复之后，启动发送该HQRequest的线程，返回相应的值
	 * 让game中的线程就像单机操作一样
	 * **/
	@Override
	public void messageReceived(IoSession session, Object message)throws Exception {
		HQResponse response=(HQResponse)message;
		//System.out.println(message==null);
		Thread thread=(Thread)session.getAttribute(response.reponseId+"thread");
		if(thread!=null){
			session.setAttribute(response.reponseId+"response", response);
			synchronized (thread) {
				thread.notify();
			}
		}
	}
	//当一个客户端连接关闭时
	@Override
	public void sessionClosed(IoSession session)throws Exception {
		System.out.println("one client closed");
	}
	/**通过循环发送HQRequest**/
	@Override
	public void run() {
		// 循环发送hqrequest
		while(true){
			if(!requestQuery.isEmpty()){
				HQRequest request=requestQuery.poll();
				this.session.write(request);
				System.out.println("send type "+request.type.toString());
			}else {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
				
		}
	}
}
class ProtocolCodec  implements ProtocolCodecFactory{
	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return new InnerServerProtocolDecoder();
	}
	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return new InnerServerProtocolEncoder();
	}
}
class InnerServerProtocolDecoder implements ProtocolDecoder {
	private static IoBuffer io=null;
	public void decode(IoSession session, IoBuffer buff, ProtocolDecoderOutput out)
			throws Exception {
		if(io==null){
			io= IoBuffer.allocate(100);
			io.setAutoExpand(true);
			io.setAutoShrink(true);
		}
		io.put(buff);
		do{
    		io.flip();
    		if(io.remaining() < Integer.SIZE/Byte.SIZE){
    			//剩余字节长度不足，等待下次信息
    			io.compact();
    			break;
    		}
    		//获得信息长度
    		int length = io.getInt();
    		if(io.remaining() < length){
    			io.rewind();
    			//剩余字节长度不足，等待下次信息
    			io.compact();
    			break;
    		}else{
    			Object object=io.getObject();
    			//System.out.println("receive packet size:"+(io.limit()-Integer.SIZE));
    			//返回字节数组
    			out.write(object);
    			
    			if(io.remaining() == 0){
    				io.clear();
    				io=null;
    				break;
    			}else{
    				io.compact();
    			}
    		}
    	}while(true);
	}

	public void dispose(IoSession session) throws Exception {
	}

	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {

	}
}
class InnerServerProtocolEncoder implements ProtocolEncoder {

	public void dispose(IoSession session) throws Exception {
	}
	public void encode(IoSession session, Object obj, ProtocolEncoderOutput out)
			throws Exception {
		IoBuffer buf = IoBuffer.allocate(100);
		buf.setAutoExpand(true);
		buf.setAutoShrink(true);
		buf.putInt(0);
		buf.putObject(obj);
		buf.flip();
		buf.putInt(buf.limit()-Integer.SIZE/Byte.SIZE);
		// System.out.println("send packet size:"+(buf.limit()));
		
		buf.rewind();
		out.write(buf);
		out.flush();
	}

}