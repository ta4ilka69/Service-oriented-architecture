package ru.itmo.soa.music.ejb;

import java.util.List;

import jakarta.ejb.Stateless;
import ru.itmo.soa.music.dto.MusicBandAllSchema;
import ru.itmo.soa.music.dto.MusicBandCreateUpdate;
import ru.itmo.soa.music.dto.MusicBandPatchDto;
import ru.itmo.soa.music.repo.MusicBandRepository;

@Stateless
public class MusicBandServiceBean implements MusicBandServiceRemote {

    private static final MusicBandRepository repository = new MusicBandRepository();

    @Override
    public List<MusicBandAllSchema> list(List<String> sort, Integer page, Integer size, List<String> filters) {
        return repository.list(sort, page, size, filters);
    }

    @Override
    public MusicBandAllSchema create(MusicBandCreateUpdate dto) {
        return repository.create(dto);
    }

    @Override
    public MusicBandAllSchema getById(int id) {
        return repository.getById(id);
    }

    @Override
    public MusicBandAllSchema replace(int id, MusicBandCreateUpdate dto) {
        return repository.replace(id, dto);
    }

    @Override
    public void delete(int id) {
        repository.delete(id);
    }

    @Override
    public MusicBandAllSchema patch(int id, MusicBandPatchDto dto) {
        return repository.patch(id, dto);
    }

    @Override
    public void deleteAllWithDescription(String description) {
        repository.deleteAllWithDescription(description);
    }

    @Override
    public void deleteOneWithGenre(String genre) {
        repository.deleteOneWithGenre(genre);
    }

    @Override
    public long countBestAlbum(String albumName, Long albumTracks) {
        return repository.countBestAlbum(albumName, albumTracks);
    }
}


