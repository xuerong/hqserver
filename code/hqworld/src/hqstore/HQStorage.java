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
 * 存储器类
 * 存储器类用来负责存储数据，以及和数据库的同步工作
 * 存储器类存储的对象为HQEntity对象
 * 存储器类根据配置文件来决定存储容量和扩展策略
 * 存储器类根据HQEntity对象的权重和HQEntity的类型决定HQEntity对象的淘汰策略
 * 
 * 存储的数据分为以下几种；
 * 1存：以Player为单位存储，取：以player-属性
 * 2存：key，取：key，sql语句（是否存储缓存）
 * 3
 * 
 * 当内存不够的时候，清理进最终缓存，并通知数据库同步
 * 当内存足够的时候，每隔一段时间与数据库同步一次
 * cpu空闲的时候进行数据的同步
 * 数据分为两种，和数据库中一致的，和数据库中不一致的
 * 
 * 只提供四个对外函数，添加1玩家，删除1玩家，获取1玩家（或玩家数据），存数据（包括修改,删除（仅限list类型））
 * 用concurrenthashmap存储数据，根据分配的内存大小决定concurrenthashmap对象的数量，
 * 并根据hashcode决定放在哪个concurrenthashmap中
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
		// 启动保存数据线程
		new Thread(new SaveDataThread()).start();
		return true;
	}
	
	
	/**每个map存储的数据大小**/
	private static final long EVERYMAPMEMORYSIZE=2*1024*1024;
	/**最小内存大小**/
	private static final long MINMEMORYSIZE=1024*1024;
	/**最大内存大小,由系统内存设置**/
	private static final long MAXMEMORYSIZE=1024*1024*1024*1024;
	/////////////////清除，同步内存参数///////////////
	/** 建议清除内存百分比，即达到该内存比例时，如果cpu空闲，则清理 **/
	private static final short ADVICECLEARRATE=75; 
	/**判定cpu空闲的百分比**/
	private static final short CPUIDLE=25;
	/**必须清理内存百分比**/
	private static final short MUSTCLEARRATE=95;
	/**定时同步缓存时，在同步环节之后，进入删除环节要求的内存百分比，即，低于这个内存，进入删除环节**/
	private static final short ENTERDELETELINKRATE=50;
	/**清理内存的周期,INTERVAL+处理时间**/
	private static final int INTERVAL=10;
	/**ADJUSTCACHELEVELTIME时间没有处理，调整等级**/
	private static final int ADJUSTCACHELEVELTIME=5000;
	/////////////////////////////////////////////
	
	////////////////将所有玩家的id及对应的是否在缓存存储在这,用这个可以判断数据库中是否有该玩家///////////////////
	/**系统初始化时调入缓存，添加玩家时,TLongList最大可添加80000000数据**/
	//private static TLongList memoryExist=null;
	private static TLongList allPlayerId=null;
	///////////////////////////////////////////////////
	
	/**单位是b**/
	private long memorySize;
	/**map的数量,同时作为map的主键**/
	private int mapCount;
	/**当前占用内存大小,根据此判断是否内存溢出,注意，由于需要多个线程修改此值，所以，修改的时候要加锁**/
	private Long currentMemorySize;
	/**用于存储数据的maps**/
	private ConcurrentHashMap<Integer, ConcurrentHashMap<Long, HQPlayer>> maps;
	/**
	 * 有参构造函数
	 * 1设置缓存大小
	 * 2初始化maps
	 * @throws HQInitMemoryTooLargeException 
	 * **/
	private HQStorage(){
		
	}
	/**
	 * 增加或一个player
	 * 需要判断内存大小
	 * 如果已经存在，添加失败，返回-1(如果是用当前时间生成用户id，那么返回-1后，gameserver要重新请求添加用户)
	 * 内存溢出，返回-2
	 * 成功返回1
	 * 
	 * **/
	public int addPlayer(Long key,HQPlayer player){
		if(testMemoryOverflow(player))
			return -2;
		if(allPlayerId.contains(key))
			return -1;
		// 判断新新玩家是否合法
		
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
	 * 增加或修改一个player中的变量
	 * 其中包括：
	 * 1增加player中某个list中的record
	 * 2修改list（中的某个record*）
	 * 3修改某个record
	 * 4修改某个pair
	 * 5（1,2,3,4）中的多个组合
	 * 
	 * 策略：
	 * 1 varNames!=null，增加或修改varNames标识的变量
	 * 2 varNames==null,遍历整个player对象并做增加和修改（建议，没有变化的变量设置为null,且内部的变化的也为null，否则isUpdate=true）
	 * 3 根据versionNum决定是否修改，这要求客户端生成发送请求的时候修改版本号
	 * 
	 * 返回：
	 * player不存在 返回0
	 * 如果存在修改失败，其它不被修改，返回-1
	 * 成功，返回1
	 * **/
	public int put(Long key,HQPlayer player,List<String> varNames){
		if(!allPlayerId.contains(key)){
			return 0;
		}
		int mapKey=key.hashCode()%mapCount;
		ConcurrentHashMap<Long, HQPlayer> map=maps.get(mapKey);
		if(!map.containsKey(key)){
			// 将player调到缓存，并得到一个playerOld对象
			map.put(key, new HQPlayer(key));
		}
		HQPlayer playerOld=map.get(key);
		// 设置player状态
		playerOld.setState(HQPlayerState.OnLine);
		
		HashMap<String,HQPlayerListVar> listVars=player.getPlayerListVarList();
		HashMap<String,HQPlayerObjectVar> objectVars=player.getPlayerObjectVarList();
		HashMap<String,HQPair> pairs=player.getPairList();
		/// 将要修改的数据在此放置
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
		/// 更新数据
		synchronized (playerOld) {
			iter = listVarsChanged.entrySet().iterator();
			while (iter.hasNext()) {
				Entry entry = (Entry) iter.next();
				playerOld.getPlayerListVarList().put((String)entry.getKey(), (HQPlayerListVar)entry.getValue());// 版本号在此更新了
			}
			iter = objectVarsChanged.entrySet().iterator();
			while (iter.hasNext()) {
				Entry entry = (Entry) iter.next();
				playerOld.getPlayerObjectVarList().put((String)entry.getKey(), (HQPlayerObjectVar)entry.getValue());// 版本号在此更新了
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
	 * 获取玩家数据，包括：
	 * 1 获取整个玩家数据,varNames==null时
	 * 2 获取部分玩家数据，根据varNames
	 * 
	 * 不存在，返回null
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
	 * 删除一个玩家，删除数据库中的数据和缓存中的数据
	 * 删除成功，返回1
	 * 删除失败，返回-1
	 * 不存在返回0
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
	/**将一个玩家从缓存中移除**/
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
	 * 添加player的时候，判断内存是否溢出
	 * ***/
	private boolean testMemoryOverflow(HQPlayer player){
		if(HQSizeof.getInstance().getHQPlayerSimilarSize(player)>memorySize-currentMemorySize)
			return true;
		return false;
	}
	/**
	 * 保存数据的线程：
	 * 1 把数据同步到数据库(同步为主，删除删除等级高的)
	 * 2 删除数据，得到内存（删除为主，根据当前内存情况）
	 * 
	 * 同步数据的时候锁住对象
	 * 删除数据之前，同步数据，然后判断状态决定是否删除，以确保同步数据的时候数据被修改
	 * **/
	public class SaveDataThread implements Runnable{
		/**同步中的player数量，在同步阶段完成后方可进入删除阶段（反之亦然），用它表示是否完成**/
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
		 * 当我同步它的时候，需要遍历map，此时，如果向map中插入数据，是否会出问题？？？
		 * **/
		private void doDbSync(){
			/**此处必须等到上一个delete中所有的线程结束方可执行**/
			waitForSavePlayer(1000);
			// 第一遍主要用于同步,这一边不更新player的基础数据
			for(Map.Entry<Integer, ConcurrentHashMap<Long, HQPlayer>> entryMap : maps.entrySet()){
				//Integer keyMap = entryMap.getKey();
				final ConcurrentHashMap<Long, HQPlayer> map = entryMap.getValue();
				synchronized (this) {
					try {
						// 这个值要根据map大小设定，当前，30比较适合每秒添加200个player（这是一个player建立一个连接的情况）
						// 设置太小，导致瞬间开启的线程和连接较多，导致线程池和连接池报异常
						// 设置太大，导致player堆积，降低服务器运行效率
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
			/**此处必须等到上一个update中所有的线程结束方可执行**/
			waitForSavePlayer(2000);
			// 第一遍主要用于删除,这一边更新player的基础数据
			// ENTERDELETELINKRATE
			if(currentMemorySize*100/memorySize>ENTERDELETELINKRATE){
				for(Map.Entry<Integer, ConcurrentHashMap<Long, HQPlayer>> entryMap : maps.entrySet()){
					//Integer keyMap = entryMap.getKey();
					ConcurrentHashMap<Long, HQPlayer> map = entryMap.getValue();
					for(Map.Entry<Long, HQPlayer> entry : map.entrySet()){
						//Long key = entry.getKey();
						final HQPlayer value = entry.getValue();
						///////////////////改成线程池///////////////////////
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
		/**同步，如果player处于下线或delete状态，删除之**/
		public void saveHQPlayer(HQPlayer player){
			synchronized (player) {
				//int result=saveHQPlayerEx(player);
				//long time1=System.currentTimeMillis();
				int result=HQDbOper.getInstance().doPlayerData(player);
				//System.out.println("save player time:"+(System.currentTimeMillis()-time1)+"ms");
				// 当DeleteLevel3积攒多了，说明更新数据库有问题
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
		/**删除，maps中取出，然后同步，然后删除
		 * @throws HQPlayerDataLoseException **/
		private void deleteHQPlayer(HQPlayer player) throws HQPlayerDataLoseException{
			synchronized (player) {
				// 他们还存在，说明第一遍更新失败
				// 如果这一边更新仍然失败，存入文件...
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
		 * 该函数用于同步数据到数据库，返回值为同步数据的情况
		 * 1 成功，返回1
		 * 2 失败一部分，返回-1
		 * 3 全部失败，返回-2
		 * 4 无需更新，返回2
		 * **/
		private int saveHQPlayerEx(HQPlayer player){
			////////// 暂定为player一个访问，每个object一个访问，每个list一个访问////////////////////
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
		 * 更新两次数据库失败，将player存入文件
		 * 成功，返回1
		 * 失败，返回-1
		 * **/
		private int saveHQPlayerToFile(HQPlayer player){
			return 1;
		}
	}
}
