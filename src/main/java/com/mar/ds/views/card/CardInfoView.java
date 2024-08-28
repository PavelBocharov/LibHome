package com.mar.ds.views.card;

import com.mar.ds.db.entity.Card;
import com.mar.ds.utils.PropertiesLoader;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.ContentView;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileNotFoundException;

import static com.mar.ds.utils.ViewUtils.*;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@RequiredArgsConstructor
public class CardInfoView implements ContentView {

    @Value("${data.path:./}")
    private String dataPath;

    private final MainView appLayout;
    private final Card card;

    @SneakyThrows
    public VerticalLayout getContent() {
        log.debug("Init dataPath in CardInfoView: {}", dataPath);
        log.debug("Show card info: {}: ", card);
        String dataDir = PropertiesLoader.loadProperties("application.properties").getProperty("data.path");
        HorizontalLayout imageAndTitle = new HorizontalLayout();

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

        Image cover;
        try {
            cover = getImage(dataDir + "cards/" + card.getId() + "/cover/main.jpg");
            cover.setSizeFull();
        } catch (FileNotFoundException ex) {
            cover = getImageByResource("static/img/not_cover.jpeg");
            cover.setSizeFull();
        }
        Button updMainImage = new Button("New main image", VaadinIcon.UPLOAD_ALT.create());
        // TODO load file view to "base.dir/cards/id/cover/main.jpg"
        updMainImage.setWidthFull();

        VerticalLayout imageInfo = new VerticalLayout(cover, updMainImage);
        imageInfo.setSizeFull();
        imageInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout cardInfo = new VerticalLayout(
                headerInfo,
                getTextField("Type", card.getCardType().getTitle()),
                getTextField("Status", card.getCardStatus().getTitle()),
                link
        );

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

        File fileDir = new File(dataDir + "cards/", + card.getId() + "/");
        if (fileDir.exists()) {
            Grid<File> cardFiles = new Grid<>();
            cardFiles.addColumn(File::getAbsolutePath).setHeader("File path").setAutoWidth(true);
            cardFiles.addComponentColumn(file -> new Button(VaadinIcon.DOWNLOAD.create())).setHeader("Download");
            cardFiles.addComponentColumn(file -> new Button(VaadinIcon.CLOSE_CIRCLE.create())).setHeader("Delete");
            cardFiles.setItems(FileUtils.listFiles(fileDir, null, true));
            cardFiles.setWidthFull();
            cardFiles.addThemeVariants(GridVariant.LUMO_COMPACT);

            Accordion accordion = new Accordion();
            accordion.setWidthFull();
            VerticalLayout images = new VerticalLayout();
            images.setSizeFull();
            for (File file : FileUtils.listFiles(fileDir, new String[]{"png", "jpg", "jpeg"}, false)) {
                Image accImage = getImage(file.getAbsolutePath());
                accImage.setSizeFull();
                images.add(accImage);
            }
            accordion.add("Images", getAccordionContent(images));
            accordion.close();

            data.add(imageAndTitle, textArea, accordion, cardFiles);
        } else {
            data.add(imageAndTitle, textArea);
        }

        // Buttons
        Button backBtn = new Button("Back", VaadinIcon.ARROW_BACKWARD.create());
        backBtn.addClickListener(buttonClickEvent -> appLayout.setContent(appLayout.getCardView().getContent()));
        backBtn.setWidthFull();

        Button addFiles = new Button("Add files", VaadinIcon.UPLOAD.create());
        // TODO Load files to base.dir/cards/id/ - create load view
//        backBtn.addClickListener(buttonClickEvent -> appLayout.setContent(appLayout.getCardView().getContent()));
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

    private TextField getTextField(String label, String text) {
        TextField textField = new TextField();
        textField.setReadOnly(true);
        textField.setLabel(label);
        textField.setValue(text);
        textField.setWidthFull();
        return textField;
    }

    private Icon getStatusIcon(Card card, boolean hasUpd) {
        Icon icon = VaadinIcon.BULLSEYE.create();

        if (card != null && card.getCardStatus() != null && isNotBlank(card.getCardStatus().getColor())) {
            if (hasUpd) {
                icon = VaadinIcon.EXCLAMATION_CIRCLE.create();
                icon.setColor("#0B6623");
            } else {
                icon.setColor(card.getCardStatus().getColor());
            }
            icon.getElement().setAttribute("title", card.getInfo());
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
