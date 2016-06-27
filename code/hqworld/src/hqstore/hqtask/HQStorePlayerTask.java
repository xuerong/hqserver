package hqstore.hqtask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import hqio.hqplayer.HQPlayer;
import hqstore.HQStorage;
import hqstore.HQStoreTaskInterface;

/**存储player任务，存储一个map中的player**/
public class HQStorePlayerTask implements HQStoreTaskInterface {
	ConcurrentHashMap<Long, HQPlayer> map=null;
	AtomicLong savingPlayerNum=null;
	private HQStorage.SaveDataThread storageThread=null;
	@Override
	public void handle() {
		for(Map.Entry<Long, HQPlayer> entry : map.entrySet()){
			//Long key = entry.getKey();
			final HQPlayer value = entry.getValue();
			savingPlayerNum.getAndIncrement();
			storageThread.saveHQPlayer(value);
			savingPlayerNum.getAndDecrement();
	    }
	}
	public void setMap(ConcurrentHashMap<Long, HQPlayer> map) {
		this.map = map;
	}
	public void setSavingPlayerNum(AtomicLong savingPlayerNum) {
		this.savingPlayerNum = savingPlayerNum;
	}
	public void setStorageThread(HQStorage.SaveDataThread storageThread) {
		this.storageThread = storageThread;
	}
	
}
