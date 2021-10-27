/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.navispy.test;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonValue;

import jakarta.servlet.ServletContext;

/**
 *
 * @author knud
 */
@WebServlet(name = "GetFlightInfo", urlPatterns = {"/GetFlightInfo"})
public class GetFlightInfo extends HttpServlet {
    
    final static double LB2KG_ratio = 0.453592;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    protected double getWeightByFlight(String flightID, JsonArray allRecs, String tag) {
        
        double totalWeight = 0;
        
        for (JsonValue rec : allRecs) {
            JsonObject obj = rec.asJsonObject();            
            
            JsonArray tagRecs = obj.getJsonArray(tag);
            int jsonFlightID = obj.getInt("flightId");
            
            if(Integer.valueOf(flightID) != jsonFlightID) {
                continue;
            }
            
            for (JsonValue tagRec : tagRecs) {
                 JsonObject recObj = tagRec.asJsonObject();
                 int weight = recObj.getInt("weight");
                 String strWeightUnit = recObj.getString("weightUnit");
                 int pieces = recObj.getInt("pieces");
                 
                 double subTotalWeightUnfixed = weight * pieces;
                 double subTotalWeight = subTotalWeightUnfixed;
                 
                 if(strWeightUnit.equals("lb")) { // in this case we need to convert from lb to kg
                    subTotalWeight = subTotalWeightUnfixed * LB2KG_ratio;
                 }
                 
                 totalWeight += subTotalWeight;
            }
            
        }
        
        return totalWeight;
    }
    
    protected double getBaggageWeightByFlight(String flightID, JsonArray cargo) {
        
        return 0.00;
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
        JsonArray recs = null;
        
        double cargoWeight = 0;
        double baggageWeight = 0;
        double totalWeight = 0;
            
        try {
            
  
            ServletContext ctx = this.getServletContext();
            
            flights = Helper.getJsonRecs("flights.json", ctx);
            recs = Helper.getJsonRecs("cargo.json", ctx);

 
            cargoWeight = getWeightByFlight(flightID, recs, "cargo");
            baggageWeight = getWeightByFlight(flightID, recs, "baggage");
            totalWeight = cargoWeight + baggageWeight;
            
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
        jsonBuilder.add("cargoWeight", cargoWeight);
        jsonBuilder.add("baggageWeight", baggageWeight);
        jsonBuilder.add("totalWeight", totalWeight);
        
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
