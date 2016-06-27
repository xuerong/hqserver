package hqlaunch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import hqfile.HQPlayerVarModelReader;
import hqio.hqplayer.HQPlayerVarModel;
import hqio.hqplayer.HQRecordState;
import hqio.hqplayer.HQValueType;

public class HQEntityBuilder {
	private final String entityFilePath="src/hqservice/entity/";
	private final String playerFilePath="src/hqservice/";
	private final String nextLine="\r\n";
	private List<HQPlayerVarModel> playerVarModelList=null;
	public static void main(String[] args){
		new HQEntityBuilder().begin();
	}
	private HQEntityBuilder(){
		HQPlayerVarModelReader.getInstance().init();
		playerVarModelList=HQPlayerVarModelReader.getInstance().getPlayerVarModels();
	}
	private void begin() {
		// 创建entity，包括player和entity
		for (HQPlayerVarModel model : playerVarModelList) {
			if(model.getVarName().equals("player")){
				buildPlayer(model);
			}else{
				buildEntity(model);
			}
		}
		buildIOPlayer();
		
		System.out.println("entitys 生成成功，请刷新项目");
	}
	// 创建player类
	private void buildPlayer(HQPlayerVarModel model){
		HQStringBuilder sb=new HQStringBuilder(0);
		sb.append("/**create by entityBuilder**/")
		.append("package hqservice;")
		.append(nextLine)
		.append("import hqservice.entity.*;")
		.append("import hqio.hqplayer.HQRecordState;")
		.append("import hqio.hqrequest.HQPlayerInterface;")
		.append("import java.util.List;")
		.append(nextLine)
		.append("public class "+firstCharToUpper(model.getVarName())+" extends HQPlayerInterface{");
		sb.append("// vars");
		// pair
		for (HQPlayerVarModel m : playerVarModelList) {
			if(m.getVarType()==3){
				int varNameNum=m.getColumnNames().length;
				for(int i=0;i<varNameNum;i++){
					sb.append(buildVar(m.getColumnNames()[i], m.getColumnTypes()[i]));
				}
				break;
			}
		}
		// object
		for (HQPlayerVarModel m : playerVarModelList) {
			if(m.getVarType()==2){
				sb.append(buildPlayerVar(m.getVarName(), m.getVarType()));
			}
		}
		// list
		for (HQPlayerVarModel m : playerVarModelList) {
			if(m.getVarType()==1){
				sb.append(buildPlayerVar(m.getVarName(), m.getVarType()));
			}
		}
//		sb.append(nextLine);
//		sb.append("// 表示变量是否改变:通过set变化的为-1，未变化的：");
//		sb.append("// 普通类型：为1，变化通过set捕捉改为-1");
//		sb.append("// object类型：为1，变化通过set捕捉改为-1，否则通过对应对象内的recordState捕捉");
//		sb.append("// list类型：为变化前的size，变化通过set捕捉改为-1，否则通过当前size和变化前的size比较，或通过对应对象内的recordState捕捉");
//		sb.append("private HashMap<String, Integer> varChangeMakes=new HashMap<String, Integer>();");
		
		sb.append(nextLine);
		// 构造函数
		sb.append("public Player(){");
		for (HQPlayerVarModel m : playerVarModelList) {
			if(m.getVarType()!=3){
				sb.append("varChangeMakes.put(\""+m.getVarName()+"\",1);");
			}
		}
		for (HQPlayerVarModel m : playerVarModelList) {
			if(m.getVarType()!=3){
				sb.append("versionNums.put(\""+m.getVarName()+"\",0l);");
			}
		}
		sb.append("}");
		sb.append(nextLine);
		// 获取变量变化情况
//		sb.append("public HashMap<String, Integer> getVarChangeMakes(){");
//		sb.append("return varChangeMakes;");
//		sb.append("}");
		// pair
		for (HQPlayerVarModel m : playerVarModelList) {
			if(m.getVarType()==3){
				int varNameNum=m.getColumnNames().length;
				for(int i=0;i<varNameNum;i++){
					sb.append(buildGetMethod(m.getColumnNames()[i], m.getColumnTypes()[i]));
					sb.append(buildPlayerPairVarSetMethod(m.getColumnNames()[i], m.getColumnTypes()[i]));
				}
				break;
			}
		}
		// object
		for (HQPlayerVarModel m : playerVarModelList) {
			if(m.getVarType()==2){
				sb.append(buildPlayerGetMethod(m.getVarName(), m.getVarType()));
				sb.append(buildPlayerSetMethod(m.getVarName(), m.getVarType()));
			}
		}
		// list
		for (HQPlayerVarModel m : playerVarModelList) {
			if(m.getVarType()==1){
				sb.append(buildPlayerGetMethod(m.getVarName(), m.getVarType()));
				sb.append(buildPlayerSetMethod(m.getVarName(), m.getVarType()));
			}
		}
		sb.append("}");
		DirMaker.createFileByFileName(
				playerFilePath+firstCharToUpper(model.getVarName())+".java",sb.toString());
	}
	// 创建entity类
	private void buildEntity(HQPlayerVarModel model){
		HQStringBuilder sb=new HQStringBuilder(0);
		sb.append("/**create by entityBuilder**/")
		.append("package hqservice.entity;")
		.append(nextLine)
		.append("import hqio.hqrequest.HQEntityInterface;")
		.append("import hqio.hqplayer.HQRecordState;")
		.append(nextLine)
		.append("public class "+firstCharToUpper(model.getVarName())+" extends HQEntityInterface{");
		int varNameNum=model.getColumnNames().length;
		for(int i=0;i<varNameNum;i++){
			sb.append(buildVar(model.getColumnNames()[i], model.getColumnTypes()[i]));
		}
		sb.nextLine();
		sb.append("public "+firstCharToUpper(model.getVarName())+"(){");
		sb.append("super(\""+model.getVarName()+"\");");
		sb.append("}");
		sb.nextLine();
		for(int i=0;i<varNameNum;i++){
			sb.append(buildGetMethod(model.getColumnNames()[i], model.getColumnTypes()[i]));
			sb.append(buildSetMethod(model.getColumnNames()[i], model.getColumnTypes()[i]));
		}
		sb.append("}");
		DirMaker.createFileByFileName(
				entityFilePath+firstCharToUpper(model.getVarName())+".java",sb.toString());
	}
	//////创建IOPlayer，用于和world通信（包括，Player和HQPlayer的转换和调用通信）////////
	private void buildIOPlayer(){
		HQStringBuilder sb=new HQStringBuilder(0);
		sb.append("/**create by entityBuilder**/")
		.append("package hqservice;")
		.append(nextLine)
		.append("import hqservice.entity.*;")
		.append("import hqfile.HQPlayerVarModelReader;")
		.append("import hqio.HQIOWorld;")
		.append("import hqio.hqrequest.HQIOPlayerInterface;")
		.append("import hqio.hqrequest.HQPlayerInterface;")
		.append("import hqio.hqrequest.HQRequest;")
		.append("import hqio.hqrequest.HQRequestType;")
		.append("import hqio.hqrequest.HQResponse;")
		.append("import hqio.hqplayer.HQPair;")
		.append("import hqio.hqplayer.HQPlayer;")
		.append("import hqio.hqplayer.HQPlayerListVar;")
		.append("import hqio.hqplayer.HQPlayerObjectVar;")
		.append("import hqio.hqplayer.HQPlayerVarModel;")
		.append("import hqio.hqplayer.HQRecord;")
		
		.append(nextLine)
		.append("import java.util.ArrayList;")
		.append("import java.util.List;")
		.append("import java.util.Iterator;")
		.append("import java.util.Map;")
		.append("import java.util.HashMap;")
		.append("import java.util.concurrent.ConcurrentHashMap;")
		.append(nextLine)
		
		.append("public class IOPlayer implements HQIOPlayerInterface{");
		
		sb.append("private static final IOPlayer ioPlayer=new IOPlayer();");
		sb.append("public static IOPlayer getInstance(){");
		sb.append("return ioPlayer;");
		sb.append("}");
		sb.append("public boolean init(){");
		sb.append("return true;");
		sb.append("}");
		sb.append("private IOPlayer(){");
		sb.append("");
		sb.append("}");
		
		sb.append("@Override");
		sb.append("public int addPlayer(HQPlayerInterface player){");
		
		sb.append("HQPlayer hqPlayer = buildHQPlayer((Player)player);");
		sb.append("HQRequest request = new HQRequest();");
		sb.append("request.requestId=1;");
		sb.append("request.type=HQRequestType.AddPlayer;");
		sb.append("request.key=hqPlayer.getPlayerId();");
		sb.append("request.player=hqPlayer;");
		sb.append("request.varNames=null;");
		sb.append("HQResponse response = HQIOWorld.getInstance().sendHQRequest(request);");
		sb.append("return response.result;");
		
		
		sb.append("}");
		sb.append("@Override")
		.append("public Player getPlayer(long key){");
		
		sb.append("HQRequest request = new HQRequest();");
		sb.append("request.requestId=1;");
		sb.append("request.type=HQRequestType.GetPlayer;");
		sb.append("request.key=key;");
		sb.append("request.player=null;");
		sb.append("request.varNames=null;");
		sb.append("HQResponse response = HQIOWorld.getInstance().sendHQRequest(request);");
		sb.append("if(response.player==null)");
		sb.append("return null;");
		sb.append("return buildPlayer(response.player);");
		
		sb.append("}");
		sb.append("@Override")
		.append("public int deletePlayer(HQPlayerInterface player){")
		.append("return deletePlayer(((Player)player).getId());")
		.append("}");
		sb.append("@Override")
		.append("public int deletePlayer(long playerId){")
		.append("HQRequest request = new HQRequest();")
		.append("request.requestId=1;")
		.append("request.type=HQRequestType.DeletePlayer;")
		.append("request.key=playerId;")
		.append("request.player=null;")
		.append("request.varNames=null;")
		.append("HQResponse response = HQIOWorld.getInstance().sendHQRequest(request);")
		.append("return response.result;")
		.append("}");
		
		sb.append("@Override")
		.append("public int updatePlayer(HQPlayerInterface player){")
		.append("HQRequest request = new HQRequest();")
		.append("request.requestId=1;")
		.append("request.type=HQRequestType.UpdatePlayer;")
		.append("request.key=((Player)player).getId();")
		.append("request.player=buildHQPlayer((Player)player);")
		.append("request.varNames=null;")
		.append("HQResponse response = HQIOWorld.getInstance().sendHQRequest(request);")
		.append("return response.result;")
		.append("}");
		
		sb.append(buildPlayerToHQPlayer());
		sb.append(buildHQPlayerToPlayer());
		
		sb.append("}");
		
		DirMaker.createFileByFileName(
				playerFilePath+"IOPlayer.java",sb.toString());
	}
	private String buildPlayerToHQPlayer(){
		HQStringBuilder sb=new HQStringBuilder(1);
		sb.append("// 根据Player生成HQPlayer");
		sb.append("private HQPlayer buildHQPlayer(Player player){");
		sb.append("HashMap<String, Integer> varChangeMakes=player.getVarChangeMakes();")
		.append("ConcurrentHashMap<String,HQPlayerVarModel> playerVarModels=HQPlayerVarModelReader.getInstance().getPlayerVarModelsFromVarName();")
		.append("HQPlayer hqPlayer=new HQPlayer(player.getId());")
		
		.append("HashMap<String,HQPair> pairList = hqPlayer.getPairList();")
		.append("// 普通类型的变量不判断更新，全部保存");
		for (HQPlayerVarModel model : playerVarModelList) {
			if(model.getVarType()==3){
				for (String varName : model.getColumnNames()) {
					sb.append("pairList.get(\""+varName+"\").value=player.get"+firstCharToUpper(varName)+"();");
				}
				break;
			}
		}
		sb.append("hqPlayer.setPairVarState(player.getPairVarState());");
		sb.append("hqPlayer.setPairVarVersionNum(player.getPairVarVersionNum()+1);");
		
		sb.append("Iterator iter = varChangeMakes.entrySet().iterator();")
		.append("while (iter.hasNext()) {")
		.append("Map.Entry entry = (Map.Entry) iter.next();")
		.append("String key = (String)entry.getKey();")
		.append("int value = (Integer)entry.getValue();");
		//.append(" if(value==-1){");
		
		for (HQPlayerVarModel model : playerVarModelList) {
			sb.append("if(key.equals(\""+model.getVarName()+"\")){");
			if(model.getVarType()==1){
				sb.append("boolean needChange=false;");
				sb.append("if(value==-1){");
				sb.append("needChange=true;");
				sb.append("}else if(value!=player.get"+firstCharToUpper(model.getVarName())+"List().size()){");
				
				sb.append("needChange=true;");
				sb.append("}else{");
				sb.append("List<"+firstCharToUpper(model.getVarName())+"> "+
						model.getVarName()+"s=player.get"+firstCharToUpper(model.getVarName())+"List();");
				sb.append("for ("+firstCharToUpper(model.getVarName())+" "+model.getVarName()+
						" : "+model.getVarName()+"s) {");
				sb.append("if("+model.getVarName()+".isChanged()){");
				sb.append("needChange=true;");
				sb.append("break;");
				sb.append("}");
				sb.append("}");
				sb.append("}");
				
				sb.append("if(!needChange){");
				sb.append("hqPlayer.getPlayerListVarList().put(\""+model.getVarName()+"\", null);");
				sb.append("continue;");
				sb.append("}");
				
				sb.append("List<"+firstCharToUpper(model.getVarName())+"> "+
						model.getVarName()+"s=player.get"+firstCharToUpper(model.getVarName())+"List();");
				sb.append("HQPlayerListVar listVar=hqPlayer.getPlayerListVarList().get(\""+model.getVarName()+"\");");
				sb.append("listVar.setVersionNum(player.getVersionNums().get(\""+model.getVarName()+"\")+1);");
				sb.append("// 转化成List<HQRecord>");
				sb.append("for ("+firstCharToUpper(model.getVarName())+" "+model.getVarName()+
						" : "+model.getVarName()+"s) {");
				sb.append("HQRecord record=new HQRecord(playerVarModels.get(\""+model.getVarName()+"\"));");
				sb.append("record.setRecordState("+model.getVarName()+".getState());");
				sb.append("record.setRecordId("+model.getVarName()+".getId());");
				sb.append("record.setPlayerId(hqPlayer.getPlayerId());");
				sb.append("for (HQPair pair : record.getPairs()) {");
				for (String varName : model.getColumnNames()) {
					sb.append("if(pair.key.equals(\""+varName+"\")){");
					sb.append("pair.value="+model.getVarName()+".get"+firstCharToUpper(varName)+"();");
					sb.append("}");
				}
				sb.append("}");
				sb.append("listVar.getRecordList().add(record);");
				sb.append("}");
				sb.append("continue;");
			}else if(model.getVarType()==2){
				sb.append(""+firstCharToUpper(model.getVarName())+" "+
						model.getVarName()+"=player.get"+firstCharToUpper(model.getVarName())+"();");
				
				sb.append("boolean needChange=false;");
				sb.append("if(value==-1){");
				sb.append("needChange=true;");
				sb.append("}else if("+model.getVarName()+".isChanged()){");
				sb.append("needChange=true;");
				sb.append("}");
				sb.append("if(!needChange){");
				sb.append("hqPlayer.getPlayerObjectVarList().put(\""+model.getVarName()+"\", null);");
				sb.append("continue;");
				sb.append("}");
				sb.append("hqPlayer.getPlayerObjectVarList().get(\""+
						model.getVarName()+"\").setVersionNum(player.getVersionNums().get(\""+model.getVarName()+"\")+1);");
				sb.append("// 转化成HQRecord");
				sb.append("HQRecord record=hqPlayer.getPlayerObjectVarList().get(\""+model.getVarName()+"\").getRecord();");
				sb.append("record.setRecordState("+model.getVarName()+".getState());");
				sb.append("record.setRecordId("+model.getVarName()+".getId());");
				sb.append("record.setPlayerId(hqPlayer.getPlayerId());");
				sb.append("for (HQPair pair : record.getPairs()) {");
				for (String varName : model.getColumnNames()) {
					sb.append("if(pair.key.equals(\""+varName+"\")){");
					sb.append("pair.value="+model.getVarName()+".get"+firstCharToUpper(varName)+"();");
					sb.append("}");
				}
				sb.append("}");
				sb.append("continue;");
			}
			sb.append("}");
		}
		
		sb.append(" ")
		//sb.append(" }")
		.append("}")
		.append("return hqPlayer;");
		sb.append("}");
		
		return sb.toString();
	}
	private String buildHQPlayerToPlayer(){
		HQStringBuilder sb=new HQStringBuilder(1);
		sb.append("// 根据HQPlayer生成Player");
		sb.append("private Player buildPlayer(HQPlayer hqPlayer){")
		.append("Player player =new Player();")
		.append("player.setId(hqPlayer.getPlayerId());")
		.append("player.setPairVarState(hqPlayer.getPairVarState());")
		.append("player.setPairVarVersionNum(hqPlayer.getPairVarVersionNum());")
		
		.append("// pair")
		.append("for (HQPair pair : hqPlayer.getPairList().values()) {");
		for (HQPlayerVarModel model : playerVarModelList) {
			if(model.getVarType()==3){
				int varNum=model.getColumnNames().length;
				for(int i=0;i<varNum;i++){
					sb.append("if(pair.key.equals(\""+model.getColumnNames()[i]+"\")){")
					.append("player.set"+firstCharToUpper(model.getColumnNames()[i])+"(("+
					HQValueType.getValueClassStr(model.getColumnTypes()[i])+")pair.value);")
					.append("}");
				}
				break;
			}
		}
		sb.append("}");
		
		sb.append("// objectVar")
		.append("for (HQPlayerObjectVar objectVar : hqPlayer.getPlayerObjectVarList().values()) {");
		for (HQPlayerVarModel model : playerVarModelList) {
			if(model.getVarType()==2){
				sb.append("if(objectVar.getVarName().equals(\""+model.getVarName()+"\")){")
				.append("player.getVersionNums().put(\""+model.getVarName()+"\", objectVar.getVersionNum());")
				.append(firstCharToUpper(model.getVarName())+" "+model.getVarName()+"=new "+firstCharToUpper(model.getVarName())+"();")
				.append(model.getVarName()+".setId(objectVar.getRecord().getRecordId());")
				.append(model.getVarName()+".setPlayerId(objectVar.getRecord().getPlayerId());")
				.append(model.getVarName()+".setState(objectVar.getRecord().getRecordState());")
				
				.append("for (HQPair pair : objectVar.getRecord().getPairs()) {");
				int varNum=model.getColumnNames().length;
				for(int i=0;i<varNum;i++){
					sb.append("if(pair.key.equals(\""+model.getColumnNames()[i]+"\")){")
					.append(model.getVarName()+".set"+firstCharToUpper(model.getColumnNames()[i])+"(("+
					HQValueType.getValueClassStr(model.getColumnTypes()[i])+")pair.value);")
					.append("}");
				}
				sb.append("}")
				.append(model.getVarName()+".setChanged(false);");
				sb.append("player.set"+firstCharToUpper(model.getVarName())+"("+model.getVarName()+");");
				sb.append("player.getVarChangeMakes().put(\""+model.getVarName()+"\",1);");
				
				sb.append("}");
			}
		}
		sb.append("}");
		
		sb.append("// listvar")
		.append("for (HQPlayerListVar listVar : hqPlayer.getPlayerListVarList().values()) {");
		for (HQPlayerVarModel model : playerVarModelList) {
			if(model.getVarType()==1){
				sb.append("if(listVar.getVarName().equals(\""+model.getVarName()+"\")){")
				.append("player.getVersionNums().put(\""+model.getVarName()+"\", listVar.getVersionNum());")
				.append("List<"+firstCharToUpper(model.getVarName())+"> "+model.getVarName()+"List=new ArrayList<"+firstCharToUpper(model.getVarName())+">();")
				.append("for (HQRecord record : listVar.getRecordList()) {")
				.append(firstCharToUpper(model.getVarName())+" "+model.getVarName()+"=new "+firstCharToUpper(model.getVarName())+"();")
				.append(model.getVarName()+".setId(record.getRecordId());")
				.append(model.getVarName()+".setPlayerId(record.getPlayerId());")
				.append(model.getVarName()+".setState(record.getRecordState());")
				
				.append("for (HQPair pair : record.getPairs()) {");
				int varNum=model.getColumnNames().length;
				for(int i=0;i<varNum;i++){
					sb.append("if(pair.key.equals(\""+model.getColumnNames()[i]+"\")){")
					.append(model.getVarName()+".set"+firstCharToUpper(model.getColumnNames()[i])+"(("+
					HQValueType.getValueClassStr(model.getColumnTypes()[i])+")pair.value);")
					.append("}");
				}
				sb.append("}")
				.append(model.getVarName()+".setChanged(false);")
				.append(model.getVarName()+"List.add("+model.getVarName()+");")
				.append("}")
				.append("player.set"+firstCharToUpper(model.getVarName())+"List("+model.getVarName()+"List);");
				sb.append("player.getVarChangeMakes().put(\""+model.getVarName()+"\", "+model.getVarName()+"List.size());");
				sb.append("}");
			}
		}
		sb.append("}");
		sb.append("return player;");
		sb.append("}");
		
		return sb.toString();
	}
	////////////////////////////////////
	
