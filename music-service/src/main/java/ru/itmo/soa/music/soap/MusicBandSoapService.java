package ru.itmo.soa.music.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.ParameterStyle;
import jakarta.jws.soap.SOAPBinding.Style;
import jakarta.jws.soap.SOAPBinding.Use;
import java.util.Arrays;
import java.util.List;
import ru.itmo.soa.music.dto.MusicBandAllSchema;
import ru.itmo.soa.music.dto.MusicBandCreateUpdate;
import ru.itmo.soa.music.dto.MusicBandList;
import ru.itmo.soa.music.dto.MusicBandPatchDto;
import ru.itmo.soa.music.error.ApiError;
import ru.itmo.soa.music.error.BadRequestException;
import ru.itmo.soa.music.error.InvalidIdFormatException;
import ru.itmo.soa.music.error.NotFoundException;
import ru.itmo.soa.music.error.SoapServiceException;
import ru.itmo.soa.music.model.Genre;
import ru.itmo.soa.music.repo.MusicBandRepository;

@WebService(
        serviceName = "MusicBandSoapService",
        portName = "MusicBandPort",
        targetNamespace = "http://music.soa.itmo.ru/",
        name = "MusicBandSoapService"
)
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public class MusicBandSoapService {

    private static final MusicBandRepository repository = new MusicBandRepository();

    @WebMethod(operationName = "ListMusicBands")
    @WebResult(name = "musicBands")
    public MusicBandList list(
            @WebParam(name = "sort") String[] sort,
            @WebParam(name = "page") Integer page,
            @WebParam(name = "size") Integer size,
            @WebParam(name = "filter") String[] filters
    ) throws SoapServiceException {
        try {
            if (page != null && page < 1) {
                throw new BadRequestException("Invalid query parameter 'page'");
            }
            if (size != null && size < 1) {
                throw new BadRequestException("Invalid query parameter 'size'");
            }
            if ((page != null && size == null) || (size != null && page == null)) {
                page = null;
                size = null;
            }
            List<String> sortList = sort == null ? null : Arrays.asList(sort);
            List<String> filterList = filters == null ? null : Arrays.asList(filters);
            List<MusicBandAllSchema> items = repository.list(sortList, page, size, filterList);
            MusicBandList wrapper = new MusicBandList();
            wrapper.setItems(items);
            return wrapper;
        } catch (BadRequestException e) {
            throw fault(400, e.getMessage());
        } catch (RuntimeException e) {
            throw fault(500, "Internal Server Error");
        }
    }

    @WebMethod(operationName = "CreateMusicBand")
    @WebResult(name = "musicBandAllSchema")
    public MusicBandAllSchema create(
            @WebParam(name = "musicBand") MusicBandCreateUpdate dto
    ) throws SoapServiceException {
        try {
            return repository.create(dto);
        } catch (BadRequestException e) {
            throw fault(400, e.getMessage());
        } catch (RuntimeException e) {
            throw fault(500, "Internal Server Error");
        }
    }

    @WebMethod(operationName = "GetMusicBandById")
    @WebResult(name = "musicBandAllSchema")
    public MusicBandAllSchema getById(
            @WebParam(name = "id") String idStr
    ) throws SoapServiceException {
        try {
            int id = parseIdForGetDelete(idStr);
            return repository.getById(id);
        } catch (InvalidIdFormatException e) {
            throw fault(422, e.getMessage());
        } catch (BadRequestException e) {
            throw fault(400, e.getMessage());
        } catch (NotFoundException e) {
            throw fault(404, "Not Found - Musicband not found.");
        } catch (RuntimeException e) {
            throw fault(500, "Internal Server Error");
        }
    }

    @WebMethod(operationName = "ReplaceMusicBand")
    @WebResult(name = "musicBandAllSchema")
    public MusicBandAllSchema replace(
            @WebParam(name = "id") String idStr,
            @WebParam(name = "musicBand") MusicBandCreateUpdate dto
    ) throws SoapServiceException {
        try {
            int id = parseId(idStr);
            return repository.replace(id, dto);
        } catch (InvalidIdFormatException e) {
            throw fault(422, e.getMessage());
        } catch (BadRequestException e) {
            throw fault(400, e.getMessage());
        } catch (NotFoundException e) {
            throw fault(404, "Not Found - Musicband not found.");
        } catch (RuntimeException e) {
            throw fault(500, "Internal Server Error");
        }
    }

    @WebMethod(operationName = "DeleteMusicBand")
    public void delete(
            @WebParam(name = "id") String idStr
    ) throws SoapServiceException {
        try {
            int id = parseIdForGetDelete(idStr);
            repository.delete(id);
        } catch (InvalidIdFormatException e) {
            throw fault(422, e.getMessage());
        } catch (BadRequestException e) {
            throw fault(400, e.getMessage());
        } catch (NotFoundException e) {
            throw fault(404, "Not Found - Musicband not found.");
        } catch (RuntimeException e) {
            throw fault(500, "Internal Server Error");
        }
    }

    @WebMethod(operationName = "PatchMusicBand")
    @WebResult(name = "musicBandAllSchema")
    public MusicBandAllSchema patch(
            @WebParam(name = "id") String idStr,
            @WebParam(name = "musicBand") MusicBandPatchDto dto
    ) throws SoapServiceException {
        try {
            int id = parseId(idStr);
            return repository.patch(id, dto);
        } catch (InvalidIdFormatException e) {
            throw fault(422, e.getMessage());
        } catch (BadRequestException e) {
            throw fault(400, e.getMessage());
        } catch (NotFoundException e) {
            throw fault(404, "Not Found - Musicband not found.");
        } catch (RuntimeException e) {
            throw fault(500, "Internal Server Error");
        }
    }

    @WebMethod(operationName = "DeleteAllWithDescription")
    public void deleteAllWithDescription(
            @WebParam(name = "description") String description
    ) throws SoapServiceException {
        try {
            repository.deleteAllWithDescription(description);
        } catch (BadRequestException e) {
            throw fault(400, e.getMessage());
        } catch (RuntimeException e) {
            throw fault(500, "Internal Server Error");
        }
    }

    @WebMethod(operationName = "DeleteOneWithGenre")
    public void deleteOneWithGenre(
            @WebParam(name = "genre") String genre
    ) throws SoapServiceException {
        try {
            parseGenreQuery(genre);
            repository.deleteOneWithGenre(genre);
        } catch (BadRequestException e) {
            throw fault(400, e.getMessage());
        } catch (RuntimeException e) {
            throw fault(500, "Internal Server Error");
        }
    }

    @WebMethod(operationName = "CountBestAlbum")
    @WebResult(name = "count")
    public long countBestAlbum(
            @WebParam(name = "albumName") String albumName,
            @WebParam(name = "albumTracks") Long albumTracks
    ) throws SoapServiceException {
        try {
            return repository.countBestAlbum(albumName, albumTracks);
        } catch (BadRequestException e) {
            throw fault(400, e.getMessage());
        } catch (RuntimeException e) {
            throw fault(500, "Internal Server Error");
        }
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
                throw new BadRequestException("Invalid ID supplied");
            }
            return id;
        } catch (BadRequestException e) {
            throw e;
        } catch (NumberFormatException ex) {
            throw new InvalidIdFormatException("Parameter 'id' must be a positive integer.");
        }
    }

    private Genre parseGenreQuery(String value) {
        if (value == null) {
            throw new BadRequestException("Invalid query parameter 'genre'");
        }
        try {
            return Genre.valueOf(value);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid query parameter 'genre'");
        }
    }

    private SoapServiceException fault(int code, String message) throws SoapServiceException {
        return new SoapServiceException(new ApiError(code, message));
    }
}


