package hqdb;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import hqexceptions.HQGetConnException;
import hqexceptions.HQManageExceptions;
import hqfile.HQPlayerVarModelReader;
import hqio.hqplayer.HQPair;
import hqio.hqplayer.HQPlayer;
import hqio.hqplayer.HQPlayerListVar;
import hqio.hqplayer.HQPlayerObjectVar;
import hqio.hqplayer.HQPlayerState;
import hqio.hqplayer.HQPlayerVarModel;
import hqio.hqplayer.HQRecord;
import hqio.hqplayer.HQRecordState;
import hqio.hqplayer.HQValueType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HQDbOper {
	private static final HQDbOper dbOper=new HQDbOper();
	public static HQDbOper getInstance(){
		return dbOper;
	}
	public boolean init(){
		return true;
	}
	private HQDbOper(){
		
	}
	
	private static final ConcurrentHashMap<String,HQPlayerVarModel> playerVarModels=
			HQPlayerVarModelReader.getInstance().getPlayerVarModelsFromVarName();
	
	
	/**获取recordList数据,record 的HQRecordState都是Identical**/
	public List<HQRecord> getRecords(String tableName,long playerId,String[] columnNames,HQValueType[] columnTypes){
		List<HQRecord> result=new ArrayList<HQRecord>();
		String sql="select * from "+tableName+" where playerid="+playerId;
		Connection conn=null;
		PreparedStatement ps =null;
		ResultSet rs=null;
		try {
			conn=HQDBConnectionPool.getInstance().getPool().getConnection();
			if(conn==null)
				throw new HQGetConnException("get connection from pool fail,maybe there is too much connections now");
			ps = conn.prepareStatement(sql);
			rs= ps.executeQuery();
			while(rs.next()) {
				HQRecord record =new HQRecord();
				record.setRecordId(rs.getLong("id"));
				record.setPlayerId(rs.getLong("playerid"));
				record.setRecordState(HQRecordState.Identical);
				record.setVersionNum(0);
				HQPair[] pairs=new HQPair[columnNames.length];
				for (int i=0;i<columnNames.length;i++) {
					pairs[i]=new HQPair();
					pairs[i].key=columnNames[i];
					pairs[i].value=rs.getObject(columnNames[i]);
					pairs[i].valueType=columnTypes[i];
				}
				record.setPairs(pairs);
				result.add(record);
			}
			return result;
		} catch (SQLException | HQGetConnException e) {
			HQManageExceptions.getInstance().manageExceptions(e);
			return null;
		}finally{
			closeConn(conn, ps, rs);
		}
	}
	/**获取recordList数据,record 的HQRecordState都是Identical**/
	public List<HQRecord> getRecords(String varName,long playerId){
		String[] columnNames=playerVarModels.get(varName).getColumnNames();
		HQValueType[] columnTypes=playerVarModels.get(varName).getColumnTypes();
		return getRecords(playerVarModels.get(varName).getTableName(), playerId, columnNames, columnTypes);
	}
	/**获取record数据**/
	public HQRecord getRecord(String tableName,long playerId,String[] columnNames,HQValueType[] columnTypes){
		HQRecord record=new HQRecord();
		String sql;
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		if(tableName.equals("player"))
			sql="select * from "+tableName+" where id="+playerId;
		else 
			sql="select * from "+tableName+" where playerid="+playerId;
		try {
			conn=HQDBConnectionPool.getInstance().getPool().getConnection();
			if(conn==null)
				throw new HQGetConnException("get connection from pool fail,maybe there is too much connections now");
			ps = conn.prepareStatement(sql);
			rs= ps.executeQuery();
			if(rs.next()) {
				record.setRecordId(rs.getLong("id"));
				if(tableName.equals("player"))
					record.setPlayerId(rs.getLong("id"));
				else
					record.setPlayerId(rs.getLong("playerid"));
				record.setRecordState(HQRecordState.Identical);
				record.setVersionNum(0);
				HQPair[] pairs=new HQPair[columnNames.length];
				for (int i=0;i<columnNames.length;i++) {
					pairs[i]=new HQPair();
					pairs[i].key=columnNames[i];
					pairs[i].value=rs.getObject(columnNames[i]);
					pairs[i].valueType=columnTypes[i];
				}
				record.setPairs(pairs);
			}
			return record;
		} catch (SQLException | HQGetConnException e) {
			HQManageExceptions.getInstance().manageExceptions(e);
			return null;
		}finally{
			closeConn(conn, ps, rs);
		}
	}
	/**获取record数据**/
	public HQRecord getRecord(String varName,long playerId){
		String[] columnNames=playerVarModels.get(varName).getColumnNames();
		HQValueType[] columnTypes=playerVarModels.get(varName).getColumnTypes();
		return getRecord(playerVarModels.get(varName).getTableName(), playerId, columnNames, columnTypes);
	}
	public boolean isPlayerExist(long playerId){
		String sql="select id from player where id="+playerId;
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			conn=HQDBConnectionPool.getInstance().getPool().getConnection();
			if(conn==null)
				throw new HQGetConnException("get connection from pool fail,maybe there is too much connections now");
			ps = conn.prepareStatement(sql);
			rs= ps.executeQuery();
			if(rs.next()) {
				return true;
			}
			return false;
		} catch (SQLException | HQGetConnException e) {
			HQManageExceptions.getInstance().manageExceptions(e);
			return false;
		}finally{
			closeConn(conn, ps, rs);
		}
	}
	/**
	 * 通过一个访问更新所有的player数据
	 * 
	 * **/
	public int doPlayerData(HQPlayer player){
		Connection conn=null;
		Statement stmt=null;
		
		boolean needUpdate=false;
		
		StringBuilder sql=null;
		try{
			conn=HQDBConnectionPool.getInstance().getPool().getConnection();
			if(conn==null)
				throw new HQGetConnException("get connection from pool fail,maybe there is too much connections now");
			conn.setAutoCommit(false);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			/// 更新pair
			if(player.getPairVarState()==HQRecordState.Add || 
					player.getPairVarState()==HQRecordState.Update ||
					player.getPairVarState()==HQRecordState.UpdateOrAdd){
				
				List<HQPair> pairs=new ArrayList<HQPair>(player.getPairList().values());
				long playerId=player.getPlayerId();
				sql=new StringBuilder();
				switch (player.getState()) {
				case New:
					sql.append("insert into player (id");
					for (HQPair hqPair : pairs) {
						sql.append(","+hqPair.key);
					}
					sql.append(") values ("+playerId);
					for (HQPair hqPair : pairs) {
						sql.append(HQValueType.getValueToDbString(hqPair.valueType, hqPair.value,true));
					}
					sql.append(")");
					break;
				case Login:
					// 刚导入数据
					break;
				case OnLine:
				case UnderLine:
				case DeleteLevel1:
				case DeleteLevel2:
				case DeleteLevel3:
					// 更新一下
					sql.append("update player set ");
					for (int i = 0; i < pairs.size(); i++) {
						HQPair pair=pairs.get(i);
						if(i==0)
							sql.append(pair.key+"=");
						else 
							sql.append(","+pair.key+"=");
						sql.append(HQValueType.getValueToDbString(pair.valueType, pair.value,false));
					}
					sql.append(" where id="+playerId);
					break;
				default:
					return -1;
				}
				if(sql.length()>0){
					needUpdate=true;
					stmt.addBatch(sql.toString());
				}
			}
			// objects
			for (HQPlayerObjectVar objectVar : player.getPlayerObjectVarList().values()) {
				HQRecord hqRecord=objectVar.getRecord();
				String tableName=objectVar.getTableName();
				if(hqRecord.getRecordState()==HQRecordState.Add
						|| hqRecord.getRecordState()==HQRecordState.Update
						|| hqRecord.getRecordState()==HQRecordState.Delete){
					sql=new StringBuilder();
					switch (hqRecord.getRecordState()) {
					case Identical:
						continue;
					case Add:
						//sql.append("insert into "+tableName+" values ("+hqRecord.getRecordId()+","+hqRecord.getPlayerId());
						sql.append("insert into "+tableName+" (id,playerid");
						for (HQPair hqPair : hqRecord.getPairs()) {
							sql.append(","+hqPair.key);
						}
						sql.append(") values ("+hqRecord.getRecordId()+","+hqRecord.getPlayerId());
						for (HQPair pair : hqRecord.getPairs()) {
							sql.append(HQValueType.getValueToDbString(pair.valueType, pair.value,true));
						}
						sql.append(")");
						break;
					case Update:
						sql.append("update "+tableName+" set playerId="+hqRecord.getPlayerId());
						for (HQPair pair : hqRecord.getPairs()) {
							sql.append(","+pair.key+"=");
							sql.append(HQValueType.getValueToDbString(pair.valueType, pair.value,false));
						}
						sql.append(" where id="+hqRecord.getRecordId());
						break;
					case Delete:
						sql.append("delete from "+tableName+" where id="+hqRecord.getRecordId());
						break;
					case UpdateOrAdd:break;

					default:
						break;
					}
					needUpdate=true;
					stmt.addBatch(sql.toString());
				}
			}
			for (HQPlayerListVar listVar : player.getPlayerListVarList().values()) {
				String tableName=listVar.getTableName();
				for (HQRecord hqRecord : listVar.getRecordList()) {
					sql=new StringBuilder();
					switch (hqRecord.getRecordState()) {
					case Identical:break;
					case Add:
						sql.append("insert into "+tableName+" (id,playerid");
						for (HQPair hqPair : hqRecord.getPairs()) {
							sql.append(","+hqPair.key);
						}
						sql.append(") values ("+hqRecord.getRecordId()+","+hqRecord.getPlayerId());
						for (HQPair pair : hqRecord.getPairs()) {
							sql.append(HQValueType.getValueToDbString(pair.valueType, pair.value,true));
						}
						sql.append(")");
						stmt.addBatch(sql.toString());
						needUpdate=true;
						break;
					case Update:
						sql.append("update "+tableName+" set playerId="+hqRecord.getPlayerId());
						for (HQPair pair : hqRecord.getPairs()) {
							sql.append(","+pair.key+"=");
							sql.append(HQValueType.getValueToDbString(pair.valueType, pair.value,false));
						}
						sql.append(" where id="+hqRecord.getRecordId());
						stmt.addBatch(sql.toString());
						needUpdate=true;
						break;
					case Delete:
						sql.append("delete from "+tableName+" where id="+hqRecord.getRecordId());
						stmt.addBatch(sql.toString());
						needUpdate=true;
						break;
					case UpdateOrAdd:break;

					default:
						break;
					}
				}
			}
			if(!needUpdate)
				return 2;
			stmt.executeBatch();
			conn.commit();
		}catch (SQLException | HQGetConnException e) {
			doSqlException(conn, e);
			return -1;
		}finally{
			closeConn(conn, stmt, null);
		}
		// 更新状态
		player.setPairVarState(HQRecordState.Identical);
		for (HQPlayerObjectVar objectVar : player.getPlayerObjectVarList().values()) {
			objectVar.getRecord().setRecordState(HQRecordState.Identical);
		}
		for (HQPlayerListVar listVar : player.getPlayerListVarList().values()) {
			for (HQRecord hqRecord : listVar.getRecordList()) {
				hqRecord.setRecordState(HQRecordState.Identical);
			}
		}
		return 1;
	}
	
	/**
	 * 更新player自身的变量，即pair变量
	 * 
	 * return 
	 * 1 处理成功
	 * -1 处理失败
	 * **/
	public int doPlayerPairVar(long playerId,HQPlayerState state,List<HQPair> pairs){
		StringBuilder sql=new StringBuilder();
		switch (state) {
		case New:
			sql.append("insert into player (id");
			for (HQPair hqPair : pairs) {
				sql.append(","+hqPair.key);
			}
			sql.append(") values ("+playerId);
			for (HQPair hqPair : pairs) {
				sql.append(HQValueType.getValueToDbString(hqPair.valueType, hqPair.value,true));
			}
			sql.append(")");
			break;
		case Login:
			// 刚导入数据
			return 1;
		case OnLine:
		case UnderLine:
		case DeleteLevel1:
		case DeleteLevel2:
		case DeleteLevel3:
			// 更新一下
			sql.append("update player set ");
			for (int i = 0; i < pairs.size(); i++) {
				HQPair pair=pairs.get(i);
				if(i==0)
					sql.append(pair.key+"=");
				else 
					sql.append(","+pair.key+"=");
				sql.append(HQValueType.getValueToDbString(pair.valueType, pair.value,false));
			}
			sql.append(" where id="+playerId);
			break;
		default:
			return -1;
		}
		Connection conn=null;
		Statement stmt=null;
		try {
			conn=HQDBConnectionPool.getInstance().getPool().getConnection();
			if(conn==null)
				throw new HQGetConnException("get connection from pool fail,maybe there is too much connections now");
			conn.setAutoCommit(false);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.execute(sql.toString());
			conn.commit();
		} catch (SQLException | HQGetConnException e) {
			doSqlException(conn, e);
			return -1;
		}finally{
			closeConn(conn, stmt, null);
		}
		return 1;
	}
	/***
	 * 插入，更新或删除同一个表中的记录HQRecord
	 * Identical,Add,Update,Delete,UpdateOrAdd
	 * 这里要求传过来的record必须是需要更新的
	 * 
	 * return 
	 * 1 处理成功
	 * -1 处理失败
	 * **/
	public int doRecord(String tableName,HQRecord hqRecord){
		StringBuilder sql=new StringBuilder();
		switch (hqRecord.getRecordState()) {
		case Identical:return 1;
		case Add:
			//sql.append("insert into "+tableName+" values ("+hqRecord.getRecordId()+","+hqRecord.getPlayerId());
			sql.append("insert into "+tableName+" (id,playerid");
			for (HQPair hqPair : hqRecord.getPairs()) {
				sql.append(","+hqPair.key);
			}
			sql.append(") values ("+hqRecord.getRecordId()+","+hqRecord.getPlayerId());
			for (HQPair pair : hqRecord.getPairs()) {
				sql.append(HQValueType.getValueToDbString(pair.valueType, pair.value,true));
			}
			sql.append(")");
			break;
		case Update:
			sql.append("update "+tableName+" set playerId="+hqRecord.getPlayerId());
			for (HQPair pair : hqRecord.getPairs()) {
				sql.append(","+pair.key+"=");
				sql.append(HQValueType.getValueToDbString(pair.valueType, pair.value,false));
			}
			sql.append(" where id="+hqRecord.getRecordId());
			break;
		case Delete:
			sql.append("delete from "+tableName+" where id="+hqRecord.getRecordId());
			break;
		case UpdateOrAdd:break;

		default:
			break;
		}
		Connection conn=null;
		Statement stmt=null;
		try {
			conn=HQDBConnectionPool.getInstance().getPool().getConnection();
			if(conn==null)
				throw new HQGetConnException("get connection from pool fail,maybe there is too much connections now");
			conn.setAutoCommit(false);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.execute(sql.toString());
			conn.commit();
		}catch (SQLException | HQGetConnException e) {
			doSqlException(conn, e);
			return -1;
		}finally{
			closeConn(conn, stmt, null);
		}
		return 1;
	}
	/***
	 * 批量插入，更新或删除同一个表中的记录HQRecord
	 * Identical,Add,Update,Delete,UpdateOrAdd
	 * 这里要求传过来的record都必须是需要更新的
	 * 
	 * return 
	 * 1 处理成功
	 * -1 处理失败
	 * **/
	public int doMuchRecord(String tableName,List<HQRecord> records){
		if(records.size()==0)
			return 1;
		Connection conn=null;
		Statement stmt=null;
		try {
			conn=HQDBConnectionPool.getInstance().getPool().getConnection();
			if(conn==null)
				throw new HQGetConnException("get connection from pool fail,maybe there is too much connections now");
			conn.setAutoCommit(false);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			for (HQRecord hqRecord : records) {
				StringBuilder sql=new StringBuilder();
				switch (hqRecord.getRecordState()) {
				case Identical:break;
				case Add:
					sql.append("insert into "+tableName+" (id,playerid");
					for (HQPair hqPair : hqRecord.getPairs()) {
						sql.append(","+hqPair.key);
					}
					sql.append(") values ("+hqRecord.getRecordId()+","+hqRecord.getPlayerId());
					for (HQPair pair : hqRecord.getPairs()) {
						sql.append(HQValueType.getValueToDbString(pair.valueType, pair.value,true));
					}
					sql.append(")");
					stmt.addBatch(sql.toString());
					break;
				case Update:
					sql.append("update "+tableName+" set playerId="+hqRecord.getPlayerId());
					for (HQPair pair : hqRecord.getPairs()) {
						sql.append(","+pair.key+"=");
						sql.append(HQValueType.getValueToDbString(pair.valueType, pair.value,false));
					}
					sql.append(" where id="+hqRecord.getRecordId());
					stmt.addBatch(sql.toString());
					break;
				case Delete:
					sql.append("delete from "+tableName+" where id="+hqRecord.getRecordId());
					stmt.addBatch(sql.toString());
					break;
				case UpdateOrAdd:break;

				default:
					break;
				}
			}
			stmt.executeBatch();
			conn.commit();
		}catch (SQLException | HQGetConnException e) {
			doSqlException(conn, e);
			return -1;
		}finally{
			closeConn(conn, stmt, null);
		}
		return 1;
	}
	/**
	 * 获取所有玩家的id
	 * **/
	public TLongList getAllPlayerId(){
		String sql="select id from player";
		TLongList result=new TLongArrayList();
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			conn=HQDBConnectionPool.getInstance().getPool().getConnection();
			if(conn==null)
				throw new HQGetConnException("get connection from pool fail,maybe there is too much connections now");
			ps = conn.prepareStatement(sql);
			rs= ps.executeQuery();
			while(rs.next()) {
				result.add(rs.getLong("id"));
			}
		}catch (SQLException | HQGetConnException e) {
			HQManageExceptions.getInstance().manageExceptions(e);
		}finally{
			closeConn(conn, ps, rs);
		}
		return result;
	}
	/**获取所有table的最大id，用于给新创建的对象分配id**/
	public ConcurrentHashMap<String, Long> getTableMaxIds(){
		ConcurrentHashMap<String, Long> result=new ConcurrentHashMap<String, Long>();
		//select MAX(id) from table
		Connection conn=null;
		PreparedStatement ps =null;
		try {
			conn=HQDBConnectionPool.getInstance().getPool().getConnection();
			if(conn==null)
				throw new HQGetConnException("get connection from pool fail,maybe there is too much connections now");
			conn.setAutoCommit(false);
			for (String tableName : HQPlayerVarModelReader.getInstance().getTableNames()) {
				ps = conn.prepareStatement("select MAX(id) from "+tableName);
				ResultSet rs= ps.executeQuery();
				while(rs.next()) {
					result.put(tableName, rs.getLong(1));
				}
			}
			conn.close();
		} catch (SQLException | HQGetConnException e) {
			doSqlException(conn, e);
			return null;
		}finally{
			closeConn(conn, ps, null);
		}
		return result;
	}
	/**
	 * 删除玩家
	 * 成功返回1
	 * 失败返回-1
	 * **/
	public int deletePlayer(long playerId){
		Connection conn=null;
		Statement stmt=null;
		try{
			conn=HQDBConnectionPool.getInstance().getPool().getConnection();
			if(conn==null)
				throw new HQGetConnException("get connection from pool fail,maybe there is too much connections now");
			conn.setAutoCommit(false);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			// objectvar and listvar
			List<String> tableNames=HQPlayerVarModelReader.getInstance().getTableNames();
			for (String tableName : tableNames) {
				if(!tableName.equals("player"))
					stmt.addBatch("delete from "+ tableName+" where playerid="+playerId);
			}
			// player
			stmt.addBatch("delete from player where id="+playerId);
			stmt.executeBatch();
			conn.commit();
		}catch (SQLException | HQGetConnException e) {
			doSqlException(conn, e);
			return -1;
		}finally{
			closeConn(conn, stmt, null);
		}
		return 1;
	}
	/**处理数据库异常，就是回滚**/
	private void doSqlException(Connection conn,Exception e){
		try {
			if(conn!=null)
				conn.rollback();
		} catch (SQLException e1) {
			HQManageExceptions.getInstance().manageExceptions(e1);
		}
		HQManageExceptions.getInstance().manageExceptions(e);
	}
	/**关闭数据库连接，实际被放回池中了**/
	private void closeConn(Connection conn,Statement ps,ResultSet rs){
		try {
			if(rs!=null)
				rs.close();
			if(ps!=null)
				ps.close();
			if(conn!=null)
				conn.close();
		} catch (SQLException e1) {
			HQManageExceptions.getInstance().manageExceptions(e1);
		}
	}
	
}
