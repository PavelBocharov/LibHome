package com.mar.ds.views.card.type;

import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.build.popup.UpdateViewDialog;
import com.mar.libhome.dto.CardTypeDto;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

public class CardTypeUpdateDialog extends UpdateViewDialog<CardTypeDto, CardTypeViewDialog> {

    @Override
    protected CardTypeDto updateEntity(CardTypeDto entity, BigDecimalField enumId, TextField title) {
        entity.setTitle(ViewUtils.getTextFieldValue(title).orElseThrow(() -> new RuntimeException("Card type TITLE is EMPTY.")));
        return entity;
    }
}
