package com.mar.ds.views.card.type;

import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.build.popup.CreateViewDialog;
import com.mar.libhome.dto.CardTypeDto;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

public class CardTypeCreateDialog extends CreateViewDialog<CardTypeDto, CardTypeViewDialog> {

    @Override
    protected CardTypeDto getNewEntity(BigDecimalField enumId, TextField title) {
        return CardTypeDto.builder()
                .title(ViewUtils.getTextFieldValue(title).orElseThrow(() -> new RuntimeException("Card type TITLE is EMPTY.")))
                .build();
    }

}
