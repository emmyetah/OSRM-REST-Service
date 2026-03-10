/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osrm;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.WebApplicationException;


//this class will log full details server-side and return a safe, generic error message to the client.
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable ex) {

        //log server side
        ex.printStackTrace();

        //if it's a WebApplicationException I keep its status
        if (ex instanceof WebApplicationException) {
            int status = ((WebApplicationException) ex).getResponse().getStatus();
            String msg = ex.getMessage() == null ? "Request failed." : ex.getMessage();

            return Response.status(status)
                    .entity(msg)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        //generic 500 response for client.
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("An internal error occurred. Please try again later.")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
