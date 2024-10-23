package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.db.entity.GameEngine;
import com.mar.ds.db.entity.Language;
import com.mar.ds.db.entity.ViewType;
import com.mar.ds.db.jpa.CardRepository;
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
import com.vaadin.flow.component.grid.GridSortOrder;
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
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.VaadinSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.klaudeta.PaginatedGrid;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.mar.ds.data.GridInfo.GRID_BTNS;
import static com.mar.ds.data.GridInfo.GRID_DATE_GAME;
import static com.mar.ds.data.GridInfo.GRID_DATE_UPD;
import static com.mar.ds.data.GridInfo.GRID_ENGINE;
import static com.mar.ds.data.GridInfo.GRID_ID;
import static com.mar.ds.data.GridInfo.GRID_LANGUAGE;
import static com.mar.ds.data.GridInfo.GRID_LINK;
import static com.mar.ds.data.GridInfo.GRID_POINT;
import static com.mar.ds.data.GridInfo.GRID_RATE;
import static com.mar.ds.data.GridInfo.GRID_STATUS;
import static com.mar.ds.data.GridInfo.GRID_TAGS;
import static com.mar.ds.data.GridInfo.GRID_TITLE;
import static com.mar.ds.data.GridInfo.GRID_TYPE;
import static com.mar.ds.utils.FileUtils.getTitles;
import static com.mar.ds.utils.ViewUtils.getStatusIcon;
import static com.mar.ds.utils.ViewUtils.getTextFieldValue;
import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.icon.VaadinIcon.ANGLE_DOWN;
import static com.vaadin.flow.component.icon.VaadinIcon.ANGLE_UP;
import static com.vaadin.flow.component.icon.VaadinIcon.BAN;
import static com.vaadin.flow.component.icon.VaadinIcon.COG;
import static com.vaadin.flow.component.icon.VaadinIcon.COMPILE;
import static com.vaadin.flow.component.icon.VaadinIcon.CUBES;
import static com.vaadin.flow.component.icon.VaadinIcon.DATE_INPUT;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;
import static java.lang.Math.abs;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
public class CardView implements ContentView {

    private final MainView mainView;
    @Getter
    private final ViewType viewType;

    private int minPoint;
    private int maxPoint;
    private int minRate = Integer.MAX_VALUE;
    private int maxRate = Integer.MIN_VALUE;
    private PaginatedGrid<Card> grid;

    public static final int DEFAULT_GRID_ICON_SIZE_INT = 36;
    public static final String DEFAULT_GRID_ICON_SIZE_VAR = "var(--iron-icon-width, " + DEFAULT_GRID_ICON_SIZE_INT + "px)";

    public static final String GRID_COLUMN_SORT_KEY = "grid-column-sort-key";
    // TODO for custom pagination
//    public static final String GRID_COLUMN_PAGE_KEY = "grid-column-page-key";
    public static final String GRID_COLUMN_SEARCH_TEXT_KEY = "grid-column-search-text-key";
    public static final String GRID_COLUMN_EMPTY = "";

