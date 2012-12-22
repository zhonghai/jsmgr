package com.wisetop.javascript.analysis.domain;

/**
 *  * t_jsfuncs
 create table t_jsfuncs(
 	funcid varchar2(400),
 	fileid varchar2(400),
 	filename varchar2(1000),
 	ffuncname varchar2(255),
 	fparam varchar2(2000),
 	fremark varchar2(2000),
 	constraint pk_jsfuncs_funcid primary key (funcid) 
 );
 * @author user
 *
 */
public class JsFunc {
	String	funcid;
	String	fileid;
	String	filename;
	String	ffuncname;
	String	fparam;
	String	fremark;
	String	ffullstr;
	public String getFuncid() {
		return funcid;
	}
	public void setFuncid(String funcid) {
		this.funcid = funcid;
	}
	public String getFileid() {
		return fileid;
	}
	public void setFileid(String fileid) {
		this.fileid = fileid;
	}
	
	public String getFfullstr() {
		return ffullstr;
	}
	public void setFfullstr(String ffullstr) {
		this.ffullstr = ffullstr;
	}
	public String getFilename() {
		return filename;
	}
	
	public String getFfuncname() {
		return ffuncname;
	}
	public void setFfuncname(String ffuncname) {
		this.ffuncname = ffuncname;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFparam() {
		return fparam;
	}
	public void setFparam(String fparam) {
		this.fparam = fparam;
	}
	public String getFremark() {
		return fremark;
	}
	public void setFremark(String fremark) {
		this.fremark = fremark;
	}
	
	
}
