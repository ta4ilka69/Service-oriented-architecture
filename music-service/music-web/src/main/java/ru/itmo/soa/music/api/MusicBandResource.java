package ru.itmo.soa.music.api;

import java.util.List;

import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.itmo.soa.music.dto.MusicBandAllSchema;
import ru.itmo.soa.music.dto.MusicBandCreateUpdate;
import ru.itmo.soa.music.dto.MusicBandList;
import ru.itmo.soa.music.dto.MusicBandPatchDto;
import ru.itmo.soa.music.ejb.MusicBandServiceRemote;
import ru.itmo.soa.music.error.InvalidIdFormatException;

@Path("/music-bands")
@Produces(MediaType.APPLICATION_XML)
public class MusicBandResource {

    @EJB(lookup = "java:global/music-ejb/MusicBandServiceBean!ru.itmo.soa.music.ejb.MusicBandServiceRemote")
    private MusicBandServiceRemote service;

    @GET
    public Response list(
            @QueryParam("sort") List<String> sort,
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("filter") List<String> filters
    ) {
        if (page != null && page < 1) {
            throw new jakarta.ws.rs.BadRequestException("Invalid query parameter 'page'");
        }
        if (size != null && size < 1) {
            throw new jakarta.ws.rs.BadRequestException("Invalid query parameter 'size'");
        }
        if ((page != null && size == null) || (size != null && page == null)) {
            page = null;
            size = null;
        }

        List<MusicBandAllSchema> items = service.list(sort, page, size, filters);
        MusicBandList wrapper = new MusicBandList();
        wrapper.setItems(items);
        return Response.ok(wrapper).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response create(MusicBandCreateUpdate dto) {
        MusicBandAllSchema created = service.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") String idStr) {
        int id = parseIdForGetDelete(idStr);
        MusicBandAllSchema found = service.getById(id);
        return Response.ok(found).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response replace(@PathParam("id") String idStr, MusicBandCreateUpdate dto) {
        int id = parseId(idStr);
        MusicBandAllSchema updated = service.replace(id, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String idStr) {
        int id = parseIdForGetDelete(idStr);
        service.delete(id);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response patch(@PathParam("id") String idStr, MusicBandPatchDto dto) {
        int id = parseId(idStr);
        MusicBandAllSchema updated = service.patch(id, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/all-with-description")
    public Response deleteAllWithDescription(@QueryParam("description") String description) {
        service.deleteAllWithDescription(description);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/one-with-genre")
    public Response deleteOneWithGenre(@QueryParam("genre") String genre) {
        ru.itmo.soa.music.model.Genre parsedGenre = parseGenreQuery(genre);
        service.deleteOneWithGenre(parsedGenre.name());
        return Response.noContent().build();
    }

    @GET
    @Path("/count-best-album")
    public Response countBestAlbum(
            @QueryParam("albumName") String albumName,
            @QueryParam("albumTracks") Long albumTracks
    ) {
        long count = service.countBestAlbum(albumName, albumTracks);
        String xml = "<count>" + count + "</count>";
        return Response.ok(xml).type(MediaType.APPLICATION_XML).build();
    }

    private int parseId(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            if (id < 1) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException ex) {
            throw new InvalidIdFormatException("Parameter 'id' must be a positive integer.");
        }
    }

    private int parseIdForGetDelete(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            if (id < 1) {
                throw new ru.itmo.soa.music.error.BadRequestException("Invalid ID supplied");
            }
            return id;
        } catch (ru.itmo.soa.music.error.BadRequestException e) {
            throw e;
        } catch (NumberFormatException ex) {
            throw new InvalidIdFormatException("Parameter 'id' must be a positive integer.");
        }
    }

    private ru.itmo.soa.music.model.Genre parseGenreQuery(String value) {
        if (value == null) {
            throw new jakarta.ws.rs.BadRequestException("Invalid query parameter 'genre'");
        }
        try {
            return ru.itmo.soa.music.model.Genre.valueOf(value);
        } catch (IllegalArgumentException ex) {
            throw new jakarta.ws.rs.BadRequestException("Invalid query parameter 'genre'");
        }
    }
}


