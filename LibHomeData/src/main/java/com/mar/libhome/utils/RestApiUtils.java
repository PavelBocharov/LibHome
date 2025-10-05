package com.mar.libhome.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mar.libhome.exception.BaseLibHomeException;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

@UtilityClass
public class RestApiUtils {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);


    public static String getUri(String host, Integer port) {
        return String.format("http://%s:%d", host, port);
    }

    public String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new BaseLibHomeException(String.format("Cannot mapping obj to JSON '%s'", obj), e);
        }
    }

    public <Clazz> Clazz fromJson(String data, Class<Clazz> dataClass) {
        try {
            return mapper.readValue(data, dataClass);
        } catch (JsonProcessingException e) {
            throw new BaseLibHomeException(String.format("Cannot mapping JSON to obj '%s' with class '%s'", data, dataClass), e);
        }
    }

    public <Clazz> Clazz fromJson(String data, TypeReference<Clazz> typeReference) {
        try {
            return mapper.readValue(data, typeReference);
        } catch (JsonProcessingException e) {
            throw new BaseLibHomeException(String.format("Cannot mapping JSON to obj '%s' with type reference '%s'", data, typeReference), e);
        }
    }

    public static String post(String uri, String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(HttpRequest.BodyPublishers.ofString(body, UTF_8))
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new BaseLibHomeException(String.format("Cannot send POST '%s'", uri), e);
        }

        if (response.statusCode() != 200) {
            throw new BaseLibHomeException(String.format("Cannot send POST '%s'", uri), new Exception(response.body()));
        }
        return response.body();
    }

    public static String delete(String uri, String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .method("DELETE", HttpRequest.BodyPublishers.ofString(body))
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new BaseLibHomeException(String.format("Cannot send POST '%s'", uri), e);
        }

        if (response.statusCode() != 200) {
            throw new BaseLibHomeException(String.format("Cannot send POST '%s'", uri), new Exception(response.body()));
        }
        return response.body();
    }

    public static String get(String uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new BaseLibHomeException(String.format("Cannot send GET '%s'", uri), e);
        }

        if (response.statusCode() != 200) {
            throw new BaseLibHomeException(String.format("Cannot send GET '%s'", uri), new Exception(response.body()));
        }
        return response.body();
    }

}