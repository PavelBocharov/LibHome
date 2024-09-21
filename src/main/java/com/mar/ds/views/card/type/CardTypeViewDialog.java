package com.mar.ds.views.card.type;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardType;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.db.jpa.CardTypeRepository;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.mar.ds.views._build.popup.ViewDialog;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
public class CardTypeViewDialog extends ViewDialog<CardType, CardTypeRepository, CardTypeCreateDialog, CardTypeUpdateDialog> {

    public CardTypeViewDialog(MainView appLayout) {
        super(appLayout, "Card type");
    }

    @Override
    protected String getText(CardType entity) {
        return entity.getTitle();
    }

    @Override
    protected CardTypeCreateDialog getCreateViewDialog() {
        return (CardTypeCreateDialog) new CardTypeCreateDialog()
                .withoutEnumNumber()
                .withNameEntity("Card type");
    }

    @Override
    protected CardTypeUpdateDialog getUpdateViewDialog() {
        return (CardTypeUpdateDialog) new CardTypeUpdateDialog()
                .withoutEnumNumber()
                .withNameEntity("Card type");
    }

    @Override
    protected void deleteData(CardType entity) {
        try {
            new DeleteDialogWidget(() -> {
                List<Card> cards = appLayout.getRepositoryService().getCardRepository().findByCardType(entity);
                if (isEmpty(cards)) {
                    log.info("Delete card type: {}", entity);
                    List<CardTypeTag> tags = appLayout.getRepositoryService().getCardTypeTagRepository().findByCardType(entity);
                    for (CardTypeTag tag : tags) {
                        log.info("Delete card type tag: {}", tag);
                        appLayout.getRepositoryService().getCardTypeTagRepository().deleteById(tag.getId());
                    }
                    getRepository().delete(entity);
                    reloadData();
                } else {
                    log.warn("Delete card type error. Find cards with type: {}, list: {}", entity, cards);
                    ViewUtils.showErrorMsg(
                            "Delete card type ERROR",
                            new Exception(String.format("Find cards with type: '%s', count: %d.", entity.getTitle(), cards.size()))
                    );
                }
            });
        } catch (Exception ex) {
            ViewUtils.showErrorMsg("Create ERROR", ex);
        }
    }

    public CardTypeRepository getRepository() {
        return appLayout.getRepositoryService().getCardTypeRepository();
    }
}
