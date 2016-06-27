package hqfile;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import hqio.hqplayer.HQPlayerVarModel;
import hqexceptions.HQManageExceptions;
import hqexceptions.HQPropertiesModelException;
import hqio.hqplayer.HQValueType;
/**
 * player变量模板类，即table模板类
 * 该类从文件中读取数据，并保存所有变量的模板对象
 * 其中player中的普通对象保存在一个模板对象里
 * **/
public class HQPlayerVarModelReader {
	/**模板配置文件**/
	private static final String PLAYERVARMODELFILE_STRING="playervarmodel.properties";
	/**key：变量名**/
	private final ConcurrentHashMap<String,HQPlayerVarModel> varModels=new ConcurrentHashMap<String, HQPlayerVarModel>();
	private static HQPlayerVarModelReader instance=new HQPlayerVarModelReader();
	
	public static HQPlayerVarModelReader getInstance(){
		return instance;
	}
	/**
	 * 从文件中读取模板，放入varModels中
	 * 读取后要检验模板配置是否异常
	 * **/
	public boolean init(){
		Properties pps = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(PLAYERVARMODELFILE_STRING));
			pps.load(in);
			Enumeration en = pps.propertyNames(); //得到配置文件的名字
			while(en.hasMoreElements()) {
				String strKey = (String) en.nextElement();
				String strValue = pps.getProperty(strKey);
				if(!checkPropertiesValue(strKey,strValue))
					throw new HQPropertiesModelException("init "+
							PLAYERVARMODELFILE_STRING+" exception,varname "+ strKey+" model is error");
				varModels.put(strKey, trans(strKey, strValue));
			}
		}catch (IOException e) {
			HQManageExceptions.getInstance().manageExceptions(e);
			return false;
		} catch (HQPropertiesModelException e) {
			HQManageExceptions.getInstance().manageExceptions(e);
			return false;
		}
		return true;
	}
	
	private HQPlayerVarModelReader(){
		
	}
	/**根据变量名获取模板对象**/
	public HQPlayerVarModel get(String key){
		return varModels.get(key);
	}
	/**获取模板的list形式**/
	public List<HQPlayerVarModel> getPlayerVarModels(){
		List<HQPlayerVarModel> result=new ArrayList<HQPlayerVarModel>() ;
		for (HQPlayerVarModel playerVarModel : varModels.values()) {
			result.add(playerVarModel);
		}
		return result;
	}
	/**获取模板的map形式**/
	public ConcurrentHashMap<String,HQPlayerVarModel> getPlayerVarModelsFromVarName(){
		return varModels;
	}
	/**获取所有的表名**/
	public List<String> getTableNames(){
		List<String> result=new ArrayList<String>();
		for (HQPlayerVarModel model : varModels.values()) {
			result.add(model.getTableName());
		}
		return result;
	}
	/**检验模板文件配置是否正确**/
	private boolean checkPropertiesValue(String key,String value){
		return true;
	}
	/**模板的转换，由配置文件中的键值对，转换成HQPlayerVarModel类型**/
	private HQPlayerVarModel trans(String key,String value){
		if(key.equals("player")){
			String[] modelValues = value.split(",");
			HQPlayerVarModel model=new HQPlayerVarModel();
			model.setVarName(key);
			model.setTableName(modelValues[0]);
			model.setVarType((short)3);
			String[] columnNames=new String[modelValues.length-3];
			HQValueType[] columnTypes=new HQValueType[modelValues.length-3];
			for(int i=3;i<modelValues.length;i++){
				String[] columns=modelValues[i].split("-");
				columnNames[i-3]=columns[0].replace("(", "").replace(")", "");
				columnTypes[i-3]=HQValueType.getValueType(columns[1].replace("(", "").replace(")", ""));
			}
			model.setColumnNames(columnNames);
			model.setColumnTypes(columnTypes);
			return model;
		}
		
		String[] modelValues = value.split(",");
		HQPlayerVarModel model=new HQPlayerVarModel();
		model.setVarName(key);
		model.setTableName(modelValues[0]);
		model.setPriorityLevel(Integer.parseInt(modelValues[1]));
		model.setVarType((short)(modelValues[2].equals("list")?1:(modelValues[2].equals("object")?2:3)));
		String[] columnNames=new String[modelValues.length-5];
		HQValueType[] columnTypes=new HQValueType[modelValues.length-5];
		for(int i=5;i<modelValues.length;i++){
			String[] columns=modelValues[i].split("-");
			columnNames[i-5]=columns[0].replace("(", "").replace(")", "");
			columnTypes[i-5]=HQValueType.getValueType(columns[1].replace("(", "").replace(")", ""));
		}
		model.setColumnNames(columnNames);
		model.setColumnTypes(columnTypes);
		return model;
	}
}
