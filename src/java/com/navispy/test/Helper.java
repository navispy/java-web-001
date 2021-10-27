/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.navispy.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

import jakarta.servlet.ServletContext;
/**
 *
 * @author knud
 */
public class Helper {
    public static JsonArray getJsonRecs(String path, ServletContext ctx) {
        
        String realPath = ctx.getRealPath(path);
        File jsonInputFile = new File(realPath);
        InputStream is;
        
        JsonArray recs = null;
        
        try {
            is = new FileInputStream(jsonInputFile);
            // Create JsonReader from Json.
            JsonReader reader = Json.createReader(is);
            // Get the JsonObject structure from JsonReader.
            recs = reader.readArray();
            reader.close();
     
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
                
        return recs;
    }
}
