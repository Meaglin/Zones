package com.zones.util.properties;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.zones.util.FileUtil;

public class Properties {
	private TrieMap<Property> properties = new TrieMap<Property>();
	private File file;
	private boolean isMissingProperties;
	
    private static Logger     log              = Logger.getLogger(Properties.class.getName());
	
	public Properties() { }
	public Properties(File file) { this(file, false); }
	public Properties(File file, boolean load) {
		this.file = file;
		if(load) load();
	}
	
	public Properties(InputStream resourceAsStream) {
        load(FileUtil.readLines(resourceAsStream));
    }
	
    public boolean load() {
        if(file == null) return false;
        if(!file.canRead()) return false;
        properties.clear();
        return load(FileUtil.readLines(getFile()));
    }
    
    private synchronized boolean load(String[] lines) {	
		Property currentproperty = new Property();
		currentproperty.setIndex(0);
		char[] buffer = new char[1024];
		int bufferindex = 0;
		for(int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			//System.out.println(line);

			if(line.equals("") || line.isEmpty() || line.charAt(0) == '#' || line.charAt(0) == '!') {
				currentproperty.addComment(line);
				continue;
			}
			
			for(char c : line.toCharArray()) {
				if(c == '=') {
					if(currentproperty.getKey() == null) {
						currentproperty.setKey(new String(buffer, 0, bufferindex));
						bufferindex = 0;
						continue;
					}
				}
				buffer[bufferindex++] = c;
			}
			
			
			if(bufferindex == 0 || buffer[bufferindex-1] != '\\') {
				currentproperty.setValue(new String(buffer, 0, bufferindex));
				setProperty(currentproperty);
				currentproperty = new Property(currentproperty.getIndex()+1);
				bufferindex = 0;
			} else {
				bufferindex--; // skip the '\' character.
			}
		}
		buffer = null;
		return true;
	}
	
	public synchronized boolean save(boolean addComments) {
		if(!file.canWrite()) return false;
		List<Property> properties = new ArrayList<Property>();
		properties.addAll(this.properties.values());
		Collections.sort(properties);
		List<String> lines = new ArrayList<String>();
		for(int i = 0; i < properties.size(); i++) {
			Property property = properties.get(i);
			if(addComments && property.getComments() != null) {
				for(String line : property.getComments()) 
					lines.add(line);
			}
			lines.add(property.getKey() + " = " + property.getValue());
		}
		return FileUtil.writeFile(getFile(), lines.toArray(new String[lines.size()]));
	}
	
	public synchronized int restore(Properties original) {
		List<Property> properties = new ArrayList<Property>();
		properties.addAll(original.properties.values());
		Collections.sort(properties);
		Property last = null;
		int count = 0;
		for(Property p : properties) {
			Property current = this.properties.get(p.getKey());
			if(current == null) {
				if(last == null) {
					p.setIndex(0);
					addProperty(p);
				} else {
					last.addAfter(p);
				}
				count++;
				last = p;
			} else {
				last = current;
			}
		}
		return count;
	}
	
	public synchronized Property getProperty(String name) {
	    Property property = properties.get(name);
	    if(property == null) {
	        isMissingProperties = true;
	        log.info("Missing property " + name + " in " + getFile().getName() + "!");
	    }
		return property;
	}
	
	public synchronized void setProperty(Property property) {
		properties.put(property.getKey(), property);
		property.setParent(this);
	}
	
	protected synchronized void addProperty(Property property) {
		Collection<Property> properties = this.properties.values();
		for(Property p : properties)
			if(p.getIndex() >= property.getIndex())
				p.setIndex(p.getIndex()+1);
		setProperty(property);
	}
	
	public boolean isMissingProperties() {
	    return isMissingProperties;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(String newfile) { 	setFile(new File(newfile)); }
	public void setFile(File newfile) {		file = newfile; }
}
