/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */

package osrm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author emset
 */
@Path("routes")
public class RouteResource {

    
    
    @Context
    private UriInfo context;
    //mapping a json to store city coordinate linked to city names so thast I can get the proximity
    private static Map<String, double[]> cityCoordsCache = null;
    
    private synchronized void ensureCityCacheLoaded() {
        if (cityCoordsCache != null) {
            return;
        }

        cityCoordsCache = new java.util.HashMap<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            //cities.json is in the same package (osrm)
            try (java.io.InputStream is = RouteResource.class.getResourceAsStream("cities.json")) {
                if (is == null) {
                    //to check if its being read
                    System.err.println("cities.json not found on classpath!");
                    return;
                }

                //Represent each entry as a small helper class or a generic map
                CityCoord[] cities = mapper.readValue(is, CityCoord[].class);

                for (CityCoord c : cities) {
                    if (c.getName() != null) {
                        String key = c.getName().trim().toLowerCase();
                        cityCoordsCache.put(key, new double[]{c.getLon(), c.getLat()});
                    }
                }
                System.out.println("Loaded " + cityCoordsCache.size() + " city coordinates from JSON.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            //if we fail here, lookupCoords will just return null
        }
        
    }
    
    //helper, turns string to lon, lat from the cache
    private double[] lookupCoords(String location) {
        if (location == null) {
            return null;
        }
        ensureCityCacheLoaded();

        if (cityCoordsCache == null) {
            return null;
        }
        
        //changed to lower case for error handling & validation
        String key = location.trim().toLowerCase();
        return cityCoordsCache.get(key);
    }
    
    
    //main proximity method to get distance details 
    @GET
    @Path("proximity")
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public Response getProximity(
            @QueryParam("itemLocation") String itemLocation,
            @QueryParam("userLocation") String userLocation
    ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        
        //first validate inputs
        if (itemLocation == null || itemLocation.trim().isEmpty() || userLocation == null || userLocation.trim().isEmpty()) {
            result.put("error", "Both the items location and the users location query parameters are required.");
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        
        //lookup coordinates from json file
        double[] itemCoords = lookupCoords(itemLocation);
        double[] userCoords = lookupCoords(userLocation);
        
        //check if item location & user location exsist in json
        if(itemCoords == null) {
            result.put("error", "Unknown item location: " + itemLocation);
            return Response.status(Response.Status.NOT_FOUND).entity(result).build();
    }
        
        if (userCoords == null) {
            result.put("error", "Unknown userLocation: " + userLocation);
            return Response.status(Response.Status.NOT_FOUND).entity(result).build();
        }
        
        //if present initialise coordinates
        double userLon = userCoords[0];
        double userLat = userCoords[1];
        double itemLon = itemCoords[0];
        double itemLat = itemCoords[1];
        
        //build osrm url, I chose to use driving as my profile.
        String url = String.format(
            "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false&steps=false",
            userLon, userLat, itemLon, itemLat
        );
        
        try {
            //following structure of lab example here to get the proximity
            URI uri = new URI(url);
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
            //error handling to catch what went wrong 
            if (res.statusCode() !=200) {
                result.put("error", "OSRM service returned status " + res.statusCode());
                return Response.status(Response.Status.BAD_GATEWAY).entity(result).build();
            }
            
            String body = res.body();
            //reading getters and setters form main json class for osrm data
            MainJSON data = mapper.readValue(body, MainJSON.class);
            
            //if anything goes wrong mapping the results form the osrm json result, print error.
            if (data.getCode() == null || !"Ok".equalsIgnoreCase(data.getCode()) || data.getRoutes() == null|| data.getRoutes().length == 0) {
                result.put("error", "OSRM returned no valid route.");
                return Response.status(Response.Status.BAD_GATEWAY).entity(result).build();
            }
            
            //otherwsie get the distance details and print it ot the user
            double distance = data.getRoutes()[0].getDistance();
            double duration = ((data.getRoutes()[0].getDuration()) / 60);
            
            result.put("from:", userLocation);
            result.put("to:", itemLocation);
            result.put("profile:", "driving");
            result.put("distance (meters): ", distance);
            result.put("duration (minutes): ", duration);
            
            return Response.ok(result.toString(), MediaType.APPLICATION_JSON).build(); 
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            result.put("error", "Invalid OSRM URL built on server.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }  
        }
        result.put("error", "OSRM service unavailable or request failed.");
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(result).build();
    } 

    
    //original code from labs for getting location.
    //Not using this specifically as it required coordinates whereas items uses location names
 
    @GET
@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
public Response getJson(
        @QueryParam("fromLon") double fromLon,
        @QueryParam("fromLat") double fromLat,
        @QueryParam("toLon") double toLon,
        @QueryParam("toLat") double toLat
) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode result = mapper.createObjectNode();

    // Basic validation (optional but recommended)
    if (fromLon < -180 || fromLon > 180 || toLon < -180 || toLon > 180
            || fromLat < -90 || fromLat > 90 || toLat < -90 || toLat > 90) {
        result.put("error", "Invalid coordinates. Check longitude/latitude ranges.");
        return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
    }

    String url = String.format(
            "https://router.project-osrm.org/route/v1/walking/%f,%f;%f,%f?overview=false&steps=false",
            fromLon, fromLat, toLon, toLat
    );

    URI uri;
    try {
        uri = new URI(url);
    } catch (URISyntaxException e) {
        result.put("error", "Invalid OSRM URL built on server.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
    }

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build();

    try {
        HttpResponse<String> res = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        // If OSRM returns non-200, pass a useful error back
        if (res.statusCode() != 200) {
            result.put("error", "OSRM returned status " + res.statusCode());
            result.put("osrm_response", res.body());
            return Response.status(Response.Status.BAD_GATEWAY).entity(result).build();
        }

        String body = res.body();
        MainJSON data = mapper.readValue(body, MainJSON.class);

        if (data.getRoutes() != null && data.getRoutes().length > 0) {
            double distance = data.getRoutes()[0].getDistance();
            double duration = data.getRoutes()[0].getDuration();

            ObjectNode ok = mapper.createObjectNode();
            ok.put("distance_meters", distance);
            ok.put("duration_seconds", duration);

            return Response.ok(result.toString(), MediaType.APPLICATION_JSON).build();
        } else {
            result.put("error", "No routes returned by OSRM.");
            return Response.status(Response.Status.BAD_GATEWAY).entity(result).build();
        }

    } catch (IOException e) {
        result.put("error", "Failed to parse OSRM response.");
        return Response.status(Response.Status.BAD_GATEWAY).entity(result).build();

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        result.put("error", "OSRM request interrupted.");
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(result).build();
    }
    }
}

    
    
    //Add caching so that if same person is using the system and gives an incorrect coordiate, their previous coordinate is returmed (mid 1st)
    //Add more error handling so that the server still works if coordinates are correct
    //Also commit more frequently!!
   
