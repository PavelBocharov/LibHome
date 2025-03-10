package com.mar.ds.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mar.ds.db.entity.Card;
import com.mar.ds.db.entity.CardStatus;
import com.mar.ds.db.entity.CardTypeTag;
import com.mar.ds.db.entity.Language;
import com.mar.ds.db.entity.ViewType;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.server.StreamResource;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.vaadin.flow.component.icon.VaadinIcon.DOWNLOAD;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@UtilityClass
public class FileUtils {

    private static volatile EnumMap<ViewType, Map<String, String>> viewInfos;

    @SneakyThrows
    public static void deleteDir(String pathDir) {
        File dir = new File(pathDir);
        if (dir.exists() && dir.isDirectory()) {
            org.apache.commons.io.FileUtils.deleteDirectory(dir);
        }
    }

    public static @Nullable Map<String, String> getTitles(
            @NotNull ViewType viewType,
            @NotBlank @NotNull String filePath
    ) {
        if (viewType == null || isBlank(filePath)) {
            throw new RuntimeException("Cannot load titles: viewType is null or filePath is blank.");
        }

        if (viewInfos == null) {
            synchronized (FileUtils.class) {
                if (viewInfos == null) {
                    viewInfos = FileUtils.loadContentInfo(filePath);
                }
            }
        }

        return viewInfos.get(viewType);
    }

    public static EnumMap<ViewType, Map<String, String>> loadContentInfo(@NotBlank @NotNull String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        EnumMap<ViewType, Map<String, String>> res = new EnumMap<>(ViewType.class);
        try {
            Map<String, Map<String, String>> json = mapper.readValue(new File(filePath), Map.class);
            for (String key : json.keySet()) {
                try {
                    ViewType type = ViewType.valueOf(key);
                    res.put(type, json.get(key));
                } catch (Exception ignored) {
                    log.warn("Ignore JSON key: {}. Exception: {}", key, ExceptionUtils.getMessage(ignored));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    public static FileDownloadWrapper getDownloadFileButton(String fileName, Supplier<List<Card>> cardSupplier) {
        Button downloadJson = new Button("Export to Excel", new Icon(DOWNLOAD));
        downloadJson.setWidthFull();
        downloadJson.getStyle().set("color", "black");
        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(new StreamResource(fileName, () -> {
            log.debug("Export data to file '{}' START...", fileName);
            ByteArrayInputStream stream = createExcel(cardSupplier.get());
            log.debug("Export data to file '{}' END.", fileName);
            return stream;
        }));
        buttonWrapper.wrapComponent(downloadJson);
        return buttonWrapper;
    }

    @SneakyThrows
    public static ByteArrayInputStream createExcel(List<Card> cardList) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Employee Data");

        Object[] header = new Object[]{
                "Status", "Engine", "Language", "Title", "Point", "URL link", "Last update", "Type", "Tags"
        };
        Map<String, Object[]> data = new TreeMap<>();
        int i = 1;
        data.put(String.valueOf(i++), header);
        for (Card card : cardList) {
            data.put(String.valueOf(i++), convertToArray(card));
        }

        XSSFCellStyle headerStyle = ExcelUtils.headerStyle(workbook);
        XSSFCellStyle baseStyle = ExcelUtils.baseStyle(workbook);
        XSSFCellStyle dateStyle = ExcelUtils.dateStyle(workbook);
        XSSFCellStyle titleStyle = ExcelUtils.titleStyle(workbook);
        XSSFCellStyle linkStyle = ExcelUtils.linkStyle(workbook);

        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset) {
            Row row = sheet.createRow(rownum++);

            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (rownum == 1) {
                    cell.setCellStyle(headerStyle);
                } else {
                    if (cellnum == 4) {
                        cell.setCellStyle(titleStyle);
                    } else {
                        cell.setCellStyle(baseStyle);
                    }
                }
                if (obj instanceof CardStatus) {
                    CardStatus status = (CardStatus) obj;
                    cell.setCellValue(status.getTitle());
                    XSSFCellStyle statusStyle = ExcelUtils.statusStyle(workbook, status.getColor());
                    cell.setCellStyle(statusStyle);
                } else if (obj instanceof Double) {
                    cell.setCellValue((Double) obj);
                } else if (obj instanceof Date) {
                    cell.setCellValue((Date) obj);
                    cell.setCellStyle(dateStyle);
                } else {
                    String val = obj == null ? "" : String.valueOf(obj);
                    if (cellnum == 6) {
                        try {
                            if (isNotBlank(val)) {
                                cell.setCellValue("Link");
                                XSSFHyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
                                link.setAddress(val);
                                cell.setHyperlink(link);
                                cell.setCellStyle(linkStyle);
                            } else {
                                cell.setCellValue(val);
                            }
                        } catch (Exception ex) {
                            cell.setCellValue(val);
                        }
                    } else {
                        cell.setCellValue(val);
                    }
                }
            }
        }

        for (int j = 0; j < header.length; j++) {
            sheet.autoSizeColumn(j, true);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);

        return new ByteArrayInputStream(bos.toByteArray());
    }

    private static Object[] convertToArray(Card card) {
        Object[] rez = new Object[9];

        rez[0] = card.getCardStatus();
        rez[1] = card.getEngine().getName();
        rez[2] = Optional.ofNullable(card.getLanguage()).orElse(Language.DEFAULT).getTitle();
        rez[3] = card.getTitle();
        rez[4] = card.getPoint();
        rez[5] = card.getLink();
        rez[6] = card.getLastUpdate();
        rez[7] = card.getCardType().getTitle();
        rez[8] = card.getTagList().stream().map(CardTypeTag::getTitle).collect(Collectors.joining(", "));

        return rez;
    }

}
