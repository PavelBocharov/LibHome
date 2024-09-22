package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.db.entity.GameEngine;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.FileUtils;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
import com.mar.ds.views.card.status.CardStatusViewDialog;
import com.mar.ds.views.card.tags.CardTagsView;
import com.mar.ds.views.card.type.CardTypeViewDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.RequiredArgsConstructor;
import org.vaadin.klaudeta.PaginatedGrid;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.mar.ds.utils.ViewUtils.getStatusIcon;
import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.icon.VaadinIcon.BAN;
import static com.vaadin.flow.component.icon.VaadinIcon.COG;
import static com.vaadin.flow.component.icon.VaadinIcon.COMPILE;
import static com.vaadin.flow.component.icon.VaadinIcon.CUBES;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;
import static org.apache.commons.lang3.StringUtils.isBlank;

@RequiredArgsConstructor
public class CardView implements ContentView {

    private final MainView appLayout;

    private PaginatedGrid<Card> grid;

    public VerticalLayout getContent() {
        H2 label = new H2("Card list");
        label.setWidthFull();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        // TABLE
        grid = new PaginatedGrid<>();

        // column
        grid.addColumn(Card::getId)
                .setHeader("ID")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);
        grid.addComponentColumn(card -> {
                    Icon icon = getStatusIcon(card, mathUpd(card));
                    icon.getStyle().set("margin", "0px");
                    return icon;
                })
                .setHeader("Status").setSortable(true)
                // ~ -> last symbol in ASCII table (nope, 'DEL' is last).
                .setComparator(Comparator.comparing(o -> mathUpd(o) ? "~" : o.getCardStatus().getTitle()))
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addComponentColumn(this::getEngineIcon)
                .setHeader("Engine")
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(Card::getTitle)
                .setHeader("Title")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addComponentColumn(card -> getGridColorValue(card::getPoint)).setHeader("Point")
                .setSortable(true)
                .setComparator(Card::getPoint)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addComponentColumn(card -> getGridColorValue(() -> calcRate(card))).setHeader("Rate")
                .setSortable(true)
                .setComparator(this::calcRate)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(card -> dateFormat.format(card.getLastUpdate()))
                .setHeader("Last UPD").setSortable(true)
                .setComparator(Comparator.comparingLong(value -> value.getLastUpdate().getTime()))
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(card -> dateFormat.format(card.getLastGame()))
                .setHeader("Last game").setSortable(true)
                .setComparator(Comparator.comparingLong(value -> value.getLastGame().getTime()))
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(card -> card.getCardType().getTitle())
                .setHeader("Type")
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(card -> card.getTagList() == null || card.getTagList().isEmpty()
                        ? "---"
                        : card.getTagList().stream()
                        .map(CardTypeTag::getTitle)
                        .collect(Collectors.joining(", "))
                )
                .setHeader("Tags")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addComponentColumn(this::getLinkIcon)
                .setHeader("Link")
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addComponentColumn(card -> {
                    // Open Info BTN
                    Button infoBtn = new Button(new Icon(VaadinIcon.INFO_CIRCLE), clk -> openInfo(card));
                    infoBtn.addThemeVariants(LUMO_TERTIARY);
                    infoBtn.getStyle().set("color", "green").set("margin", "0px");
                    // Edit BTN
                    Button edtBtn = new Button(new Icon(VaadinIcon.PENCIL), clk -> new UpdateCardView(
                            appLayout,
                            card,
                            () -> appLayout.setContent(appLayout.getCardView().getContent()))
                    );
                    edtBtn.addThemeVariants(LUMO_TERTIARY);
                    edtBtn.getStyle().set("margin", "0px");
                    // Delete BN
                    Button dltBtn = new Button(new Icon(BAN), clk -> new DeleteDialogWidget(() -> {
                        appLayout.getRepositoryService().getCardRepository().delete(card);
                        appLayout.setContent(appLayout.getCardView().getContent());

                        FileUtils.deleteDir(appLayout.getEnv().getProperty("data.path") + "cards/" + card.getId());

                    }));
                    dltBtn.addThemeVariants(LUMO_TERTIARY);
                    dltBtn.getStyle().set("color", "red").set("margin", "0px");

                    return new HorizontalLayout(infoBtn, edtBtn, dltBtn);
                })
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        // settings
        grid.setWidthFull();
        grid.setPageSize(15);
        grid.setPaginatorSize(3);
        // edit
        grid.addItemDoubleClickListener(
                dialogItemDoubleClickEvent -> {
                    openInfo(dialogItemDoubleClickEvent.getItem());
                }
        );

        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setClearButtonVisible(true);
        searchField.addValueChangeListener(e -> {
            List<Card> cards = appLayout.getRepositoryService().getCardRepository().findAll();
            String text = getTextFieldValue(searchField);
            if (text != null) {
                String finalText = text.trim().toLowerCase();
                if (!finalText.isEmpty()) {
                    grid.setItems(cards.stream().filter(
                            card -> card.getTitle().toLowerCase().contains(finalText)
                                    || card.getInfo().toLowerCase().contains(finalText)
                                    || String.valueOf(card.getId()).contains(finalText)
                                    || card.getTagList().stream()
                                    .anyMatch(cardTypeTag -> cardTypeTag.getTitle().toLowerCase().contains(finalText))
                    ));
                } else {
                    grid.setItems(cards);
                }
            } else {
                grid.setItems(cards);
            }
        });
        label.getStyle().set("margin", "var(--lumo-space-m)");

