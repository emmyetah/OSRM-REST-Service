/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osrm;

/**
 *
 * @author emset
 */
public class MessageResponse {
    private String message;
    private String requestId;

    public MessageResponse() {}

    public MessageResponse(String message, String requestId) {
        this.message = message;
        this.requestId = requestId;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
}
