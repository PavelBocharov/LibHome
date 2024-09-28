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
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.klaudeta.PaginatedGrid;

import java.awt.*;
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
import static java.lang.Math.abs;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
public class CardView implements ContentView {

    private final MainView appLayout;

    private int minPoint;
    private int maxPoint;
    private int minRate = Integer.MAX_VALUE;
    private int maxRate = Integer.MIN_VALUE;
    private PaginatedGrid<Card> grid;

    public VerticalLayout getContent() {
        minPoint = Integer.parseInt(appLayout.getEnv().getProperty("card.point.min", "0"));
        maxPoint = Integer.parseInt(appLayout.getEnv().getProperty("card.point.max", "10"));

        List<Card> cardList = appLayout.getRepositoryService().getCardRepository().findWithOrderByPoint();
        for (Card card : cardList) {
            double rate = calcRate(card);
            if (rate < minRate) minRate = (int) rate;
            if (rate > maxRate) maxRate = (int) rate;
        }
        if (maxRate <= minRate) maxRate = minRate + 1;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        // TABLE
        grid = new PaginatedGrid<>();

        // column
        grid.addColumn(Card::getId)
                .setHeader("ID")
                .setAutoWidth(true).setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.START);
        grid.addComponentColumn(card -> {
                    Icon icon = getStatusIcon(card, mathUpd(card));
                    icon.getStyle().set("margin", "0px");
                    return icon;
                })
                .setHeader("Status").setSortable(true)
                // ~ -> last symbol in ASCII table (nope, 'DEL' is last).
                .setComparator(Comparator.comparing(o -> mathUpd(o) ? "~" : o.getCardStatus().getTitle()))
                .setAutoWidth(true).setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addComponentColumn(this::getEngineIcon)
                .setHeader("Engine")
                .setAutoWidth(true).setFlexGrow(0)
                .setSortable(true)
                .setComparator(Card::getEngine)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(Card::getTitle)
                .setHeader("Title")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addComponentColumn(card -> getLabelWithColor(card::getPoint)).setHeader("Point")
                .setAutoWidth(true).setFlexGrow(0)
                .setSortable(true)
                .setComparator(Card::getPoint)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addComponentColumn(card -> getLabelWithColor(() -> calcRate(card), minRate, maxRate)).setHeader("Rate")
                .setAutoWidth(true).setFlexGrow(0)
                .setSortable(true)
                .setComparator(this::calcRate)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(card -> dateFormat.format(card.getLastUpdate()))
                .setHeader("Last UPD").setSortable(true)
                .setComparator(Comparator.comparingLong(value -> value.getLastUpdate().getTime()))
                .setAutoWidth(true).setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(card -> dateFormat.format(card.getLastGame()))
                .setHeader("Last game").setSortable(true)
                .setComparator(Comparator.comparingLong(value -> value.getLastGame().getTime()))
                .setAutoWidth(true).setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(card -> card.getCardType().getTitle())
                .setAutoWidth(true).setFlexGrow(0)
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
                .setAutoWidth(true).setFlexGrow(0)
                .setHeader("Link")
                .setTextAlign(ColumnTextAlign.END);
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
                .setAutoWidth(true).setFlexGrow(0)
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
            List<Card> cards = appLayout.getRepositoryService().getCardRepository().findWithOrderByPoint();
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

        // value
        reloadGrid();

        // create view
        H3 label = new H3("Card list");
        label.setWidthFull();

        Select<Button> settingButtons = new Select<>();
        settingButtons.setPlaceholder("Settings");
        settingButtons.add(getBtns());

        Div div = new Div();
        div.setWidthFull();

        HorizontalLayout header = new HorizontalLayout(label, div, searchField, settingButtons);
        header.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, div, searchField, settingButtons);
        header.setWidthFull();

        header.setMaxHeight(40, Unit.PIXELS);
        grid.setSizeFull();

        VerticalLayout verticalLayout = new VerticalLayout(header, grid);
        verticalLayout.setSizeFull();
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, header);

        return verticalLayout;
    }

    private Button[] getBtns() {
        Button crtBtn = new Button("Add", new Icon(PLUS), click -> new CreateCardView(appLayout));
        crtBtn.setWidthFull();
        crtBtn.getStyle().set("color", "green");

        Button cardStatusView = new Button(
                "Status list", new Icon(COG), click -> new CardStatusViewDialog(appLayout)
        );
        cardStatusView.setWidthFull();

        Button cardTypeView = new Button(
                "Types", new Icon(COMPILE), click -> new CardTypeViewDialog(appLayout)
        );
        cardTypeView.setWidthFull();

        Button cardTypeTagView = new Button(
                "Type tags", new Icon(CUBES), click -> new CardTagsView(appLayout).showDialog()
        );
        cardTypeTagView.setWidthFull();

        return new Button[]{crtBtn, cardStatusView, cardTypeView, cardTypeTagView};
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

    private Component getLabelWithColor(Supplier<Double> forColor, int min, int max) {
        double value = forColor.get() != null ? forColor.get() : 0;
        Label res = new Label(String.format("%.1f", value));

        String greenHex = "3cb043";
        String redHex = "c91203";
        Color colorTo = new Color(
                Integer.valueOf(greenHex.substring(0, 2), 16),
                Integer.valueOf(greenHex.substring(2, 4), 16),
                Integer.valueOf(greenHex.substring(4, 6), 16)
        );
        Color colorFrom = new Color(
                Integer.valueOf(redHex.substring(0, 2), 16),
                Integer.valueOf(redHex.substring(2, 4), 16),
                Integer.valueOf(redHex.substring(4, 6), 16)
        );

        String hex = String.format("#%02x%02x%02x",
                calcGradient(colorFrom.getRed(), colorTo.getRed(), min, max, (int) value),
                calcGradient(colorFrom.getGreen(), colorTo.getGreen(), min, max, (int) value),
                calcGradient(colorFrom.getBlue(), colorTo.getBlue(), min, max, (int) value)
        );

//        log.info("Get color - min: {}, max: {}, value: {}, color: {}", min, max, value, hex);
        res.getStyle().set("color", hex);
        return res;
    }

    private Component getLabelWithColor(Supplier<Double> forColor) {
        return getLabelWithColor(forColor, minPoint, maxPoint);
    }

    private int calcGradient(int colorFrom, int colorTo, int min, int max, int point) {
        int steps = abs(min - max);
        double colorStep = (double) (colorTo - colorFrom) / steps;
        int bufValue = point;
        double color = colorFrom;
        while (bufValue > min) {
            color += colorStep;
            bufValue--;
        }
//        log.debug("colorFrom: {}, colorTo: {}, cStep: {}, color: {}, steps: {}", colorFrom, colorTo, colorStep, color, steps);
        return (int) color;
    }

    public void reloadGrid() {
        List<Card> cards = appLayout.getRepositoryService().getCardRepository().findWithOrderByPoint();
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
