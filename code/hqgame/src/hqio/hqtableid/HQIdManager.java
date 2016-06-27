package hqio.hqtableid;

import hqexceptions.HQManageExceptions;
import hqfile.HQPlayerVarModelReader;
import hqio.HQIOWorld;
import hqio.hqrequest.HQRequest;
import hqio.hqrequest.HQRequestType;
import hqio.hqrequest.HQResponse;

import java.util.concurrent.ConcurrentHashMap;

/**
 * table���id������
 * Ϊ��ʹgame����������record�Ĵ���������֪���Լ�Ҫ������record��id
 * game��������world����id��ɣ�Ȼ����ȥ�Լ����ڷ���
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
	/**��ʣ���id��С�ڸ�ֵ��ʱ�������µ�id,��ֵ����С��world�е�IDNUMONCE**/
	private static final int NUMFORREQUEST=100;
	/**table��ǰ���е�id���ֵ���ø�ֵ����IDNUMONCE�󣬷������ǰ�����game**/
	private ConcurrentHashMap<String, HQIdState[]> tableIdState=
			new ConcurrentHashMap<String, HQIdManager.HQIdState[]>();
	/**��ǰHQIdState��index**/
	private ConcurrentHashMap<String, Integer> currentStateIndex=
			new ConcurrentHashMap<String, Integer>();
	
	private HQIdManager(){
		
	}
	/**
	 * ����tableName��ȡ��ǰid
	 * 1 ����id++
	 * 2 ���id�����������µ�HQIdState
	 * **/
	public final long getId(final String tableName){
		final int index=currentStateIndex.get(tableName);
		HQIdState state=tableIdState.get(tableName)[index];
		if(state.nowId+NUMFORREQUEST>state.maxId && 
				tableIdState.get(tableName)[1-index]==null){
			/**����ط����������ö��̣߳����Ƕ��̻߳ᱨ��
			 * ͬʱ�������һ���Բ���϶��player����id��δ�õ����ͻᵼ��id�����ã������õ��̸߳���ȫ
			 * ��ȡ��world����Ͽ�**/
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
	/**��ǰid��HQIdState������id**/
	class HQIdState{
		public long nowId;
		public long maxId;
	}
}
