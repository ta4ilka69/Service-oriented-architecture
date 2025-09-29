package ru.itmo.soa.music.error.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.soa.music.error.ApiError;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        // Unwrap to root cause
        Throwable cause = exception;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        // Map query param and similar bad request cases to 400 without depending on container classes
        String className = exception.getClass().getSimpleName();
        boolean isBadRequestLike = className.contains("BadRequest") || className.contains("Param");
        if (isBadRequestLike || cause instanceof NumberFormatException) {
            ApiError error = new ApiError(400, "Bad Request");
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_XML)
                    .entity(error)
                    .build();
        }

        ApiError error = new ApiError(500, "Internal Server Error");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_XML)
                .entity(error)
                .build();
    }
}


