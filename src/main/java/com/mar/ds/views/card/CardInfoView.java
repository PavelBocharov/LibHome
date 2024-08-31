package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.utils.PropertiesLoader;
import com.mar.ds.utils.UploadFileDialog;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.mar.ds.utils.ViewUtils.findImage;
import static com.mar.ds.utils.ViewUtils.getAccordionContent;
import static com.mar.ds.utils.ViewUtils.getImage;
import static com.mar.ds.utils.ViewUtils.getImageByResource;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@RequiredArgsConstructor
public class CardInfoView implements ContentView {

    private final MainView appLayout;
    private final Card card;

    @SneakyThrows
    public VerticalLayout getContent() {
        String dataDir = PropertiesLoader.loadProperties("application.properties").getProperty("data.path");
        File fileDir = new File(dataDir + "cards/", +card.getId() + "/");

        HorizontalLayout imageAndTitle = new HorizontalLayout();
        imageAndTitle.setPadding(false);

        Label title = new Label(card.getTitle());
        HorizontalLayout headerInfo = new HorizontalLayout(
                ViewUtils.getStatusIcon(card, false),
                title
        );
        headerInfo.setWidthFull();

        Anchor link = new Anchor();
        TextField linkText;
        if (isBlank(card.getLink())) {
            linkText = getTextField("Link", "-");
            link.setEnabled(false);
        } else {
            linkText = getTextField("Link", card.getLink());
            link.setHref(card.getLink());
            link.setTarget("_blank"); // new tab
        }
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
                headerInfo,
                getTextField("Type", card.getCardType().getTitle()),
                getTextField("Status", card.getCardStatus().getTitle()),
                link,
                tags
        );
        // Cover
        Image cover;
        try {
            cover = findImage(dataDir + "cards/" + card.getId() + "/cover/", "static/img/not_cover.jpeg");
            cover.setSizeFull();
        } catch (FileNotFoundException ex) {
            cover = getImageByResource("static/img/not_cover.jpeg");
            cover.setSizeFull();
        }
        Button updMainImage = new Button("New main image", VaadinIcon.UPLOAD_ALT.create());
        updMainImage.addClickListener(event -> new UploadFileDialog(
                        dataDir + "cards/" + card.getId() + "/cover/",
                        true,
                        1,
                        () -> appLayout.setContent(this.getContent())
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
        textArea.setSizeFull();
        textArea.setReadOnly(true);
        textArea.setLabel("Info");
        textArea.setValue(card.getInfo());

        VerticalLayout data = new VerticalLayout();
        data.setWidth(70, Unit.PERCENTAGE);

        if (fileDir.exists()) {

            Grid<File> cardFiles = new Grid<>();
            cardFiles.addComponentColumn(this::openFile).setHeader("File path").setAutoWidth(true);
            cardFiles.addComponentColumn(this::getDeleteFileButton).setHeader("Delete").setWidth("32px");
            cardFiles.setItems(FileUtils.listFiles(fileDir, null, true));
            cardFiles.setWidthFull();
            cardFiles.addThemeVariants(GridVariant.LUMO_COMPACT);

            Accordion accordion = new Accordion();
            accordion.setWidthFull();
            VerticalLayout images = new VerticalLayout();
            images.setSizeFull();
            for (File file : FileUtils.listFiles(fileDir, new String[]{"png", "jpg", "jpeg"}, true)) {
                Image accImage = getImage(file.getAbsolutePath());
                accImage.setSizeFull();
                images.add(accImage);
            }
            accordion.add("Images", getAccordionContent(images));
            accordion.add("Files", getAccordionContent(cardFiles));
            accordion.close();

            data.add(imageAndTitle, textArea, accordion);
        } else {
            data.add(imageAndTitle, textArea);
        }

        // Buttons
        Button backBtn = new Button("Back", VaadinIcon.ARROW_BACKWARD.create());
        backBtn.addClickListener(buttonClickEvent -> appLayout.setContent(appLayout.getCardView().getContent()));
        backBtn.setWidthFull();

        Button addFiles = new Button("Add files", VaadinIcon.UPLOAD.create());
        addFiles.addClickListener(event -> new UploadFileDialog(
                        dataDir + "cards/" + card.getId() + "/",
                        false,
                        10,
                        () -> appLayout.setContent(this.getContent())
                )
        );
        addFiles.setWidthFull();

        Button updBtn = new Button("Update", VaadinIcon.PENCIL.create());
        updBtn.addClickListener(buttonClickEvent -> new UpdateCardView(appLayout, card));
        updBtn.setWidthFull();

        HorizontalLayout footer = new HorizontalLayout(
                backBtn,
                addFiles,
                updBtn
        );
        footer.setWidthFull();

        // create view
        VerticalLayout verticalLayout = new VerticalLayout(
                data,
                footer
        );
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
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

    private Button getDeleteFileButton(File file) {
        Icon icon = VaadinIcon.CLOSE_CIRCLE.create();
        icon.setColor("red");

        Button btn = new Button(icon);
        btn.addClickListener(event -> {
            try {
                FileUtils.delete(file);
                appLayout.setContent(this.getContent());
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

}
