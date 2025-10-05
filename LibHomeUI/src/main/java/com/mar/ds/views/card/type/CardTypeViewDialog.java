package com.mar.ds.views.card.type;

import com.mar.ds.db.service.CardTypeService;
import com.mar.ds.db.service.CardTypeTagService;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.mar.ds.views.build.popup.ViewDialog;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardTypeDto;
import com.mar.libhome.dto.CardTypeTagDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static java.lang.String.format;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
public class CardTypeViewDialog
        extends ViewDialog<CardTypeDto, CardTypeService, CardTypeCreateDialog, CardTypeUpdateDialog> {

    public CardTypeViewDialog(MainView appLayout) {
        super(appLayout, "Card type");
    }

    @Override
    protected String getText(CardTypeDto entity) {
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
    protected void deleteData(CardTypeDto entity) {
        try {
            new DeleteDialogWidget(() -> {
                List<CardDto> cards = appLayout.getCardService().findByCardType(entity);
                if (isEmpty(cards)) {
                    log.info("Delete card type: {}", entity);
                    List<CardTypeTagDto> tags = getTagRepository().findByCardType(entity);
                    for (CardTypeTagDto tag : tags) {
                        log.info("Delete card type tag: {}", tag);
                        getTagRepository().deleteById(tag.getId());
                    }
                    getRepository().delete(entity);
                    reloadData();
                } else {
                    log.warn("Delete card type error. Find cards with type: {}, list: {}", entity, cards);
                    ViewUtils.showErrorMsg(
                            "Delete card type ERROR",
                            new Exception(
                                    format("Find cards with type: '%s', count: %d.", entity.getTitle(), cards.size())
                            )
                    );
                }
            });
        } catch (Exception ex) {
            ViewUtils.showErrorMsg("Create ERROR", ex);
        }
    }

    public CardTypeService getRepository() {
        return appLayout.getCardTypeService();
    }

    public CardTypeTagService getTagRepository() {
        return appLayout.getCardTypeTagService();
    }
}
