package com.mar.ds.service;

import com.mar.ds.views.jsonDialog.mapper.ItemMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
public class MapperService {

    private final ItemMapper itemMapper;

}
