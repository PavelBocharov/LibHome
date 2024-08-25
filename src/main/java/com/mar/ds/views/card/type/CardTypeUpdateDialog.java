package com.mar.ds.views.card.type;

import com.mar.ds.db.entity.CardType;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views._build.popup.UpdateViewDialog;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

public class CardTypeUpdateDialog extends UpdateViewDialog<CardType, CardTypeViewDialog> {

    @Override
    protected CardType updateEntity(CardType entity, BigDecimalField enumId, TextField title) {
        entity.setTitle(ViewUtils.getTextFieldValue(title));
        return entity;
    }
}
