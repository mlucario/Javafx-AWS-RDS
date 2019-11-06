package com.quy.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class FileHandler {
	 // get file from classpath, resources folder
    public File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }

	public ArrayList<String> readFile (File file) throws IOException, IOException{
		ArrayList<String> result = new ArrayList<>();
		if (file == null) return null;
		
		
		 try (FileReader reader = new FileReader(file);
	             BufferedReader br = new BufferedReader(reader)) {

			
	            String line;
	            while ((line = br.readLine()) != null) {
//	                System.out.println(line);
	            	result.add(line);
	            	
	            }
	        }
		
//		try (FileReader f = new FileReader(pathFilename)) {
//		    StringBuffer sb = new StringBuffer();
//		    try {
//		    	 while (f.ready()) {
//				        char c = (char) f.read();
//				        if (c == '\n') {
//				            result.add(sb.toString());
//				            sb = new StringBuffer();
//				        } else {
//				            sb.append(c);
//				        }
//				    }
//				    if (sb.length() > 0) {
//				        result.add(sb.toString());
//				    }
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		   
//		} catch (FileNotFoundException e1) {
//			System.out.println("Print out file not founds");
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}     
		 
		return result;

		
	}
}
