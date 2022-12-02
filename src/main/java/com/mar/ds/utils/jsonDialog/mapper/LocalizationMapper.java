package com.mar.ds.utils.jsonDialog.mapper;

import com.mar.ds.db.entity.Localization;
import com.mar.ds.utils.jsonDialog.jsonData.LocalizationData;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocalizationMapper {

    default LocalizationData getLocalizationDataList(List<Localization> localizationList) {
        if (localizationList == null || localizationList.isEmpty()) return new LocalizationData();

        Map<String, Map<LocalizationData.Localization, String>> dictionary = new HashMap<>();

        for (Localization localization : localizationList) {
            HashMap<LocalizationData.Localization, String> localMap = new HashMap<>();
            localMap.put(LocalizationData.Localization.en, localization.getEn());
            localMap.put(LocalizationData.Localization.ru, localization.getRu());

            dictionary.put(localization.getKey(), localMap);
        }

        return LocalizationData.builder()
                .dictionary(dictionary)
                .build();
    }


}
