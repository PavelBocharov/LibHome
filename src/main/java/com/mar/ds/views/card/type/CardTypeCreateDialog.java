package com.mar.ds.views.card.type;

import com.mar.ds.db.entity.CardType;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views._build.popup.CreateViewDialog;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

public class CardTypeCreateDialog extends CreateViewDialog<CardType, CardTypeViewDialog> {

    @Override
    protected CardType getNewEntity(BigDecimalField enumId, TextField title) {
        return CardType.builder()
                .title(ViewUtils.getTextFieldValue(title))
                .build();
    }

}
