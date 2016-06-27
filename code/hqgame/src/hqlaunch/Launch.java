package hqlaunch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import hqexceptions.HQManageExceptions;
import hqfile.HQPlayerVarModelReader;
import hqio.HQIOWorld;
import hqio.hqtableid.HQIdManager;
import hqservice.IOPlayer;
import hqservice.Player;
import hqservice.entity.Body;
import hqservice.entity.Capacity;
import hqservice.entity.Home;
import hqservice.entity.Island;
import hqservice.entity.Plant;

public class Launch{

	public static void main(String[] args) {
		boolean isSuccess;
		/**初始化异常处理单例**/
		isSuccess=HQManageExceptions.getInstance().init();
		System.out.println("初始化异常处理单例"+(isSuccess?" success":" fail"));
		/**初始化玩家变量模板**/
		isSuccess=HQPlayerVarModelReader.getInstance().init();
		System.out.println("初始化玩家变量模板"+(isSuccess?" success":" fail"));
		/**加载game通信类单例**/
		isSuccess=HQIOWorld.getInstance().init();
		System.out.println("加载world通信类单例"+(isSuccess?" success":" fail"));
		/**初始化id管理器**/
		isSuccess=HQIdManager.getInstance().init();
		System.out.println("初始化id管理器"+(isSuccess?" success":" fail"));
		/**加载player通信类单例**/
		isSuccess=IOPlayer.getInstance().init();
		System.out.println("加载player通信类单例"+(isSuccess?" success":" fail"));
		Launch launch=new Launch();
		//launch.getPlayerTest();
		//launch.addPlayersTest();
		launch.addPlayersTest();
	}
	private void testMap(){
		HashMap<String, String> aaHashMap=new HashMap<String, String>();
		aaHashMap.put("sdf", "sdfsdfsdf");
		aaHashMap.put("sdf", null);
		System.out.println(aaHashMap.get("sdf"));
	}
	private void updatePlayerTest(){
		Player player=IOPlayer.getInstance().getPlayer(27l);
		System.out.println(player.getId());
		player.setName("haha");
		Plant plant=new Plant();
		plant.setLevel(2);
		plant.setName("sjdflskd");
		player.getPlantList().add(plant);
		int result = IOPlayer.getInstance().updatePlayer(player);
		System.out.println(result);
	}
	private void deletePlayerTest(){
		int result = IOPlayer.getInstance().deletePlayer(26l);
		System.out.println(result);
	}
	private void getPlayerTest(){
		Player player=IOPlayer.getInstance().getPlayer(26l);
		System.out.println(player.getName());
	}
	private void addPlayersTest(){
		for(int k=0;k<100000;k++){
			for(int i=0;i<10;i++){
				Player player=new Player();
				player.setId(HQIdManager.getInstance().getId("player"));
				player.setName("xiaoqiang3");
				player.setAge(19);
				player.setLevel(10);
				player.setMoney(100);
				Body body=new Body();
				body.setHand("dashou");
				body.setHead("xiaotou");
				body.setLeg("potui");
				player.setBody(body);
				Home home=new Home();
				home.setPeoplenum(20);
				player.setHome(home);
				Capacity capacity=new Capacity();
				capacity.setEat(1);
				capacity.setHobby("chisi");
				capacity.setRun(100);
				player.setCapacity(capacity);
				Island island=new Island();
				island.setName("dadao");
				island.setPlantcount(12);
				island.setTime(new Date());
				List<Island> islands=new ArrayList<Island>();
				islands.add(island);
				island=new Island();
				island.setName("dadao2");
				island.setPlantcount(14);
				island.setTime(new Date());
				islands.add(island);
				island=new Island();
				island.setName("dadao3");
				island.setPlantcount(14);
				island.setTime(new Date());
				islands.add(island);
				player.setIslandList(islands);
				Plant plant=new Plant();
				plant.setLevel(2);
				plant.setName("hahaplant");
				List<Plant> plants=new ArrayList<Plant>();
				plants.add(plant);
				player.setPlantList(plants);
				IOPlayer.getInstance().addPlayer(player);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	static{
		System.out.println("d");
	}
}