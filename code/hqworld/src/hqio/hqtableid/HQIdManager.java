package hqio.hqtableid;

import hqdb.HQDbOper;

import java.util.concurrent.ConcurrentHashMap;

/**
 * table表的id管理器
 * 为了使game自身就能完成record的创建，必须知道自己要创建的record的id
 * game服务器向world请求id许可，然后拿去自己用于分配
 * 
 * **/
public class HQIdManager {
	
	private static final HQIdManager idManager=new HQIdManager();
	public static HQIdManager getInstance(){
		return idManager;
	}
	public boolean init(){
		tablecurrentIds=HQDbOper.getInstance().getTableMaxIds();
		return true;
	}
	/**每次请求给与的id数量**/
	private static final int IDNUMONCE=1000;
	/**table表当前存有的id最大值，用该值加上IDNUMONCE后，分配给当前请求的game**/
	private ConcurrentHashMap<String, Long> tablecurrentIds=null;
	
	private HQIdManager(){
		
	}
	/**根据tableName获取可以分配的id**/
	public final HQIdEntity getIdEntity(String tableName){
		HQIdEntity result = new HQIdEntity();
		result.idLength=IDNUMONCE;
		result.idStart=tablecurrentIds.get(tableName)+1;
		result.tableName=tableName;
		
		tablecurrentIds.put(tableName, result.idStart+IDNUMONCE);
		return result;
	}
}
