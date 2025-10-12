package ru.itmo.soa.music.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import ru.itmo.soa.music.api.readers.MusicBandCreateUpdateReader;
import ru.itmo.soa.music.error.mappers.BadRequestExceptionMapper;
import ru.itmo.soa.music.error.mappers.EjbExceptionMapper;
import ru.itmo.soa.music.error.mappers.GenericExceptionMapper;
import ru.itmo.soa.music.error.mappers.InvalidIdFormatExceptionMapper;
import ru.itmo.soa.music.error.mappers.JaxrsBadRequestMapper;
import ru.itmo.soa.music.error.mappers.NotFoundExceptionMapper;

@ApplicationPath("/")
public class JaxrsApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Resources
        classes.add(MusicBandResource.class);
        classes.add(HealthResource.class);
        // Filters / Providers
        classes.add(CorsFilter.class);
        classes.add(XmlDecimalValidationFilter.class);
        classes.add(JaxbContextResolver.class);
        classes.add(MusicBandCreateUpdateReader.class);
        // Exception mappers
        classes.add(GenericExceptionMapper.class);
        classes.add(EjbExceptionMapper.class);
        classes.add(BadRequestExceptionMapper.class);
        classes.add(InvalidIdFormatExceptionMapper.class);
        classes.add(JaxrsBadRequestMapper.class);
        classes.add(NotFoundExceptionMapper.class);
        return classes;
    }
}


