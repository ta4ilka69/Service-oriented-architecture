package ru.itmo.soa.music.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/health")
public class HealthResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response health() {
        return Response.ok("OK").build();
    }
}


