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
 * ��worldͨ�ŵ���
 * ������Ҫ�ṩ��������
 * 1 ��world������hqrequest
 * 2 ��world����hqrequest
 * 
 * ���������ʹ�����ǲ�͸����
 * ioplayerͨ��������������������player����ͨ��
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
		// �ο���http://blog.sina.com.cn/s/blog_3f77ac270100p3yf.html
		OrderedThreadPoolExecutor threadpool=new OrderedThreadPoolExecutor(500);
		socket.getFilterChain().addLast("threadPool", new ExecutorFilter(threadpool));
		// ��ֵ̫С��java.nio.BufferUnderflowException
		int recsize = 16*512 * 1024;
		int sendsize = 16*1024 * 1024;
		SocketSessionConfig sc = socket.getSessionConfig();
		sc.setReceiveBufferSize(recsize);// �������뻺�����Ĵ�С
		sc.setSendBufferSize(sendsize);// ��������������Ĵ�С
		sc.setSoLinger(0);
		
		//��ʼ���˿�
		socket.setHandler(this);
		//�������������
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
	 * ������Ϣ�ķ�����
	 * ioplayer���ô˷�������hqrequest����
	 * �÷�����hqrequest���������У������ѷ����߳�
	 * ��game�е��߳̾��񵥻�����һ��
	 * **/
	public HQResponse sendHQRequest(final HQRequest request){
		return sendHQRequest(request, 10000);
	}
	/**
	 * ����HQRequest
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
	 * ���ͻ��˷�����Ϣ����ʱHQResponse
	 * �յ�world�ظ�֮���������͸�HQRequest���̣߳�������Ӧ��ֵ
	 * ��game�е��߳̾��񵥻�����һ��
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
	//��һ���ͻ������ӹر�ʱ
	@Override
	public void sessionClosed(IoSession session)throws Exception {
		System.out.println("one client closed");
	}
	/**ͨ��ѭ������HQRequest**/
	@Override
	public void run() {
		// ѭ������hqrequest
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
    			//ʣ���ֽڳ��Ȳ��㣬�ȴ��´���Ϣ
    			io.compact();
    			break;
    		}
    		//�����Ϣ����
    		int length = io.getInt();
    		if(io.remaining() < length){
    			io.rewind();
    			//ʣ���ֽڳ��Ȳ��㣬�ȴ��´���Ϣ
    			io.compact();
    			break;
    		}else{
    			Object object=io.getObject();
    			//System.out.println("receive packet size:"+(io.limit()-Integer.SIZE));
    			//�����ֽ�����
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