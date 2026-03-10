/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

//http://localhost:8080/Coursework/webresources/dbtest test db
//need to link so that i can test or services (create re, scancel re and search items) via http
//also need to link the osrm api to items maybe? and incorporate distance into json files.

package osrm;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 *
 * @author emset
 */

public class DatabaseConnector {
    private static final String ENDPOINT = System.getenv("COSMOS_ENDPOINT");
    private static final String KEY = System.getenv("COSMOS_KEY");
    private static final String DATABASE_NAME = System.getenv("COSMOS_DATABASE");
    private static final String CONTAINER_NAME = "items";
    
    
    
    //turning the print into a service In my own way not utilising lab code.
    private final CosmosClient client;
    private final CosmosContainer itemsContainer;
    private final CosmosContainer requestsContainer;
    
    public DatabaseConnector() {
        if (ENDPOINT == null || KEY == null) {
            throw new IllegalStateException("Cosmos DB credentials not configured via environment variables");
        }
        //building a new client
        this.client  = new CosmosClientBuilder()
                .endpoint(ENDPOINT)
                .key(KEY)
                .consistencyLevel(ConsistencyLevel.EVENTUAL)
                .buildClient();
        
        
        //initlising db values
        CosmosDatabase db = client.getDatabase(DATABASE_NAME);
        this.itemsContainer = db.getContainer("items");
        this.requestsContainer = db.getContainer("requests");
    
    }
    
    //REQUIREMENT COMPLETED: filter bt at least 1 item!
    
    //updated ! method to search avialable items with filters, using location & price as filters
    //using lab code logic
    
    //can expand parameters when suitable (if I have time)
    public String searchItems(String location, Double maxPrice, int page, int pageSize) {
        //cosmos db supports offset/limit (servr side paging)
        int offset = (page -1) * pageSize;

        // updated sql for projection with only neded fields. No longer pulling entire docs
        String sql = "SELECT c.name, c.location, c.daily_rate, c.category, c.available, c.description FROM c WHERE c.location = @location ";
        if (maxPrice != null) {
            sql += " AND c.daily_rate <= @maxPrice";
            //if price is not entered
        } else {
            sql = "SELECT * FROM c WHERE c.location = @location";
        }
        
        sql += " ORDER BY c.daily_rate OFFSET @offset LIMIT @limit";
        
        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

        //build parameters list
        List<SqlParameter> params = new ArrayList<>();
        params.add(new SqlParameter("@location", location));

        if (maxPrice != null) {
            params.add(new SqlParameter("@maxPrice", maxPrice));
        }
        
        params.add(new SqlParameter("@offset", offset));
        params.add(new SqlParameter("@limit", pageSize));

        SqlQuerySpec querySpec = new SqlQuerySpec(sql, params);

        CosmosPagedIterable<ObjectNode> items = itemsContainer.queryItems(querySpec, options, ObjectNode.class);

        //build output string + print to console
        StringBuilder sb = new StringBuilder();
        
        
        sb.append("Items available at ").append(location);
        
        //adds price filter if entered
        if (maxPrice != null) {
            sb.append(" with max price £").append(maxPrice);
        }
        
        

        int count = 0;
        
        sb.append(":Page: ").append(page).append(" (pageSize = ").append(pageSize).append(")\n\n");

        for (ObjectNode item : items) {
            count++;
            //prints important item info to screen.
            String name = item.get("name").asText();
            String loc = item.get("location").asText();
            double price = item.get("daily_rate").asDouble();
            String cat = item.get("category").asText();
            boolean availability = item.get("available").asBoolean();
            String des = item.get("description").asText();

            // Console output (simple summary)
            System.out.println(name + " - " + loc + " (£" + price + ") " + (availability ? "[Available]" : "[Not Available]"));

            //output (multi-line entry for each item)
            sb.append("Name: ").append(name).append("\n")
            .append("Location: ").append(loc).append("\n")
            .append("Daily Rate: £").append(price).append("\n")
            .append("Category: ").append(cat).append("\n")
            .append("Availability: ").append(availability ? "Available" : "Not Available").append("\n")
            .append("Description: ").append(des).append("\n")
            .append("--------------------------------------------\n");
        }


        if (count == 0) {
        sb.append("No items found.\n");
        }

        return sb.toString();
    }
    
