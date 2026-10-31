package com.mar.libhome.db.api;

import com.mar.libhome.db.service.CardHistoryService;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardHistoryDto;
import com.mar.libhome.dto.CardStatusDto;
import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.dto.CardTypeTagDto;
import com.mar.libhome.enums.GameEngine;
import com.mar.libhome.enums.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:apitestdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=usr",
        "spring.datasource.password=pwd",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class ApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    CardHistoryService historyService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void createCard() {
        CardStatusDto status = createStatus();
        CardTypeDto type = createType();
        CardTypeTagDto tag = createTypeTag(type.getId());

        CardDto card = CardDto.builder()
                .viewType(1)
                .title("title")
                .point(1.2)
                .lastGame(new Date())
                .lastUpdate(new Date())
                .engine(GameEngine.DEFAULT)
                .language(Language.DEFAULT)
                .rate(0.0)
                .cardStatus(status)
                .cardType(type)
                .tagList(List.of(tag))
                .build();

        List<CardDto> rs = webTestClient.post()
                .uri("/card")
                .bodyValue(List.of(card))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CardDto>>() {
                }) 
                .returnResult()
                .getResponseBody();

        assertNotNull(rs);
        assertEquals(1, rs.size());
        assertNotNull(rs.get(0));

        rs = webTestClient.get()
                .uri("/card")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CardDto>>() {
                }) 
                .returnResult()
                .getResponseBody();

        assertNotNull(rs);
        assertEquals(1, rs.size());
        assertNotNull(rs.get(0));

        createHistory(rs.get(0).getId());        
    }
    
    private CardHistoryDto createHistory(UUID cardId) {
        CardHistoryDto historyDto = CardHistoryDto.builder()
                .editableId(cardId)
                .titlePage("test page")
                .columnName("test column")
                .updateCardTime(new Date())
                .oldValue("")
                .newValue("new value")
                .build();

        List<CardHistoryDto> rs = webTestClient.post()
                .uri("/card/history")
                .bodyValue(List.of(historyDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CardHistoryDto>>() {
                }) 
                .returnResult()
                .getResponseBody();
        
        assertNotNull(rs);
        assertEquals(1, rs.size());
        assertNotNull(rs.get(0));
        
        rs = historyService.getByCardId(cardId);
        assertNotNull(rs);
        assertEquals(1, rs.size());
        assertNotNull(rs.get(0));
        
        return rs.get(0);
    }

    private CardStatusDto createStatus() {
        CardStatusDto status = CardStatusDto.builder()
                .icon("test")
                .color("#123457")
                .title("title status")
                .order(1L)
                .hasUpdStatus(false)
                .isRate(false)
                .build();

        List<CardStatusDto> rs = webTestClient.post()
                .uri("/card/status")
                .bodyValue(List.of(status))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CardStatusDto>>() {
                }) 
                .returnResult()
                .getResponseBody();

        assertNotNull(rs);
        assertEquals(1, rs.size());
        assertNotNull(rs.get(0));
        return rs.get(0);
    }

    private CardTypeDto createType() {
        CardTypeDto type = CardTypeDto.builder()
                .title("title type")
                .build();

        List<CardTypeDto> rs = webTestClient.post()
                .uri("/card/type")
                .bodyValue(List.of(type))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CardTypeDto>>() {
                }) 
                .returnResult()
                .getResponseBody();

        assertNotNull(rs);
        assertEquals(1, rs.size());
        assertNotNull(rs.get(0));
        return rs.get(0);
    }

    private CardTypeTagDto createTypeTag(UUID typeId) {
        CardTypeTagDto tag = CardTypeTagDto.builder()
                .title("title tag")
                .cardTypeId(typeId)
                .build();

        List<CardTypeTagDto> rs = webTestClient.post()
                .uri("/card/type/tag")
                .bodyValue(List.of(tag))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CardTypeTagDto>>() {
                }) 
                .returnResult()
                .getResponseBody();

        assertNotNull(rs);
        assertEquals(1, rs.size());
        assertNotNull(rs.get(0));
        return rs.get(0);
    }


}