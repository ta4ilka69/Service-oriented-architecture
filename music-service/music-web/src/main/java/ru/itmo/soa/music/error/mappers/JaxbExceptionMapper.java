package ru.itmo.soa.music.error.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.xml.bind.UnmarshalException;
import ru.itmo.soa.music.error.ApiError;

@Provider
public class JaxbExceptionMapper implements ExceptionMapper<UnmarshalException> {
    @Override
    public Response toResponse(UnmarshalException exception) {
        Throwable cause = exception;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        String message = cause.getMessage() != null ? cause.getMessage() : "Bad Request";
        ApiError error = new ApiError(400, message);
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_XML)
                .entity(error)
                .build();
    }
}


