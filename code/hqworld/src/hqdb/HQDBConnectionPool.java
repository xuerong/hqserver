package hqdb;

import hqexceptions.HQManageExceptions;
import hqfile.HQWorldProperties;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import snaq.db.ConnectionPool;
/**
 * 数据库连接池
 * **/
public class HQDBConnectionPool
{
    private static final HQDBConnectionPool dbConnectionPool=new HQDBConnectionPool();
    public static HQDBConnectionPool getInstance(){
    	return dbConnectionPool;
    }
    private ConnectionPool pools;
    public ConnectionPool getPool(){
    	return pools;
    }
    public boolean init(){
    	String driverClass = "com.mysql.jdbc.Driver";
        try
        {
            //加载数据库驱动
            Class c = Class.forName(driverClass);
            Driver driver = (Driver) c.newInstance();
            //注册数据库驱动
            DriverManager.registerDriver(driver);
            String url = HQWorldProperties.getInstance().get("dburl");
            String user = HQWorldProperties.getInstance().get("dbuser");
            String password = HQWorldProperties.getInstance().get("dbpassword");
            //设置连接池的 名字 最小连接数量 最大连接数量 池的大小 超时 连接数据库时的 URL, USER 与 PASSWORD
            //最小 最大连接数量: 如果当前池中的连接数量小于最小数量, 或创建几个新的连接以达到最小连接数量
            //同理, 如果达到最大连接数量, 或关闭连接
            //应该有这样的关系 minPool <= maxPool <= maxSize 
            pools = new ConnectionPool("dbSeekHandler", 30, 500, 800, 200, url, user, password);
            //pools.setLog(null);
        }
        catch (ClassNotFoundException |InstantiationException 
        		|IllegalAccessException | SQLException e){
            //System.out.println("加载数据库驱动失败!");
            HQManageExceptions.getInstance().manageExceptions(e);
            return false;
        }
        return true;
    }
    private HQDBConnectionPool(){
    	
    }
}