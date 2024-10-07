package com.mar.ds.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mar.ds.data.GridInfo;
import com.mar.ds.data.ViewInfo;
import com.mar.ds.db.entity.ViewType;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@UtilityClass
public class FileUtils {

    @SneakyThrows
    public static void deleteDir(String pathDir) {
        File dir = new File(pathDir);
        if (dir.exists() && dir.isDirectory()) {
            org.apache.commons.io.FileUtils.deleteDirectory(dir);
        }
    }

    public static EnumMap<ViewType, Map<String, String>> loadContentInfo(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("load json from: " + filePath);
        EnumMap<ViewType, Map<String, String>> res = new EnumMap<>(ViewType.class);
        try {
            Map<String, Map<String, String>> json = mapper.readValue(new File(filePath), Map.class);
            for (String key : json.keySet()) {
                try {
                    ViewType type = ViewType.valueOf(key);
                    res.put(type, json.get(key));
                } catch (Exception ignored) {
                    log.warn("Ignore JSON key: {}. Exception: {}", key, ExceptionUtils.getMessage(ignored));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

}
