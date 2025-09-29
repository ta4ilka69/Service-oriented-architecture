package ru.itmo.soa.music.error.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.soa.music.error.ApiError;
import ru.itmo.soa.music.error.BadRequestException;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
    @Override
    public Response toResponse(BadRequestException exception) {
        String message = exception.getMessage() == null || exception.getMessage().isBlank()
                ? "Bad Request" : exception.getMessage();
        ApiError error = new ApiError(400, message);
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_XML)
                .entity(error)
                .build();
    }
}


