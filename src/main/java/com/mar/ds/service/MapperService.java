package com.mar.ds.service;

import com.mar.ds.utils.jsonDialog.mapper.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
public class MapperService {

    private final ItemMapper itemMapper;
    private final TaskMapper taskMapper;
    private final MissionMapper missionMapper;
    private final ActionMapper actionMapper;
    private final DialogMapper dialogMapper;
    private final DocumentMapper documentMapper;
    private final LocalizationMapper localizationMapper;

}
