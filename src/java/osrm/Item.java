/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osrm;

/**
 *
 * @author emset
 */
public class Item {
    private String id;
    private String ownerId;
    private String name;
    private String category;
    private String location;
    private int dailyRate;
    private boolean availability;
    private String condition;
    private String description;
    
    public Item () {
        
    }
    
    public String getId() {return id;}
    public void setId(String value) {this.id = value;}
    
    public String getOwnerId() {return ownerId;}
    public void setOwnerId(String value) {this.ownerId = value;}
    
    public String getName() {return name;}
    public void setName(String value) {this.name = value;}
    
    public String getCategory() {return category;}
    public void setCategory(String value) {this.category = value;}
    
    public String getLocation() {return location;}
    public void setLocation(String value) {this.location = value;}
    
    public int getdDailyRate() {return dailyRate;}
    public void setDailyRate(int value) {this.dailyRate = value;}
    
    public boolean getAvailability() {return availability;}
    public void setAvailability(boolean value) {this.availability = value;}
    
    public String getCondition() {return condition;}
    public void setCondition(String value) {this.condition = value;}
    
    public String getDescription() {return description;}
    public void setDescription(String value) {this.description = value;}
}
