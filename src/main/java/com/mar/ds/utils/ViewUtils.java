package com.mar.ds.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.io.Resources;
import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.HasId;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.vaadin.gatanaso.MultiselectComboBox;
import org.vaadin.olli.FileDownloadWrapper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.vaadin.flow.component.icon.VaadinIcon.CLOSE_SMALL;
import static com.vaadin.flow.component.icon.VaadinIcon.DOWNLOAD;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class ViewUtils {

    public static final int DEFAULT_DURATION_ERROR_MSG = 15_000;

    public static void showErrorMsg(String title, Throwable ex) {
        showErrorMsg(title, ex, DEFAULT_DURATION_ERROR_MSG);
    }

    public static void showErrorMsg(String title, Throwable ex, int duration) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setDuration(duration);
        notification.setPosition(Notification.Position.TOP_END);

        VerticalLayout layout = new VerticalLayout();
        Accordion accordion = new Accordion();
        accordion.add(title + ": ", new Label(ExceptionUtils.getRootCauseMessage(ex)));
        accordion.close();

        Button clsBtn = new Button();
        clsBtn.setIcon(VaadinIcon.CLOSE.create());
        clsBtn.addClickListener(btnClick -> notification.close());

        layout.add(accordion, clsBtn);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, accordion, clsBtn);
        layout.getStyle().set("padding", "0px");

        notification.add(layout);
        notification.open();
    }

    @SneakyThrows
    public static Image getImage(String pathInResource) {
        File image = new File(pathInResource);
        byte[] img = FileUtils.readFileToByteArray(image);

        Image result = new Image(
                new StreamResource(
                        image.getName(),
                        () -> new ByteArrayInputStream(img)
                ),
                String.format("Not load image: %s", pathInResource)
        );

        BufferedImage myPicture = ImageIO.read(image);
        result.setWidth(myPicture.getWidth(), Unit.PIXELS);
        result.setHeight(myPicture.getHeight(), Unit.PIXELS);

        return result;
    }

    public static Image findImage(String dir, String defaultImage) throws IOException {
        File coverDir = new File(dir);

        if (coverDir.exists() && coverDir.isDirectory()) {
            Collection<File> covers = FileUtils.listFiles(coverDir, new String[]{"png", "jpg", "jpeg"}, false);
            if (covers != null && !covers.isEmpty()) {
                File image = covers.stream().findFirst().get();
                byte[] img = FileUtils.readFileToByteArray(image);
                Image result = new Image(
                        new StreamResource(
                                image.getName(),
                                () -> new ByteArrayInputStream(img)
                        ),
                        String.format("Not load image: %s", dir)
                );

                BufferedImage myPicture = ImageIO.read(image);

                result.setWidth(myPicture.getWidth(), Unit.PIXELS);
                result.setHeight(myPicture.getHeight(), Unit.PIXELS);

                return result;
            }
        }
        return getImage(defaultImage);
    }

    public static Map<String, byte[]> imageCache = Collections.synchronizedMap(new HashMap<>());

    public static Image getImageByResource(String pathInResource) throws IOException {
        final byte[] finalCacheImageByte = imageCache.get(pathInResource);
        String fileName = FilenameUtils.getName(pathInResource);
        if (!isEmpty(finalCacheImageByte)) {
            return new Image(
                    new StreamResource(fileName, () -> new ByteArrayInputStream(finalCacheImageByte)),
                    String.format("Not load image: %s", pathInResource)
            );
        }

        URL imgUrl = Resources.getResource(pathInResource);
        byte[] imageByte = Resources.asByteSource(imgUrl).read();

        Image res = new Image(
                new StreamResource(fileName, () -> new ByteArrayInputStream(imageByte)),
                String.format("Not load image: %s", pathInResource)
        );
        imageCache.putIfAbsent(pathInResource, imageByte);
        return res;
    }

    public static TextField getTextField(String text, boolean enable) {
        TextField textField = new TextField();
        textField.setReadOnly(true);
        textField.setValue(text);
        return textField;
    }

    public static Button getCloseButton(Dialog closeDialog) {
        Button clsBtn = new Button(new Icon(CLOSE_SMALL));
        clsBtn.getStyle().set("color", "red");
        clsBtn.addClickListener(btnClick -> closeDialog.close());
        return clsBtn;
    }

    public static <T extends HasId> void setSelectValue(Select<T> select, T value, List<T> initDataProviderList) {
        if (isNull(select)
                || isNull(value)
                || isNull(initDataProviderList)
                || initDataProviderList.isEmpty()
        )
            return;

        T selectValue = initDataProviderList.stream()
                .filter(hasId -> hasId.getId().equals(value.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot select " + value));
        if (nonNull(selectValue)) {
            select.setItems(initDataProviderList);
            select.setValue(selectValue);
        }
    }

    public static DatePicker setValue(DatePicker datePicker, Date date) {
        if (isNull(datePicker) || isNull(date)) {
            throw new IllegalArgumentException("Cannot init datePicker: datePicker or date is null.");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        datePicker.setValue(LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        ));
        return datePicker;
    }

    public static <E extends Enum> void setSelectValue(Select<E> select, E value, E[] selectData, E defaultValue) {
        if (isNull(value) && nonNull(defaultValue)) {
            setSelectValue(select, defaultValue, selectData);
        } else {
            setSelectValue(select, value, selectData);
        }
    }

    public static <E extends Enum> void setSelectValue(Select<E> select, E value, E[] selectData) {
        if (isNull(select) || isNull(value) || isEmpty(selectData)) {
            return;
        }

        E selectValue = Arrays.stream(selectData)
                .filter(e -> value.equals(e))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot select " + value));
        select.setItems(selectData);
        select.setValue(selectValue);
    }

    public static float getFloatValue(BigDecimalField field) {
        if (field == null || field.getValue() == null) return 0;
        return field.getValue().floatValue();
    }

    public static double getDoubleValue(BigDecimalField field) {
        if (field == null || field.getValue() == null) return 0;
        return field.getValue().doubleValue();
    }


    public static long getLongValue(BigDecimalField field) {
        if (field == null || field.getValue() == null) return 0;
        return field.getValue().longValue();
    }

    public static void setBigDecimalFieldValue(BigDecimalField field, Float value) {
        field.setValue(value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value));
    }

    public static void setBigDecimalFieldValue(BigDecimalField field, Double value) {
        field.setValue(value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value));
    }

    public static void setBigDecimalFieldValue(BigDecimalField field, Long value) {
        field.setValue(value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value));
    }

    public static void setCheckbox(Checkbox checkbox, Boolean flag) {
        checkbox.setValue(flag == Boolean.TRUE);
    }

    public static String getTextFieldValue(TextField field) {
        if (field == null || isBlank(field.getValue())) return null;
        return field.getValue().trim();
    }

    public static void setTextFieldValue(TextField field, String text) {
        field.setValue(text == null ? "" : text);
    }

    public static void setTextFieldValue(TextArea field, String text) {
        field.setValue(text == null ? "" : text);
    }

    public static String getTextFieldValue(TextArea field) {
        if (field == null || isBlank(field.getValue())) return null;
        return field.getValue().trim();
    }

    public static Date getValue(DatePicker date, Date defaultDate) {
        if (date == null || date.getValue() == null) {
            return defaultDate;
        }
        return Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static <T> T getValue(Select<T> selector, T defaultValue) {
        if (selector == null) {
            return defaultValue;
        }
        return Optional.ofNullable(selector.getValue()).orElse(defaultValue);
    }

    /**
     * @param textArea
     * @param countWorldInLine
     * @return has error
     */
    public static boolean checkString(TextArea textArea, int countWorldInLine) {
        String str = textArea.getValue();

        String[] arrStr = str.split("\n");
        for (int i = 0; i < arrStr.length; i++) {
            if (arrStr[i].length() > countWorldInLine) {
                textArea.setErrorMessage(format("В %d строке было превышен лимит символов (макс. %d)", i + 1, countWorldInLine));
                textArea.setInvalid(true);
                return true;
            }
        }
        textArea.setInvalid(false);
        textArea.setErrorMessage(null);
        return false;
    }

    public static VerticalLayout getAccordionContent(Component... components) {
        VerticalLayout content = new VerticalLayout(components);
        content.setPadding(false);
        content.setSpacing(false);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        return content;
    }

    public static FileDownloadWrapper getDownloadFileButton(String fileName, Object objToJson) {
        try {
            String json = new JsonMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .enable(SerializationFeature.INDENT_OUTPUT)
                    .writeValueAsString(objToJson);
            return getDownloadFileButton(fileName, new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileDownloadWrapper getDownloadFileButton(String fileName, ByteArrayInputStream fileBody) {
        Button downloadJson = new Button("Выгрузить JSON", new Icon(DOWNLOAD));
        downloadJson.setWidthFull();
        downloadJson.getStyle().set("color", "black");
        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(new StreamResource(fileName, () -> fileBody));
        buttonWrapper.wrapComponent(downloadJson);
        return buttonWrapper;
    }

    public static Icon getStatusIcon(Card card, boolean hasUpd) {
        Icon icon;

        if (card != null && card.getCardStatus() != null && isNotBlank(card.getCardStatus().getColor())) {
            if (hasUpd) {
                icon = VaadinIcon.BELL.create();
                icon.setColor("#0B6623");
            } else {
                icon = getIconByText(card.getCardStatus().getIcon(), VaadinIcon.BULLSEYE.create());
                icon.setColor(card.getCardStatus().getColor());
            }
            icon.getElement().setAttribute("title", card.getInfo());
        } else {
            icon = VaadinIcon.BULLSEYE.create();
            icon.setColor("grey");
        }

        return icon;
    }

    private static Icon getIconByText(@NotBlank String iconName, @NotNull Icon defaultIcon) {
        try {
            return VaadinIcon.valueOf(iconName.toUpperCase()).create();
        } catch (Exception ex) {
            return defaultIcon;
        }
    }

    public static <T extends HasId> MultiselectComboBox<T> setMultiSelectComboBoxValue(
            MultiselectComboBox<T> select, Collection<T> allData, Collection<T> selectedData
    ) {
        Set<T> items = allData.stream().collect(Collectors.toUnmodifiableSet());
        select.setItems(items);

        if (selectedData != null && !selectedData.isEmpty()) {
            Set<T> newSelected = new HashSet<>();

            for (T tag : selectedData) {
                T item = items.stream().filter(t -> t.getId().equals(tag.getId())).findFirst().orElse(null);
                if (item != null) {
                    newSelected.add(item);
                }
            }
            select.select(newSelected);
        }
        return select;
    }

}
