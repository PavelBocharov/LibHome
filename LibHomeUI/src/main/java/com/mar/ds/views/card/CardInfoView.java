package com.mar.ds.views.card;

import com.brownie.videojs.VideoJS;
import com.mar.ds.utils.UploadFileDialog;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardTypeTagDto;
import com.mar.libhome.enums.Language;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.mar.ds.data.GridInfo.GRID_DATE_GAME;
import static com.mar.ds.data.GridInfo.GRID_DATE_UPD;
import static com.mar.ds.data.GridInfo.GRID_ENGINE;
import static com.mar.ds.data.GridInfo.GRID_FILES;
import static com.mar.ds.data.GridInfo.GRID_IMAGE;
import static com.mar.ds.data.GridInfo.GRID_INFO;
import static com.mar.ds.data.GridInfo.GRID_LANGUAGE;
import static com.mar.ds.data.GridInfo.GRID_LINK;
import static com.mar.ds.data.GridInfo.GRID_STATUS;
import static com.mar.ds.data.GridInfo.GRID_TAGS;
import static com.mar.ds.data.GridInfo.GRID_TYPE;
import static com.mar.ds.data.GridInfo.GRID_VIDEO;
import static com.mar.ds.utils.ViewUtils.findImage;
import static com.mar.ds.utils.ViewUtils.getAccordionContent;
import static com.mar.ds.utils.ViewUtils.getImage;
import static java.lang.Float.parseFloat;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

/**
 * Диалоговое окно с информацией о карточке.
 */
@Slf4j
public class CardInfoView extends Dialog {

    private final MainView mainView;
    private final CardDto card;
    private final com.mar.ds.utils.FileUtils.ViewTypeDto viewType;

    /**
     * Конструктор.
     *
     * @param mainView родительское окно.
     * @param card     по какой карточке будет история.
     * @param viewType тип карточки
     */
    public CardInfoView(MainView mainView, CardDto card, com.mar.ds.utils.FileUtils.ViewTypeDto viewType) {
        this.mainView = mainView;
        this.card = card;
        this.viewType = viewType;

        try {
            this.add(loadData());
        } catch (IOException e) {
            e.printStackTrace();
            ViewUtils.showErrorMsg("Load card info ERROR", e);
        }

        this.setWidth(70, Unit.PERCENTAGE);
        this.setHeight(90, Unit.PERCENTAGE);
    }

