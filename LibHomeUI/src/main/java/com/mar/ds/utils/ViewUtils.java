package com.mar.ds.utils;

import com.mar.ds.db.entity.Card;
import com.mar.libhome.dto.HasId;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.vaadin.flow.component.icon.VaadinIcon.CLOSE_SMALL;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
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

    public static Image findImage(String coverPath) throws IOException {
        File coverDir = new File(coverPath);

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
                        String.format("Not load image: %s", coverPath)
                );

                BufferedImage myPicture = ImageIO.read(image);

                result.setWidth(myPicture.getWidth(), Unit.PIXELS);
                result.setHeight(myPicture.getHeight(), Unit.PIXELS);

                return result;
            }
        }
        throw new FileNotFoundException("Not find image in dir: " + coverPath);
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

    public static DatePicker getDatePicker(String title, LocalDate initDate) {
        DatePicker datePicker = new DatePicker(title, initDate);
        datePicker.setLocale(new Locale("ru", "RU"));
        datePicker.setWidthFull();
        return datePicker;
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

    public static <T extends HasId> void setSelectValue(Select<T> select, T value, List<T> initDataProviderList) {
        if (isNull(select)
                || isNull(value)
                || isNull(initDataProviderList)
                || initDataProviderList.isEmpty()
        ) {
            return;
        }

        T selectValue = initDataProviderList.stream()
                .filter(hasId -> hasId.getLongId().equals(value.getLongId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot select " + value));
        if (nonNull(selectValue)) {
            select.setItems(initDataProviderList);
            select.setValue(selectValue);
        }
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
        if (field == null || field.getValue() == null) {
            return 0;
        }
        return field.getValue().floatValue();
    }

    public static double getDoubleValue(BigDecimalField field) {
        if (field == null || field.getValue() == null) {
            return 0;
        }
        return field.getValue().doubleValue();
    }

    public static long getLongValueGet(BigDecimalField field, Long defValue) {
        return getLongValue(field).orElse(defValue);
    }

    public static Optional<Long> getLongValue(BigDecimalField field) {
        if (field == null || field.getValue() == null) {
            return Optional.empty();
        }
        return Optional.of(field.getValue().longValue());
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

    public static void setTextFieldValue(TextField field, String text) {
        field.setValue(text == null ? "" : text);
    }

    public static void setTextFieldValue(TextArea field, String text) {
        field.setValue(text == null ? "" : text);
    }

    public static String getTextFieldValue(TextField field) {
        if (field == null || isBlank(field.getValue())) {
            return null;
        }
        return field.getValue().trim();
    }

    public static String getTextFieldValue(TextArea field) {
        if (field == null || isBlank(field.getValue())) {
            return null;
        }
        return field.getValue().trim();
    }

    public static Date getValue(DatePicker date, Date defaultDate) {
        if (date == null || date.getValue() == null) {
            return defaultDate;
        }
        LocalDate ld = date.getValue();
        Date d = new Date(
                ld.getYear() - 1900,
                ld.getMonthValue() - 1,
                ld.getDayOfMonth(),
                0, 0, 0
        );
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static <T> T getValue(Select<T> selector, T defaultValue) {
        if (selector == null) {
            return defaultValue;
        }
        return Optional.ofNullable(selector.getValue()).orElse(defaultValue);
    }

    /**
     * Проверка введенного текста.
     *
     * @param textArea         поле для ввода.
     * @param countWorldInLine длинна строки.
     * @return флаг о наличии ошибки.
     */
    public static boolean checkString(TextArea textArea, int countWorldInLine) {
        String str = textArea.getValue();

        String[] arrStr = str.split("\n");
        for (int i = 0; i < arrStr.length; i++) {
            if (arrStr[i].length() > countWorldInLine) {
                textArea.setErrorMessage(
                        format("В %d строке было превышен лимит символов (макс. %d)", i + 1, countWorldInLine)
                );
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

    public static Icon getStatusIcon(Card card) {
        Icon icon;

        if (card != null && card.getCardStatus() != null && isNotBlank(card.getCardStatus().getColor())) {
            icon = getIconByText(card.getCardStatus().getIcon(), VaadinIcon.BULLSEYE.create());
            icon.setColor(card.getCardStatus().getColor());
            icon.getElement().setAttribute("title", card.getInfo());
        } else {
            icon = VaadinIcon.BULLSEYE.create();
            icon.setColor("grey");
        }
        icon.getStyle().set("margin", "0px");
        return icon;
    }

    /**
     * Получение иконки по имени.
     *
     * @param iconName    именование иконки.
     * @return иконка (<code>VaadinIcon.BULLSEYE</code>, если не нашел).
     */
    public static Icon getIconByText(@NotBlank String iconName) {
        return getIconByText(iconName, VaadinIcon.BULLSEYE.create());
    }

    /**
     * Получение иконки по имени.
     *
     * @param iconName    именование иконки.
     * @return иконка (<code>VaadinIcon.BULLSEYE</code>, если не нашел).
     */
    public static VaadinIcon getVaadinIconByText(@NotBlank String iconName) {
        try {
            return VaadinIcon.valueOf(iconName.toUpperCase());
        } catch (Exception ex) {
            log.error("Not find icon by text: {}", iconName);
            return VaadinIcon.BULLSEYE;
        }
    }

    /**
     * Получение иконки по имени.
     *
     * @param iconName    именование иконки.
     * @param defaultIcon возвращает если не нашли.
     * @return иконка.
     */
    public static Icon getIconByText(@NotBlank String iconName, @NotNull Icon defaultIcon) {
        try {
            return VaadinIcon.valueOf(iconName.toUpperCase()).create();
        } catch (Exception ex) {
            log.error("Not find icon by text: {}", iconName);
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
                T item = items.stream().filter(t -> t.getLongId().equals(tag.getLongId())).findFirst().orElse(null);
                if (item != null) {
                    newSelected.add(item);
                }
            }
            select.select(newSelected);
        }
        return select;
    }

}
