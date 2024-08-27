package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
import com.mar.ds.views.card.status.CardStatusViewDialog;
import com.mar.ds.views.card.type.CardTypeViewDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.icon.VaadinIcon.BAN;
import static com.vaadin.flow.component.icon.VaadinIcon.COG;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RequiredArgsConstructor
public class CardView implements ContentView {

    private Logger logger = LoggerFactory.getLogger(CardView.class);

    private final MainView appLayout;

    private Icon getStatusIcon(CardStatus status, boolean hasUpd) {
        if (hasUpd) {
            Icon icon = VaadinIcon.EXCLAMATION_CIRCLE.create();
            icon.setColor("#0B6623");
            return icon;
        }

        Icon icon = VaadinIcon.BULLSEYE.create();

        if (status != null && isNotBlank(status.getColor())) {
            icon.setColor(status.getColor());
        } else {
            icon.setColor("grey");
        }

        return icon;
    }

    private Anchor getLinkIcon(Card card) {
        Icon icon = VaadinIcon.EXTERNAL_LINK.create();
        Anchor anchor = new Anchor();
        anchor.add(icon);
        if (card == null || isBlank(card.getLink())) {
            icon.setColor("grey");
            anchor.setEnabled(false);
            return anchor;
        }
        anchor.setHref(card.getLink());
        return anchor;
    }

    private boolean mathUpd(Card card) {
        if (card == null || card.getLastGame() == null || card.getLastUpdate() == null) {
            return false;
        }

        return card.getLastUpdate().getTime() - card.getLastGame().getTime() > 0;
    }

    public VerticalLayout getContent() {
        H2 label = new H2("Card list");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        // TABLE
        Grid<Card> grid = new Grid<>();

        // column
//        grid.addColumn(Card::getId).setHeader("ID").setAutoWidth(true);
        grid.addComponentColumn(card -> this.getStatusIcon(card.getCardStatus(), mathUpd(card)))
                .setHeader("Status").setAutoWidth(true).setSortable(true)
                .setComparator(Comparator.comparing(o -> o.getCardStatus().getTitle()));
        grid.addColumn(card -> card.getTitle()).setHeader("Title").setAutoWidth(true);
        grid.addColumn(card -> card.getPoint())
                .setHeader("Point").setAutoWidth(true)
                .setSortable(true);
        grid.addColumn(card -> dateFormat.format(card.getLastUpdate()))
                .setHeader("Last UPD").setAutoWidth(true).setSortable(true);
        grid.addColumn(card -> dateFormat.format(card.getLastGame()))
                .setHeader("Last game").setAutoWidth(true).setSortable(true);
        grid.addColumn(card -> card.getCardType().getTitle())
                .setHeader("Type").setAutoWidth(true).setSortable(true);
        grid.addComponentColumn(this::getLinkIcon).setHeader("Type").setAutoWidth(true);
        // settings
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        // edit
        grid.addItemDoubleClickListener(
                dialogItemDoubleClickEvent -> new UpdateCardView(appLayout, dialogItemDoubleClickEvent.getItem())
        );
        grid.addComponentColumn(card -> {
            Button edtBtn = new Button(new Icon(VaadinIcon.PENCIL), clk -> {
                new UpdateCardView(appLayout, card);
            });
//            edtBtn.addThemeVariants(LUMO_TERTIARY);
            Button dltBtn = new Button(new Icon(BAN), clk -> {
                new DeleteDialogWidget(() -> {
                    appLayout.getRepositoryService().getCardRepository().delete(card);
                    appLayout.setContent(appLayout.getCardView().getContent());
                });
            });
            dltBtn.addThemeVariants(LUMO_TERTIARY);
            dltBtn.getStyle().set("color", "red");
            return new HorizontalLayout(
                    edtBtn, dltBtn
            );
        });

        // value
        List<Card> cards = appLayout.getRepositoryService().getCardRepository().findAll();
        grid.setItems(cards);
//        logger.debug("Load {} cards.", cards != null ? cards.size() : 0);
        // down buttons
        Button crtBtn = new Button("Add", new Icon(PLUS), click -> new CreateCardView(appLayout));
        crtBtn.setWidthFull();
        crtBtn.getStyle().set("color", "green");

        Button cardStatusView = new Button(
                "Status list", new Icon(COG), click -> new CardStatusViewDialog(appLayout)
        );
        cardStatusView.setWidthFull();

        Button cardTypeView = new Button(
                "Type list", new Icon(COG), click -> new CardTypeViewDialog(appLayout)
        );
        cardTypeView.setWidthFull();

        HorizontalLayout btns = new HorizontalLayout(
                crtBtn,
                cardStatusView,
                cardTypeView
        );
        btns.setWidthFull();

        // create view
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, label);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, grid);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, btns);
        verticalLayout.add(label, grid, btns);
        return verticalLayout;
    }
}
