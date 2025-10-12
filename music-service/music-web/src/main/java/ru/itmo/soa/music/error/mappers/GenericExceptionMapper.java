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
        Throwable cause = unwrap(exception);

        String className = exception.getClass().getSimpleName();
        boolean isBadRequestLike = className.contains("BadRequest") || className.contains("Param");
        if (isBadRequestLike || cause instanceof NumberFormatException || cause instanceof IllegalArgumentException) {
            String msg = (cause.getMessage() == null || cause.getMessage().isBlank()) ? "Bad Request" : cause.getMessage();
            ApiError error = new ApiError(400, msg);
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_XML)
                    .entity(error)
                    .build();
        }

        String msg = (cause.getMessage() == null || cause.getMessage().isBlank()) ? "Internal Server Error" : cause.getMessage();
        ApiError error = new ApiError(500, msg);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_XML)
                .entity(error)
                .build();
    }

    private static Throwable unwrap(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null) {
            cur = cur.getCause();
        }
        return cur;
    }
}


