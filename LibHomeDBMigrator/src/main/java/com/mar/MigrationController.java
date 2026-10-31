package com.mar;

import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardStatusDto;
import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.dto.CardTypeTagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@RestController
@RequestMapping(value = "/migration")
@RequiredArgsConstructor
public class MigrationController {

    private final RestTemplate sourceDbRestClient;
    private final RestTemplate targetDbRestClient;

    @Value("${rest.client.url.source}")
    private String sourceUrl;

    @Value("${rest.client.url.target}")
    private String targetUrl;

    @Value("${rest.client.batch.size:50}")
    private Integer maxBatchSize;

    @Value("${storage.files}")
    private String filesPath;

    @GetMapping
    public String migrate() {
        Map<UUID, Pair<CardStatusDto, CardStatusDto>> statusList = migrateCardStatus();
        Map<UUID, Pair<CardTypeDto, CardTypeDto>> typeList = migrateCardType();
        Map<UUID, Pair<CardTypeTagDto, CardTypeTagDto>> tagList = migrateCardTypeTags(typeList);

        migrateCards(statusList, typeList, tagList);

        return "OK";
    }

    private void migrateCards(Map<UUID, Pair<CardStatusDto, CardStatusDto>> statusList,
                              Map<UUID, Pair<CardTypeDto, CardTypeDto>> typeList,
                              Map<UUID, Pair<CardTypeTagDto, CardTypeTagDto>> tagList) {
        System.out.println("Start cards migrate...");
        List<CardDto> source = sourceDbRestClient.exchange(
                sourceUrl + "/card",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CardDto>>() {
                }
        ).getBody();

        List<CardDto> cards = new ArrayList<>(maxBatchSize);
        for (CardDto card : source) {
            card.setCardStatus(statusList.get(card.getCardStatus().getId()).target);
            if (card.getOldCardStatus() != null) {
                card.setOldCardStatus(statusList.get(card.getOldCardStatus().getId()).target);
            }
            card.setCardType(typeList.get(card.getCardType().getId()).target);
            card.setTagList(
                    card.getTagList().stream()
                            .map(tag -> tagList.get(tag.getId()).target)
                            .toList()
            );

            cards.add(card);
            if (cards.size() == maxBatchSize) {
                sendCards(cards);
                cards.clear();
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        List<CardDto> target = new ArrayList<>();
        if (!cards.isEmpty()) {
            target = sendCards(cards);
        }

        int moveCount = 0;
        for (CardDto oldCard : source) {
            try {
                CardDto newCard = target.stream().filter(
                    cardDto -> Objects.equals(cardDto.getTitle(), oldCard.getTitle())
                        && Objects.equals(cardDto.getEngine(), oldCard.getEngine())
                        && Objects.equals(cardDto.getLanguage(), (oldCard.getLanguage()))
                        && Objects.equals(cardDto.getLastGame(), (oldCard.getLastGame()))
                        && Objects.equals(cardDto.getLastUpdate(), (oldCard.getLastUpdate()))
                        && Objects.equals(cardDto.getLink(), (oldCard.getLink()))
                        && Objects.equals(cardDto.getPoint(), (oldCard.getPoint()))
                        && Objects.equals(cardDto.getCardStatus().getTitle(), (oldCard.getCardStatus().getTitle()))
                        && Objects.equals(cardDto.getCardType().getTitle(), (oldCard.getCardType().getTitle()))
                        && Objects.equals(cardDto.getRate(), (oldCard.getRate()))
                ).findFirst().get();
                Path fileSource = Path.of(filesPath, oldCard.getId().toString());
                Path fileTarget = Path.of(filesPath, newCard.getId().toString());
                if (Files.exists(fileSource)) {
                    Files.move(fileSource, fileTarget);
                    moveCount++;
                } else {
                    System.out.println("Cannot move dir (not exist): " + oldCard.getId());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        System.out.printf("End cards migrate. Taget: %d, move: %d%n", target.size(), moveCount);
    }

    private List<CardDto> sendCards(List<CardDto> cards) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<CardDto>> rqList = new HttpEntity<>(cards, headers);

        List<CardDto> target = targetDbRestClient.exchange(
                targetUrl + "/card",
                HttpMethod.POST,
                rqList,
                new ParameterizedTypeReference<List<CardDto>>() {
                }
        ).getBody();
        System.out.println("Send cards migrate: " + target.size());
        return target;
    }

    private Map<UUID, Pair<CardStatusDto, CardStatusDto>> migrateCardStatus() {
        System.out.println("Start status migrate...");
        List<CardStatusDto> source = sourceDbRestClient.exchange(
                sourceUrl + "/card/status",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CardStatusDto>>() {
                }
        ).getBody();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<CardStatusDto>> rqList = new HttpEntity<>(source, headers);

        List<CardStatusDto> target = targetDbRestClient.exchange(
                targetUrl + "/card/status",
                HttpMethod.POST,
                rqList,
                new ParameterizedTypeReference<List<CardStatusDto>>() {
                }
        ).getBody();

        System.out.printf("End status migrate. Source size: %d, target size: %d%n", source.size(), target.size());

        Map<UUID, Pair<CardStatusDto, CardStatusDto>> map = new HashMap<>();

        for (CardStatusDto s1 : source) {
            CardStatusDto s2 = target.stream()
                    .filter(t -> t.getTitle().equals(s1.getTitle()))
                    .findFirst().get();
            map.put(s1.getId(), new Pair<>(s1, s2));
        }

        return map;
    }

    private Map<UUID, Pair<CardTypeDto, CardTypeDto>> migrateCardType() {
        System.out.println("Start type migrate...");
        List<CardTypeDto> source = sourceDbRestClient.exchange(
                sourceUrl + "/card/type",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CardTypeDto>>() {
                }
        ).getBody();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<CardTypeDto>> rqList = new HttpEntity<>(source, headers);

        List<CardTypeDto> target = targetDbRestClient.exchange(
                targetUrl + "/card/type",
                HttpMethod.POST,
                rqList,
                new ParameterizedTypeReference<List<CardTypeDto>>() {
                }
        ).getBody();

        System.out.printf("End type migrate. Source size: %d, target size: %d%n", source.size(), target.size());

        Map<UUID, Pair<CardTypeDto, CardTypeDto>> map = new HashMap<>();
        for (CardTypeDto s1 : source) {
            CardTypeDto s2 = target.stream()
                    .filter(t -> t.getTitle().equals(s1.getTitle()))
                    .findFirst().get();
            map.put(s1.getId(), new Pair<>(s1, s2));
        }
        return map;

    }

    private Map<UUID, Pair<CardTypeTagDto, CardTypeTagDto>> migrateCardTypeTags(Map<UUID, Pair<CardTypeDto, CardTypeDto>> types) {
        System.out.println("Start type tags migrate...");
        List<CardTypeTagDto> source = sourceDbRestClient.exchange(
                sourceUrl + "/card/type/tag",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CardTypeTagDto>>() {
                }
        ).getBody();

        for (CardTypeTagDto tag : source) {
            tag.setCardTypeId(types.get(tag.getCardTypeId()).target.getId());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<CardTypeTagDto>> rqList = new HttpEntity<>(source, headers);

        List<CardTypeTagDto> target = targetDbRestClient.exchange(
                targetUrl + "/card/type/tag",
                HttpMethod.POST,
                rqList,
                new ParameterizedTypeReference<List<CardTypeTagDto>>() {
                }
        ).getBody();

        System.out.printf("End type tags migrate. Source size: %d, target size: %d%n", source.size(), target.size());

        Map<UUID, Pair<CardTypeTagDto, CardTypeTagDto>> map = new HashMap<>();
        for (CardTypeTagDto s1 : source) {
            CardTypeTagDto s2 = target.stream()
                    .filter(t -> t.getTitle().equals(s1.getTitle()))
                    .findFirst().get();
            map.put(s1.getId(), new Pair<>(s1, s2));
        }
        return map;
    }


    class Pair<S, T> {
        public S source;
        public T target;

        public Pair(S source, T target) {
            this.source = source;
            this.target = target;
        }
    }
}
