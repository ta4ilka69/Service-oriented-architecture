package ru.itmo.soa.music.repo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ru.itmo.soa.music.dto.MusicBandAllSchema;
import ru.itmo.soa.music.dto.MusicBandCreateUpdate;
import ru.itmo.soa.music.dto.MusicBandPatchDto;
import ru.itmo.soa.music.error.BadRequestException;
import ru.itmo.soa.music.error.NotFoundException;
import ru.itmo.soa.music.model.Album;
import ru.itmo.soa.music.model.MusicBand;

public class MusicBandRepository {

    private final Map<Integer, MusicBand> store = new ConcurrentHashMap<>();
    private final AtomicInteger idSeq = new AtomicInteger(0);

    private static final Pattern FILTER_PATTERN = Pattern.compile("^((name|creationDate|description|genre|bestAlbum\\.name)(>=|<|>|<=|!=|==|\\^=|\\$=|@=).+)|(coordinates\\.x(>=|<=|>|<|!=|==)-?\\d+(\\.\\d+)?|(id|coordinates\\.y|numberOfParticipants|albumsCount|bestAlbum\\.tracks)(>=|<=|>|<|!=|==)-?\\d+)$");

    public MusicBandRepository() {
    }

    public MusicBandAllSchema create(MusicBandCreateUpdate dto) {
        validateCreateUpdate(dto);
        MusicBand entity = new MusicBand();
        entity.setId(idSeq.incrementAndGet());
        entity.setName(dto.getName());
        entity.setCoordinates(dto.getCoordinates());
        entity.setCreationDate(LocalDate.now());
        entity.setNumberOfParticipants(dto.getNumberOfParticipants());
        entity.setAlbumsCount(dto.getAlbumsCount());
        entity.setDescription(dto.getDescription());
        entity.setGenre(dto.getGenre());
        entity.setBestAlbum(dto.getBestAlbum());
        store.put(entity.getId(), entity);
        return toDto(entity);
    }

    public MusicBandAllSchema getById(int id) {
        MusicBand entity = store.get(id);
        if (entity == null) {
            throw new NotFoundException("MusicBand not found");
        }
        return toDto(entity);
    }

    public MusicBandAllSchema replace(int id, MusicBandCreateUpdate dto) {
        validateCreateUpdate(dto);
        MusicBand existing = store.get(id);
        if (existing == null) {
            throw new NotFoundException("MusicBand not found");
        }
        existing.setName(dto.getName());
        existing.setCoordinates(dto.getCoordinates());
        // creationDate remains unchanged
        existing.setNumberOfParticipants(dto.getNumberOfParticipants());
        existing.setAlbumsCount(dto.getAlbumsCount());
        existing.setDescription(dto.getDescription());
        existing.setGenre(dto.getGenre());
        existing.setBestAlbum(dto.getBestAlbum());
        return toDto(existing);
    }

    public MusicBandAllSchema patch(int id, MusicBandPatchDto patch) {
        MusicBand existing = store.get(id);
        if (existing == null) {
            throw new NotFoundException("MusicBand not found");
        }
        if (patch.getName() != null) existing.setName(patch.getName());
        if (patch.getCoordinates() != null) existing.setCoordinates(patch.getCoordinates());
        if (patch.getNumberOfParticipants() != null) existing.setNumberOfParticipants(patch.getNumberOfParticipants());
        if (patch.getAlbumsCount() != null) existing.setAlbumsCount(patch.getAlbumsCount());
        if (patch.getDescription() != null) existing.setDescription(patch.getDescription());
        if (patch.getGenre() != null) existing.setGenre(patch.getGenre());
        if (patch.getBestAlbum() != null || isExplicitNullAlbum(patch)) existing.setBestAlbum(patch.getBestAlbum());
        validateEntity(existing);
        return toDto(existing);
    }

    private boolean isExplicitNullAlbum(MusicBandPatchDto patch) {
        // JAXB won't differentiate absent vs null on nested; assume null only when provided as empty tag <bestAlbum/>
        return patch.getBestAlbum() == null;
    }

