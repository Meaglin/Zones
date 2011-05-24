package com.zones.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


public class FileUtil {
    
    public static boolean copyFile(File in, File out) {
        try {
            return copyFile(new FileInputStream(in), new FileOutputStream(out));
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean copyFile(InputStream in, File out) {
        try {
            return copyFile(in, new FileOutputStream(out));
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean copyFile(InputStream input, OutputStream output) {
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = input.read(buf)) > 0){
              output.write(buf, 0, len);
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(input != null) input.close();
                if(output != null) output.close();
            } catch(Exception e) { }
        }
        return true;
    }
    
    public static String readFile(File in) {
        try {
            return readFile(new FileInputStream(in));
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String readFile(InputStream input) {
        BufferedInputStream reader = null;
        String rt = null;
        try {
            byte[] buffer = new byte[input.available()];
            reader = new BufferedInputStream(input);
            reader.read(buffer);
            reader.close();
            input.close();
            rt = new String(buffer);
        } catch(Exception e) {
            e.printStackTrace();
            return rt;
        } finally {
            try{
                if(input != null)input.close();
                if(reader != null)reader.close();
            } catch(Exception e) {}
        }
        
        return rt;
    }
    
    public static boolean writeFile(File out, String text) {
        try {
            return writeFile(new FileOutputStream(out), text);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean writeFile(OutputStream output, String text) {
        OutputStreamWriter out = null;
        BufferedWriter buffer = null;
        try {
            out = new OutputStreamWriter(output);
            buffer = new BufferedWriter(out);
            buffer.write(text);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(buffer != null) buffer.close();
                if(out != null) out.close();
                if(output != null) output.close();
            } catch(Exception e) { }
        }
        return true;
    }
}
