package hqio;

import gnu.trove.map.hash.TIntObjectHashMap;
import hqio.hqpbservice.HQPBHandleInterface;
import hqio.hqpbservice.HQPBServiceInterface;

/**
 * pb��������Ҫ����PB��Ϣ
 * **/
public class HQPBHandler {
	TIntObjectHashMap<HQPBServiceInterface> services=new TIntObjectHashMap<HQPBServiceInterface>();
	public void register(int obcode,HQPBHandleInterface handle){
		//services.put(obcode);
	}
}
