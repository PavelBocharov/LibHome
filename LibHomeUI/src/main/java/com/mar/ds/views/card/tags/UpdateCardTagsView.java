package com.mar.ds.views.card.tags;

import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UpdateCardTagsView {

    private final MainView mainView;
    private final CardTagsView parentView;
    private final CardTypeTag tag;

    public void showDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth(50, Unit.PERCENTAGE);

        TextField title = new TextField("Tag title");
        title.setWidthFull();
        ViewUtils.setTextFieldValue(title, tag.getTitle());

        Button crtBtn = new Button(
                "Update tag",
                VaadinIcon.PLUS.create(),
                event -> {
                    tag.setTitle(ViewUtils.getTextFieldValue(title));
                    mainView.getRepositoryService().getCardTypeTagRepository().save(tag);
                    dialog.close();
                    parentView.reloadData();
                }
        );
        crtBtn.setWidthFull();

        dialog.removeAll();
        dialog.add(
                new H3("Update card tag"),
                title,
                new HorizontalLayout(
                        crtBtn,
                        ViewUtils.getCloseButton(dialog)
                )
        );
        dialog.open();
    }


}
