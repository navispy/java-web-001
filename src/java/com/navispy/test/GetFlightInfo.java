/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.navispy.test;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//import org.glassfish.json.*;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonReader;
import javax.json.JsonValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;

import java.lang.Object;
/**
 *
 * @author knud
 */

public class GetFlightInfo extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    protected double getCargoWeightByFlight(String flightID, JsonArray allCargo) {
        
        HashMap<String, JsonArray> hm = new HashMap();

        for (JsonValue cargo : allCargo) {
            JsonObject obj = cargo.asJsonObject();            
            JsonArray cargoRecs = obj.getJsonArray("cargo");
            
            for (JsonValue rec : cargoRecs) {
                 JsonObject recObj = rec.asJsonObject();
                 String strWeight = recObj.getString("weight");
                 String strWeightUnit = recObj.getString("weightUnit");
                 
                 
            }
            
            System.out.println(cargo);
        }
        
        return 0.00;
    }
    
    protected double getBaggageWeightByFlight(String flightID, JsonArray cargo) {
        
        return 0.00;
    }
    
    protected JsonArray getJsonRecs(String path) {
        
        String realPath = this.getServletContext().getRealPath(path);
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
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet GetFlightInfo</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetFlightInfo at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        String flightId = request.getParameter("flightID");
        
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        String flightID = request.getParameter("flightID");
        // read json file
        
        JsonArray flights = null;
        JsonArray cargo = null;
        
        try {
            flights = getJsonRecs("flights.json");
            cargo = getJsonRecs("cargo.json");
            
            double cargoWeight = getCargoWeightByFlight(flightID, cargo);
            double baggageWeight = getBaggageWeightByFlight(flightID, cargo);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        ///////////////////////////////////////////////////////////////////////
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObjectBuilder jsonBuilder;
        //Json.createWriter(out)
        try {
            jsonBuilder = Json.createObjectBuilder();
        } catch (JsonException e) {
            String errorMsg = e.toString();
        } finally {
            jsonBuilder = Json.createObjectBuilder();
        }
        
        jsonBuilder.add("flightID", flightID);
        jsonBuilder.add("totalWeight", 0);
        JsonObject json = jsonBuilder.build(); 
        
        //request.setAttribute("ArraySize", id);
       // getServletContext().getRequestDispatcher("ResultServlet").forward(request,response);
        PrintWriter out = response.getWriter();
        out.write(json.toString());
        out.flush();
        out.close();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
