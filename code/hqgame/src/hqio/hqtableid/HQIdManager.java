package hqio.hqtableid;

import hqexceptions.HQManageExceptions;
import hqfile.HQPlayerVarModelReader;
import hqio.HQIOWorld;
import hqio.hqrequest.HQRequest;
import hqio.hqrequest.HQRequestType;
import hqio.hqrequest.HQResponse;

import java.util.concurrent.ConcurrentHashMap;

/**
 * table表的id管理器
 * 为了使game自身就能完成record的创建，必须知道自己要创建的record的id
 * game服务器向world请求id许可，然后拿去自己用于分配
 * **/
public class HQIdManager {
	
	private static final HQIdManager idManager=new HQIdManager();
	public static HQIdManager getInstance(){
		return idManager;
	}
	public boolean init(){
		for (String tableName : HQPlayerVarModelReader.getInstance().getTableNames()) {
			HQRequest request=new HQRequest();
			request.otherData=tableName;
			request.type=HQRequestType.GetTableId;
			HQResponse response=HQIOWorld.getInstance().sendHQRequest(request);
			HQIdEntity entity=(HQIdEntity)response.otherData;
			HQIdState state=new HQIdState();
			state.nowId=entity.idStart;
			state.maxId=entity.idStart+entity.idLength;
			HQIdState[] states=new HQIdState[2];
			states[0]=state;
			states[1]=null;
			tableIdState.put(tableName, states);
			currentStateIndex.put(tableName, 0);
		}
		return true;
	}
	/**当剩余的id数小于该值的时候，请求新的id,该值必须小于world中的IDNUMONCE**/
	private static final int NUMFORREQUEST=100;
	/**table表当前存有的id最大值，用该值加上IDNUMONCE后，分配给当前请求的game**/
	private ConcurrentHashMap<String, HQIdState[]> tableIdState=
			new ConcurrentHashMap<String, HQIdManager.HQIdState[]>();
	/**当前HQIdState的index**/
	private ConcurrentHashMap<String, Integer> currentStateIndex=
			new ConcurrentHashMap<String, Integer>();
	
	private HQIdManager(){
		
	}
	/**
	 * 根据tableName获取当前id
	 * 1 更新id++
	 * 2 如果id不够，更新新的HQIdState
	 * **/
	public final long getId(final String tableName){
		final int index=currentStateIndex.get(tableName);
		HQIdState state=tableIdState.get(tableName)[index];
		if(state.nowId+NUMFORREQUEST>state.maxId && 
				tableIdState.get(tableName)[1-index]==null){
			/**这个地方本来打算用多线程，但是多线程会报错
			 * 同时，如果，一次性插入较多的player，新id还未得到，就会导致id不够用，所以用单线程更安全
			 * 获取，world处理较快**/
			try{
				HQRequest request=new HQRequest();
				request.otherData=tableName;
				request.type=HQRequestType.GetTableId;
				HQResponse response=HQIOWorld.getInstance().sendHQRequest(request);
				HQIdEntity entity=(HQIdEntity)response.otherData;
				HQIdState state2=new HQIdState();
				state2.nowId=entity.idStart;
				state2.maxId=entity.idStart+entity.idLength;
				tableIdState.get(tableName)[1-index]=state2;
			}catch(Exception e){
				HQManageExceptions.getInstance().manageExceptions(e);
			}
//			new Thread(){
//				@Override
//				public void run(){
//					
//				}
//			}.start();
		}
		if(state.nowId>=state.maxId && tableIdState.get(tableName)[1-index]!=null){
			currentStateIndex.put(tableName,1-index);
			tableIdState.get(tableName)[index]=null;
			state=tableIdState.get(tableName)[1-index];
		}
		return state.nowId++;
	}
	/**当前id和HQIdState的最大的id**/
	class HQIdState{
		public long nowId;
		public long maxId;
	}
}
