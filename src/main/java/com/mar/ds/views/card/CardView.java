package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.db.entity.GameEngine;
import com.mar.ds.db.entity.Language;
import com.mar.ds.db.entity.ViewType;
import com.mar.ds.utils.DeleteDialogWidget;
import com.mar.ds.utils.FileUtils;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
import com.mar.ds.views._build.pagination_grid.PaginationGridService;
import com.mar.ds.views.card.status.CardStatusViewDialog;
import com.mar.ds.views.card.tags.CardTagsView;
import com.mar.ds.views.card.type.CardTypeViewDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.icon.VaadinIcon.BAN;
import static com.vaadin.flow.component.icon.VaadinIcon.COG;
import static com.vaadin.flow.component.icon.VaadinIcon.COGS;
import static com.vaadin.flow.component.icon.VaadinIcon.COMMENT_O;
import static com.vaadin.flow.component.icon.VaadinIcon.COMPILE;
import static com.vaadin.flow.component.icon.VaadinIcon.CUBES;
import static com.vaadin.flow.component.icon.VaadinIcon.DATE_INPUT;
import static com.vaadin.flow.component.icon.VaadinIcon.MEDAL;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;
import static com.vaadin.flow.component.icon.VaadinIcon.TEXT_LABEL;
import static java.lang.Math.abs;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
public class CardView implements ContentView {

    public static final int DEFAULT_GRID_ICON_SIZE_INT = 36;
    public static final String DEFAULT_GRID_ICON_SIZE_VAR = "var(--iron-icon-width, " + DEFAULT_GRID_ICON_SIZE_INT + "px)";

    private final MainView mainView;
    @Getter
    private final ViewType viewType;

    private int minPoint;
    private int maxPoint;
    private int minRate = Integer.MAX_VALUE;
    private int maxRate = Integer.MIN_VALUE;
    private Grid<Card> grid;
    private PaginationGridService<Card> paginationGridService;
    private int pageSize;

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
        pageSize = mainView.getEnv().getProperty("app.grid.row.count", Integer.class, 15);

        // TABLE
        grid = new Grid<>();
        TextField searchField = new TextField();
        paginationGridService = new PaginationGridService<Card>(grid, pageSize,
                data -> {
                    String searchText = ViewUtils.getTextFieldValue(searchField);
                    Sort sort = Sort.by(data.sortOrders());
                    PageRequest pageRequest = PageRequest.of(data.page(), data.pageSize(), sort);

                    if (isBlank(searchText)) {
                        return mainView.getRepositoryService().getCardRepository()
                                .findAllByView(viewType, pageRequest);
                    } else {
                        return mainView.getRepositoryService().getCardRepository()
                                .findAllByViewAndLikeTitle(viewType, searchText, pageRequest);
                    }
                }
        );
        // listeners
        initGridListeners();
        // column
        initGridColumn();
        // settings
        grid.setWidthFull();

        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setClearButtonVisible(true);
        searchField.addValueChangeListener(e -> paginationGridService.reloadData(1));

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

        VerticalLayout verticalLayout = new VerticalLayout(header, grid, paginationGridService.getPaginationButtons());
        verticalLayout.setSizeFull();
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, header);

        return verticalLayout;
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
        paginationGridService.reloadData();
//        if (!isBlank(searchText)) {
//            setItemsByTextSearch(cards, searchText);
//        } else {
//            grid.setItems(cards);
//        }
    }

    private void initGridListeners() {
        grid.addItemDoubleClickListener(
                dialogItemDoubleClickEvent -> {
                    openInfo(dialogItemDoubleClickEvent.getItem());
                }
        );
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
                    // TODO Status sort ??? - сортировка опирается на наличие обновлений - если есть то они в топе.
                    .setSortable(true)
                    // ~ -> last symbol in ASCII table (nope, 'DEL' is last).
                    .setComparator(Comparator.comparing(o -> mathUpd(o) ? "~" : o.getCardStatus().getTitle()))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_STATUS);
        }
        if (gridConfig.containsKey(GRID_ENGINE)) {
            grid.addComponentColumn(this::getEngineIcon)
                    .setHeader(paginationGridService.getHeader(COGS, gridConfig.get(GRID_ENGINE), "engine"))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_ENGINE);
        }
        if (gridConfig.containsKey(GRID_LANGUAGE)) {
            grid.addComponentColumn(card -> Optional
                            .ofNullable(card.getLanguage())
                            .orElse(Language.DEFAULT)
                            .getImage(DEFAULT_GRID_ICON_SIZE_INT)
                    )
                    .setHeader(paginationGridService.getHeader(
                            COMMENT_O, gridConfig.get(GRID_LANGUAGE), "language"
                    ))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_LANGUAGE);
        }
        if (gridConfig.containsKey(GRID_TITLE)) {
            grid.addColumn(Card::getTitle)
                    .setHeader(paginationGridService.getHeader(
                            TEXT_LABEL, gridConfig.get(GRID_TITLE), "title"
                    ))
                    .setAutoWidth(true)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_TITLE);
        }
        if (gridConfig.containsKey(GRID_POINT)) {
            grid.addComponentColumn(card -> getLabelWithColor(card::getPoint))
                    .setHeader(paginationGridService.getHeader(MEDAL, gridConfig.get(GRID_POINT), "point"))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_POINT);
        }
        if (gridConfig.containsKey(GRID_RATE)) {
            grid.addComponentColumn(card -> getLabelWithColor(() -> calcRate(card), minRate, maxRate))
                    .setHeader(gridConfig.get(GRID_RATE))
                    .setAutoWidth(true)
                    .setFlexGrow(0)
                    // TODO Calc and sort by rate in DB
                    .setSortable(true)
                    .setComparator(this::calcRate)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_RATE);
        }

        if (gridConfig.containsKey(GRID_DATE_UPD)) {
            grid.addColumn(card -> dateFormat.format(card.getLastUpdate()))
                    .setHeader(paginationGridService.getHeader(DATE_INPUT, "UPD date", "lastUpdate"))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_DATE_UPD);
        }
        if (gridConfig.containsKey(GRID_DATE_GAME)) {
            grid.addColumn(card -> dateFormat.format(card.getLastGame()))
                    .setHeader(paginationGridService.getHeader(
                            DATE_INPUT, gridConfig.get(GRID_DATE_GAME), "lastGame"
                    ))
                    .setAutoWidth(true).setFlexGrow(0)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setId(GRID_DATE_GAME);
        }
        if (gridConfig.containsKey(GRID_TYPE)) {
            grid.addColumn(card -> card.getCardType().getTitle())
                    .setAutoWidth(true).setFlexGrow(0)
                    .setHeader(paginationGridService.getHeader(
                            COMPILE, gridConfig.get(GRID_TYPE), "cardType.title"
                    ))
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
