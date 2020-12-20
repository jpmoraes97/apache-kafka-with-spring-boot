package br.com.moraesit.api.controller;

import br.com.moraesit.domain.LibraryEvent;
import br.com.moraesit.domain.LibraryEventType;
import br.com.moraesit.producer.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/v1/library-event")
@RequiredArgsConstructor
public class LibraryEventsController {

    private final LibraryEventProducer libraryEventProducer;

    @PostMapping
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws
            JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        //invoke kafka producer

        //libraryEventProducer.sendLibraryEvent_Approach1(libraryEvent);
        //libraryEventProducer.sendLibraryEventSynchronous_Approach2(libraryEvent);

        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEvent_Approach3(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryEvent);
    }



}