package hqdb;

import hqexceptions.HQManageExceptions;
import hqfile.HQWorldProperties;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import snaq.db.ConnectionPool;
/**
 * ���ݿ����ӳ�
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
            //�������ݿ�����
            Class c = Class.forName(driverClass);
            Driver driver = (Driver) c.newInstance();
            //ע�����ݿ�����
            DriverManager.registerDriver(driver);
            String url = HQWorldProperties.getInstance().get("dburl");
            String user = HQWorldProperties.getInstance().get("dbuser");
            String password = HQWorldProperties.getInstance().get("dbpassword");
            //�������ӳص� ���� ��С�������� ����������� �صĴ�С ��ʱ �������ݿ�ʱ�� URL, USER �� PASSWORD
            //��С �����������: �����ǰ���е���������С����С����, �򴴽������µ������Դﵽ��С��������
            //ͬ��, ����ﵽ�����������, ��ر�����
            //Ӧ���������Ĺ�ϵ minPool <= maxPool <= maxSize 
            pools = new ConnectionPool("dbSeekHandler", 30, 500, 800, 200, url, user, password);
            //pools.setLog(null);
        }
        catch (ClassNotFoundException |InstantiationException 
        		|IllegalAccessException | SQLException e){
            //System.out.println("�������ݿ�����ʧ��!");
            HQManageExceptions.getInstance().manageExceptions(e);
            return false;
        }
        return true;
    }
    private HQDBConnectionPool(){
    	
    }
}