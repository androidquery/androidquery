package com.androidquery.test;

public class ActivityItem {

	private String name;
	private Class<?> cls;
	private String type;
	
	public boolean isLink(){
		return type.startsWith("http");
	}
	
	
	public ActivityItem(Class<?> cls, String name, String type){
		this.name = name;
		this.cls = cls;
		this.type = type;
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
	
}