    public void delete(int id) {
        if (store.remove(id) == null) {
            throw new NotFoundException("MusicBand not found");
        }
    }

    public List<MusicBandAllSchema> list(List<String> sort, Integer page, Integer size, List<String> filters) {
        List<Predicate<MusicBand>> predicates = buildPredicates(filters);
        Comparator<MusicBand> comparator = buildComparator(sort);

        List<MusicBand> all = new ArrayList<>(store.values());
        List<MusicBand> filtered = all.stream()
                .filter(predicates.stream().reduce(x -> true, Predicate::and))
                .sorted(comparator)
                .collect(Collectors.toList());

        if (page != null && size != null) {
            int from = Math.max(0, (page - 1) * size);
            int to = Math.min(filtered.size(), from + size);
            if (from > filtered.size()) {
                return List.of();
            }
            filtered = filtered.subList(from, to);
        }

        return filtered.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void deleteAllWithDescription(String description) {
        if (description == null) {
            throw new BadRequestException("Missing description");
        }
        store.values().removeIf(mb -> Objects.equals(mb.getDescription(), description));
    }

    public void deleteOneWithGenre(String genre) {
        if (genre == null) {
            throw new BadRequestException("Missing genre");
        }
        Optional<Integer> any = store.values().stream()
                .filter(mb -> mb.getGenre() != null && mb.getGenre().name().equals(genre))
                .map(MusicBand::getId)
                .findAny();
        any.ifPresent(store::remove);
    }

    public long countBestAlbum(String albumName, Long tracks) {
        if (albumName == null || albumName.isBlank() || tracks == null || tracks < 1) {
            throw new BadRequestException("Invalid parameters");
        }
        return store.values().stream()
                .filter(mb -> compareAlbum(mb.getBestAlbum(), albumName, tracks) > 0)
                .count();
    }

    private int compareAlbum(Album album, String name, Long tracks) {
        if (album == null) return -1;
        int cmp = Long.compare(Optional.ofNullable(album.getTracks()).orElse(0L), tracks);
        if (cmp == 0) {
            String a = Optional.ofNullable(album.getName()).orElse("");
            return a.compareTo(name);
        }
        return cmp;
    }

    private void validateCreateUpdate(MusicBandCreateUpdate dto) {
        if (dto.getName() == null || dto.getName().isBlank()) throw new BadRequestException("Field 'name' is invalid or missing");
        if (dto.getCoordinates() == null) throw new BadRequestException("Field 'coordinates' is invalid or missing");
        if (dto.getCoordinates().getX() == null || dto.getCoordinates().getX() <= -975) throw new BadRequestException("Field 'coordinates.x' must be greater than -975");
        if (dto.getCoordinates().getY() == null || dto.getCoordinates().getY() < 1) throw new BadRequestException("Field 'coordinates.y' must be >= 1");
        if (dto.getNumberOfParticipants() == null || dto.getNumberOfParticipants() < 1) throw new BadRequestException("Field 'numberOfParticipants' must be >= 1");
        if (dto.getAlbumsCount() == null || dto.getAlbumsCount() < 1) throw new BadRequestException("Field 'albumsCount' must be >= 1");
        if (dto.getDescription() == null) throw new BadRequestException("Field 'description' is required");
        if (dto.getGenre() == null) throw new BadRequestException("Field 'genre' is required");
        if (dto.getBestAlbum() != null) {
            if (dto.getBestAlbum().getName() == null || dto.getBestAlbum().getName().isBlank()) throw new BadRequestException("Field 'bestAlbum.name' is invalid or missing");
            if (dto.getBestAlbum().getTracks() == null || dto.getBestAlbum().getTracks() < 1) throw new BadRequestException("Field 'bestAlbum.tracks' must be >= 1");
        }
    }

    private void validateEntity(MusicBand e) {
        if (e.getName() == null || e.getName().isBlank()) throw new BadRequestException("Field 'name' is invalid or missing");
        if (e.getCoordinates() == null) throw new BadRequestException("Field 'coordinates' is invalid or missing");
        if (e.getCoordinates().getX() == null || e.getCoordinates().getX() <= -975) throw new BadRequestException("Field 'coordinates.x' must be greater than -975");
        if (e.getCoordinates().getY() == null || e.getCoordinates().getY() < 1) throw new BadRequestException("Field 'coordinates.y' must be >= 1");
        if (e.getNumberOfParticipants() == null || e.getNumberOfParticipants() < 1) throw new BadRequestException("Field 'numberOfParticipants' must be >= 1");
        if (e.getAlbumsCount() == null || e.getAlbumsCount() < 1) throw new BadRequestException("Field 'albumsCount' must be >= 1");
        if (e.getDescription() == null) throw new BadRequestException("Field 'description' is required");
        if (e.getGenre() == null) throw new BadRequestException("Field 'genre' is required");
        if (e.getBestAlbum() != null) {
            if (e.getBestAlbum().getName() == null || e.getBestAlbum().getName().isBlank()) throw new BadRequestException("Field 'bestAlbum.name' is invalid or missing");
            if (e.getBestAlbum().getTracks() == null || e.getBestAlbum().getTracks() < 1) throw new BadRequestException("Field 'bestAlbum.tracks' must be >= 1");
        }
    }

    private List<Predicate<MusicBand>> buildPredicates(List<String> filters) {
        List<Predicate<MusicBand>> list = new ArrayList<>();
        if (filters == null) return list;
        for (String f : filters) {
            if (!FILTER_PATTERN.matcher(f).matches()) {
                throw new BadRequestException("Invalid filter: " + f);
            }
            list.add(buildPredicate(f));
        }
        return list;
    }

    private Predicate<MusicBand> buildPredicate(String f) {
        // Simple parser for a subset per regex
        // Split by operators in precedence order
        String[] ops = {">=","<=","!=","==",">","<","^=","$=","@="};
        for (String op : ops) {
            int idx = f.indexOf(op);
            if (idx > 0) {
                String field = f.substring(0, idx);
                String value = f.substring(idx + op.length());
                return buildFieldPredicate(field, op, value);
            }
        }
        throw new BadRequestException("Invalid filter: " + f);
    }

    private Predicate<MusicBand> buildFieldPredicate(String field, String op, String value) {
        return mb -> {
            switch (field) {
                case "name":
                case "description":
                case "genre":
                case "bestAlbum.name":
                case "creationDate": {
                    String left = switch (field) {
                        case "name" -> mb.getName();
                        case "description" -> mb.getDescription();
                        case "genre" -> mb.getGenre() == null ? null : mb.getGenre().name();
                        case "bestAlbum.name" -> mb.getBestAlbum() == null ? null : mb.getBestAlbum().getName();
                        case "creationDate" -> mb.getCreationDate() == null ? null : mb.getCreationDate().toString();
                        default -> null;
                    };
                    if (left == null) return false;
                    return compareString(left, op, value);
                }
                case "coordinates.x": {
                    Double left = mb.getCoordinates() == null ? null : mb.getCoordinates().getX();
                    if (left == null) return false;
                    return compareNumber(left, op, Double.parseDouble(value));
                }
                case "id": {
                    Integer left = mb.getId();
                    return compareNumber(left, op, Integer.parseInt(value));
                }
                case "coordinates.y": {
                    Long left = mb.getCoordinates() == null ? null : mb.getCoordinates().getY();
                    if (left == null) return false;
                    return compareNumber(left, op, Long.parseLong(value));
                }
                case "numberOfParticipants": {
                    Integer left = mb.getNumberOfParticipants();
                    return compareNumber(left, op, Integer.parseInt(value));
                }
                case "albumsCount": {
                    Integer left = mb.getAlbumsCount();
                    return compareNumber(left, op, Integer.parseInt(value));
                }
                case "bestAlbum.tracks": {
                    Long left = mb.getBestAlbum() == null ? null : mb.getBestAlbum().getTracks();
                    if (left == null) return false;
                    return compareNumber(left, op, Long.parseLong(value));
                }
                default:
                    return false;
            }
        };
    }

    private boolean compareString(String left, String op, String right) {
        return switch (op) {
            case "==" -> left.equals(right);
            case "!=" -> !left.equals(right);
            case ">" -> left.compareTo(right) > 0;
            case "<" -> left.compareTo(right) < 0;
            case ">=" -> left.compareTo(right) >= 0;
            case "<=" -> left.compareTo(right) <= 0;
            case "^=" -> left.startsWith(right);
            case "$=" -> left.endsWith(right);
            case "@=" -> left.contains(right);
            default -> false;
        };
    }

    private <N extends Number & Comparable<N>> boolean compareNumber(N left, String op, N right) {
        int cmp = left.compareTo(right);
        return switch (op) {
            case "==" -> cmp == 0;
            case "!=" -> cmp != 0;
            case ">" -> cmp > 0;
            case "<" -> cmp < 0;
            case ">=" -> cmp >= 0;
            case "<=" -> cmp <= 0;
            default -> false;
        };
    }

    private Comparator<MusicBand> buildComparator(List<String> sortFields) {
        if (sortFields == null || sortFields.isEmpty()) {
            return Comparator.comparing(MusicBand::getId);
        }
        Comparator<MusicBand> comparator = null;
        for (String s : sortFields) {
            boolean desc = s.startsWith("-");
            String field = desc ? s.substring(1) : s;
            Comparator<MusicBand> c = switch (field) {
                case "id" -> Comparator.comparing(MusicBand::getId);
                case "name" -> Comparator.comparing(MusicBand::getName, Comparator.nullsLast(String::compareTo));
                case "coordinates.x" -> Comparator.comparing(m -> m.getCoordinates() == null ? null : m.getCoordinates().getX(), Comparator.nullsLast(Double::compareTo));
                case "coordinates.y" -> Comparator.comparing(m -> m.getCoordinates() == null ? null : m.getCoordinates().getY(), Comparator.nullsLast(Long::compareTo));
                case "creationDate" -> Comparator.comparing(MusicBand::getCreationDate, Comparator.nullsLast(LocalDate::compareTo));
                case "numberOfParticipants" -> Comparator.comparing(MusicBand::getNumberOfParticipants, Comparator.nullsLast(Integer::compareTo));
                case "albumsCount" -> Comparator.comparing(MusicBand::getAlbumsCount, Comparator.nullsLast(Integer::compareTo));
                case "description" -> Comparator.comparing(MusicBand::getDescription, Comparator.nullsLast(String::compareTo));
                case "genre" -> Comparator.comparing(m -> m.getGenre() == null ? null : m.getGenre().name(), Comparator.nullsLast(String::compareTo));
                case "bestAlbum.name" -> Comparator.comparing(m -> m.getBestAlbum() == null ? null : m.getBestAlbum().getName(), Comparator.nullsLast(String::compareTo));
                case "bestAlbum.tracks" -> Comparator.comparing(m -> m.getBestAlbum() == null ? null : m.getBestAlbum().getTracks(), Comparator.nullsLast(Long::compareTo));
                default -> Comparator.comparing(MusicBand::getId);
            };
            if (desc) c = c.reversed();
            comparator = comparator == null ? c : comparator.thenComparing(c);
        }
        return comparator == null ? Comparator.comparing(MusicBand::getId) : comparator;
    }

    private MusicBandAllSchema toDto(MusicBand e) {
        MusicBandAllSchema dto = new MusicBandAllSchema();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setCoordinates(e.getCoordinates());
        dto.setCreationDate(e.getCreationDate());
        dto.setNumberOfParticipants(e.getNumberOfParticipants());
        dto.setAlbumsCount(e.getAlbumsCount());
        dto.setDescription(e.getDescription());
        dto.setGenre(e.getGenre());
        dto.setBestAlbum(e.getBestAlbum());
        return dto;
    }
}


