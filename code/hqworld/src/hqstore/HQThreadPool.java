package hqstore;

import hqexceptions.HQManageExceptions;
import hqexceptions.HQThreadNumOverHeap;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 线程池
 * 该线程池初始通过接受一个HQStoreTaskInterface实例，然后调用HQStoreTaskInterface的handle方法来处理一个任务
 * 线程池初始化时，开启MINTHREADNUM个线程，最大开启MAXTHREADNUM个线程，超出时，报HQThreadNumOverHeap异常
 * 
 * 线程池原理：启动的线程处于wait状态，并放入idle队列中，任务来时，从队列中弹出线程，
 * 设置任务指向HQStoreTaskInterface，notify线程，执行完毕，放回队列
 * 线程不够，分配10个，最大分配到MAXTHREADNUM个
 * **/
public class HQThreadPool {
	
	private static final HQThreadPool pool=new HQThreadPool();
	public static HQThreadPool getInstance(){
		return pool;
	}
	private HQThreadPool(){
		
	}
	
	/**最小的线程数，也是默认开启的线程数**/
	private static final int MINTHREADNUM=30;
	/**最大的线程数**/
	private static final int MAXTHREADNUM=800;
	/**当前线程数**/
	private int nowThreadNum=0;
	
	
	private final Queue<HQStoreTaskThread> idleThreads=new ConcurrentLinkedQueue<HQStoreTaskThread>();
	//private final Queue<HQStoreTaskThread> workingThreads=new ConcurrentLinkedQueue<HQStoreTaskThread>();
	
	public boolean init(){
		for(int i=0;i<MINTHREADNUM;i++){
			createTaskThread();
		}
		return true;
	}
	
	public void workTask(HQStoreTaskInterface task){
		// 分配更多的线程
		if(idleThreads.isEmpty()){
			if(nowThreadNum+10<=MAXTHREADNUM){
				for(int i=0;i<10;i++){
					createTaskThread();
				}
			}else if(nowThreadNum==MAXTHREADNUM){
				try {
					throw new HQThreadNumOverHeap("store task threads is too much , max num = "+MAXTHREADNUM);
				} catch (HQThreadNumOverHeap e) {
					HQManageExceptions.getInstance().manageExceptions(e);
				}
			}else{
				for(int i=0;i<MAXTHREADNUM-nowThreadNum;i++){
					createTaskThread();
				}
			}
		}
		HQStoreTaskThread taskThread=idleThreads.poll();
		taskThread.workTask(task);
	}
	
	private void createTaskThread(){
		HQStoreTaskThread taskThread=new HQStoreTaskThread();
		taskThread.start();
		idleThreads.add(taskThread);
		nowThreadNum++;
	}
	
	
	
	public int getNowThreadNum() {
		return nowThreadNum;
	}
	public Queue<HQStoreTaskThread> getIdleThreads() {
		return idleThreads;
	}



	class HQStoreTaskThread extends Thread{
		private HQStoreTaskInterface task=null;
		public void workTask(HQStoreTaskInterface task){
			this.task=task;
			synchronized (this) {
				notify();
			}
		}
		@Override
		public void run() {
			while(true){
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				task.handle();
				// 处理完之后，把它放入空闲线程
				idleThreads.add(this);
			}
		}
	}
}
