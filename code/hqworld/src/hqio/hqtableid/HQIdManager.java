package hqio.hqtableid;

import hqdb.HQDbOper;

import java.util.concurrent.ConcurrentHashMap;

/**
 * table���id������
 * Ϊ��ʹgame����������record�Ĵ���������֪���Լ�Ҫ������record��id
 * game��������world����id��ɣ�Ȼ����ȥ�Լ����ڷ���
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
	/**ÿ����������id����**/
	private static final int IDNUMONCE=1000;
	/**table��ǰ���е�id���ֵ���ø�ֵ����IDNUMONCE�󣬷������ǰ�����game**/
	private ConcurrentHashMap<String, Long> tablecurrentIds=null;
	
	private HQIdManager(){
		
	}
	/**����tableName��ȡ���Է����id**/
	public final HQIdEntity getIdEntity(String tableName){
		HQIdEntity result = new HQIdEntity();
		result.idLength=IDNUMONCE;
		result.idStart=tablecurrentIds.get(tableName)+1;
		result.tableName=tableName;
		
		tablecurrentIds.put(tableName, result.idStart+IDNUMONCE);
		return result;
	}
}
