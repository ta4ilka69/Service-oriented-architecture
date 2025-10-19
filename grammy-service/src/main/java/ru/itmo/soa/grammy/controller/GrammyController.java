package ru.itmo.soa.grammy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import jakarta.validation.Valid;

import ru.itmo.soa.grammy.dto.ErrorResponse;
import ru.itmo.soa.grammy.dto.MusicBandAllSchema;
import ru.itmo.soa.grammy.dto.ParticipantSchema;
import ru.itmo.soa.grammy.dto.Single;
import ru.itmo.soa.grammy.dto.SingleSchema;
import ru.itmo.soa.grammy.dto.MusicBandPatch;
import java.util.Collections;

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
    public ResponseEntity<?> addSingle(
            @PathVariable("band-id") String bandId,
            @Valid @RequestBody SingleSchema body) {
        try {
            // Validate that the band exists and read current albumsCount
            String url = musicServiceBaseUrl + "/music-bands/" + bandId;

            HttpHeaders getHeaders = new HttpHeaders();
            getHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            MusicBandAllSchema current = restTemplate
                    .exchange(url, HttpMethod.GET, new HttpEntity<>(getHeaders), MusicBandAllSchema.class)
                    .getBody();

            if (current == null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_XML);
                return new ResponseEntity<>(new ErrorResponse(404, "Not Found"), headers, HttpStatus.NOT_FOUND);
            }

            // Increase albumsCount by 1
            int nextAlbumsCount = (current.getAlbumsCount() == null ? 1 : current.getAlbumsCount() + 1);
            MusicBandPatch patch = new MusicBandPatch();
            patch.setAlbumsCount(nextAlbumsCount);

            HttpHeaders patchHeaders = new HttpHeaders();
            patchHeaders.setContentType(MediaType.APPLICATION_XML);
            patchHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            restTemplate.exchange(
                    url,
                    HttpMethod.PATCH,
                    new HttpEntity<>(patch, patchHeaders),
                    MusicBandAllSchema.class);

            // Create and return Single
            Single created = new Single();
            created.setName(body.getName());
            created.setTracks(1L);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RestClientResponseException ex) {
            int status = ex.getStatusCode().value();
            ErrorResponse error = new ErrorResponse(status, ex.getStatusText());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            return new ResponseEntity<>(error, headers, HttpStatus.valueOf(status));
        }
    }

    @PostMapping(value = "/band/{band-id}/participants/add", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> addParticipant(
            @PathVariable("band-id") String bandId,
            @Valid @RequestBody ParticipantSchema body) {
        try {
            // Call music-service PATCH to increase numberOfParticipants by 1
            String url = musicServiceBaseUrl + "/music-bands/" + bandId;

            // Fetch current participants, then PATCH only that field
            HttpHeaders getHeaders = new HttpHeaders();
            getHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            MusicBandAllSchema current = restTemplate
                    .exchange(url, HttpMethod.GET, new HttpEntity<>(getHeaders), MusicBandAllSchema.class)
                    .getBody();
            if (current == null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_XML);
                return new ResponseEntity<>(new ErrorResponse(404, "Not Found"), headers, HttpStatus.NOT_FOUND);
            }
            int next = (current.getNumberOfParticipants() == null ? 1 : current.getNumberOfParticipants() + 1);
            MusicBandPatch patch = new MusicBandPatch();
            patch.setNumberOfParticipants(next);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_XML));

            MusicBandAllSchema updated = restTemplate
                    .exchange(
                            url,
                            HttpMethod.PATCH,
                            new HttpEntity<>(patch, headers),
                            MusicBandAllSchema.class)
                    .getBody();
            return ResponseEntity.status(HttpStatus.CREATED).body(updated);
        } catch (RestClientResponseException ex) {
            int status = ex.getStatusCode().value();
            ErrorResponse error = new ErrorResponse(status, ex.getStatusText());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            return new ResponseEntity<>(error, headers, HttpStatus.valueOf(status));
        }
    }
}
