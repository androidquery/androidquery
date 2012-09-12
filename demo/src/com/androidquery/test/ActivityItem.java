package com.androidquery.test;

public class ActivityItem {

	private String name;
	private Class<?> cls;
	private String type;
	private String meta;
	
	public boolean isLink(){
		return type.startsWith("http");
	}
	
	
	public ActivityItem(Class<?> cls, String name, String type, String meta){
		this.name = name;
		this.cls = cls;
		this.type = type;
		this.meta = meta;
	}

	public String getName() {
		return name;
	}

	public Class<?> getActivityClass() {
		return cls;
	}
	
	public String toString(){
		return name;
	}
	
	public String getType(){
		return type;
	}
	
	public String getMeta() {
		return meta;
	}
	
}
