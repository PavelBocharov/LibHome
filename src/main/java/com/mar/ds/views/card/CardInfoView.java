package com.mar.ds.views.card;

import com.brownie.videojs.VideoJS;
import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.db.entity.Language;
import com.mar.ds.utils.UploadFileDialog;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
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
import org.vaadin.gatanaso.MultiselectComboBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.mar.ds.data.GridInfo.GRID_DATE_GAME;
import static com.mar.ds.data.GridInfo.GRID_DATE_UPD;
import static com.mar.ds.data.GridInfo.GRID_ENGINE;
import static com.mar.ds.data.GridInfo.GRID_FILES;
import static com.mar.ds.data.GridInfo.GRID_IMAGE;
import static com.mar.ds.data.GridInfo.GRID_INFO;
import static com.mar.ds.data.GridInfo.GRID_LINK;
import static com.mar.ds.data.GridInfo.GRID_STATUS;
import static com.mar.ds.data.GridInfo.GRID_TAGS;
import static com.mar.ds.data.GridInfo.GRID_TYPE;
import static com.mar.ds.data.GridInfo.GRID_VIDEO;
import static com.mar.ds.utils.ViewUtils.findImage;
import static com.mar.ds.utils.ViewUtils.getAccordionContent;
import static com.mar.ds.utils.ViewUtils.getImage;
import static java.lang.Float.parseFloat;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@Slf4j
public class CardInfoView extends Dialog {

    private final MainView appLayout;
    private final Card card;

    public CardInfoView(MainView appLayout, Card card) {
        this.appLayout = appLayout;
        this.card = card;

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
        String dataDir = appLayout.getEnv().getProperty("app.data.path");
        File fileDir = new File(dataDir + "cards/", +card.getId() + "/");
        Map<String, String> titles = com.mar.ds.utils.FileUtils.getTitles(
                card.getViewType(), appLayout.getContentJSON()
        );

        HorizontalLayout imageAndTitle = new HorizontalLayout();
        imageAndTitle.setPadding(false);

        HorizontalLayout headerInfo = new HorizontalLayout(
                ViewUtils.getStatusIcon(card, false),
                Optional.ofNullable(card.getLanguage()).orElse(Language.DEFAULT).getImage(26),
                new Label( " [" + card.getId() + "] " + card.getTitle())
        );
        headerInfo.setWidthFull();
//        headerInfo.setAlignItems(FlexComponent.Alignment.END);
        Button returnBtn = new Button(
                VaadinIcon.ARROW_BACKWARD.create(),
                buttonClickEvent -> closeBtn()
        );

        Div d = new Div();
        d.setWidthFull();

        HorizontalLayout header = new HorizontalLayout(returnBtn, d, headerInfo);
        header.setWidthFull();
        VerticalLayout cardInfo = new VerticalLayout();

        if (titles.containsKey(GRID_TYPE)) {
            cardInfo.add(getTextField(titles.get(GRID_TYPE), card.getCardType().getTitle()));
        }
        if (titles.containsKey(GRID_STATUS)) {
            cardInfo.add(getTextField(titles.get(GRID_STATUS), card.getCardStatus().getTitle()));
        }
        if (titles.containsKey(GRID_ENGINE)) {
            cardInfo.add(getTextField(
                    titles.get(GRID_ENGINE),
                    card.getEngine() == null ? "---" : card.getEngine().getName())
            );
        }
        if (titles.containsKey(GRID_DATE_UPD)) {
            DatePicker lastUpdDate = new DatePicker(titles.get(GRID_DATE_UPD), LocalDate.now());
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
            DatePicker lastGameDate = new DatePicker(titles.get(GRID_DATE_GAME), LocalDate.now());
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
            MultiselectComboBox<CardTypeTag> tags = new MultiselectComboBox<>();
            tags.setLabel(titles.get(GRID_TAGS));
            tags.setItemLabelGenerator(CardTypeTag::getTitle);
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
            cover = findImage(dataDir + "cards/" + card.getId() + "/cover/", "imgs/not_cover.jpeg");
        } catch (FileNotFoundException ex) {
            cover = new Image("imgs/not_cover.jpeg", "Not cover");
        }
        cover.setMaxWidth(cover.getWidth());
        cover.setMaxHeight(cover.getHeight());
        cover.setSizeFull();
        cover.addClickListener(
                event -> new UploadFileDialog(
                        appLayout,
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
                        appLayout,
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
                            for (File file : imgFiles) {
                                Image accImage = getImage(file.getAbsolutePath());
                                accImage.setMaxWidth(
                                        parseFloat(accImage.getWidth().replace("px", ""))
                                                / parseFloat(accImage.getHeight().replace("px", ""))
                                                * 600
                                        ,
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
                            for (File file : videoFiles) {
                                VideoJS video = new VideoJS(UI.getCurrent().getSession(), file, null);
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
                    .setAutoWidth(true).setSortable(true).setComparator(file -> file.getName());
            cardFiles.addColumn(file -> FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(file)))
                    .setHeader("Size").setAutoWidth(true).setFlexGrow(0)
                    .setSortable(true).setComparator(file -> FileUtils.sizeOf(file));
            cardFiles.addComponentColumn(this::getDeleteFileButton).setHeader("Delete").setFlexGrow(0);
            cardFiles.setItems(FileUtils.listFiles(fileDir, null, true));
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
                        appLayout,
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
        updBtn.addClickListener(buttonClickEvent -> new UpdateCardView(appLayout, card, () -> {
                    this.reloadData();
                    appLayout.reloadContent();
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
//        appLayout.setContent(appLayout.getCardView().getContent());
        appLayout.reloadContent();
        this.close();
    }

}
