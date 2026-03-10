/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osrm;

/**
 *
 * @author emset
 */
//helper class for JSON entries
//note
public class CityCoord {
    private String name;
        private double lon;
        private double lat;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public double getLon() { return lon; }
        public void setLon(double lon) { this.lon = lon; }

        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }
}
