/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osrm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author emset
 */
public class Route {
    private Leg[] legs;
    @JsonProperty("weight_name")
    private String weightName;
    private String geometry;
    private double weight;
    private double duration;
    private double distance;

    public Leg[] getLegs() { return legs; }
    public void setLegs(Leg[] value) { this.legs = value; }

    public String getWeightName() { return weightName; }
    public void setWeightName(String value) { this.weightName = value; }

    public String getGeometry() { return geometry; }
    public void setGeometry(String value) { this.geometry = value; }

    public double getWeight() { return weight; }
    public void setWeight(double value) { this.weight = value; }

    public double getDuration() { return duration; }
    public void setDuration(double value) { this.duration = value; }

    public double getDistance() { return distance; }
    public void setDistance(double value) { this.distance = value; }
}
  

