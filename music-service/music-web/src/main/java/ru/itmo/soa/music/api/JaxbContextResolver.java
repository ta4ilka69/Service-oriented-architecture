package ru.itmo.soa.music.api;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import ru.itmo.soa.music.dto.MusicBandAllSchema;
import ru.itmo.soa.music.dto.MusicBandCreateUpdate;
import ru.itmo.soa.music.dto.MusicBandList;
import ru.itmo.soa.music.dto.MusicBandPatchDto;
import ru.itmo.soa.music.model.Album;
import ru.itmo.soa.music.model.Coordinates;
import ru.itmo.soa.music.model.Genre;

@Provider
@Produces(MediaType.APPLICATION_XML)
public class JaxbContextResolver implements ContextResolver<JAXBContext> {

    private final JAXBContext context;

    public JaxbContextResolver() {
        try {
            this.context = JAXBContext.newInstance(
                    MusicBandCreateUpdate.class,
                    MusicBandAllSchema.class,
                    MusicBandPatchDto.class,
                    MusicBandList.class,
                    Coordinates.class,
                    Album.class,
                    Genre.class
            );
        } catch (JAXBException e) {
            throw new IllegalStateException("Failed to initialize JAXBContext", e);
        }
    }

    @Override
    public JAXBContext getContext(Class<?> type) {
        // Use the prebuilt context for our DTOs/models
        if (type.getName().startsWith("ru.itmo.soa.music.dto") ||
            type.getName().startsWith("ru.itmo.soa.music.model")) {
            return context;
        }
        return null;
    }
}


