/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osrm;

/**
 *
 * @author emset
 */
public class Waypoint {
    private String hint;
    private double[] location;
    private String name;
    private double distance;
    
    public Waypoint () {
        
    }

    public String getHint() { return hint; }
    public void setHint(String value) { this.hint = value; }

    public double[] getLocation() { return location; }
    public void setLocation(double[] value) { this.location = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public double getDistance() { return distance; }
    public void setDistance(double value) { this.distance = value; }
}