	//////player///////
	private String buildPlayerVar(String varName,short varType){
		return "private "+getVarTypeStr(varName,varType)+" "+getVarName(varName,varType)+";";
	}
	private String buildPlayerGetMethod(String varName,short varType){
		return new StringBuilder()
		.append("public "+getVarTypeStr(varName,varType)+" get"+
				getVarName(firstCharToUpper(varName),varType)+"(){"+nextLine)
		.append("\t\treturn this."+getVarName(varName,varType)+";"+nextLine)
		.append("\t}").toString();
	}
	private String buildPlayerSetMethod(String varName,short varType){
		return new StringBuilder()
		.append("public void set"+getVarName(firstCharToUpper(varName),varType)+"("+
				getVarTypeStr(varName,varType)+" "+getVarName(varName,varType)+"){"+nextLine)
		.append("\t\tthis."+getVarName(varName,varType)+"="+getVarName(varName,varType)+";"+nextLine)
		.append("\t\tthis.varChangeMakes.put(\""+varName+"\",-1);"+nextLine)
		.append("\t}").toString();
	}
	private String buildPlayerPairVarSetMethod(String varName,HQValueType varType){
		return new StringBuilder()
		.append("public void set"+firstCharToUpper(varName)+"("+HQValueType.getValueClassStr(varType)+" "+varName+"){"+nextLine)
		.append("\t\tthis."+varName+"="+varName+";"+nextLine)
		.append("\t\tthis.pairVarState=(pairVarState==HQRecordState.Identical?HQRecordState.Update:pairVarState);"+nextLine)
		.append("\t}").toString();
	}
	private String getVarName(String varName,short varType){
		return varType==1?(varName+"List"):varName;
	}
	private String getVarTypeStr(String className,short varType){
		String result=null;
		switch (varType) {
		case 1:
			result= "List<"+firstCharToUpper(className)+">";
			break;
		case 2:
			result= firstCharToUpper(className);
			break;
		case 3://没用上
			result= HQValueType.getValueClassStr(HQValueType.getValueType(className));
			break;
		default:
			result= "Object";
			break;
		}
		return result;
	}
	//////entity///////
	private String buildVar(String varName,HQValueType varType){
		return "private "+HQValueType.getValueClassStr(varType)+" "+varName+";";
	}
	private String buildGetMethod(String varName,HQValueType varType){
		return new StringBuilder()
		.append("public "+HQValueType.getValueClassStr(varType)+" get"+firstCharToUpper(varName)+"(){"+nextLine)
		.append("\t\treturn this."+varName+";"+nextLine)
		.append("\t}").toString();
	}
	private String buildSetMethod(String varName,HQValueType varType){
		return new StringBuilder()
		.append("public void set"+firstCharToUpper(varName)+"("+HQValueType.getValueClassStr(varType)+" "+varName+"){"+nextLine)
		.append("\t\tthis."+varName+"="+varName+";"+nextLine)
		.append("\t\tthis.state=(state==HQRecordState.Identical?HQRecordState.Update:state);"+nextLine)
		.append("\t\tthis.isChanged=true;"+nextLine)
		.append("\t}").toString();
	}
	////// 工具方法 //////
	private String firstCharToUpper(String str){
		StringBuilder sb = new StringBuilder(str);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}
}
class DirMaker {
	public static boolean createFileByFileName(String fileName,String content){
		boolean result=false;
		try {
			result=createFile(new File(fileName));
			FileWriter fileWriter=new FileWriter(fileName,false);
			fileWriter.write(content);
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return result;
	}
	public static boolean createFile(File file) throws IOException {
		if(!file.exists() && file.getParentFile()!=null) {
			makeDir(file.getParentFile());
		}
		return file.createNewFile();
	}
	public static void makeDir(File dir) {
		if(! dir.getParentFile().exists()) {
			makeDir(dir.getParentFile());
		}
		dir.mkdir();
	}
}

class HQStringBuilder{
	private StringBuilder sb=new StringBuilder();
	private String currentTab="";
	private final String nextLine="\r\n";
	private final int tabLength=1;
	public HQStringBuilder(int tabNum){
		for(int i=0;i<tabNum;i++){
			currentTab+="\t";
		}
	}
	public HQStringBuilder append(String str){
		if(str.startsWith("}")){
			currentTab=currentTab.substring(0, currentTab.length()-tabLength);
		}
		sb.append(currentTab+str+nextLine);
		if(str.endsWith("{"))
			currentTab+="\t";
		return this;
	}
	public HQStringBuilder nextLine(){
		sb.append(nextLine);
		return this;
	}
	public HQStringBuilder tab(){
		sb.append("\t");
		return this;
	}
	@Override
	public String toString(){
		return sb.toString();
	}
}