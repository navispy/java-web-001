/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.navispy.test;

import jakarta.servlet.ServletContext;
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
import javax.json.JsonReader;
import javax.json.JsonValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 *
 * @author knud
 */
@WebServlet(name = "GetAirportInfo", urlPatterns = {"/GetAirportInfo"})
public class GetAirportInfo extends HttpServlet {

    protected double getNumPiecesByFlight(int flightID, JsonArray allRecs, String tag) {
        
        double totalPieces = 0;
        
        for (JsonValue rec : allRecs) {
            JsonObject obj = rec.asJsonObject();            
            
            JsonArray tagRecs = obj.getJsonArray(tag);
            int jsonFlightID = obj.getInt("flightId");
            
            if(flightID != jsonFlightID) {
                continue;
            }
            
            for (JsonValue tagRec : tagRecs) {
                 JsonObject recObj = tagRec.asJsonObject();
        
                 int pieces = recObj.getInt("pieces");                         
                 totalPieces += pieces;
            }
            
        }
        
        return totalPieces;
    }   
    protected double[] getNumFlights(JsonArray allFlights, JsonArray allRecs, String airport, String date, String tag) {
        
        double numFlights = 0;
        double numPieces = 0;
        
        for (JsonValue flight : allFlights) {
            JsonObject obj = flight.asJsonObject();
            String jsonAirport = obj.getString(tag);
            int flightID = obj.getInt("flightId");
            
            // we assume that arrival and departure ins on the same date
            // in json there's departure date only
            String jsonDateUnfixed = obj.getString("departureDate"); 
            
            // because it contains the time we need to get rid of hours, minutes and seconds
            String jsonDate = jsonDateUnfixed.substring(0, 10);
            
            if(!airport.equals(jsonAirport) || !date.equals(jsonDate)) {
                continue;
            }
            
            double numFlightPieces = getNumPiecesByFlight(flightID, allRecs, "baggage");
            numPieces += numFlightPieces;
            numFlights++;
        }
        
        double[] result = {numFlights, numPieces}; 
        return result;
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet GetAirportInfo</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetAirportInfo at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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
        
        String airport = request.getParameter("airportCode");
        String date = request.getParameter("date");
        
        JsonArray flights = null;
        JsonArray recs = null;
        
        double numFlightsIn = 0;
        double numFlightsOut = 0;
        double numPiecesIn = 0;
        double numPiecesOut = 0;
            
        try {
            
            ServletContext ctx = this.getServletContext();
            
            flights = Helper.getJsonRecs("flights.json", ctx);
            recs = Helper.getJsonRecs("cargo.json", ctx);

            double[] resIn = getNumFlights(flights, recs, airport, date, "arrivalAirportIATACode");
            double[] resOut = getNumFlights(flights, recs, airport, date, "departureAirportIATACode");
            
            numFlightsIn = resIn[0];
            numPiecesIn = resIn[1];
            
            numFlightsOut = resOut[0];
            numPiecesOut = resOut[1];
            
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
        
        jsonBuilder.add("airportCode", airport);
        jsonBuilder.add("numFlightsIn", numFlightsIn);
        jsonBuilder.add("numFlightsOut", numFlightsOut);
        jsonBuilder.add("numPiecesIn", numPiecesIn);
        jsonBuilder.add("numPiecesOut", numPiecesOut);
        
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
