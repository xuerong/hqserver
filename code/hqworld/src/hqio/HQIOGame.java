package hqio;

import hqexceptions.HQManageExceptions;
import hqio.hqrequest.HQRequest;
import hqio.hqrequest.HQRequestType;
import hqio.hqrequest.HQResponse;
import hqio.hqtableid.HQIdManager;
import hqstore.HQStorage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
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
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * 服务器连接者，负责注册服务器连接
 * **/
public class HQIOGame  extends IoHandlerAdapter {
	
	private static final HQIOGame ioGame=new HQIOGame();
	public static HQIOGame getInstance(){
		return ioGame;
	}
	
	public boolean init(){
		//创建一个非阻塞的Server端socket，用NIO
		IoAcceptor acceptor = new NioSocketAcceptor();
		//创建接受数据的过滤器
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ProtocolCodec()));
		//设定这个过滤器将一行一行的读取数据,// 参考：http://blog.sina.com.cn/s/blog_3f77ac270100p3yf.html
		OrderedThreadPoolExecutor threadpool=new OrderedThreadPoolExecutor(500);
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(threadpool));
		// 指定业务逻辑处理器
		acceptor.setHandler(this);
		// 设置端口号
		acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));
		try {
			acceptor.bind();//启动监听
		} catch (IOException e) {
			HQManageExceptions.getInstance().manageExceptions(e);
			return false;
		} 
		return true;
	}
	
	private static final int PORT = 22222;// 定义监听端口
	private HQIOGame() {
	}
	
	//当一个客户端连接进入时
	@Override
	public void sessionOpened(IoSession session)throws Exception {
		System.out.println("incoming client:"+session.getRemoteAddress());
		//System.out.println(InnerServerProtocolDecoder.gameIos==null);
		IoBuffer newIO=IoBuffer.allocate(100);
		newIO.setAutoExpand(true);
		newIO.setAutoShrink(true);
		InnerServerProtocolDecoder.gameIos.put(session.getRemoteAddress().toString(), newIO);
	}
	//当客户端发送消息到达时
	@Override
	public void messageReceived(IoSession session, Object message)throws Exception {
    	HQRequest request=(HQRequest)message;
    	session.write(doHQRequest(request));
    	//System.out.println("receive type "+request.type.toString());
	}
	//当一个客户端连接关闭时
	@Override
	public void sessionClosed(IoSession session)throws Exception {
		System.out.println("one client closed"+session.getRemoteAddress());
	}
	@Override
	public void exceptionCaught(IoSession iosession, Throwable cause){
		cause.printStackTrace();
	}
	/**处理请求HQRequest的方法，并返回HQResponse**/
	private HQResponse doHQRequest(HQRequest request){
		HQResponse response = new HQResponse();
		response.reponseId=request.requestId;
		response.type=HQRequestType.GetPlayer;
		switch (request.type) {
		case AddPlayer:
			response.result=HQStorage.getInstance().addPlayer(request.key, request.player);
			break;
		case GetTableId:
			response.otherData=HQIdManager.getInstance().getIdEntity((String)request.otherData);
			break;
		case GetPlayer:
			response.player=HQStorage.getInstance().get(request.key);
			break;
		case UpdatePlayer:
			response.result=HQStorage.getInstance().put(request.key, request.player, request.varNames);
			break;
		case DeletePlayer:
			response.result=HQStorage.getInstance().deleteHQPalyer(request.key);
			break;
		default:
			break;
		}
		return response;
	}
}
/**过滤器**/
class ProtocolCodec  implements ProtocolCodecFactory{
	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return new InnerServerProtocolDecoder();
	}
	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return new InnerServerProtocolEncoder();
	}
}
/**解码器,注意：一个tcp包可能分多次到达**/
class InnerServerProtocolDecoder implements ProtocolDecoder {
	// 应该一个game（一个连接）对应一个iobuffer
	public static ConcurrentHashMap<String, IoBuffer> gameIos=new ConcurrentHashMap<String, IoBuffer>();
	//private static IoBuffer io=null;
	public void decode(IoSession session, IoBuffer buff, ProtocolDecoderOutput out)
			throws Exception {
//		if(gameIos.get(session.getRemoteAddress().toString())==null){
//			IoBuffer newIO=IoBuffer.allocate(100);
//			newIO.setAutoExpand(true);
//			newIO.setAutoShrink(true);
//			gameIos.replace(session.getRemoteAddress().toString(), newIO);
//		}
		IoBuffer io=gameIos.get(session.getRemoteAddress().toString());
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
    			//返回字节数组
    			out.write(object);
    			
    			if(io.remaining() == 0){
    				io.clear();
    				//gameIos.replace(session.getRemoteAddress().toString(), null);
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
/**编码器，size+object**/
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
		buf.rewind();
		out.write(buf);
		out.flush();
	}

}