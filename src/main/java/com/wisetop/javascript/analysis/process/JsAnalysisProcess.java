package com.wisetop.javascript.analysis.process;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.wisetop.base.work.WorkProcessParent;
import com.wisetop.base.work.impl.DirScanWorkImpl;
import com.wisetop.javascript.analysis.domain.JSFile;
import com.wisetop.javascript.analysis.domain.JsFunc;
import com.wisetop.npf.util.Common;
import com.wisetop.npf.util.classop.ClassUtil;
import com.wisetop.npf.util.config.ConfigManager;
import com.wisetop.npf.util.db.DbDataUtil;

public class JsAnalysisProcess extends WorkProcessParent {
	private  String app = "";
	private com.wisetop.base.work.impl.DirScanWorkImpl mywork = null;
	private File curFile = null;
	private JSFile jsFile = null;
	private Connection	conn=ConfigManager.getConn();
	public static void main(String[] args) throws Exception{
//		String	str = "";
//		str = " a aabasdf   function sdf(a a)";
//		Pattern p =Pattern.compile("(function )");
//		Matcher m = p.matcher(str);
//		while (m.find()) {
//			System.out.println(m.group());
//		}
//		
//		
//		if(1==1) return;		
		
		com.wisetop.base.work.impl.DirScanWorkImpl dsw = new com.wisetop.base.work.impl.DirScanWorkImpl();
		dsw.setSrcDir("I:\\svn\\工程文档\\最新程序\\js脚本\\scripts\\");
		dsw.setFilefilter(".js");
		dsw.setIfScanChild(true);
		JsAnalysisProcess jap = new JsAnalysisProcess();
		
		try{
			dsw.setProcess(jap);
			dsw.work();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void doBusiProcessBefore() throws Exception {
		jsFile = null;
		mywork = (DirScanWorkImpl) this.getWork();
		curFile = mywork.getCurFile();
		String	fileContent = "";
		
		if(curFile.isDirectory()) return;
		
		byte[] filebytes = FileUtils.readFileToByteArray(mywork.getCurFile());
		if(filebytes[0] == -17 && filebytes[1] == -69 && filebytes[2] == -65){
			// utf-8
			fileContent = FileUtils.readFileToString(mywork.getCurFile(),"utf-8");
		}else{
			fileContent = FileUtils.readFileToString(mywork.getCurFile());
		}
				
		System.out.println(curFile.getAbsolutePath());
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(fileContent)){
			jsFile = analysisFile(fileContent);
		}

	}

	private JSFile analysisFile(String fileContent){
		String	fileHead = "";
		if("/**".equals(fileContent.substring(0, 3))){
//			System.out.println("head:=====" + curFile.getAbsolutePath());
			fileHead = fileContent.substring(0, fileContent.indexOf("*/") + 2);
			fileContent = fileContent.substring(fileContent.indexOf("*/") + 3);
			//System.out.println("============" + fileHead + "========");
		}
		String	fileid = "";
		String	filename = "";
		filename = curFile.getName();
		JSFile jsFile = new JSFile();
		jsFile.setFapp(app);
		jsFile.setFfilename(filename);
		jsFile.setFpath(mywork.getCurcPath());
		jsFile.setFremark(fileHead);
		jsFile.setFuncs(new HashMap());

		fileid = app + ":" + mywork.getCurcPath() + filename;
		jsFile.setFileid(fileid);
		if("public:wisetop.util.WindowSrv.js".equals(fileid)){
			System.out.println("debug");
		}
		analysisFileSub1(fileid,filename,fileContent,jsFile.getFuncs());
		analysisFileSub2(fileid,filename,fileContent,jsFile.getFuncs());
		analysisFileSub3(fileid,filename,fileContent,jsFile.getFuncs());
		System.out.println("size:" + jsFile.getFuncs().size());
		for(Object key:jsFile.getFuncs().keySet()){
			JsFunc jsFunds = (JsFunc) jsFile.getFuncs().get(key);
			System.out.println("===" + key + "===" + jsFunds.getFfuncname() + "==" + jsFunds.getFparam() + "===" + jsFunds.getFremark() + "===" + jsFunds.getFfullstr());
		}
		
		return jsFile;
	}
	
	private void analysisFileSub1(String fileid,String filename,String	fileContent,Map data){
		List funcs = regexp(fileContent,"(\\/\\*(([.\\s\\S])(?!\\/\\*))*\\*\\/)*[\r\n\\s]*[A-Za-z0-9_\\.]+[\r\n\\s]*:[\r\n\\s]*function *\\([A-Za-z0-9, _$]*\\)");
		String	funcid;
		String	ffuncname;
		String	fparam;
		String	fremark;
		String	fstemp;
		for(int i=0;i<funcs.size();i++){		
			fstemp = (String) funcs.get(i);
			JsFunc jsFuncs = new JsFunc();
			jsFuncs.setFfullstr(fstemp);
			jsFuncs.setFileid(fileid);	
			fremark = regexpOneRst(fstemp,"(\\/\\*(([.\\s\\S])(?!\\/\\*))*\\*\\/)*");
			fstemp = fstemp.replace(fremark, ""); 
			fparam = regexpOneRst(fstemp,"\\([A-Za-z0-9, _$]*\\)");
			fstemp = fstemp.replace(fparam, ""); 
			ffuncname = regexpOneRst(fstemp, "[A-Za-z0-9_\\.]+[\r\n\\s]*"); 
			jsFuncs.setFileid(fileid);
			jsFuncs.setFilename(filename);
			jsFuncs.setFparam(fparam);
			jsFuncs.setFremark(fremark);
			jsFuncs.setFfuncname(ffuncname);			
			jsFuncs.setFuncid(fileid + "-" + ffuncname + "-" + fparam);
			data.put(jsFuncs.getFuncid(), jsFuncs);
		}
	}
	private void analysisFileSub2(String fileid,String filename,String	fileContent,Map data){
		List funcs = regexp(fileContent,"(\\/\\*(([.\\s\\S])(?!\\/\\*))*\\*\\/)*[\r\n\\s]*[A-Za-z0-9_\\.]+[\r\n\\s]*=[\r\n\\s]*function *\\([A-Za-z0-9, _$]*\\)");
		String	funcid;
		String	ffuncname;
		String	fparam;
		String	fremark;
		String	fstemp;
		for(int i=0;i<funcs.size();i++){		
			fstemp = (String) funcs.get(i);
			JsFunc jsFuncs = new JsFunc();
			jsFuncs.setFfullstr(fstemp);
			jsFuncs.setFileid(fileid);	
			fremark = regexpOneRst(fstemp,"(\\/\\*(([.\\s\\S])(?!\\/\\*))*\\*\\/)*");
			fstemp = fstemp.replace(fremark, ""); 
			fparam = regexpOneRst(fstemp,"\\([A-Za-z0-9, _$]*\\)");
			fstemp = fstemp.replace(fparam, ""); 
			ffuncname = regexpOneRst(fstemp, "[A-Za-z0-9_\\.]+[\r\n\\s]*"); 
			jsFuncs.setFileid(fileid);
			jsFuncs.setFilename(filename);
			jsFuncs.setFparam(fparam);
			jsFuncs.setFremark(fremark);
			jsFuncs.setFfuncname(ffuncname);			
			jsFuncs.setFuncid(fileid + "-" + ffuncname + "-" + fparam);
			data.put(jsFuncs.getFuncid(), jsFuncs);
		}
	}
	private void analysisFileSub3(String fileid,String filename,String	fileContent,Map data){
		List funcs = regexp(fileContent,"(\\/\\*(([.\\s\\S])(?!\\/\\*))*\\*\\/)*[\r\n\\s]*function *[A-Za-z0-9_\\.]+ *\\([A-Za-z0-9, _]*\\)");
		String	funcid;
		String	ffuncname;
		String	fparam;
		String	fremark;
		String	fstemp;
		for(int i=0;i<funcs.size();i++){		
			fstemp = (String) funcs.get(i);
			JsFunc jsFuncs = new JsFunc();
			jsFuncs.setFfullstr(fstemp);
			jsFuncs.setFileid(fileid);	
			fremark = regexpOneRst(fstemp,"(\\/\\*(([.\\s\\S])(?!\\/\\*))*\\*\\/)*");
			fstemp = fstemp.replace(fremark, ""); 
			fparam = regexpOneRst(fstemp,"\\([A-Za-z0-9, _$]*\\)");
			fstemp = fstemp.replace(fparam, ""); 
			fstemp = fstemp.replace("function ", "");
			ffuncname = regexpOneRst(fstemp, "[A-Za-z0-9_\\.]+[\r\n\\s]*"); 
			jsFuncs.setFileid(fileid);
			jsFuncs.setFilename(filename);
			jsFuncs.setFparam(fparam);
			jsFuncs.setFremark(fremark);
			jsFuncs.setFfuncname(ffuncname);			
			jsFuncs.setFuncid(fileid + "-" + ffuncname + "-" + fparam);
			data.put(jsFuncs.getFuncid(), jsFuncs);
		}
	}
	
	private String regexpOneRst(String src,String regexp){
		String	rst = "";
		List rtn = new ArrayList();
		Pattern p =Pattern.compile(regexp);
		Matcher m = p.matcher(src);
		while (m.find()) {
			rst = m.group();
			break;
		}
		return rst;
	}
	private List regexp(String src,String regexp){
		List rtn = new ArrayList();
		Pattern p =Pattern.compile(regexp);
		Matcher m = p.matcher(src);
		try{
			while (m.find()) {
				rtn.add(m.group());
			}			
		}catch(Exception e){
			System.out.println("===================error");
		}

		return rtn;
	}
	public void doBusiProcessAfter() throws Exception {
		if(jsFile == null) return;
		String	sql;
		List lst = null;
		lst = new ArrayList();
		lst.add(jsFile);
		sql = "select count(*) from t_jsfile where fileid='" + jsFile.getFileid() + "'";
		Long cnt =(Long) DbDataUtil.getOneResult(conn, sql);
		if(cnt > 0){
			DbDataUtil.updateTableEx(conn, "t_jsfile", "fileid", "fapp,fpath,ffilename,fremark", ClassUtil.beanToMap(jsFile));
		}else{
			DbDataUtil.insertToTableByBeans(conn, lst, "t_jsfile", "fileid,fapp,fpath,ffilename,fremark");
		}
		
		Map funcs = jsFile.getFuncs();
		
		for(Object key:funcs.keySet()){
			JsFunc jsFunc = (JsFunc) funcs.get(key);
			lst.clear();
			lst.add(jsFunc);
			sql = "select count(*) from t_jsfuncs where funcid='" + jsFunc.getFuncid() + "'";
			cnt =(Long) DbDataUtil.getOneResult(conn, sql);
			if(cnt > 0){
				DbDataUtil.updateTableEx(conn, "t_jsfuncs", "funcid", "ffuncname,fremark", ClassUtil.beanToMap(jsFunc));
			}else{
				DbDataUtil.insertToTableByBeans(conn, lst, "t_jsfuncs", "funcid,fileid,filename,ffuncname,fparam,fremark");
			}
		}
	}

	public void processSucc() throws Exception {
		if(jsFile == null) return;
		conn.commit();

	}

	public void processFailed(Exception e) throws Exception {
		conn.rollback();
	}

	public void processFinished() throws Exception {
		// TODO Auto-generated method stub

	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

}
