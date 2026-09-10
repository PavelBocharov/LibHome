package com.mar.ds.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mar.libhome.dto.CardDto;
import com.mar.libhome.dto.CardTypeTagDto;
import com.mar.libhome.enums.Language;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.StreamResource;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.vaadin.olli.FileDownloadWrapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.icon.VaadinIcon.DOWNLOAD;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@UtilityClass
public class FileUtils {

    private static volatile Map<String, Map<String, String>> viewInfos;

    @SneakyThrows
    public static void deleteDir(String pathDir) {
        File dir = new File(pathDir);
        if (dir.exists() && dir.isDirectory()) {
            org.apache.commons.io.FileUtils.deleteDirectory(dir);
        }
    }

    public static Map<String, String> getTitles(
            @NotNull FileUtils.ViewTypeDto viewType,
            @NotBlank @NotNull String filePath
    ) {
        if (viewType == null || isBlank(filePath)) {
            throw new RuntimeException("Cannot load titles: viewType is null or filePath is blank.");
        }

        return loadContentInfo(filePath).get(viewType.key);
    }

    public static List<ViewTypeDto> getCardViewTypeList(@NotBlank @NotNull String filePath) {
        Map<String, Map<String, String>> viewTypes = loadContentInfo(filePath);
        List<ViewTypeDto> views = new ArrayList<>(viewTypes.size());
        viewTypes.forEach((key, mapType) -> {
            ViewTypeDto viewTypeDto = new ViewTypeDto(
                    Integer.parseInt(mapType.get("id")),
                    mapType.get("title"),
                    key,
                    ViewUtils.getVaadinIconByText(mapType.get("icon")),
                    Integer.parseInt(mapType.get("order"))
            );
            views.add(viewTypeDto);
        });

        return views;
    }

    public static Map<String, Map<String, String>> loadContentInfo(@NotBlank @NotNull String filePath) {
        if (viewInfos == null) {
            synchronized (FileUtils.class) {
                if (viewInfos == null) {
                    try {
                        Map<String, Map<String, String>> data = new ObjectMapper().readValue(new File(filePath), Map.class);
                        Map<String, Map<String, String>> temp = new HashMap<>(data.size());
                        for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
                            temp.put(entry.getKey(), Collections.unmodifiableMap(entry.getValue()));
                        }
                        viewInfos = Collections.unmodifiableMap(temp);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return Collections.unmodifiableMap(viewInfos);
    }

    public static FileDownloadWrapper getDownloadFileButton(String fileName, Supplier<List<CardDto>> cardSupplier) {
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
    public static ByteArrayInputStream createExcel(List<CardDto> cardList) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Employee Data");

        Object[] header = new Object[]{
                "Status", "Engine", "Language", "Title", "Point", "URL link", "Last update", "Type", "Tags"
        };
        Map<String, Object[]> data = new TreeMap<>();
        int i = 1;
        data.put(String.valueOf(i++), header);
        for (CardDto card : cardList) {
            data.put(String.valueOf(i++), convertToArray(card));
        }

        XSSFCellStyle headerStyle = ExcelUtils.headerStyle(workbook);
        XSSFCellStyle baseStyle = ExcelUtils.baseStyle(workbook);
        XSSFCellStyle dateStyle = ExcelUtils.dateStyle(workbook);
        XSSFCellStyle titleStyle = ExcelUtils.titleStyle(workbook);
        XSSFCellStyle linkStyle = ExcelUtils.linkStyle(workbook);

        AtomicInteger rownum = new AtomicInteger(0);
        data.forEach((key, objArr) -> {
            Row row = sheet.createRow(rownum.getAndIncrement());
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (rownum.get() == 1) {
                    cell.setCellStyle(headerStyle);
                } else {
                    if (cellnum == 4) {
                        cell.setCellStyle(titleStyle);
                    } else {
                        cell.setCellStyle(baseStyle);
                    }
                }
//                if (obj instanceof CardStatus) {
//                    CardStatus status = (CardStatus) obj;
//                    cell.setCellValue(status.getTitle());
//                    XSSFCellStyle statusStyle = ExcelUtils.statusStyle(workbook, status.getColor());
//                    cell.setCellStyle(statusStyle);
//                } else
                if (obj instanceof Double) {
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
        });

        for (int j = 0; j < header.length; j++) {
            sheet.autoSizeColumn(j, true);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);

        return new ByteArrayInputStream(bos.toByteArray());
    }

    private static Object[] convertToArray(CardDto card) {
        Object[] rez = new Object[9];

        rez[0] = card.getCardStatus().isTech() ? card.getOldCardStatus().getTitle() : card.getCardStatus().getTitle();
        rez[1] = card.getEngine().getName();
        rez[2] = Optional.ofNullable(card.getLanguage()).orElse(Language.DEFAULT).getTitle();
        rez[3] = card.getTitle();
        rez[4] = card.getPoint();
        rez[5] = card.getLink();
        rez[6] = card.getLastUpdate();
        rez[7] = card.getCardType().getTitle();
        rez[8] = card.getTagList().stream().map(CardTypeTagDto::getTitle).collect(Collectors.joining(", "));

        return rez;
    }

    public record ViewTypeDto(Integer id, String title, String key,
                              VaadinIcon icon, Integer order
    ) implements Serializable {
    }

}
