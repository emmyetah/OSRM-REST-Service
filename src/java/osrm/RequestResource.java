/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osrm;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 *
 * @author emset
 */

@Path("items")
public class RequestResource {
    
    //D1 : security considerations
    private static final String API_KEY = "SECRET_KEY_123";
    //first i need a database object to access db functions
    private final DatabaseConnector db = new DatabaseConnector();
    
    //Need Method to search for items via http - sample url below
    //http://localhost:8080/Coursework/webresources/items?location=Nottingham&maxPrice=20.0
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //updated to improve test results. Decided to use pagination
    
    public Response searchItems(//updated params to accept paging
            @QueryParam("location")String location,
            @QueryParam("maxPrice")Double maxPrice,
            //added default values so we can keep urls the same
            @QueryParam("page")@DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize)
        {
            
        if (location == null || location.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("location query parameter is required. Example: ?location=Nottingham"))
                .build();
        }
        if (page < 1) {
            page = 1;
        }
        if(pageSize< 1) {
            pageSize = 20;
        }
        //put a hard upper limit to protect server
        if (pageSize > 50) {
            pageSize = 50; 
        }

        System.out.println("HTTP /items called with location=" + location +
                           ", maxPrice=" + maxPrice);

        String json = db.searchItems(location, maxPrice, page, pageSize);
        return Response.ok(json).build();
    }
              
    //method to create a request
    @POST
    @Path("request")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    
    public Response createRequest(Request request,
            @HeaderParam("X-API-Key") String apiKey
            
    ) {
        //added right at the beginning to ensure nothing is carried out unless the api key is corrcet.
        if (apiKey == null || !apiKey.equals(API_KEY)) {
        
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("Unauthorised: missing or invalid API key."))
                .build();
        }
        
        System.out.println("POST /items/request called");
        
        if (request == null
            || request.getItemId() == null || request.getItemId().isBlank()
            || request.getUserId() == null || request.getUserId().isBlank()
            || request.getRentalDuration() <= 0) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Missing/invalid fields: itemId, userId, rentalDuration."))
                .build();
        }
        //display the user that wants to rent and the id of the item that they want to rent
        //callS method from the db here
        Request saved = db.createRequest(
            request.getItemId(),
            request.getUserId(),
            request.getRentalDuration(),
            request.getMessage()
        //removed last to params as client doesn't need to send t
        );
        
        return Response.status(Response.Status.CREATED)
            .entity(saved)
            .build();             
    }
 
    //also added api key to this method.
    
    //Correct Delete Http Method
    @DELETE
    @Path("request")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelRequest(
            Request request,
            @HeaderParam("X-API-Key") String apiKey){
        
        if (apiKey == null || !apiKey.equals(API_KEY)) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("Unauthorised: missing or invalid API key."))
                .build();
        }
        
        //error handling for no request entered
        if (request == null || request.getRequestId() == null || request.getRequestId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("requestId is required."))
                .build();
        }
        
        String requestId = request.getRequestId().trim();
        boolean success = db.cancelRequest(requestId);

        if (!success) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Request not found: " + requestId))
                .build();
        }
        System.out.println("DELETE called for requestId: " + requestId);
        
        return Response.ok(new MessageResponse("Request cancelled", requestId)).build();
       
    }
}