        // value
        reloadGrid();

        // down buttons
        HorizontalLayout btns = getBtns();

        // create view
        HorizontalLayout header = new HorizontalLayout(label, searchField);
//        header.setVerticalComponentAlignment(FlexComponent.Alignment.START, label);
//        header.setVerticalComponentAlignment(FlexComponent.Alignment.END, searchField);
        header.setWidthFull();

        header.setMaxHeight(5, Unit.PERCENTAGE);
        grid.setHeight(80, Unit.PERCENTAGE);
        btns.setMaxHeight(5, Unit.PERCENTAGE);

        VerticalLayout verticalLayout = new VerticalLayout(header, grid, btns);
        verticalLayout.setSizeFull();
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, header);
//        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, grid);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, btns);

        return verticalLayout;
    }

    private HorizontalLayout getBtns() {
        Button crtBtn = new Button("Add", new Icon(PLUS), click -> new CreateCardView(appLayout));
        crtBtn.setWidth(10, Unit.PERCENTAGE);
        crtBtn.getStyle().set("color", "green");

        Button cardStatusView = new Button(
                "Status list", new Icon(COG), click -> new CardStatusViewDialog(appLayout)
        );
        cardStatusView.setWidth(10, Unit.PERCENTAGE);

        Button cardTypeView = new Button(
                "Types", new Icon(COMPILE), click -> new CardTypeViewDialog(appLayout)
        );
        cardTypeView.setWidth(10, Unit.PERCENTAGE);

        Button cardTypeTagView = new Button(
                "Type tags", new Icon(CUBES), click -> new CardTagsView(appLayout).showDialog()
        );
        cardTypeTagView.setWidth(10, Unit.PERCENTAGE);

        Div div = new Div();
        div.setWidthFull();
        HorizontalLayout btns = new HorizontalLayout(
                div,
                crtBtn,
                cardStatusView,
                cardTypeView,
                cardTypeTagView
        );
        btns.setWidthFull();
        btns.getStyle().set("margin", "0px");
        return btns;
    }

    private Component getEngineIcon(Card card) {
        try {
            GameEngine engine = GameEngine.DEFAULT;
            if (card != null && card.getEngine() != null) {
                engine = card.getEngine();
            }
            Image icon = ViewUtils.getImageByResource(engine.getIconPath());
            icon.setTitle(engine.getName());
            icon.setHeight(32, Unit.PIXELS);
            icon.setWidth(32, Unit.PIXELS);
            icon.getStyle().set("margin", "0px");
            return icon;
        } catch (IOException e) {
            e.printStackTrace();
            ViewUtils.showErrorMsg("Cannot load engine icon", e);
            return VaadinIcon.START_COG.create();
        }
    }

    private void openInfo(Card card) {
        CardInfoView info = new CardInfoView(appLayout, card);
        info.open();
    }

    private Component getGridColorValue(Supplier<Double> forColor) {
        double value = forColor.get() != null ? forColor.get() : 0;
        Label res = new Label(String.format("%.1f", value));

        res.getStyle().set("color", "green");
        if (value < 9) {
            res.getStyle().set("color", "#98db00");
        }
        if (value < 7) {
            res.getStyle().set("color", "#c6b400");
        }
        if (value < 5) {
            res.getStyle().set("color", "#f46900");
        }
        if (value < 3) {
            res.getStyle().set("color", "#fc4700");
        }
        if (value < 1) {
            res.getStyle().set("color", "red");
        }

        return res;
    }

    public void reloadGrid() {
        List<Card> cards = appLayout.getRepositoryService().getCardRepository().findAll();
        grid.setItems(cards);
    }

    private double calcRate(Card card) {
        if (card == null || !card.getCardStatus().getIsRate()) {
            return 0.0f;
        }

        long deltaGame = -1;
        if (card.getLastGame() != null) {
            long now = new Date().getTime() / 86400000;
            long lastGameTime = card.getLastGame().getTime() / 86400000;
            deltaGame = now - lastGameTime;
        }

        return (float) (card.getPoint() * deltaGame * 0.01);
    }


    private Anchor getLinkIcon(Card card) {
        Icon icon = VaadinIcon.EXTERNAL_LINK.create();
        icon.getStyle().set("margin", "0px");
        Anchor anchor = new Anchor();
        anchor.add(icon);
        if (card == null || isBlank(card.getLink())) {
            icon.setColor("grey");
            anchor.setEnabled(false);
            return anchor;
        }
        anchor.setHref(card.getLink());
        anchor.getStyle().set("margin", "0px");
        anchor.setTarget("_blank"); // new tab
        return anchor;
    }

    private boolean mathUpd(Card card) {
        if (card == null || card.getLastGame() == null || card.getLastUpdate() == null) {
            return false;
        }

        return card.getLastUpdate().getTime() - card.getLastGame().getTime() > 0;
    }
}
