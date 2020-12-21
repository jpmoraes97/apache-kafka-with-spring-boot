package br.com.moraesit.api.v1.controller;

import br.com.moraesit.domain.LibraryEvent;
import br.com.moraesit.domain.LibraryEventType;
import br.com.moraesit.producer.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/v1/library-event")
@RequiredArgsConstructor
public class LibraryEventsController {

    private final LibraryEventProducer libraryEventProducer;

    @PostMapping
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws
            JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        //invoke kafka producer

        //libraryEventProducer.sendLibraryEvent_Approach1(libraryEvent);
        //libraryEventProducer.sendLibraryEventSynchronous_Approach2(libraryEvent);

        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEvent_Approach3(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryEvent);
    }

    @PutMapping
    public ResponseEntity<?> putLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws
            JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        //invoke kafka producer
        if (libraryEvent.getLibraryEventId() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the libraryEventId");

        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEventProducer.sendLibraryEvent_Approach3(libraryEvent);

        return ResponseEntity.status(HttpStatus.OK)
                .body(libraryEvent);
    }


}