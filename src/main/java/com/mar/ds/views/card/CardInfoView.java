package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.utils.UploadFileDialog;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
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

import static com.mar.ds.utils.ViewUtils.findImage;
import static com.mar.ds.utils.ViewUtils.getAccordionContent;
import static com.mar.ds.utils.ViewUtils.getImage;
import static com.mar.ds.utils.ViewUtils.getImageByResource;
import static org.apache.commons.lang3.StringUtils.isBlank;

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

        String dataDir = appLayout.getEnv().getProperty("data.path");
        File fileDir = new File(dataDir + "cards/", +card.getId() + "/");

        HorizontalLayout imageAndTitle = new HorizontalLayout();
        imageAndTitle.setPadding(false);

        HorizontalLayout headerInfo = new HorizontalLayout(
                ViewUtils.getStatusIcon(card, false),
                new Label("[" + card.getId() + "] " + card.getTitle())
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

        // last update
        DatePicker lastUpdDate = new DatePicker("Last update", LocalDate.now());
        lastUpdDate.setWidthFull();
        lastUpdDate.setRequired(true);
        lastUpdDate.setReadOnly(true);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(card.getLastUpdate());
        lastUpdDate.setValue(LocalDate.of(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)
        ));
        // last game
        DatePicker lastGameDate = new DatePicker("Last game", LocalDate.now());
        lastGameDate.setWidthFull();
        lastGameDate.setRequired(true);
        lastGameDate.setReadOnly(true);
        calendar.setTime(card.getLastGame());
        lastGameDate.setValue(LocalDate.of(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)
        ));

        Anchor link = new Anchor();
        TextField linkText;
        if (isBlank(card.getLink())) {
            linkText = getTextField("\uD83D\uDD17 Link", "-");
            link.setEnabled(false);
        } else {
            linkText = getTextField("\uD83D\uDD17 Link", card.getLink());
            link.setHref(card.getLink());
            link.setTarget("_blank"); // new tab
        }
        linkText.setSuffixComponent(VaadinIcon.LINK.create());
        link.add(linkText);
        link.setWidthFull();

        MultiselectComboBox<CardTypeTag> tags = new MultiselectComboBox<>();
        tags.setLabel("Tags");
        tags.setItemLabelGenerator(CardTypeTag::getTitle);
        tags.setWidthFull();
        tags.setAllowCustomValues(false);
        tags.setReadOnly(true);
        tags.setItems(card.getTagList());
        tags.select(card.getTagList());

        VerticalLayout cardInfo = new VerticalLayout(
//                headerInfo,
                getTextField("Type", card.getCardType().getTitle()),
                getTextField("Status", card.getCardStatus().getTitle()),
                lastUpdDate,
                lastGameDate,
                link,
                tags
        );
        // Cover
        Image cover;
        try {
            cover = findImage(dataDir + "cards/" + card.getId() + "/cover/", "static/img/not_cover.jpeg");
        } catch (FileNotFoundException ex) {
            cover = getImageByResource("static/img/not_cover.jpeg");
        }
        cover.setMaxWidth(cover.getWidth());
        cover.setMaxHeight(cover.getHeight());
        cover.setSizeFull();
        cover.addClickListener(
                event -> new UploadFileDialog(
                        appLayout,
                        dataDir + "cards/" + card.getId() + "/cover/",
                        true,
                        1,
                        this::reloadData
                )
        );
        Button updMainImage = new Button("New main image", VaadinIcon.UPLOAD_ALT.create());
        updMainImage.addClickListener(event -> new UploadFileDialog(
                        appLayout,
                        dataDir + "cards/" + card.getId() + "/cover/",
                        true,
                        1,
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
        textArea.setLabel("Info");
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
                accordion.add("Images", accImages);
                accordion.addOpenedChangeListener(event -> {
                    if (event.getOpenedIndex().isPresent()) {
                        if (event.getOpenedPanel().get().getContent().findFirst().get().equals(accImages)) {
                            images.removeAll();
                            for (File file : imgFiles) {
                                Image accImage = getImage(file.getAbsolutePath());
                                accImage.setMaxWidth(accImage.getWidth());
                                accImage.setMaxHeight(accImage.getHeight());
                                accImage.setSizeFull();
                                images.add(accImage);
                            }
                        }
                    }
                });
            }

            Grid<File> cardFiles = new Grid<>();
            cardFiles.addComponentColumn(this::openFile).setHeader("File path").setAutoWidth(true);
            cardFiles.addComponentColumn(this::getDeleteFileButton).setHeader("Delete").setTextAlign(ColumnTextAlign.END);
            cardFiles.setItems(FileUtils.listFiles(fileDir, null, true));
            cardFiles.setWidthFull();
            cardFiles.addThemeVariants(GridVariant.LUMO_COMPACT);
            accordion.add("Files", getAccordionContent(cardFiles));
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
//        data.setHeightFull();
//        data.setWidth(70, Unit.PERCENTAGE);
        data.setSizeFull();

        // Buttons
        Button backBtn = new Button("Back", VaadinIcon.ARROW_BACKWARD.create());
//        backBtn.addClickListener(buttonClickEvent -> appLayout.setContent(appLayout.getCardView().getContent()));
        backBtn.addClickListener(buttonClickEvent -> closeBtn());
        backBtn.setWidthFull();

        Button addFiles = new Button("Add files", VaadinIcon.UPLOAD.create());
        addFiles.addClickListener(event -> new UploadFileDialog(
                        appLayout,
                        dataDir + "cards/" + card.getId() + "/",
                        false,
                        10,
                        this::reloadData
                )
        );
        addFiles.setWidthFull();

        Button updBtn = new Button("Update", VaadinIcon.PENCIL.create());
        updBtn.addClickListener(buttonClickEvent -> new UpdateCardView(appLayout, card, () -> {
            this.reloadData();
            appLayout.setContent(appLayout.getCardView().getContent());
        }));
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
        appLayout.setContent(appLayout.getCardView().getContent());
        this.close();
    }

}
