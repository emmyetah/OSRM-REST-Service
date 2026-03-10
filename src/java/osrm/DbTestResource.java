/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osrm;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author emset
 */

@Path("/dbtest")
public class DbTestResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String testDB() {
        try {
            DatabaseConnector db = new DatabaseConnector();
            db.testQuery();   // We will create this method in your DatabaseConnector
            return "SUCCESS: DB call executed (check Tomcat logs).";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }
}
