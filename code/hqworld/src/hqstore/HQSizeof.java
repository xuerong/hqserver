package hqstore;

import java.util.HashMap;
import java.util.List;

import hqfile.HQPlayerVarModelReader;
import hqio.hqplayer.HQPlayer;
import hqio.hqplayer.HQPlayerVarModel;
import hqio.hqplayer.HQValueType;
import hqio.hqplayer.HQPlayerListVar;

/**
 * ���ڼ���player��list�������ڴ��С
 * �ֶ��ּ��㷽ʽ��
 * 1 �ϸ���㣺����ÿһ�������Ĵ�С
 * 2 ���ϸ���㣬���������ļ�����list�Ĵ�С����
 * 3 ����������ʽ���㣬��UNSAFE,instrumentation,GC��
 * **/
public class HQSizeof {
	private static final HQSizeof sizeOf=new HQSizeof();
	public static HQSizeof getInstance(){
		return sizeOf;
	}
	
	/**��������**/
	private static final int intSize=Integer.SIZE/Byte.SIZE;
	private static final int longSize=Long.SIZE/Byte.SIZE;
	private static final int stringSize=12;
	private static final int objectSize=8;
	
	
	//////////////���ϸ����Ĳ���///////////////
	/**ȥ��records��listvar��size**/
	private static int playerListVarSizeExceptRecords;
	/**ȥ��listvar��HQPlayer�����size**/
	private static int playerSizeExceptListVars;
	/**���е�object�����size**/
	private static int playerObjectVarsSize;
	private static HashMap<String, Integer> recordSizeMap=new HashMap<String, Integer>();
	/////////////////////////////
	/**������������ݵĴ�С**/
	public boolean init(){
		/*******��ʼ�����ϸ����Ĳ���*********/
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
	 * ����player���ƴ�С��Ч�ʸ�
	 * ���㷽����
	 * 1 ����String�Ĵ�СΪ12
	 * 2 һ��HQPair�Ĵ�С���Լ��㣺objectsize+stringsize+intsize*2+objectsize+��Ӧtypesize
	 * 3 һ��HQRecord�Ĵ�С���Լ��㣺objectsize+3*longsize+pairsize*n+objectsize+intsize
	 * 4 һ��HQPlayerListVar�Ĵ�С���Լ��㣺objectsize+stringsize*2+recordsize*n+objectsize+longsize+intsize
	 * 5 һ��HQPlayerObjectVar�Ĵ�С���Լ��㣺objectsize+stringsize*2+recordsize+longsize+intsize
	 * 6 һ��HQPlayer�Ĵ�С���Լ��㣺objectsize+longsize+intsize+(stringsize+HQPlayerListVar)*x+
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
