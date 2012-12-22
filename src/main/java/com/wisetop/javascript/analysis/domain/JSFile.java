package com.wisetop.javascript.analysis.domain;

import java.util.Map;

/**
 * t_jsfile
 create table t_jsfile(
 	fileid varchar2(400),
 	fapp varchar2(30),
 	fpath varchar2(1000),
 	ffilename varchar2(255),
 	fremark varchar2(2000),
 	constraint pk_jsfile_fileid primary key (fileid) 
 );
 * @author user
 *
 */
public class JSFile {
	private String	fileid;
	private String	fapp;
	private String	fpath;
	private String	ffilename;
	private String	fremark;
	private Map 		funcs;
	
	public String getFileid() {
		return fileid;
	}
	public void setFileid(String fileid) {
		this.fileid = fileid;
	}

	public String getFapp() {
		return fapp;
	}
	public void setFapp(String fapp) {
		this.fapp = fapp;
	}
	public String getFpath() {
		return fpath;
	}
	public void setFpath(String fpath) {
		this.fpath = fpath;
	}
	public String getFfilename() {
		return ffilename;
	}
	public void setFfilename(String ffilename) {
		this.ffilename = ffilename;
	}
	public String getFremark() {
		return fremark;
	}
	public void setFremark(String fremark) {
		this.fremark = fremark;
	}
	public Map getFuncs() {
		return funcs;
	}
	public void setFuncs(Map funcs) {
		this.funcs = funcs;
	}
	
}
