package ru.itmo.soa.grammy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import ru.itmo.soa.grammy.dto.MusicBandAllSchema;
import ru.itmo.soa.grammy.dto.ParticipantSchema;
import ru.itmo.soa.grammy.dto.Single;
import ru.itmo.soa.grammy.dto.SingleSchema;
import ru.itmo.soa.grammy.dto.MusicBandPatch;

@RestController
@RequestMapping(value = "/api/v1/grammy", produces = MediaType.APPLICATION_XML_VALUE)
public class GrammyController {

    private final RestTemplate restTemplate;

    @Value("${music.service.base-url:https://localhost:5252}")
    private String musicServiceBaseUrl;

    public GrammyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping(value = "/band/{band-id}/singles/add", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Single> addSingle(
            @PathVariable("band-id") int bandId,
            @RequestBody SingleSchema body
    ) {
        try {
            // Validate that the band exists by calling the first service
            String url = musicServiceBaseUrl + "/music-bands/" + bandId;
            restTemplate.getForObject(url, MusicBandAllSchema.class);

            // The spec says: create Single from Album schema; here we just echo the name and 1 track
            Single created = new Single();
            created.setName(body.getName());
            created.setTracks(1L);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RestClientResponseException ex) {
            return ResponseEntity.status(ex.getRawStatusCode()).build();
        }
    }

    @PostMapping(value = "/band/{band-id}/participants/add", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<MusicBandAllSchema> addParticipant(
            @PathVariable("band-id") int bandId,
            @RequestBody ParticipantSchema body
    ) {
        try {
            // Call music-service PATCH to increase numberOfParticipants by 1
            String url = musicServiceBaseUrl + "/music-bands/" + bandId;

            // Fetch current participants, then PATCH only that field
            MusicBandAllSchema current = restTemplate.getForObject(url, MusicBandAllSchema.class);
            if (current == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            int next = (current.getNumberOfParticipants() == null ? 1 : current.getNumberOfParticipants() + 1);
            MusicBandPatch patch = new MusicBandPatch(next);

            MusicBandAllSchema updated = restTemplate
                    .exchange(url, org.springframework.http.HttpMethod.PATCH,
                            new org.springframework.http.HttpEntity<>(patch),
                            MusicBandAllSchema.class)
                    .getBody();
            return ResponseEntity.status(HttpStatus.CREATED).body(updated);
        } catch (RestClientResponseException ex) {
            return ResponseEntity.status(ex.getRawStatusCode()).build();
        }
    }
}


