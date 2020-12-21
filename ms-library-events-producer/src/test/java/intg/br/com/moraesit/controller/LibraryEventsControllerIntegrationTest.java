package br.com.moraesit.controller;

import br.com.moraesit.domain.Book;
import br.com.moraesit.domain.LibraryEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils
                .consumerProps("group1", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer())
        .createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @Timeout(3)
    @DisplayName("POST library event with successfully")
    void postLibraryEventTest() throws InterruptedException {
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

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LibraryEvent> httpEntity = new HttpEntity<>(libraryEvent, httpHeaders);

        //when
        ResponseEntity<LibraryEvent> exchange = restTemplate.exchange("/v1/library-event", HttpMethod.POST,
                httpEntity, LibraryEvent.class);

        //then
        assertEquals(HttpStatus.CREATED, exchange.getStatusCode());

        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
        //Thread.sleep(3000);

        String expectedRecord = "{\"libraryEventId\":null,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":123,\"bookName\":\"Kafka with Spring Boot\",\"bookAuthor\":\"Joao Pedro Moraes\",\"bookIsbn\":\"1232323232323\"}}";
        String value = consumerRecord.value();

        assertEquals(expectedRecord, value);
    }

    @Test
    @Timeout(3)
    @DisplayName("PUT library event with successfully")
    void putLibraryEventTest() throws InterruptedException {
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

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LibraryEvent> httpEntity = new HttpEntity<>(libraryEvent, httpHeaders);

        //when
        ResponseEntity<LibraryEvent> exchange = restTemplate.exchange("/v1/library-event", HttpMethod.PUT,
                httpEntity, LibraryEvent.class);

        //then
        assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
        //Thread.sleep(3000);

        String expectedRecord = "{\"libraryEventId\":123,\"libraryEventType\":\"UPDATE\",\"book\":{\"bookId\":123,\"bookName\":\"Kafka with Spring Boot\",\"bookAuthor\":\"Joao Pedro Moraes\",\"bookIsbn\":\"1232323232323\"}}";
        String value = consumerRecord.value();

        assertEquals(expectedRecord, value);
    }
}