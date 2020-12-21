package br.com.moraesit.controller;

import br.com.moraesit.api.v1.controller.LibraryEventsController;
import br.com.moraesit.domain.Book;
import br.com.moraesit.domain.LibraryEvent;
import br.com.moraesit.domain.LibraryEventType;
import br.com.moraesit.producer.LibraryEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {LibraryEventsController.class})
@AutoConfigureMockMvc
public class LibraryEventsControllerUnitTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    LibraryEventProducer libraryEventProducer;

    @Test
    void postLibraryEventTest() throws Exception {
        //given
        Book book = Book.builder()
                .bookId(123)
                .bookName("Kafka with Spring Boot")
                .bookAuthor("Joao Pedro Moraes")
                .bookIsbn("1232323232323")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/v1/library-event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(libraryEvent));

        when(libraryEventProducer.sendLibraryEvent_Approach3(isA(LibraryEvent.class))).thenReturn(null);

        //when
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("book.bookName").value("Kafka with Spring Boot"))
                .andReturn();
    }

    @Test
    void postLibraryEventTest_4xx() throws Exception {
        //given
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(null)
                .build();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/v1/library-event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(libraryEvent));

        when(libraryEventProducer.sendLibraryEvent_Approach3(isA(LibraryEvent.class))).thenReturn(null);

        //when
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("fields", hasSize(1)))
                .andReturn();
    }

    @Test
    void putLibraryEventTest() throws Exception {
        //given
        Book book = Book.builder()
                .bookId(123)
                .bookName("Kafka with Spring Boot")
                .bookAuthor("Joao Pedro Moraes")
                .bookIsbn("1232323232323")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(123)
                .book(book)
                .build();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/v1/library-event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(libraryEvent));

        when(libraryEventProducer.sendLibraryEvent_Approach3(isA(LibraryEvent.class))).thenReturn(null);

        //when
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("book.bookName").value("Kafka with Spring Boot"))
                .andExpect(jsonPath("libraryEventType").value("UPDATE"))
                .andReturn();
    }

    @Test
    void putLibraryEventTest_4xx() throws Exception {
        //given
        Book book = Book.builder()
                .bookId(123)
                .bookName("Kafka with Spring Boot")
                .bookAuthor("Joao Pedro Moraes")
                .bookIsbn("1232323232323")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/v1/library-event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(libraryEvent));

        when(libraryEventProducer.sendLibraryEvent_Approach3(isA(LibraryEvent.class))).thenReturn(null);

        //when
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();
    }
}