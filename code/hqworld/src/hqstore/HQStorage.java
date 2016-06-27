package hqstore;

import gnu.trove.list.TLongList;
import hqdb.HQDBConnectionPool;
import hqdb.HQDbOper;
import hqio.hqplayer.HQPair;
import hqio.hqplayer.HQPlayer;
import hqio.hqplayer.HQPlayerObjectVar;
import hqio.hqplayer.HQPlayerState;
import hqio.hqplayer.HQRecord;
import hqio.hqplayer.HQRecordState;
import hqio.hqplayer.HQPlayerListVar;
import hqstore.hqtask.HQStorePlayerTask;
import hqexceptions.HQInitMemoryTooLargeException;
import hqexceptions.HQInitMemoryTooSmallException;
import hqexceptions.HQManageExceptions;
import hqexceptions.HQPlayerDataLoseException;
import hqexceptions.HQPlayerNotExistException;
import hqfile.HQWorldProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

/**
 * �洢����
 * �洢������������洢���ݣ��Լ������ݿ��ͬ������
 * �洢����洢�Ķ���ΪHQEntity����
 * �洢������������ļ��������洢��������չ����
 * �洢�������HQEntity�����Ȩ�غ�HQEntity�����;���HQEntity�������̭����
 * 
 * �洢�����ݷ�Ϊ���¼��֣�
 * 1�棺��PlayerΪ��λ�洢��ȡ����player-����
 * 2�棺key��ȡ��key��sql��䣨�Ƿ�洢���棩
 * 3
 * 
 * ���ڴ治����ʱ����������ջ��棬��֪ͨ���ݿ�ͬ��
 * ���ڴ��㹻��ʱ��ÿ��һ��ʱ�������ݿ�ͬ��һ��
 * cpu���е�ʱ��������ݵ�ͬ��
 * ���ݷ�Ϊ���֣������ݿ���һ�µģ������ݿ��в�һ�µ�
 * 
 * ֻ�ṩ�ĸ����⺯�������1��ң�ɾ��1��ң���ȡ1��ң���������ݣ��������ݣ������޸�,ɾ��������list���ͣ���
 * ��concurrenthashmap�洢���ݣ����ݷ�����ڴ��С����concurrenthashmap�����������
 * ������hashcode���������ĸ�concurrenthashmap��
 * **/
public class HQStorage {
	protected Logger log = Logger.getLogger(HQStorage.class);
	private static final HQStorage hqStorage=new HQStorage();
	
	public static HQStorage getInstance(){
		return hqStorage;
	}
	public boolean init(){
		allPlayerId=HQDbOper.getInstance().getAllPlayerId();
		try {
			memorySize=Long.parseLong(HQWorldProperties.getInstance().get("memorysize")); 
			if(memorySize<MINMEMORYSIZE)
				throw new HQInitMemoryTooSmallException("init memory "+memorySize+" too small , the minimum memory is "+MINMEMORYSIZE);
			if(memorySize>=Runtime.getRuntime().maxMemory())
				throw new HQInitMemoryTooLargeException("init memory "+memorySize+" too large , the jvm system memory is "+Runtime.getRuntime().maxMemory());
		} catch (HQInitMemoryTooSmallException | HQInitMemoryTooLargeException 
				| NumberFormatException e) {
			HQManageExceptions.getInstance().manageExceptions(e);
			return false;
		}
		mapCount=(int)(memorySize/EVERYMAPMEMORYSIZE);
		if(memorySize%EVERYMAPMEMORYSIZE!=0)
			mapCount++;
		currentMemorySize=0l;
		maps=new ConcurrentHashMap<Integer, ConcurrentHashMap<Long,HQPlayer>>();
		for (int i=0;i<mapCount;i++) {
			maps.put(i, new ConcurrentHashMap<Long,HQPlayer>());
		}
		// �������������߳�
		new Thread(new SaveDataThread()).start();
		return true;
	}
	
	
	/**ÿ��map�洢�����ݴ�С**/
	private static final long EVERYMAPMEMORYSIZE=2*1024*1024;
	/**��С�ڴ��С**/
	private static final long MINMEMORYSIZE=1024*1024;
	/**����ڴ��С,��ϵͳ�ڴ�����**/
	private static final long MAXMEMORYSIZE=1024*1024*1024*1024;
	/////////////////�����ͬ���ڴ����///////////////
	/** ��������ڴ�ٷֱȣ����ﵽ���ڴ����ʱ�����cpu���У������� **/
	private static final short ADVICECLEARRATE=75; 
	/**�ж�cpu���еİٷֱ�**/
	private static final short CPUIDLE=25;
	/**���������ڴ�ٷֱ�**/
	private static final short MUSTCLEARRATE=95;
	/**��ʱͬ������ʱ����ͬ������֮�󣬽���ɾ������Ҫ����ڴ�ٷֱȣ�������������ڴ棬����ɾ������**/
	private static final short ENTERDELETELINKRATE=50;
	/**�����ڴ������,INTERVAL+����ʱ��**/
	private static final int INTERVAL=10;
	/**ADJUSTCACHELEVELTIMEʱ��û�д��������ȼ�**/
	private static final int ADJUSTCACHELEVELTIME=5000;
	/////////////////////////////////////////////
	
