package ru.itmo.soa.music.api;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.soa.music.error.ApiError;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Provider
public class XmlDecimalValidationFilter implements ContainerRequestFilter {

    // Pattern to find <x>...</x> with a decimal separator followed by more than 5 digits
    private static final Pattern COORDINATES_X_PATTERN = Pattern.compile(
        "<x>\\s*(-?\\d+)[.,](\\d+)\\s*</x>",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Only check POST/PUT/PATCH with XML content
        String method = requestContext.getMethod();
        if (!("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method))) {
            return;
        }

        MediaType mediaType = requestContext.getMediaType();
        if (mediaType == null || !mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
            return;
        }

        // Read the body
        String body = readBody(requestContext);
        if (body == null || body.isEmpty()) {
            return;
        }

        // Check for coordinates.x with more than 5 decimal places
        Matcher matcher = COORDINATES_X_PATTERN.matcher(body);
        while (matcher.find()) {
            String fractionalPart = matcher.group(2);
            if (fractionalPart.length() > 5) {
                ApiError error = new ApiError(400, "Field 'coordinates.x' must have at most 5 digits after decimal");
                requestContext.abortWith(
                    Response.status(Response.Status.BAD_REQUEST)
                        .type(MediaType.APPLICATION_XML)
                        .entity(error)
                        .build()
                );
                return;
            }
        }

        // Reset the input stream so JAX-RS can read it again
        requestContext.setEntityStream(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
    }

    private String readBody(ContainerRequestContext requestContext) throws IOException {
        if (!requestContext.hasEntity()) {
            return null;
        }

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(requestContext.getEntityStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line).append("\n");
            }
        }
        return body.toString();
    }
}

