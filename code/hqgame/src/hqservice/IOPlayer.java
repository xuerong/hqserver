/**create by entityBuilder**/
package hqservice;


import hqservice.entity.*;
import hqfile.HQPlayerVarModelReader;
import hqio.HQIOWorld;
import hqio.hqrequest.HQIOPlayerInterface;
import hqio.hqrequest.HQPlayerInterface;
import hqio.hqrequest.HQRequest;
import hqio.hqrequest.HQRequestType;
import hqio.hqrequest.HQResponse;
import hqio.hqplayer.HQPair;
import hqio.hqplayer.HQPlayer;
import hqio.hqplayer.HQPlayerListVar;
import hqio.hqplayer.HQPlayerObjectVar;
import hqio.hqplayer.HQPlayerVarModel;
import hqio.hqplayer.HQRecord;


import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class IOPlayer implements HQIOPlayerInterface{
	private static final IOPlayer ioPlayer=new IOPlayer();
	public static IOPlayer getInstance(){
		return ioPlayer;
	}
	public boolean init(){
		return true;
	}
	private IOPlayer(){
		
	}
	@Override
	public int addPlayer(HQPlayerInterface player){
		HQPlayer hqPlayer = buildHQPlayer((Player)player);
		HQRequest request = new HQRequest();
		request.requestId=1;
		request.type=HQRequestType.AddPlayer;
		request.key=hqPlayer.getPlayerId();
		request.player=hqPlayer;
		request.varNames=null;
		HQResponse response = HQIOWorld.getInstance().sendHQRequest(request);
		return response.result;
	}
	@Override
	public Player getPlayer(long key){
		HQRequest request = new HQRequest();
		request.requestId=1;
		request.type=HQRequestType.GetPlayer;
		request.key=key;
		request.player=null;
		request.varNames=null;
		HQResponse response = HQIOWorld.getInstance().sendHQRequest(request);
		if(response.player==null)
		return null;
		return buildPlayer(response.player);
	}
	@Override
	public int deletePlayer(HQPlayerInterface player){
		return deletePlayer(((Player)player).getId());
	}
	@Override
	public int deletePlayer(long playerId){
		HQRequest request = new HQRequest();
		request.requestId=1;
		request.type=HQRequestType.DeletePlayer;
		request.key=playerId;
		request.player=null;
		request.varNames=null;
		HQResponse response = HQIOWorld.getInstance().sendHQRequest(request);
		return response.result;
	}
	@Override
	public int updatePlayer(HQPlayerInterface player){
		HQRequest request = new HQRequest();
		request.requestId=1;
		request.type=HQRequestType.UpdatePlayer;
		request.key=((Player)player).getId();
		request.player=buildHQPlayer((Player)player);
		request.varNames=null;
		HQResponse response = HQIOWorld.getInstance().sendHQRequest(request);
		return response.result;
	}
		// 根据Player生成HQPlayer
	private HQPlayer buildHQPlayer(Player player){
		HashMap<String, Integer> varChangeMakes=player.getVarChangeMakes();
		ConcurrentHashMap<String,HQPlayerVarModel> playerVarModels=HQPlayerVarModelReader.getInstance().getPlayerVarModelsFromVarName();
		HQPlayer hqPlayer=new HQPlayer(player.getId());
		HashMap<String,HQPair> pairList = hqPlayer.getPairList();
		// 普通类型的变量不判断更新，全部保存
		pairList.get("name").value=player.getName();
		pairList.get("age").value=player.getAge();
		pairList.get("level").value=player.getLevel();
		pairList.get("money").value=player.getMoney();
		hqPlayer.setPairVarState(player.getPairVarState());
		hqPlayer.setPairVarVersionNum(player.getPairVarVersionNum()+1);
		Iterator iter = varChangeMakes.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String)entry.getKey();
			int value = (Integer)entry.getValue();
			if(key.equals("island")){
				boolean needChange=false;
				if(value==-1){
					needChange=true;
				}else if(value!=player.getIslandList().size()){
					needChange=true;
				}else{
					List<Island> islands=player.getIslandList();
					for (Island island : islands) {
						if(island.isChanged()){
							needChange=true;
							break;
						}
					}
				}
				if(!needChange){
					hqPlayer.getPlayerListVarList().put("island", null);
					continue;
				}
				List<Island> islands=player.getIslandList();
				HQPlayerListVar listVar=hqPlayer.getPlayerListVarList().get("island");
				listVar.setVersionNum(player.getVersionNums().get("island")+1);
				// 转化成List<HQRecord>
				for (Island island : islands) {
					HQRecord record=new HQRecord(playerVarModels.get("island"));
					record.setRecordState(island.getState());
					record.setRecordId(island.getId());
					record.setPlayerId(hqPlayer.getPlayerId());
					for (HQPair pair : record.getPairs()) {
						if(pair.key.equals("name")){
							pair.value=island.getName();
						}
						if(pair.key.equals("plantcount")){
							pair.value=island.getPlantcount();
						}
						if(pair.key.equals("time")){
							pair.value=island.getTime();
						}
					}
					listVar.getRecordList().add(record);
				}
				continue;
			}
			if(key.equals("body")){
				Body body=player.getBody();
				boolean needChange=false;
				if(value==-1){
					needChange=true;
				}else if(body.isChanged()){
					needChange=true;
				}
				if(!needChange){
					hqPlayer.getPlayerObjectVarList().put("body", null);
					continue;
				}
				hqPlayer.getPlayerObjectVarList().get("body").setVersionNum(player.getVersionNums().get("body")+1);
				// 转化成HQRecord
				HQRecord record=hqPlayer.getPlayerObjectVarList().get("body").getRecord();
				record.setRecordState(body.getState());
				record.setRecordId(body.getId());
				record.setPlayerId(hqPlayer.getPlayerId());
				for (HQPair pair : record.getPairs()) {
					if(pair.key.equals("head")){
						pair.value=body.getHead();
					}
					if(pair.key.equals("leg")){
						pair.value=body.getLeg();
					}
					if(pair.key.equals("hand")){
						pair.value=body.getHand();
					}
				}
				continue;
			}
			if(key.equals("player")){
			}
			if(key.equals("home")){
				Home home=player.getHome();
				boolean needChange=false;
				if(value==-1){
					needChange=true;
				}else if(home.isChanged()){
					needChange=true;
				}
				if(!needChange){
					hqPlayer.getPlayerObjectVarList().put("home", null);
					continue;
				}
				hqPlayer.getPlayerObjectVarList().get("home").setVersionNum(player.getVersionNums().get("home")+1);
				// 转化成HQRecord
				HQRecord record=hqPlayer.getPlayerObjectVarList().get("home").getRecord();
				record.setRecordState(home.getState());
				record.setRecordId(home.getId());
				record.setPlayerId(hqPlayer.getPlayerId());
				for (HQPair pair : record.getPairs()) {
					if(pair.key.equals("peoplenum")){
						pair.value=home.getPeoplenum();
					}
				}
				continue;
			}
			if(key.equals("plant")){
				boolean needChange=false;
				if(value==-1){
					needChange=true;
				}else if(value!=player.getPlantList().size()){
					needChange=true;
				}else{
					List<Plant> plants=player.getPlantList();
					for (Plant plant : plants) {
						if(plant.isChanged()){
							needChange=true;
							break;
						}
					}
				}
				if(!needChange){
					hqPlayer.getPlayerListVarList().put("plant", null);
					continue;
				}
				List<Plant> plants=player.getPlantList();
				HQPlayerListVar listVar=hqPlayer.getPlayerListVarList().get("plant");
				listVar.setVersionNum(player.getVersionNums().get("plant")+1);
				// 转化成List<HQRecord>
				for (Plant plant : plants) {
					HQRecord record=new HQRecord(playerVarModels.get("plant"));
					record.setRecordState(plant.getState());
					record.setRecordId(plant.getId());
					record.setPlayerId(hqPlayer.getPlayerId());
					for (HQPair pair : record.getPairs()) {
						if(pair.key.equals("name")){
							pair.value=plant.getName();
						}
						if(pair.key.equals("level")){
							pair.value=plant.getLevel();
						}
					}
					listVar.getRecordList().add(record);
				}
				continue;
			}
			if(key.equals("capacity")){
				Capacity capacity=player.getCapacity();
				boolean needChange=false;
				if(value==-1){
					needChange=true;
				}else if(capacity.isChanged()){
					needChange=true;
				}
				if(!needChange){
					hqPlayer.getPlayerObjectVarList().put("capacity", null);
					continue;
				}
				hqPlayer.getPlayerObjectVarList().get("capacity").setVersionNum(player.getVersionNums().get("capacity")+1);
				// 转化成HQRecord
				HQRecord record=hqPlayer.getPlayerObjectVarList().get("capacity").getRecord();
				record.setRecordState(capacity.getState());
				record.setRecordId(capacity.getId());
				record.setPlayerId(hqPlayer.getPlayerId());
				for (HQPair pair : record.getPairs()) {
					if(pair.key.equals("eat")){
						pair.value=capacity.getEat();
					}
					if(pair.key.equals("run")){
						pair.value=capacity.getRun();
					}
					if(pair.key.equals("hobby")){
						pair.value=capacity.getHobby();
					}
				}
				continue;
			}
			 
		}
		return hqPlayer;
	}

		// 根据HQPlayer生成Player
	private Player buildPlayer(HQPlayer hqPlayer){
		Player player =new Player();
		player.setId(hqPlayer.getPlayerId());
		player.setPairVarState(hqPlayer.getPairVarState());
		player.setPairVarVersionNum(hqPlayer.getPairVarVersionNum());
		// pair
		for (HQPair pair : hqPlayer.getPairList().values()) {
			if(pair.key.equals("name")){
				player.setName((String)pair.value);
			}
			if(pair.key.equals("age")){
				player.setAge((int)pair.value);
			}
			if(pair.key.equals("level")){
				player.setLevel((int)pair.value);
			}
			if(pair.key.equals("money")){
				player.setMoney((int)pair.value);
			}
		}
		// objectVar
		for (HQPlayerObjectVar objectVar : hqPlayer.getPlayerObjectVarList().values()) {
			if(objectVar.getVarName().equals("body")){
				player.getVersionNums().put("body", objectVar.getVersionNum());
				Body body=new Body();
				body.setId(objectVar.getRecord().getRecordId());
				body.setPlayerId(objectVar.getRecord().getPlayerId());
				body.setState(objectVar.getRecord().getRecordState());
				for (HQPair pair : objectVar.getRecord().getPairs()) {
					if(pair.key.equals("head")){
						body.setHead((String)pair.value);
					}
					if(pair.key.equals("leg")){
						body.setLeg((String)pair.value);
					}
					if(pair.key.equals("hand")){
						body.setHand((String)pair.value);
					}
				}
				body.setChanged(false);
				player.setBody(body);
				player.getVarChangeMakes().put("body",1);
			}
			if(objectVar.getVarName().equals("home")){
				player.getVersionNums().put("home", objectVar.getVersionNum());
				Home home=new Home();
				home.setId(objectVar.getRecord().getRecordId());
				home.setPlayerId(objectVar.getRecord().getPlayerId());
				home.setState(objectVar.getRecord().getRecordState());
				for (HQPair pair : objectVar.getRecord().getPairs()) {
					if(pair.key.equals("peoplenum")){
						home.setPeoplenum((int)pair.value);
					}
				}
				home.setChanged(false);
				player.setHome(home);
				player.getVarChangeMakes().put("home",1);
			}
			if(objectVar.getVarName().equals("capacity")){
				player.getVersionNums().put("capacity", objectVar.getVersionNum());
				Capacity capacity=new Capacity();
				capacity.setId(objectVar.getRecord().getRecordId());
				capacity.setPlayerId(objectVar.getRecord().getPlayerId());
				capacity.setState(objectVar.getRecord().getRecordState());
				for (HQPair pair : objectVar.getRecord().getPairs()) {
					if(pair.key.equals("eat")){
						capacity.setEat((int)pair.value);
					}
					if(pair.key.equals("run")){
						capacity.setRun((int)pair.value);
					}
					if(pair.key.equals("hobby")){
						capacity.setHobby((String)pair.value);
					}
				}
				capacity.setChanged(false);
				player.setCapacity(capacity);
				player.getVarChangeMakes().put("capacity",1);
			}
		}
		// listvar
		for (HQPlayerListVar listVar : hqPlayer.getPlayerListVarList().values()) {
			if(listVar.getVarName().equals("island")){
				player.getVersionNums().put("island", listVar.getVersionNum());
				List<Island> islandList=new ArrayList<Island>();
				for (HQRecord record : listVar.getRecordList()) {
					Island island=new Island();
					island.setId(record.getRecordId());
					island.setPlayerId(record.getPlayerId());
					island.setState(record.getRecordState());
					for (HQPair pair : record.getPairs()) {
						if(pair.key.equals("name")){
							island.setName((String)pair.value);
						}
						if(pair.key.equals("plantcount")){
							island.setPlantcount((int)pair.value);
						}
						if(pair.key.equals("time")){
							island.setTime((java.util.Date)pair.value);
						}
					}
					island.setChanged(false);
					islandList.add(island);
				}
				player.setIslandList(islandList);
				player.getVarChangeMakes().put("island", islandList.size());
			}
			if(listVar.getVarName().equals("plant")){
				player.getVersionNums().put("plant", listVar.getVersionNum());
				List<Plant> plantList=new ArrayList<Plant>();
				for (HQRecord record : listVar.getRecordList()) {
					Plant plant=new Plant();
					plant.setId(record.getRecordId());
					plant.setPlayerId(record.getPlayerId());
					plant.setState(record.getRecordState());
					for (HQPair pair : record.getPairs()) {
						if(pair.key.equals("name")){
							plant.setName((String)pair.value);
						}
						if(pair.key.equals("level")){
							plant.setLevel((int)pair.value);
						}
					}
					plant.setChanged(false);
					plantList.add(plant);
				}
				player.setPlantList(plantList);
				player.getVarChangeMakes().put("plant", plantList.size());
			}
		}
		return player;
	}

}
