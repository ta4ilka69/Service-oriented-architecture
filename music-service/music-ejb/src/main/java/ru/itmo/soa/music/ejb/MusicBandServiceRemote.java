package ru.itmo.soa.music.ejb;

import java.util.List;

import jakarta.ejb.Remote;
import ru.itmo.soa.music.dto.MusicBandAllSchema;
import ru.itmo.soa.music.dto.MusicBandCreateUpdate;
import ru.itmo.soa.music.dto.MusicBandPatchDto;

@Remote
public interface MusicBandServiceRemote {

    List<MusicBandAllSchema> list(List<String> sort, Integer page, Integer size, List<String> filters);

    MusicBandAllSchema create(MusicBandCreateUpdate dto);

    MusicBandAllSchema getById(int id);

    MusicBandAllSchema replace(int id, MusicBandCreateUpdate dto);

    void delete(int id);

    MusicBandAllSchema patch(int id, MusicBandPatchDto dto);

    void deleteAllWithDescription(String description);

    void deleteOneWithGenre(String genre);

    long countBestAlbum(String albumName, Long albumTracks);
}


