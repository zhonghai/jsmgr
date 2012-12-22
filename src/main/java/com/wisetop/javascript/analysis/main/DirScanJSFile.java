package com.wisetop.javascript.analysis.main;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

import com.wisetop.javascript.analysis.process.JsAnalysisProcess;
import com.wisetop.npf.util.config.ConfigManager;


public class DirScanJSFile implements Runnable {
	private static String app="";
	private static Connection conn = null;
	private static String scandir = "";
	private static String intervalTime = "";

	private static Logger log = Logger.getLogger( DirScanJSFile.class );
	public static void main(String[] args){
		if(args.length == 6){
			// 手动调用一次模式
			System.out.println("手动调用模式");
			String ip=args[0];
			String sid=args[1];
			String user=args[2];
			String pw=args[3];
			app=args[4];
			scandir=args[5];
			intervalTime = "0";		
			conn  = getConn(ip,sid,user,pw);
		}else{
			// 定时调用
			System.out.println("定时调用");
			app = ConfigManager.getString("app");
			conn = ConfigManager.getConn();
			scandir = ConfigManager.getString("scandir");
			intervalTime = ConfigManager.getString("intervalTime");			
		}

		if(conn == null) {
			System.out.println("数据库为空!");
			return;
		}else{
			// 初始化数据库
			initDb(conn);
		}
		
		DirScanJSFile ds = new DirScanJSFile();
		Thread t = new Thread(ds);
		t.run();
	}
	private static void initDb(Connection conn) {
		String	sql = "";
		sql = " create table t_jsfile( " +
				" fileid varchar2(400)," +
				" fapp varchar2(30)," +
				" fpath varchar2(1000)," +
				" ffilename varchar2(255)," +
				" fremark varchar2(2000)," +
				" constraint pk_jsfile_fileid primary key (fileid) " +
				" ) ";
		try{
			conn.createStatement().execute(sql);
		}catch(Exception e){
			e.printStackTrace();
		}
		sql = " create table t_jsfuncs( " +
				" funcid varchar2(400), " +
				" fileid varchar2(400), " +
				" filename varchar2(1000), " +
				" ffuncname varchar2(255), " +
				" fparam varchar2(2000), " +
				" fremark varchar2(2000), " +
				" constraint pk_jsfuncs_funcid primary key (funcid)  " +
				" ) " ;
		try{
			conn.createStatement().execute(sql);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void run() {
		com.wisetop.base.work.impl.DirScanWorkImpl dsw = new com.wisetop.base.work.impl.DirScanWorkImpl();

		if(intervalTime.isEmpty()) intervalTime = "60000";
		dsw.setSrcDir(scandir);
		dsw.setFilefilter(".js");
		dsw.setIfScanChild(true);
		JsAnalysisProcess jap = new JsAnalysisProcess();
		jap.setConn(conn);
		jap.setApp(app);
		
		while(true){
			try{
				try{
					log.debug("work begin......");
					dsw.setProcess(jap);
					dsw.work();
					log.debug("work succ......");
				}catch(Exception e){
					e.printStackTrace();
					log.debug("work err......" + e.getMessage());
				}
				Thread.sleep(Long.valueOf(intervalTime));				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}

	private static Connection getConn(String ip,String sid,String user,String pw){
		  try {
				Class.forName(ConfigManager.getString("driver")).newInstance();
				Connection conn = DriverManager.getConnection(
						"jdbc:oracle:thin:@" + ip + ":1521:" + sid,
						user,
						pw);
				conn.setAutoCommit(false);
				return conn;
			  }
			  catch (Exception ex) {
				System.out.println(ex);
				return null;
			  }		
	}

}
