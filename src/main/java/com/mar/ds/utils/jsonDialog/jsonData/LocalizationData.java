package com.mar.ds.utils.jsonDialog.jsonData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalizationData implements Serializable {

    private Map<String, Map<Localization, String>> dictionary;

    public enum Localization {
        en, ru
    }

}
