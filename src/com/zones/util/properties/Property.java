package com.zones.util.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Property implements Comparable<Property> {
	private int index;
	private String key;
	private String value;
	private List<String> comments = null;
	private Properties parent;
	
	public Property(int index, String key, String value, String[] comments) {
		this.index = index;
		this.key = key == null ? key : key.trim();
		this.value = value;
		setComments(comments);
	}
	public Property(int index, String key, String value) { this(index, key, value, null); }
	
	protected Property() { }
	protected Property(int index) {
		this.index = index;
	}

	public void setIndex(int newindex) {
		index = newindex;
	}
	
	public int getIndex() {
		return index;
	}
	
	protected void setKey(String newkey) {
		key = newkey.trim();
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String newvalue) 	{ value = newvalue.trim();	}
	public void setValue(int newvalue) 		{ setValue(Integer.toString(newvalue)); }
	public void setValue(double newvalue) 	{ setValue(Double.toString(newvalue)); }
	public void setValue(float newvalue) 	{ setValue(Float.toString(newvalue)); }
	public void setValue(long newvalue) 	{ setValue(Long.toString(newvalue)); }
	
	public List<String> getComments() {
		return comments;
	}
	
	public void setComments(String[] newcomments) {
		comments = Arrays.asList(newcomments);
	}
	
	public void addComment(String comment) {
		if(comment == null) return;
		if(comments == null) comments = new ArrayList<String>();
		comments.add(comment);
	}
	
	public boolean hasComments() {
		return comments != null && comments.size() != 0;
	}
	
	public boolean isBoolean() { return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"); }
	public boolean isInt() { try { Integer.parseInt(value); } catch( NumberFormatException e) { return false; } return true; }
	public boolean isDouble() { try { Double.parseDouble(value); } catch( NumberFormatException e) { return false; } return true; }
	public boolean isFloat() { try { Float.parseFloat(value); } catch( NumberFormatException e) { return false; } return true; }
	public boolean isLong() { try { Long.parseLong(value); } catch( NumberFormatException e) { return false; } return true; }

	public boolean toBoolean() { 
		return (value != null && value.equalsIgnoreCase("true"));	
	}
	
	public int toInt() {
		try {
			return Integer.parseInt(value);
		} catch(Exception e) { return 0; }
	}
		
	public double toDouble() {
		try {
			return Double.parseDouble(value);
		} catch(Exception e) { return 0.0d; }
	}
	
	public float toFloat() {
		try {
			return Float.parseFloat(value);
		} catch(Exception e) { return 0.0F; }
	}
	
	public long toLong() {
		try {
			return Long.parseLong(value);
		} catch (Exception e) { return 0L; }
	}
	
	public void addAfter(Property property) {
		if(getParent() == null) return;
		property.setIndex(getIndex()+1);
		getParent().addProperty(property);
	}
	
	public void addBefore(Property property) {
		if(getParent() == null) return;
		property.setIndex(getIndex()-1);
		getParent().addProperty(property);
	}
	
	@Override
	public int compareTo(Property o) {
		return getIndex() - o.getIndex();
	}
	
	public void setParent(Properties properties) {
		this.parent = properties;
	}
	
	public Properties getParent() {
		return parent;
	}
}
