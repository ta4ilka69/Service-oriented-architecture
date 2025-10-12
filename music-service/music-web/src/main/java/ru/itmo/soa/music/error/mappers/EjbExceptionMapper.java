package ru.itmo.soa.music.error.mappers;

import jakarta.ejb.EJBException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.soa.music.error.ApiError;

@Provider
public class EjbExceptionMapper implements ExceptionMapper<EJBException> {
    @Override
    public Response toResponse(EJBException exception) {
        Throwable cause = unwrap(exception);
        String causeClass = cause.getClass().getName();

        if ("ru.itmo.soa.music.error.BadRequestException".equals(causeClass)) {
            ApiError error = new ApiError(400, safeMessage(cause, "Bad Request"));
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_XML)
                    .entity(error)
                    .build();
        }
        if ("ru.itmo.soa.music.error.NotFoundException".equals(causeClass)) {
            ApiError error = new ApiError(404, safeMessage(cause, "Not Found"));
            return Response.status(Response.Status.NOT_FOUND)
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

    private static Throwable unwrap(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null) {
            cur = cur.getCause();
        }
        return cur;
    }

    private static String safeMessage(Throwable t, String fallback) {
        String m = t.getMessage();
        return (m == null || m.isBlank()) ? fallback : m;
    }
}


