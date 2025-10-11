package ru.itmo.soa.music.error.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.soa.music.error.ApiError;
import ru.itmo.soa.music.error.InvalidIdFormatException;

@Provider
public class InvalidIdFormatExceptionMapper implements ExceptionMapper<InvalidIdFormatException> {
    @Override
    public Response toResponse(InvalidIdFormatException exception) {
        String message = exception.getMessage() == null || exception.getMessage().isBlank()
                ? "Parameter 'id' must be a positive integer." : exception.getMessage();
        ApiError error = new ApiError(422, message);
        return Response.status(422)
                .type(MediaType.APPLICATION_XML)
                .entity(error)
                .build();
    }
}