	////////////////��������ҵ�id����Ӧ���Ƿ��ڻ���洢����,����������ж����ݿ����Ƿ��и����///////////////////
	/**ϵͳ��ʼ��ʱ���뻺�棬������ʱ,TLongList�������80000000����**/
	//private static TLongList memoryExist=null;
	private static TLongList allPlayerId=null;
	///////////////////////////////////////////////////
	
	/**��λ��b**/
	private long memorySize;
	/**map������,ͬʱ��Ϊmap������**/
	private int mapCount;
	/**��ǰռ���ڴ��С,���ݴ��ж��Ƿ��ڴ����,ע�⣬������Ҫ����߳��޸Ĵ�ֵ�����ԣ��޸ĵ�ʱ��Ҫ����**/
	private Long currentMemorySize;
	/**���ڴ洢���ݵ�maps**/
	private ConcurrentHashMap<Integer, ConcurrentHashMap<Long, HQPlayer>> maps;
	/**
	 * �вι��캯��
	 * 1���û����С
	 * 2��ʼ��maps
	 * @throws HQInitMemoryTooLargeException 
	 * **/
	private HQStorage(){
		
	}
	/**
	 * ���ӻ�һ��player
	 * ��Ҫ�ж��ڴ��С
	 * ����Ѿ����ڣ����ʧ�ܣ�����-1(������õ�ǰʱ�������û�id����ô����-1��gameserverҪ������������û�)
	 * �ڴ����������-2
	 * �ɹ�����1
	 * 
	 * **/
	public int addPlayer(Long key,HQPlayer player){
		if(testMemoryOverflow(player))
			return -2;
		if(allPlayerId.contains(key))
			return -1;
		// �ж���������Ƿ�Ϸ�
		
		player.setState(HQPlayerState.New);
		int mapKey=key.hashCode()%mapCount;
		ConcurrentHashMap<Long, HQPlayer> map=maps.get(mapKey);
		map.put(key, player);
		player.setLastChangeTime(System.currentTimeMillis());
		allPlayerId.add(key);
		synchronized (currentMemorySize) {
			currentMemorySize+=HQSizeof.getInstance().getHQPlayerSimilarSize(player);
		}
		return 1;
	}
	/**
	 * ���ӻ��޸�һ��player�еı���
	 * ���а�����
	 * 1����player��ĳ��list�е�record
	 * 2�޸�list���е�ĳ��record*��
	 * 3�޸�ĳ��record
	 * 4�޸�ĳ��pair
	 * 5��1,2,3,4���еĶ�����
	 * 
	 * ���ԣ�
	 * 1 varNames!=null�����ӻ��޸�varNames��ʶ�ı���
	 * 2 varNames==null,��������player���������Ӻ��޸ģ����飬û�б仯�ı�������Ϊnull,���ڲ��ı仯��ҲΪnull������isUpdate=true��
	 * 3 ����versionNum�����Ƿ��޸ģ���Ҫ��ͻ������ɷ��������ʱ���޸İ汾��
	 * 
	 * ���أ�
	 * player������ ����0
	 * ��������޸�ʧ�ܣ����������޸ģ�����-1
	 * �ɹ�������1
	 * **/
	public int put(Long key,HQPlayer player,List<String> varNames){
		if(!allPlayerId.contains(key)){
			return 0;
		}
		int mapKey=key.hashCode()%mapCount;
		ConcurrentHashMap<Long, HQPlayer> map=maps.get(mapKey);
		if(!map.containsKey(key)){
			// ��player�������棬���õ�һ��playerOld����
			map.put(key, new HQPlayer(key));
		}
		HQPlayer playerOld=map.get(key);
		// ����player״̬
		playerOld.setState(HQPlayerState.OnLine);
		
		HashMap<String,HQPlayerListVar> listVars=player.getPlayerListVarList();
		HashMap<String,HQPlayerObjectVar> objectVars=player.getPlayerObjectVarList();
		HashMap<String,HQPair> pairs=player.getPairList();
		/// ��Ҫ�޸ĵ������ڴ˷���
		HashMap<String, HQPlayerListVar> listVarsChanged=new HashMap<String, HQPlayerListVar>();
		HashMap<String, HQPlayerObjectVar> objectVarsChanged=new HashMap<String, HQPlayerObjectVar>();
		//HashMap<String, HQPair> pairVarsChanged=new HashMap<String, HQPair>();
		///////////////////////////
		
		Iterator iter = listVars.entrySet().iterator();
		while (iter.hasNext()) {
			 Map.Entry entry = (Map.Entry) iter.next();
			 String keyString=(String)entry.getKey();
			 HQPlayerListVar listVar=(HQPlayerListVar)entry.getValue();
			 if(listVar!=null && (varNames==null || varNames.contains(keyString))){
					HQPlayerListVar listVarsOld=playerOld.getPlayerListVarList().get(keyString);
					if(listVar.getVersionNum()==listVarsOld.getVersionNum()+1 ||  listVarsOld.getVersionNum()==0){
						listVarsChanged.put(keyString, listVar);
					}else{
						return -1;
					}
				}
		}
		
		iter = objectVars.entrySet().iterator();
		while (iter.hasNext()) {
			 Map.Entry entry = (Map.Entry) iter.next();
			 String keyString=(String)entry.getKey();
			 HQPlayerObjectVar objectVar=(HQPlayerObjectVar)entry.getValue();
			 if(objectVar!=null && (varNames==null || varNames.contains(keyString))){
				 HQPlayerObjectVar objectVarsOld=playerOld.getPlayerObjectVarList().get(keyString);
					if(objectVar.getVersionNum()==objectVarsOld.getVersionNum()+1 ||  objectVarsOld.getVersionNum()==0){
						objectVarsChanged.put(keyString, objectVar);
					}else{
						return -1;
					}
			 }
		}
		if(player.getPairVarVersionNum()!=playerOld.getPairVarVersionNum()+1 && playerOld.getPairVarVersionNum()!=0){
			return -1;
		}
//		iter = pairs.entrySet().iterator();
//		while (iter.hasNext()) {
//			 Map.Entry entry = (Map.Entry) iter.next();
//			 String keyString=(String)entry.getKey();
//			 HQPair pairVar=(HQPair)entry.getValue();
//			 if(pairVar!=null && (varNames==null || varNames.contains(keyString))){
//				 HQPair pairVarsOld=playerOld.getPairList().get(keyString);
//					if(pairVar.getVersionNum()==pairVarsOld.getVersionNum()+1 || pairVarsOld.getVersionNum()==0){
//						pairVarsChanged.put(keyString, pairVar);
//					}else{
//						return -1;
//					}
//				}
//		}
		/// ��������
		synchronized (playerOld) {
			iter = listVarsChanged.entrySet().iterator();
			while (iter.hasNext()) {
				Entry entry = (Entry) iter.next();
				playerOld.getPlayerListVarList().put((String)entry.getKey(), (HQPlayerListVar)entry.getValue());// �汾���ڴ˸�����
			}
			iter = objectVarsChanged.entrySet().iterator();
			while (iter.hasNext()) {
				Entry entry = (Entry) iter.next();
				playerOld.getPlayerObjectVarList().put((String)entry.getKey(), (HQPlayerObjectVar)entry.getValue());// �汾���ڴ˸�����
			}
			iter = pairs.entrySet().iterator();
			while (iter.hasNext()) {
				Entry entry = (Entry) iter.next();
				playerOld.getPairList().put((String)entry.getKey(), (HQPair)entry.getValue());
			}
			playerOld.setPairVarVersionNum(player.getPairVarVersionNum());
			playerOld.setPairVarState(player.getPairVarState());
			
			playerOld.setLastChangeTime(System.currentTimeMillis());
		}
		return 1;
	}
	