    //helper funciton to create request ID
    private String generateNextRequestId() {
    //Query all request IDs sorted descending
    CosmosPagedIterable<ObjectNode> results = requestsContainer
            .queryItems("SELECT TOP 1 c.request_id FROM c WHERE IS_DEFINED(c.request_id) ORDER BY c.request_id DESC",
                    new CosmosQueryRequestOptions(),
                    ObjectNode.class);

        //if none exist, start at r1
        for (ObjectNode r : results) {
            
            if (r.get("request_id") == null) break;
            String lastId = r.get("request_id").asText();

            if (lastId == null || !lastId.startsWith("rq")) {
                break;  // avoid crashing on bad data
            }   

            try {
                int number = Integer.parseInt(lastId.substring(2));
                return String.format("rq%03d", number + 1);
            } catch (Exception e) {
                break;  // fallback
            }
        }

    return "rq001";
    }
    
    //method to create a new request
    public Request createRequest(String itemId, String userId, int rentalDuration, String message) {
        //create an object of the requets class
        Request req = new Request();
        
        String newID = generateNextRequestId();
        
        //set all variables for the request using method params and gen id & date
        req.setId(newID);
        req.setRequestId(newID);
        req.setItemId(itemId);
        req.setUserId(userId);
        req.setStatus("pending");
        req.setRentalDuration(rentalDuration);
        req.setMessage(message);
        req.setDateRequested(new Date());
        
        //adds new item to the crequests container
        //cosmost converts ot json format automatically 
        requestsContainer.createItem(
            req,
                // might have issues here depending on how I saved the db key _id or id??
            new PartitionKey(req.getRequestId()),
            new CosmosItemRequestOptions()
        );
    return req;    
    }

    
    //method to cancel request - changed to boolean for better validation 
    public boolean cancelRequest(String requestId) {
        try {
        requestsContainer.deleteItem(
                requestId,
                new PartitionKey(requestId), //partition key is /request_id
                new CosmosItemRequestOptions()
        );
        System.out.println("Request " + requestId + " deleted from Cosmos.");
        return true;

        } 
        catch (CosmosException e) {
            //returns correct error code if not found
            if (e.getStatusCode() == 404) {
                System.out.println("Request " + requestId + " not found.");
                return false;
            }
            throw e;
        }
    }
       
    
    //for testing db connection :)
    public void testQuery () {
        //Used same code as form labs: 
        
        // Connect to Cosmos DB
        try (CosmosClient client = new CosmosClientBuilder()
                .endpoint(ENDPOINT)
                .key(KEY)
                .consistencyLevel(ConsistencyLevel.EVENTUAL)
                .buildClient()) {
            
            CosmosContainer container = client.getDatabase(DATABASE_NAME).getContainer(CONTAINER_NAME);
            
            // Create the query. We want to fetch all items available at a location
            String location = "Nottingham";
            
            // @location references the location that we will pass in the SqlParameter below
            // c refers to container. "SELECT * FROM c" would return ALL your items.
            String sql = "SELECT * FROM c WHERE c.location = @location";
            
            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
            
            //Build a new SqlParameter and assign location to @location
            SqlParameter param = new SqlParameter("@location", location);
            
            
            SqlQuerySpec querySpec = new SqlQuerySpec(sql, Arrays.asList(param));
            
            // I am dynamically deserializing my json object.
            // But in your project, you need to desrialize using custom classes (creating a class "Item")
            CosmosPagedIterable<ObjectNode> items = container.queryItems(querySpec, options, ObjectNode.class);
            
            System.out.println("The following items are available for rent at " + location + ":");
            for (ObjectNode item : items) {
                System.out.println(item.get("name").asText() + " - " + item.get("location").asText());
            }
            
            // Display entire json
            System.out.println("\nThe following items, displayed as JSON, are available for rent at " + location + ":");
            for (ObjectNode item : items) {
                    System.out.println(item.toString()+"\n");
            }
            // Close connection to the client.
            client.close();
        }
        catch (Exception e) {
            System.err.println("DB ERROR: " + e.getMessage());
            throw e; // so the REST endpoint sees it
        }
  
    }
}