    public VerticalLayout getContent() {
        log.debug("Get content by {}", viewType);
        minPoint = Integer.parseInt(mainView.getEnv().getProperty("app.card.point.min", "0"));
        maxPoint = Integer.parseInt(mainView.getEnv().getProperty("app.card.point.max", "10"));
        // calc min/max rate
        List<Card> cardList = mainView.getRepositoryService().getCardRepository().findWithOrderByPoint(viewType);
        for (Card card : cardList) {
            double rate = calcRate(card);
            if (rate < minRate) minRate = (int) rate;
            if (rate > maxRate) maxRate = (int) rate;
        }
        if (maxRate <= minRate) maxRate = minRate + 1;
        // clean filter
        VaadinSession.getCurrent().setAttribute(GRID_COLUMN_SORT_KEY, emptyList());
//        VaadinSession.getCurrent().setAttribute(GRID_COLUMN_PAGE_KEY, 1);
        VaadinSession.getCurrent().setAttribute(GRID_COLUMN_SEARCH_TEXT_KEY, GRID_COLUMN_EMPTY);

        // TABLE
        grid = new PaginatedGrid<>();
        // listeners
        initGridListeners();
        // column
        initGridColumn();
        // settings
        grid.setWidthFull();
        grid.setPageSize(mainView.getEnv().getProperty("app.grid.row.count", Integer.class, 15));
        grid.setPaginatorSize(3);

        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setClearButtonVisible(true);
        searchField.addValueChangeListener(e -> {
            log.debug("search text");
            List<Card> cards = mainView.getRepositoryService().getCardRepository().findWithOrderByPoint(viewType);
            String text = getTextFieldValue(searchField);
            if (text != null) {
                String finalText = text.trim().toLowerCase();
                if (!finalText.isEmpty()) {
                    VaadinSession.getCurrent().setAttribute(GRID_COLUMN_SEARCH_TEXT_KEY, finalText);
                    setItemsByTextSearch(cards, finalText);
                } else {
                    VaadinSession.getCurrent().setAttribute(GRID_COLUMN_SEARCH_TEXT_KEY, GRID_COLUMN_EMPTY);
                    grid.setItems(cards);
                }
            } else {
                VaadinSession.getCurrent().setAttribute(GRID_COLUMN_SEARCH_TEXT_KEY, GRID_COLUMN_EMPTY);
                grid.setItems(cards);
            }
        });

        // value
//        reloadData();

        // create view
        H3 label = new H3(viewType.getTitle());
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

    private void setItemsByTextSearch(List<Card> cards, String textSearch) {
        if (isBlank(textSearch)) {
            grid.setItems(cards);
        } else {
            grid.setItems(cards.stream().filter(
                    card -> card.getTitle().toLowerCase().contains(textSearch)
                            || card.getInfo().toLowerCase().contains(textSearch)
                            || String.valueOf(card.getId()).contains(textSearch)
                            || card.getTagList().stream()
                            .anyMatch(cardTypeTag -> cardTypeTag.getTitle().toLowerCase().contains(textSearch))
            ));
        }
    }

    private Button[] getBtns() {
        Button crtBtn = new Button(
                "Add",
                new Icon(PLUS),
                click -> new CreateCardView(mainView, viewType).showDialog()
        );
        crtBtn.setWidthFull();
        crtBtn.getStyle().set("color", "green");

        Button cardStatusView = new Button(
                "Status list", new Icon(COG), click -> new CardStatusViewDialog(mainView)
        );
        cardStatusView.setWidthFull();

        Button cardTypeView = new Button(
                "Types", new Icon(COMPILE), click -> new CardTypeViewDialog(mainView)
        );
        cardTypeView.setWidthFull();

        Button cardTypeTagView = new Button(
                "Type tags", new Icon(CUBES), click -> new CardTagsView(mainView).showDialog()
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
            Image icon = new Image(engine.getIconPath(), engine.getName());
            icon.setTitle(engine.getName());
            icon.setHeight(DEFAULT_GRID_ICON_SIZE_VAR);
            icon.setWidth(DEFAULT_GRID_ICON_SIZE_VAR);
            icon.getStyle().set("margin-bottom", "-6px");
            return icon;
        } catch (Exception e) {
            e.printStackTrace();
            ViewUtils.showErrorMsg("Cannot load engine icon", e);
            return VaadinIcon.START_COG.create();
        }
    }

    private void openInfo(Card card) {
        CardInfoView info = new CardInfoView(mainView, card);
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
        return (int) color;
    }

    @Override
    public void reloadData() {
        log.debug("reloadData");
        List<GridSortOrder<Card>> sort = (List<GridSortOrder<Card>>) VaadinSession.getCurrent().getAttribute(GRID_COLUMN_SORT_KEY);
//        int page = (int) Optional.ofNullable(
//                VaadinSession.getCurrent().getAttribute(GRID_COLUMN_PAGE_KEY))
//                .orElse(1);
        String searchText = (String) VaadinSession.getCurrent().getAttribute(GRID_COLUMN_SEARCH_TEXT_KEY);

        List<Card> cards = mainView.getRepositoryService().getCardRepository().findWithOrderByPoint(viewType);
        if (!isBlank(searchText)) {
            setItemsByTextSearch(cards, searchText);
        } else {
            grid.setItems(cards);
        }
        if (!isEmpty(sort)) {
            grid.sort(sort);
        }
//        if (page > 0) {
//            grid.setPage(page);
//        }
    }

    private void initGridListeners() {
        grid.addSortListener(event -> {
            Collection<GridSortOrder<Card>> sortList = event.getSortOrder();
            if (!isEmpty(sortList)) {
                VaadinSession.getCurrent().setAttribute(GRID_COLUMN_SORT_KEY, event.getSortOrder());
            } else {
                VaadinSession.getCurrent().setAttribute(GRID_COLUMN_SORT_KEY, emptyList());
            }
        });
//        grid.addPageChangeListener(event -> {
//            VaadinSession.getCurrent().setAttribute(GRID_COLUMN_PAGE_KEY, event.getNewPage());
//        });
        grid.addItemDoubleClickListener(
                dialogItemDoubleClickEvent -> {
                    openInfo(dialogItemDoubleClickEvent.getItem());
                }
        );
        grid.setDataProvider(getDataProvider(mainView.getRepositoryService().getCardRepository()));
    }

    DataProvider<Card, String> getDataProvider(CardRepository service) {
        DataProvider<Card, Predicate<Card>> predicateDataProvider = DataProvider.fromFilteringCallbacks(
                query -> service.findWithOrderByPoint(viewType).stream(),
                query -> service.findWithOrderByPoint(viewType).size()
        );

        DataProvider<Card, String> dataProvider = predicateDataProvider
                .withConvertedFilter(text -> (card -> {
                    log.debug("withConvertedFilter: {}", text);
                    return card.getTitle().startsWith(text);
                }));

        return dataProvider;
    }

    private void initGridColumn() {
        Map<String, String> gridConfig = getTitles(viewType, mainView.getContentJSON());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        if (gridConfig.containsKey(GRID_ID)) {
            grid.addColumn(Card::getId)
                    .setHeader(gridConfig.get(GRID_ID))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.START)
                    .setId(GRID_ID);
        }
        if (gridConfig.containsKey(GRID_STATUS)) {
            grid.addComponentColumn(card -> {
                        Icon icon = getStatusIcon(card, mathUpd(card));
                        icon.getStyle().set("margin", "0px");
                        return icon;
                    })
                    .setHeader(gridConfig.get(GRID_STATUS))
                    .setSortable(true)
                    // ~ -> last symbol in ASCII table (nope, 'DEL' is last).
                    .setComparator(Comparator.comparing(o -> mathUpd(o) ? "~" : o.getCardStatus().getTitle()))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_STATUS);
        }
        if (gridConfig.containsKey(GRID_ENGINE)) {
            grid.addComponentColumn(this::getEngineIcon)
                    .setHeader(gridConfig.get(GRID_ENGINE))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setSortable(true)
                    .setComparator(Card::getEngine)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_ENGINE);
        }
        if (gridConfig.containsKey(GRID_LANGUAGE)) {
            grid.addComponentColumn(card -> Optional
                            .ofNullable(card.getLanguage())
                            .orElse(Language.DEFAULT)
                            .getImage(DEFAULT_GRID_ICON_SIZE_INT)
                    )
                    .setHeader(gridConfig.get(GRID_LANGUAGE))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setSortable(true)
                    .setComparator(Card::getLanguage)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_LANGUAGE);
        }
        if (gridConfig.containsKey(GRID_TITLE)) {
            grid.addColumn(Card::getTitle)
                    .setHeader(gridConfig.get(GRID_TITLE))
                    .setAutoWidth(true)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_TITLE);
        }
        if (gridConfig.containsKey(GRID_POINT)) {
            grid.addComponentColumn(card -> getLabelWithColor(card::getPoint))
                    .setHeader(gridConfig.get(GRID_POINT))
                    .setAutoWidth(true)
                    .setFlexGrow(0)
                    .setSortable(true)
                    .setComparator(Card::getPoint)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_POINT);
        }
        if (gridConfig.containsKey(GRID_RATE)) {
            grid.addComponentColumn(card -> getLabelWithColor(() -> calcRate(card), minRate, maxRate))
                    .setHeader(gridConfig.get(GRID_RATE))
                    .setAutoWidth(true)
                    .setFlexGrow(0)
                    .setSortable(true)
                    .setComparator(this::calcRate)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_RATE);
        }

        Icon mainIcon = new Icon(DATE_INPUT);
        mainIcon.setId("date-upd-main-icon");
        Button myHeader = new Button("Date", mainIcon);
        myHeader.addClickListener(event -> {
            log.debug("My header listener");
            Icon icon = (Icon) myHeader.getIcon();
            int order = 0;
            switch (icon.getId().get()) {
                case "date-upd-main-icon": {
                    Icon upIcon = new Icon(ANGLE_UP);
                    upIcon.setId("date-upd-up-icon");
                    myHeader.setIcon(upIcon);
                    order = 1;
                    break;
                }
                case "date-upd-up-icon": {
                    Icon downIcon = new Icon(ANGLE_DOWN);
                    downIcon.setId("date-upd-down-icon");
                    myHeader.setIcon(downIcon);
                    order = -1;
                    break;
                }
                default: {
                    order = 0;
                    myHeader.setIcon(mainIcon);
                }
            }
            List<Card> data = mainView.getRepositoryService().getCardRepository().findWithOrderByPoint(viewType);
            if (order != 0) {
                int finalOrder = order;
                grid.setItems(data.stream().sorted((o1, o2) -> finalOrder * o1.getLastUpdate().compareTo(o2.getLastUpdate())));
            } else {
                grid.setItems(data);
            }
        });

        if (gridConfig.containsKey(GRID_DATE_UPD)) {
            grid.addColumn(card -> dateFormat.format(card.getLastUpdate()))
                    .setHeader(myHeader)
//                    .setHeader(gridConfig.get(GRID_DATE_UPD))
//                    .setSortable(true)
//                    .setComparator(Comparator.comparingLong(value -> value.getLastUpdate().getTime()))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_DATE_UPD);
        }
        if (gridConfig.containsKey(GRID_DATE_GAME)) {
            grid.addColumn(card -> dateFormat.format(card.getLastGame()))
                    .setHeader(gridConfig.get(GRID_DATE_GAME)).setSortable(true)
                    .setComparator(Comparator.comparingLong(value -> value.getLastGame().getTime()))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_DATE_GAME);
        }
        if (gridConfig.containsKey(GRID_TYPE)) {
            grid.addColumn(card -> card.getCardType().getTitle())
                    .setAutoWidth(true).setFlexGrow(0)
                    .setHeader(gridConfig.get(GRID_TYPE))
                    .setSortable(true)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_TYPE);
        }
        if (gridConfig.containsKey(GRID_TAGS)) {
            grid.addColumn(card -> card.getTagList() == null || card.getTagList().isEmpty()
                            ? "---"
                            : card.getTagList().stream()
                            .map(CardTypeTag::getTitle)
                            .collect(Collectors.joining(", "))
                    )
                    .setHeader(gridConfig.get(GRID_TAGS))
                    .setAutoWidth(true)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_TAGS);
        }
        if (gridConfig.containsKey(GRID_LINK)) {
            grid.addComponentColumn(this::getLinkIcon)
                    .setAutoWidth(true).setFlexGrow(0)
                    .setHeader(gridConfig.get(GRID_LINK))
                    .setTextAlign(ColumnTextAlign.END)
                    .setId(GRID_LINK);
        }
        if (gridConfig.containsKey(GRID_BTNS)) {
            grid.addComponentColumn(card -> {
                        // Open Info BTN
                        Button infoBtn = new Button(new Icon(VaadinIcon.INFO_CIRCLE), clk -> openInfo(card));
                        infoBtn.addThemeVariants(LUMO_TERTIARY);
                        infoBtn.getStyle().set("color", "green").set("margin", "0px");
                        // Edit BTN
                        Button edtBtn = new Button(
                                new Icon(VaadinIcon.PENCIL),
                                clk -> new FastUpdateCardView(mainView, card).showDialog()
                        );
                        edtBtn.addThemeVariants(LUMO_TERTIARY);
                        edtBtn.getStyle().set("margin", "0px");
                        // Delete BN
                        Button dltBtn = new Button(new Icon(BAN), clk -> new DeleteDialogWidget(() -> {
                            mainView.getRepositoryService().getCardRepository().delete(card);
                            reloadData();
                            FileUtils.deleteDir(mainView.getEnv().getProperty("app.data.path") + "cards/" + card.getId());
                        }));
                        dltBtn.addThemeVariants(LUMO_TERTIARY);
                        dltBtn.getStyle().set("color", "red").set("margin", "0px");

                        return new HorizontalLayout(infoBtn, edtBtn, dltBtn);
                    })
                    .setHeader(gridConfig.get(GRID_BTNS))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.END)
                    .setId(GRID_BTNS);
        }
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
