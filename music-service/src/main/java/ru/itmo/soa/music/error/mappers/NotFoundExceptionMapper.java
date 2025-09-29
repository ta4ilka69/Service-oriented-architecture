package ru.itmo.soa.music.error.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.soa.music.error.ApiError;
import ru.itmo.soa.music.error.NotFoundException;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        String message = exception.getMessage() == null || exception.getMessage().isBlank()
                ? "Not Found" : exception.getMessage();
        ApiError error = new ApiError(404, message);
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_XML)
                .entity(error)
                .build();
    }
}


