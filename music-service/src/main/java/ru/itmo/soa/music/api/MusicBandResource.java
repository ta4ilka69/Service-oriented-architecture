package ru.itmo.soa.music.api;

import java.util.List;

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
import ru.itmo.soa.music.error.InvalidIdFormatException;
import ru.itmo.soa.music.repo.MusicBandRepository;

@Path("/music-bands")
@Produces(MediaType.APPLICATION_XML)
public class MusicBandResource {

    private static final MusicBandRepository repository = new MusicBandRepository();

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
        // If one of page/size is provided without the other, ignore both (per spec)
        if ((page != null && size == null) || (size != null && page == null)) {
            page = null;
            size = null;
        }

        List<MusicBandAllSchema> items = repository.list(sort, page, size, filters);
        MusicBandList wrapper = new MusicBandList();
        wrapper.setItems(items);
        return Response.ok(wrapper).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response create(MusicBandCreateUpdate dto) {
        MusicBandAllSchema created = repository.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") String idStr) {
        int id = parseIdForGetDelete(idStr);
        MusicBandAllSchema found = repository.getById(id);
        return Response.ok(found).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response replace(@PathParam("id") String idStr, MusicBandCreateUpdate dto) {
        int id = parseId(idStr);
        MusicBandAllSchema updated = repository.replace(id, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String idStr) {
        int id = parseIdForGetDelete(idStr);
        repository.delete(id);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response patch(@PathParam("id") String idStr, MusicBandPatchDto dto) {
        int id = parseId(idStr);
        MusicBandAllSchema updated = repository.patch(id, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/all-with-description")
    public Response deleteAllWithDescription(@QueryParam("description") String description) {
        repository.deleteAllWithDescription(description);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/one-with-genre")
    public Response deleteOneWithGenre(@QueryParam("genre") String genre) {
        ru.itmo.soa.music.model.Genre parsedGenre = parseGenreQuery(genre);
        repository.deleteOneWithGenre(parsedGenre.name());
        return Response.noContent().build();
    }

    @GET
    @Path("/count-best-album")
    public Response countBestAlbum(
            @QueryParam("albumName") String albumName,
            @QueryParam("albumTracks") Long albumTracks
    ) {
        long count = repository.countBestAlbum(albumName, albumTracks);
        String xml = "<count>" + count + "</count>";
        return Response.ok(xml).type(MediaType.APPLICATION_XML).build();
    }

    private int parseId(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            if (id < 1) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException ex) {
            throw new InvalidIdFormatException("Parameter 'id' must be a positive integer (int32).");
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
            throw new InvalidIdFormatException("Parameter 'id' must be a positive integer (int32).");
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


