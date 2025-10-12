package ru.itmo.soa.music.api.readers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import ru.itmo.soa.music.dto.MusicBandCreateUpdate;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Consumes(MediaType.APPLICATION_XML)
public class MusicBandCreateUpdateReader implements MessageBodyReader<MusicBandCreateUpdate> {

    private final JAXBContext ctx;

    public MusicBandCreateUpdateReader() {
        try {
            this.ctx = JAXBContext.newInstance(MusicBandCreateUpdate.class);
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == MusicBandCreateUpdate.class && mediaType != null && mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE);
    }

    @Override
    public MusicBandCreateUpdate readFrom(Class<MusicBandCreateUpdate> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        try {
            Unmarshaller u = ctx.createUnmarshaller();
            return (MusicBandCreateUpdate) u.unmarshal(entityStream);
        } catch (JAXBException e) {
            throw new WebApplicationException("Bad Request", 400);
        }
    }
}


