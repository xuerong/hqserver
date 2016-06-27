package hqstore;

import java.util.HashMap;
import java.util.List;

import hqfile.HQPlayerVarModelReader;
import hqio.hqplayer.HQPlayer;
import hqio.hqplayer.HQPlayerVarModel;
import hqio.hqplayer.HQValueType;
import hqio.hqplayer.HQPlayerListVar;

/**
 * 用于计算player及list变量的内存大小
 * 分多种计算方式：
 * 1 严格计算：计算每一个变量的大小
 * 2 非严格计算，根据配置文件，及list的大小计算
 * 3 根据其他方式计算，入UNSAFE,instrumentation,GC等
 * **/
public class HQSizeof {
	private static final HQSizeof sizeOf=new HQSizeof();
	public static HQSizeof getInstance(){
		return sizeOf;
	}
	
	/**基础数据**/
	private static final int intSize=Integer.SIZE/Byte.SIZE;
	private static final int longSize=Long.SIZE/Byte.SIZE;
	private static final int stringSize=12;
	private static final int objectSize=8;
	
	
	//////////////非严格计算的参数///////////////
	/**去出records的listvar的size**/
	private static int playerListVarSizeExceptRecords;
	/**去除listvar的HQPlayer对象的size**/
	private static int playerSizeExceptListVars;
	/**所有的object对象的size**/
	private static int playerObjectVarsSize;
	private static HashMap<String, Integer> recordSizeMap=new HashMap<String, Integer>();
	/////////////////////////////
	/**计算出基础数据的大小**/
	public boolean init(){
		/*******初始化非严格计算的参数*********/
		List<HQPlayerVarModel> models=
				HQPlayerVarModelReader.getInstance().getPlayerVarModels();
		int playerObjectVarRecordsSize=0;
		int playerPairVarSize=0;
		int mapKeysSize=0;
		for (HQPlayerVarModel model : models) {
			if(model.getVarType()!=3){
				int size=0;
				for (HQValueType valueType : model.getColumnTypes()) {
					size+=HQValueType.getValueSize(valueType);
					size+=(objectSize*2+stringSize+intSize*2);
				}
				size+=(objectSize*2+3*longSize+intSize);
				recordSizeMap.put(model.getVarName(), size);
				if(model.getVarType()==2)
					playerObjectVarRecordsSize+=size;
				mapKeysSize+=stringSize;
			}else{
				for (HQValueType valueType : model.getColumnTypes()) {
					playerPairVarSize+=HQValueType.getValueSize(valueType);
					playerPairVarSize+=(objectSize*2+stringSize+intSize*2);
					mapKeysSize+=stringSize;
				}
			}
		}
		playerListVarSizeExceptRecords=objectSize*2+stringSize*2+longSize+intSize;
		playerObjectVarsSize=objectSize+stringSize*2+longSize+intSize+playerObjectVarRecordsSize;
		playerSizeExceptListVars=objectSize*4+longSize+intSize+mapKeysSize+playerObjectVarsSize+playerPairVarSize;
		return true;
	}
	private HQSizeof(){
		
	}
	/**
	 * 计算player近似大小：效率高
	 * 计算方法：
	 * 1 假设String的大小为12
	 * 2 一个HQPair的大小可以计算：objectsize+stringsize+intsize*2+objectsize+对应typesize
	 * 3 一个HQRecord的大小可以计算：objectsize+3*longsize+pairsize*n+objectsize+intsize
	 * 4 一个HQPlayerListVar的大小可以计算：objectsize+stringsize*2+recordsize*n+objectsize+longsize+intsize
	 * 5 一个HQPlayerObjectVar的大小可以计算：objectsize+stringsize*2+recordsize+longsize+intsize
	 * 6 一个HQPlayer的大小可以计算：objectsize+longsize+intsize+(stringsize+HQPlayerListVar)*x+
	 * 		(stringsize+HQPlayerObjectVar)*y+(stringsize+HQPair)*z+objectsize*3
	 * **/
	public int getHQPlayerSimilarSize(HQPlayer player){
		int size=0;
		for (HQPlayerListVar listVar : player.getPlayerListVarList().values()) {
			if(listVar!=null && listVar.getRecordList()!=null){
				size+=playerListVarSizeExceptRecords;
				size+=(listVar.getRecordList().size()*recordSizeMap.get(listVar.getVarName()));
			}
		}
		return playerSizeExceptListVars+size;
	}
}
