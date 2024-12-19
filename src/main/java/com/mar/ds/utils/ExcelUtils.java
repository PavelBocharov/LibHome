package com.mar.ds.utils;

import lombok.experimental.UtilityClass;
import org.apache.poi.xssf.usermodel.*;

import java.awt.Color;

@UtilityClass
public class ExcelUtils {

    public static XSSFFont headerFont(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        font.setFontName("Arial Black");
        return font;
    }

    public static XSSFFont baseFont(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Courier New");
        return font;
    }

    public static XSSFCellStyle headerStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(headerFont(workbook));
        return style;
    }

    public static XSSFCellStyle baseStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(baseFont(workbook));
        return style;
    }

    public static XSSFCellStyle titleStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = baseFont(workbook);
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    public static XSSFCellStyle statusStyle(XSSFWorkbook workbook, String color) {
        Color clr = getColor(color, Color.BLACK);
        XSSFColor c = new XSSFColor(clr, new DefaultIndexedColorMap());

        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = baseFont(workbook);
        font.setColor(c);
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    public static XSSFCellStyle dateStyle(XSSFWorkbook workbook) {
        XSSFCreationHelper helper = workbook.getCreationHelper();
        short format = helper.createDataFormat().getFormat("yyyy-MM-dd");
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(format);
        style.setFont(baseFont(workbook));
        return style;
    }

    public static Color getColor(String color, Color defaultColor) {
        try {
            return Color.decode(color);
        } catch (Exception ex) {
            return defaultColor;
        }
    }
}