    private VerticalLayout loadData() throws IOException {
        Calendar calendar = Calendar.getInstance();
        String dataDir = mainView.getEnv().getProperty("app.data.path");
        File fileDir = new File(dataDir + "cards/", card.getId() + "/");
        File previewDir = new File(dataDir + "cards/", card.getId() + "/preview");
        Map<String, String> titles = com.mar.ds.utils.FileUtils.getTitles(
                viewType, mainView.getContentJson()
        );
        assert Objects.nonNull(titles);

        HorizontalLayout imageAndTitle = new HorizontalLayout();
        imageAndTitle.setPadding(false);
        HorizontalLayout headerInfo;
        if (titles.containsKey(GRID_LANGUAGE)) {
            headerInfo = new HorizontalLayout(
                    ViewUtils.getStatusIcon(card),
                    ViewUtils.getImage(Optional.ofNullable(card.getLanguage()).orElse(Language.DEFAULT), 26),
                    new Label(card.getTitle())
            );
        } else {
            headerInfo = new HorizontalLayout(
                    ViewUtils.getStatusIcon(card),
                    new Label(card.getTitle())
            );
        }


        headerInfo.setWidthFull();
        Button returnBtn = new Button(
                VaadinIcon.ARROW_BACKWARD.create(),
                buttonClickEvent -> closeBtn()
        );

        Div d = new Div();
        d.setWidthFull();

        HorizontalLayout header = new HorizontalLayout(returnBtn, d, headerInfo);
        header.setWidthFull();
        VerticalLayout cardInfo = new VerticalLayout();
        cardInfo.add(getTextField("ID", String.valueOf(card.getId())));
        if (titles.containsKey(GRID_TYPE)) {
            cardInfo.add(getTextField(titles.get(GRID_TYPE), card.getCardType().getTitle()));
        }
        if (titles.containsKey(GRID_STATUS)) {
            String title = card.getOldCardStatus() == null
                    ? card.getCardStatus().getTitle()
                    : format("%s (%s)", card.getCardStatus().getTitle(), card.getOldCardStatus().getTitle());
            cardInfo.add(getTextField(titles.get(GRID_STATUS), title));
        }
        if (titles.containsKey(GRID_ENGINE)) {
            cardInfo.add(getTextField(
                    titles.get(GRID_ENGINE),
                    card.getEngine() == null ? "---" : card.getEngine().getName())
            );
        }
        if (titles.containsKey(GRID_DATE_UPD)) {
            DatePicker lastUpdDate = ViewUtils.getDatePicker(titles.get(GRID_DATE_UPD), LocalDate.now());
            lastUpdDate.setWidthFull();
            lastUpdDate.setRequired(true);
            lastUpdDate.setReadOnly(true);
            calendar.setTime(card.getLastUpdate());
            lastUpdDate.setValue(LocalDate.of(
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)
            ));
            cardInfo.add(lastUpdDate);
        }
        if (titles.containsKey(GRID_DATE_GAME)) {
            DatePicker lastGameDate = ViewUtils.getDatePicker(titles.get(GRID_DATE_GAME), LocalDate.now());
            lastGameDate.setWidthFull();
            lastGameDate.setRequired(true);
            lastGameDate.setReadOnly(true);
            calendar.setTime(card.getLastGame());
            lastGameDate.setValue(LocalDate.of(
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)
            ));
            cardInfo.add(lastGameDate);
        }
        if (titles.containsKey(GRID_LINK)) {
            Anchor link = new Anchor();
            TextField linkText;
            String title = titles.get(GRID_LINK);
            if (isBlank(card.getLink())) {
                linkText = getTextField(title, "-");
                link.setEnabled(false);
            } else {
                linkText = getTextField(title, card.getLink());
                link.setHref(card.getLink());
                link.setTarget("_blank"); // new tab
            }
            linkText.setSuffixComponent(VaadinIcon.LINK.create());
            link.add(linkText);
            link.setWidthFull();

            cardInfo.add(link);
        }
        if (titles.containsKey(GRID_TAGS)) {
            MultiselectComboBox<CardTypeTagDto> tags = new MultiselectComboBox<>();
            tags.setLabel(titles.get(GRID_TAGS));
            tags.setItemLabelGenerator(CardTypeTagDto::getTitle);
            tags.setWidthFull();
            tags.setAllowCustomValues(false);
            tags.setReadOnly(true);
            tags.setItems(card.getTagList());
            tags.select(card.getTagList());

            cardInfo.add(tags);
        }

        // Cover
        Image cover;
        try {
            cover = findImage(dataDir + "cards/" + card.getId() + "/cover/");
        } catch (FileNotFoundException ex) {
            cover = new Image("imgs/not_cover.jpeg", "Not cover");
        }
        cover.setMaxWidth(cover.getWidth());
        cover.setMaxHeight(cover.getHeight());
        cover.setSizeFull();
        cover.addClickListener(
                event -> new UploadFileDialog(
                        mainView,
                        dataDir + "cards/" + card.getId() + "/cover/",
                        card,
                        true,
                        1,
                        Set.of(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE),
                        this::reloadData
                )
        );
        Button updMainImage = new Button("New main image", VaadinIcon.UPLOAD_ALT.create());
        updMainImage.addClickListener(event -> new UploadFileDialog(
                        mainView,
                        dataDir + "cards/" + card.getId() + "/cover/",
                        card,
                        true,
                        1,
                        Set.of(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE),
                        this::reloadData
                )
        );
        updMainImage.setWidthFull();

        VerticalLayout imageInfo = new VerticalLayout(cover, updMainImage);
        imageInfo.setSizeFull();
        imageInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        // BUILD
        imageAndTitle.setSizeFull();
        imageAndTitle.setAlignItems(FlexComponent.Alignment.CENTER);
        imageAndTitle.add(imageInfo, cardInfo);

        TextArea textArea = new TextArea();
        textArea.setWidthFull();
        textArea.setReadOnly(true);
        textArea.setLabel(titles.get(GRID_INFO));
        textArea.setValue(card.getInfo());

        Div div = new Div();

        if (fileDir.exists()) {

            Accordion accordion = new Accordion();
            accordion.setWidthFull();

            Collection<File> imgFiles = FileUtils.listFiles(fileDir, new String[]{"png", "jpg", "jpeg"}, false);
            if (imgFiles != null && !imgFiles.isEmpty()) {
                List<File> sortedImages = imgFiles.stream().sorted(Comparator.comparing(File::getName)).toList();
                VerticalLayout images = new VerticalLayout();
                images.setId("acc_image_list");
                images.setSizeFull();
                images.setAlignItems(FlexComponent.Alignment.CENTER);

                VerticalLayout accImages = getAccordionContent(images);
                accordion.add(titles.get(GRID_IMAGE), accImages);
                accordion.addOpenedChangeListener(event -> {
                    if (event.getOpenedIndex().isPresent()) {
                        if (event.getOpenedPanel().get().getContent().findFirst().get().equals(accImages)) {
                            images.removeAll();
                            for (File file : sortedImages) {
                                Image accImage = getImage(file.getAbsolutePath());
                                accImage.setMaxWidth(
                                        parseFloat(accImage.getWidth().replace("px", ""))
                                                / parseFloat(accImage.getHeight().replace("px", ""))
                                                * 600,
                                        Unit.PIXELS
                                );
                                accImage.setMaxHeight(600, Unit.PIXELS);
                                accImage.setSizeFull();
                                images.add(accImage);
                            }
                        }
                    }
                });
            }

            Collection<File> videoFiles = FileUtils.listFiles(fileDir, new String[]{"mov", "mp4"}, false);
            if (videoFiles != null && !videoFiles.isEmpty()) {
                List<File> sortedVideo = videoFiles.stream().sorted(Comparator.comparing(File::getName)).toList();
                VerticalLayout videos = new VerticalLayout();
                videos.setId("acc_videos_list");
                videos.setSizeFull();
                videos.setAlignItems(FlexComponent.Alignment.CENTER);

                VerticalLayout accVideos = getAccordionContent(videos);
                accordion.add(titles.get(GRID_VIDEO), accVideos);
                accordion.addOpenedChangeListener(event -> {
                    if (event.getOpenedIndex().isPresent()) {
                        if (event.getOpenedPanel().get().getContent().findFirst().get().equals(accVideos)) {
                            videos.removeAll();
                            for (File file : sortedVideo) {
                                File previewFile = null;

                                if (previewDir.exists() && previewDir.isDirectory()) {
                                    String fileName = FilenameUtils.getBaseName(file.getName());
                                    previewFile = FileUtils
                                            .listFiles(previewDir, new String[]{"jpg", "png"}, false)
                                            .stream()
                                            .filter(f ->
                                                    f.getName().contains(fileName + ".jpg")
                                                            || f.getName().contains(fileName + ".png")
                                            )
                                            .findFirst().orElse(null);
                                }

                                VideoJS video = new VideoJS(UI.getCurrent().getSession(), file, previewFile);
                                video.setMaxWidth(80, Unit.PERCENTAGE);
                                video.setMaxHeight(600, Unit.PIXELS);
                                video.setSizeFull();
                                videos.add(video);
                            }
                        }
                    }
                });
            }

            Grid<File> cardFiles = new Grid<>();
            cardFiles.addComponentColumn(this::openFile).setHeader("File path")
                    .setAutoWidth(true).setSortable(true).setComparator(File::getAbsolutePath);
            cardFiles.addColumn(file -> FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(file)))
                    .setHeader("Size").setAutoWidth(true).setFlexGrow(0)
                    .setSortable(true).setComparator(FileUtils::sizeOf);
            cardFiles.addComponentColumn(this::getDeleteFileButton).setHeader("Delete").setFlexGrow(0);
            cardFiles.setItems(FileUtils.listFiles(fileDir, null, true).stream().sorted(Comparator.comparing(File::getAbsolutePath)));
            cardFiles.setWidthFull();
            cardFiles.addThemeVariants(GridVariant.LUMO_COMPACT);
            accordion.add(titles.get(GRID_FILES), getAccordionContent(cardFiles));
            accordion.close();

            div.add(imageAndTitle, textArea, accordion);
        } else {
            div.add(imageAndTitle, textArea);
        }

        Scroller data = new Scroller(div);
        data.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        data.getStyle()
                .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
                .set("padding", "var(--lumo-space-m)");
        data.setSizeFull();

        // Buttons
        Button backBtn = new Button("Back", VaadinIcon.ARROW_BACKWARD.create());
        backBtn.addClickListener(buttonClickEvent -> closeBtn());
        backBtn.setWidthFull();

        Button addFiles = new Button("Add files", VaadinIcon.UPLOAD.create());
        addFiles.addClickListener(event -> new UploadFileDialog(
                        mainView,
                        dataDir + "cards/" + card.getId() + "/",
                        card,
                        false,
                        10,
                        Set.of(),
                        this::reloadData
                )
        );
        addFiles.setWidthFull();

        Button updBtn = new Button("Update", VaadinIcon.PENCIL.create());
        updBtn.addClickListener(buttonClickEvent ->
                new UpdateCardView(mainView, card, viewType, () -> {
                    this.reloadData();
                    mainView.getActiveView().reloadData();
                }).showDialog()
        );
        updBtn.setWidthFull();

        HorizontalLayout footer = new HorizontalLayout(
                backBtn,
                addFiles,
                updBtn
        );
        footer.setWidthFull();

        // create view
        VerticalLayout verticalLayout = new VerticalLayout(
                header,
                data,
                footer
        );
        verticalLayout.setSizeFull();
//        verticalLayout.setWidth(70, Unit.PERCENTAGE);
        verticalLayout.getStyle().set("padding", "0px");
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, header);
//        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, data);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, footer);

        return verticalLayout;
    }

    private Anchor openFile(File file) {
        StreamResource streamResource = new StreamResource(file.getName(), () -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        Anchor link = new Anchor(streamResource, file.getAbsolutePath());
        link.getElement().setAttribute("download", true);

        return link;
    }

    @SneakyThrows
    private void reloadData() {
        this.removeAll();
        this.add(loadData());
    }

    private Button getDeleteFileButton(File file) {
        Icon icon = VaadinIcon.CLOSE_CIRCLE.create();
        icon.setColor("red");

        Button btn = new Button(icon);
        btn.addClickListener(event -> {
            try {
                FileUtils.delete(file);
                reloadData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return btn;
    }

    private TextField getTextField(String label, String text) {
        TextField textField = new TextField();
        textField.setReadOnly(true);
        textField.setLabel(label);
        textField.setValue(text);
        textField.setWidthFull();
        return textField;
    }

    private void closeBtn() {
        mainView.getActiveView().reloadData();
        this.close();
    }

}
