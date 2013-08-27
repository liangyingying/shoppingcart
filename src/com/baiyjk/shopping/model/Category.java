package com.baiyjk.shopping.model;

public class Category {
	public String categoryId;
	public String parentId;
	public String name;
	public String categoryLevel;
	public String displayId;
	
	public Category(String categoryId, String parentId, String name,
			String categoryLevel, String displayId) {
		this.categoryId = categoryId;
		this.parentId = parentId;
		this.name = name;
		this.categoryLevel = categoryLevel;
		this.displayId = displayId;
	}

	public Category() {
	}
	
	
}
