/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osrm;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author emset
 */
public class Request {
    @JsonProperty("request_id")
    private String requestid;
    private String itemId;
    private String userId;
    private Date dateRequested;
    private String status;
    private int rentalDuration;
    private String message;
    //regular id for cosmos
    private String id;
    
    public String getRequestId() { return requestid; }
    public void setRequestId(String value) { this.requestid = value; }
    
    public String getItemId() {return itemId;}
    public void setItemId(String value) {this.itemId = value;}
    
    public String getUserId() { return userId; }
    public void setUserId(String value) { this.userId = value; }
    
    public Date getDateRequested() { return dateRequested; }
    public void setDateRequested(Date value) { this.dateRequested = value; }
    
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
    
    public int getRentalDuration() { return rentalDuration; }
    public void setRentalDuration(int value) { this.rentalDuration = value; }
    
    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }
    
    public String getId() { return id; }
    public void setId(String value) { this.id = value; }
    
    
}