	/**
	 * ��ȡ������ݣ�������
	 * 1 ��ȡ�����������,varNames==nullʱ
	 * 2 ��ȡ����������ݣ�����varNames
	 * 
	 * �����ڣ�����null
	 * **/
	public HQPlayer get(Long key,List<String> varNames){
		if(!allPlayerId.contains(key))
			return null;
		int mapKey=key.hashCode()%mapCount;
		ConcurrentHashMap<Long, HQPlayer> map=maps.get(mapKey);
		if(!map.containsKey(key)){
			try {
				if(!HQDbOper.getInstance().isPlayerExist(key)){
					allPlayerId.remove(key);
					throw new HQPlayerNotExistException("player is not exist in db");
				}
				HQPlayer player=new HQPlayer(key);
				map.put(key, player);
				synchronized (currentMemorySize) {
					currentMemorySize+=HQSizeof.getInstance().getHQPlayerSimilarSize(player);
				}
			} catch (HQPlayerNotExistException e) {
				HQManageExceptions.getInstance().manageExceptions(e);
				return null;
			}
		}
		return map.get(key);
	}
	public HQPlayer get(Long key){
		return get(key, null);
	}
	/**
	 * ɾ��һ����ң�ɾ�����ݿ��е����ݺͻ����е�����
	 * ɾ���ɹ�������1
	 * ɾ��ʧ�ܣ�����-1
	 * �����ڷ���0
	 * **/
	public int deleteHQPalyer(long key){
		try {
			if(!allPlayerId.contains(key)){
				throw new HQPlayerNotExistException("player is not exist in idlist");
			}
			int result=HQDbOper.getInstance().deletePlayer(key);
			if(result==-1)
				return -1;
			removeHQPlayer(key);
			return 1;
		} catch (HQPlayerNotExistException e) {
			HQManageExceptions.getInstance().manageExceptions(e);
			return 0;
		}
	}
	/**��һ����Ҵӻ������Ƴ�**/
	public void removeHQPlayer(Long key){
		int mapKey=key.hashCode()%mapCount;
		ConcurrentHashMap<Long, HQPlayer> map=maps.get(mapKey);
		if(map.containsKey(key)){
			synchronized (currentMemorySize) {
				currentMemorySize-=HQSizeof.getInstance().getHQPlayerSimilarSize(map.get(key));
			}
			map.remove(key);
		}
	}
	public void removeHQPlayer(HQPlayer player){
		int mapKey=((Long)player.getPlayerId()).hashCode()%mapCount;
		ConcurrentHashMap<Long, HQPlayer> map=maps.get(mapKey);
		if(map.containsKey((Long)player.getPlayerId())){
			map.remove((Long)player.getPlayerId());
			synchronized (currentMemorySize) {
				currentMemorySize-=HQSizeof.getInstance().getHQPlayerSimilarSize(player);
			}
		}
		//log.info("remove player "+player.getPlayerId());
	}
	/**
	 * ���player��ʱ���ж��ڴ��Ƿ����
	 * ***/
	private boolean testMemoryOverflow(HQPlayer player){
		if(HQSizeof.getInstance().getHQPlayerSimilarSize(player)>memorySize-currentMemorySize)
			return true;
		return false;
	}
	/**
	 * �������ݵ��̣߳�
	 * 1 ������ͬ�������ݿ�(ͬ��Ϊ����ɾ��ɾ���ȼ��ߵ�)
	 * 2 ɾ�����ݣ��õ��ڴ棨ɾ��Ϊ�������ݵ�ǰ�ڴ������
	 * 
	 * ͬ�����ݵ�ʱ����ס����
	 * ɾ������֮ǰ��ͬ�����ݣ�Ȼ���ж�״̬�����Ƿ�ɾ������ȷ��ͬ�����ݵ�ʱ�����ݱ��޸�
	 * **/
	public class SaveDataThread implements Runnable{
		/**ͬ���е�player��������ͬ���׶���ɺ󷽿ɽ���ɾ���׶Σ���֮��Ȼ����������ʾ�Ƿ����**/
		//volatile Long savingPlayerNum=0l;
		AtomicLong savingPlayerNum=new AtomicLong(0l);
		private void waitForSavePlayer(int timeOut){
			if(timeOut<=0)
				return;
			try {
				while(savingPlayerNum.get()>0 && timeOut>0){
					Thread.sleep(1000);
					timeOut--;
					//System.out.println("playernum:"+savingPlayerNum.get()+",timeout:"+timeOut);
				}
				if(timeOut==0 && savingPlayerNum.get()>0)
					throw new HQPlayerDataLoseException("save player data timeout,player num = "+savingPlayerNum.get());
			} catch (InterruptedException | HQPlayerDataLoseException e) {
				HQManageExceptions.getInstance().manageExceptions(e);
			}
		}
		@Override
		public void run() {
			while(true){
				synchronized (this) {
					try {
						wait(INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//System.out.println("begin sync,current memory="+currentMemorySize);
				doDbSync();
				System.out.println("end sync,current memory="+currentMemorySize);
//				System.gc();
//				System.out.println("end gc,current memory="+currentMemorySize);
			}
		}
		/**
		 * ����ͬ������ʱ����Ҫ����map����ʱ�������map�в������ݣ��Ƿ������⣿����
		 * **/
		private void doDbSync(){
			/**�˴�����ȵ���һ��delete�����е��߳̽�������ִ��**/
			waitForSavePlayer(1000);
			// ��һ����Ҫ����ͬ��,��һ�߲�����player�Ļ�������
			for(Map.Entry<Integer, ConcurrentHashMap<Long, HQPlayer>> entryMap : maps.entrySet()){
				//Integer keyMap = entryMap.getKey();
				final ConcurrentHashMap<Long, HQPlayer> map = entryMap.getValue();
				synchronized (this) {
					try {
						// ���ֵҪ����map��С�趨����ǰ��30�Ƚ��ʺ�ÿ�����200��player������һ��player����һ�����ӵ������
						// ����̫С������˲�俪�����̺߳����ӽ϶࣬�����̳߳غ����ӳر��쳣
						// ����̫�󣬵���player�ѻ������ͷ���������Ч��
						wait(15);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				HQStorePlayerTask task=new HQStorePlayerTask();
				task.setMap(map);
				task.setSavingPlayerNum(savingPlayerNum);
				task.setStorageThread(this);
				HQThreadPool.getInstance().workTask(task);
		    }
			System.out.println("---------------------current Connection num:"+HQDBConnectionPool.getInstance().getPool().getSize()+
					"---idle Connection num:"+HQDBConnectionPool.getInstance().getPool().getFreeCount());
			System.out.println("---------------------current thread num:"+HQThreadPool.getInstance().getNowThreadNum()+
					"---idle thread num:"+HQThreadPool.getInstance().getIdleThreads().size());
			/**�˴�����ȵ���һ��update�����е��߳̽�������ִ��**/
			waitForSavePlayer(2000);
			// ��һ����Ҫ����ɾ��,��һ�߸���player�Ļ�������
			// ENTERDELETELINKRATE
			if(currentMemorySize*100/memorySize>ENTERDELETELINKRATE){
				for(Map.Entry<Integer, ConcurrentHashMap<Long, HQPlayer>> entryMap : maps.entrySet()){
					//Integer keyMap = entryMap.getKey();
					ConcurrentHashMap<Long, HQPlayer> map = entryMap.getValue();
					for(Map.Entry<Long, HQPlayer> entry : map.entrySet()){
						//Long key = entry.getKey();
						final HQPlayer value = entry.getValue();
						///////////////////�ĳ��̳߳�///////////////////////
						new Thread(){
							@Override
							public void run(){
								try {
									savingPlayerNum.getAndIncrement();
									deleteHQPlayer(value);
									savingPlayerNum.getAndDecrement();
								} catch (HQPlayerDataLoseException e) {
									savingPlayerNum.getAndDecrement();
									HQManageExceptions.getInstance().manageExceptions(e);
								}
							}
						}.start();
				    }
			    }
			}
		}
		/**ͬ�������player�������߻�delete״̬��ɾ��֮**/
		public void saveHQPlayer(HQPlayer player){
			synchronized (player) {
				//int result=saveHQPlayerEx(player);
				//long time1=System.currentTimeMillis();
				int result=HQDbOper.getInstance().doPlayerData(player);
				//System.out.println("save player time:"+(System.currentTimeMillis()-time1)+"ms");
				// ��DeleteLevel3���ܶ��ˣ�˵���������ݿ�������
				if(System.currentTimeMillis()-player.getLastChangeTime()>
						ADJUSTCACHELEVELTIME*HQPlayerState.dbUpdateStateMultiple(player.getState())){
					HQPlayerState.dbUpdateState(player);
				}
				
				if(result==2){
					if(player.getState()==HQPlayerState.DeleteLevel3 || player.getState()==HQPlayerState.UnderLine){
						removeHQPlayer(player);
					}
					return;
				}
				if(result==1){
					if(player.getState()==HQPlayerState.DeleteLevel3 || player.getState()==HQPlayerState.UnderLine){
						removeHQPlayer(player);
					}
					//log.info("player "+player.getPlayerId()+" update success,"+"save player time:"+(System.currentTimeMillis()-time1)+"ms");
				}else{
					log.warn("player "+player.getPlayerId()+" update fault,fault type:"+result);
				}
			}
		}
		/**ɾ����maps��ȡ����Ȼ��ͬ����Ȼ��ɾ��
		 * @throws HQPlayerDataLoseException **/
		private void deleteHQPlayer(HQPlayer player) throws HQPlayerDataLoseException{
			synchronized (player) {
				// ���ǻ����ڣ�˵����һ�����ʧ��
				// �����һ�߸�����Ȼʧ�ܣ������ļ�...
				if(player.getState()==HQPlayerState.DeleteLevel3){
					int result=saveHQPlayerEx(player);
					if(result==2){
						return;
					}
					if(result!=1){
						result=saveHQPlayerToFile(player);
						if(result==-1){
							throw new HQPlayerDataLoseException("player "+player.getPlayerId()+
									" data lose while save it:save to db twice,save file once");
						}
					}
					removeHQPlayer(player);
				}
				if(player.getState()==HQPlayerState.DeleteLevel2
						|| player.getState()==HQPlayerState.DeleteLevel1
						|| player.getState()==HQPlayerState.UnderLine){
					int result=saveHQPlayerEx(player);
					//HQPlayerState.dbUpdateState(player);
					if(result==2){
						return;
					}
					if(result==1)
						removeHQPlayer(player);
				}
			}
		}
		/**
		 * �ú�������ͬ�����ݵ����ݿ⣬����ֵΪͬ�����ݵ����
		 * 1 �ɹ�������1
		 * 2 ʧ��һ���֣�����-1
		 * 3 ȫ��ʧ�ܣ�����-2
		 * 4 ������£�����2
		 * **/
		private int saveHQPlayerEx(HQPlayer player){
			////////// �ݶ�Ϊplayerһ�����ʣ�ÿ��objectһ�����ʣ�ÿ��listһ������////////////////////
			int result=0;
			int successNum=0;
			int failNum=0;
			HQRecordState recordState;
			if(player.getPairVarState()==HQRecordState.Add || 
					player.getPairVarState()==HQRecordState.Update ||
					player.getPairVarState()==HQRecordState.UpdateOrAdd){
				result=HQDbOper.getInstance().doPlayerPairVar(player.getPlayerId(), player.getState(), 
						new ArrayList<HQPair>(player.getPairList().values()));
				if(result==1){
					successNum++;
					player.setPairVarState(HQRecordState.Identical);
					//log.warn("player "+player.getPlayerId()+" update table player success");
				}
				else{
					failNum++;
					log.warn("player "+player.getPlayerId()+" update table player falut");
				}
			}
			for (HQPlayerObjectVar objectVar : player.getPlayerObjectVarList().values()) {
				recordState=objectVar.getRecord().getRecordState();
				if(recordState==HQRecordState.Add ||recordState==HQRecordState.Update){
					result=HQDbOper.getInstance().doRecord(objectVar.getTableName(), objectVar.getRecord());
					if(result==1){
						successNum++;
						objectVar.getRecord().setRecordState(HQRecordState.Identical);
						//log.warn("player "+player.getPlayerId()+" update table "+objectVar.getTableName()+" success");
					}
					else {
						failNum++;
						log.warn("player "+player.getPlayerId()+" update table "+objectVar.getTableName()+" falut");
					}
				}
			}
			for (HQPlayerListVar listVar : player.getPlayerListVarList().values()) {
				List<HQRecord> records=new ArrayList<HQRecord>();
				for (HQRecord hqRecord : listVar.getRecordList()) {
					if(hqRecord.getRecordState()==HQRecordState.Add 
							|| hqRecord.getRecordState()==HQRecordState.Update
							|| hqRecord.getRecordState()==HQRecordState.UpdateOrAdd){
						records.add(hqRecord);
					}
				}
				if(records.size()>0){
					result=HQDbOper.getInstance().doMuchRecord(listVar.getTableName(), records);
					if(result==1){
						for (HQRecord hqRecord : records) {
							hqRecord.setRecordState(HQRecordState.Identical);
						}
						successNum++;
						//log.warn("player "+player.getPlayerId()+" update table "+listVar.getTableName()+" success");
					}else {
						failNum++;
						log.warn("player "+player.getPlayerId()+" update table "+listVar.getTableName()+" falut");
					}
				}
			}
			if(failNum==0 && successNum==0)
				return 2;
			if(failNum==0)
				return 1;
			if(successNum>0)
				return -1;
			else 
				return -2;
		}
		/**
		 * �����������ݿ�ʧ�ܣ���player�����ļ�
		 * �ɹ�������1
		 * ʧ�ܣ�����-1
		 * **/
		private int saveHQPlayerToFile(HQPlayer player){
			return 1;
		}
	}
}
