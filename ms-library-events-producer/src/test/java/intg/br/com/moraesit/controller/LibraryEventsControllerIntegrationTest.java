package br.com.moraesit.controller;

import br.com.moraesit.domain.Book;
import br.com.moraesit.domain.LibraryEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DisplayName("POST library event with successfully")
    void postLibraryEventTest() {
        //given
        Book book = Book.builder()
                .bookId(123)
                .bookName("Kafka with Spring Boot")
                .bookAuthor("Joao Pedro")
                .bookIsbn("1232323232323")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LibraryEvent> httpEntity = new HttpEntity<>(libraryEvent, httpHeaders);

        //when
        ResponseEntity<LibraryEvent> exchange = restTemplate.exchange("/v1/library-event", HttpMethod.POST,
                httpEntity, LibraryEvent.class);

        //then
        assertEquals(HttpStatus.CREATED, exchange.getStatusCode());
    }

}
