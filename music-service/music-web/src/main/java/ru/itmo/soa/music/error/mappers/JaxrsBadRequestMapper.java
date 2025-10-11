package ru.itmo.soa.music.error.mappers;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.soa.music.error.ApiError;

@Provider
public class JaxrsBadRequestMapper implements ExceptionMapper<BadRequestException> {
    @Override
    public Response toResponse(BadRequestException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = "Bad Request";
        }
        ApiError error = new ApiError(400, message);
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_XML)
                .entity(error)
                .build();
    }
}


