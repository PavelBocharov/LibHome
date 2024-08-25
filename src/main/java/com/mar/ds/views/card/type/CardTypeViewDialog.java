package com.mar.ds.views.card.type;

import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.jpa.CardTypeRepository;
import com.mar.ds.views.MainView;
import com.mar.ds.views._build.popup.ViewDialog;

public class CardTypeViewDialog extends ViewDialog<CardType, CardTypeRepository, CardTypeCreateDialog, CardTypeUpdateDialog> {

    public CardTypeViewDialog(MainView appLayout) {
        super(appLayout, "Тип документа");
    }

    @Override
    protected String getText(CardType entity) {
        return entity.getTitle();
    }

    @Override
    protected CardTypeCreateDialog getCreateViewDialog() {
        return (CardTypeCreateDialog) new CardTypeCreateDialog()
                .withoutEnumNumber()
                .withNameEntity("Тип документа");
    }

    @Override
    protected CardTypeUpdateDialog getUpdateViewDialog() {
        return (CardTypeUpdateDialog) new CardTypeUpdateDialog()
                .withoutEnumNumber()
                .withNameEntity("Тип документа")
                ;
    }

    public CardTypeRepository getRepository() {
        return appLayout.getRepositoryService().getCardTypeRepository();
    }
}
