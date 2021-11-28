package com.smart.common.util;

import com.smart.common.constant.ExcelDataType;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.Color;
import java.io.*;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.List;
import java.util.*;

/**
 * 导出Excel文件（导出“XLSX”格式，支持大数据量导出 @see org.apache.poi.ss.SpreadsheetVersion）
 */
public class ExcelExport {

    private static Logger log = LoggerFactory.getLogger(ExcelExport.class);

    /**
     * 单元格靠左对齐
     */
    public static final Integer CELL_ALIGN_LEFT = 1;
    /**
     * 单元格居中对齐
     */
    public static final Integer CELL_ALIGN_CENTER = 2;
    /**
     * 单元格靠右对齐
     */
    public static final Integer CELL_ALIGN_RIGHT = 3;

    /**
     * 工作薄对象
     */
    private SXSSFWorkbook wb;

    /**
     * 工作表对象
     */
    private Sheet sheet;

    /**
     * 样式列表
     */
    private Map<String, CellStyle> styles;

    /**
     * 当前行号
     */
    private int rownum;

    /**
     * 导出模板工作薄对象
     */
    private Workbook workbook;

    /**
     * 构造函数
     *
     *            导出文件模板，读取第一个工作表
     * @param headerNum
     *            标题行号，数据行号=标题行号+1
     * @throws InvalidFormatException
     * @throws IOException
     */
    public ExcelExport(String fileName, int headerNum) throws InvalidFormatException, IOException {
        this(new File(fileName), headerNum);
    }

    /**
     * 构造函数
     *
     *            导出文件模板对象，读取第一个工作表
     * @param headerNum
     *            标题行号，数据行号=标题行号+1
     * @throws InvalidFormatException
     * @throws IOException
     */
    public ExcelExport(File file, int headerNum) throws InvalidFormatException, IOException {
        this(file, headerNum, 0);
    }

    /**
     * 构造函数
     *
     *            导出文件模板
     * @param headerNum
     *            标题行号，数据行号=标题行号+1
     * @param sheetIndex
     *            工作表编号
     * @throws InvalidFormatException
     * @throws IOException
     */
    public ExcelExport(String fileName, int headerNum, int sheetIndex) throws InvalidFormatException, IOException {
        this(new File(fileName), headerNum, sheetIndex);
    }

    /**
     * 构造函数
     *
     *            导出文件模板对象
     * @param headerNum
     *            标题行号，数据行号=标题行号+1
     * @param sheetIndex
     *            工作表编号
     * @throws InvalidFormatException
     * @throws IOException
     */
    public ExcelExport(File file, int headerNum, int sheetIndex) throws InvalidFormatException, IOException {
        this(file.getName(), new FileInputStream(file), headerNum, sheetIndex);
    }

    /**
     * 构造函数
     *
     *            导出文件模板对象
     * @param headerNum
     *            标题行号，数据行号=标题行号+1
     * @param sheetIndex
     *            工作表编号
     * @throws IOException
     */
    public ExcelExport(String fileName, InputStream is, int headerNum, int sheetIndex)
            throws IOException {
        if (StringUtils.isBlank(fileName)) {
            throw new RuntimeException("导入文档为空!");
        } else if (fileName.toLowerCase().endsWith("xls")) {
            this.workbook = new HSSFWorkbook(is);
        } else if (fileName.toLowerCase().endsWith("xlsx")) {
            this.workbook = new XSSFWorkbook(is);
        } else if (fileName.toLowerCase().endsWith("xlsm")) {
            this.workbook = new XSSFWorkbook(is);
        } else {
            throw new RuntimeException("文档格式不正确!");
        }
        if (this.workbook.getNumberOfSheets() < sheetIndex) {
            throw new RuntimeException("文档中没有工作表!");
        }
        this.sheet = this.workbook.getSheetAt(sheetIndex);
        if (this.sheet.getLastRowNum() < 0) {
            throw new RuntimeException("文档模板错误!");
        }
        this.rownum = headerNum + 1;
        this.styles = createStyles(workbook);
        log.debug("Initialize success.");
    }

    /**
     * 构造函数 -- SXSSFWorkbook
     * @param fileName
     * @param headerNum
     * @param sheetIndex
     * @param batchType
     * @throws IOException
     */
    public ExcelExport(String fileName, int headerNum, int sheetIndex, String batchType)
            throws IOException {
        if (StringUtils.isBlank(fileName)) {
            throw new RuntimeException("导出文档为空!");
        } else {
            InputStream is = new FileInputStream(new File(fileName));
            XSSFWorkbook workb = new XSSFWorkbook(is);
            this.wb = new SXSSFWorkbook(workb, 1000);
            if (this.wb.getNumberOfSheets() < sheetIndex) {
                throw new RuntimeException("文档中没有工作表!");
            }
            this.sheet = this.wb.getSheetAt(sheetIndex);
            this.rownum = headerNum + 1;
            this.styles = createStyles(wb);
            log.debug("Initialize success.");
        }
    }

    /**
     * 构造函数
     *
     * @param title
     *            表格标题，传“空值”，表示无标题
     * @param headerMap
     *            表头数组
     */
    public ExcelExport(String title, Map<String, String> headerMap) {
        List<String> headerList = new ArrayList<String>();
        headerList.add("序号");
        for (String key : headerMap.keySet()) {
            headerList.add(headerMap.get(key));
        }
        initialize(title, headerList);
    }

    /**
     * 构造函数
     *
     * @param title
     *            表格标题，传“空值”，表示无标题
     * @param headerList
     *            表头列表
     */
    public ExcelExport(String title, List<String> headerList) {
        // 自动增加序号列
        headerList.add(0, "序号");
        initialize(title, headerList);
    }

    /**
     * 初始化函数
     *
     * @param title
     *            表格标题，传“空值”，表示无标题
     * @param headerList
     *            表头列表
     */
    private void initialize(String title, List<String> headerList) {
        this.wb = new SXSSFWorkbook(500);
        this.sheet = wb.createSheet("Export");
        this.styles = createStyles(wb);
        // Create title
        if (StringUtils.isNotBlank(title)) {
            Row titleRow = sheet.createRow(rownum++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(styles.get("title"));
            titleCell.setCellValue(title);
            sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), titleRow.getRowNum(),
                    headerList.size() - 1));
        }
        // Create header
        if (headerList == null) {
            throw new RuntimeException("headerList not null!");
        }
        Row headerRow = sheet.createRow(rownum++);
        headerRow.setHeightInPoints(16);
        for (int i = 0; i < headerList.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(styles.get("header"));
            String[] ss = StringUtils.split(headerList.get(i), "**", 2);
            if (ss.length == 2) {
                cell.setCellValue(ss[0]);
                Comment comment = this.sheet.createDrawingPatriarch()
                        .createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
                comment.setString(new XSSFRichTextString(ss[1]));
                cell.setCellComment(comment);
            } else {
                cell.setCellValue(headerList.get(i));
            }
            sheet.autoSizeColumn(i);
        }
        for (int i = 0; i < headerList.size(); i++) {
            int colWidth = sheet.getColumnWidth(i) * 2;
            sheet.setColumnWidth(i, colWidth < 3000 ? 3000 : colWidth);
        }
        log.debug("Initialize success.");
    }

    /**
     * 创建表格样式
     *
     * @param wb
     *            工作薄对象
     * @return 样式列表
     */
    private Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>(6);

        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = wb.createFont();
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        style.setFont(titleFont);
        styles.put("title", style);

        style = wb.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        styles.put("data", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.LEFT);
        styles.put("data1", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.CENTER);
        styles.put("data2", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        styles.put("data3", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        // style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = wb.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);

        return styles;
    }

    /**
     * 添加一行
     *
     * @return 行对象
     */
    public Row addRow() {
        return sheet.createRow(rownum++);
    }

    /**
     * 添加一个单元格
     *
     * @param row
     *            添加的行
     * @param column
     *            添加列号
     * @param val
     *            添加值
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val) {
        return this.addCell(row, column, val, 0, Class.class);
    }

    /**
     * 添加一个单元格
     *
     * @param row
     *            添加的行
     * @param column
     *            添加列号
     * @param val
     *            添加值
     * @param align
     *            对齐方式（1：靠左；2：居中；3：靠右）
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val, int align) {
        return this.addCell(row, column, val, 0, Class.class);
    }

    /**
     * 添加一个单元格
     *
     * @param row
     *            添加的行
     * @param column
     *            添加列号
     * @param val
     *            添加值
     * @param align
     *            对齐方式（1：靠左；2：居中；3：靠右）
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val, int align, Class<?> fieldType) {
        Cell cell = row.createCell(column);
        CellStyle style = styles.get("data" + (align >= 1 && align <= 3 ? align : ""));
        try {
            if (val == null) {
                cell.setCellValue("");
            } else if (val instanceof String) {
                cell.setCellValue((String) val);
            } else if (val instanceof Integer) {
                cell.setCellValue((String) val);
            } else if (val instanceof Short) {
                cell.setCellValue((Short) val);
            } else if (val instanceof Long) {
                cell.setCellValue((Long) val);
            } else if (val instanceof Double) {
                cell.setCellValue((Double) val);
            } else if (val instanceof Float) {
                cell.setCellValue((Float) val);
            } else if (val instanceof Timestamp) {
                DataFormat format = null;
                if (wb != null) {
                    format = wb.createDataFormat();
                } else {
                    format = workbook.createDataFormat();
                }
                style.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm:ss"));
                cell.setCellValue((Date) val);
            } else if (val instanceof Date) {
                DataFormat format = null;
                if (wb != null) {
                    format = wb.createDataFormat();
                } else {
                    format = workbook.createDataFormat();
                }
                style.setDataFormat(format.getFormat("yyyy-MM-dd"));
                cell.setCellValue((Date) val);
            } else {
                if (fieldType != Class.class) {
                    cell.setCellValue((String) fieldType.getMethod("setValue", Object.class).invoke(null, val));
                } else {
                    cell.setCellValue((String) Class
                            .forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(),
                                    "fieldtype." + val.getClass().getSimpleName() + "Type"))
                            .getMethod("setValue", Object.class).invoke(null, val));
                }
            }
        } catch (Exception ex) {
            log.debug("Set cell value [" + row.getRowNum() + "," + column + "] error: " + ex.toString());
            cell.setCellValue(val.toString());
        }
        cell.setCellStyle(style);
        return cell;
    }

    /**
     * 获取行对象
     *
     * @param rownum
     * @return
     */
    public Row getRow(int rownum) {
        return this.sheet.getRow(rownum);
    }

    /**
     * 获取最后一个数据行号
     *
     * @return
     */
    public int getLastDataRowNum() {
        return this.sheet.getLastRowNum() + 1;
    }

    /**
     * 设置单元格的值
     *
     * @param row
     *            设置的行
     * @param column
     *            设置的列号
     * @param val
     *            设置的值
     *            对齐方式（1：靠左；2：居中；3：靠右）
     * @return
     */
    public void setCellValue(Row row, int column, Object val) {
        Cell cell = row.getCell(column);
        try {
            if (val == null) {
                cell.setCellValue("");
            } else if (val instanceof String) {
                cell.setCellValue((String) val);
            } else if (val instanceof Integer) {
                cell.setCellValue((Integer) val);
            } else if (val instanceof Long) {
                cell.setCellValue((Long) val);
            } else if (val instanceof Double) {
                cell.setCellValue((Double) val);
            } else if (val instanceof Float) {
                cell.setCellValue((Float) val);
            } else if (val instanceof Date) {
                cell.setCellValue((Date) val);
            } else {
                cell.setCellValue((String) Class
                        .forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(),
                                "fieldtype." + val.getClass().getSimpleName() + "Type"))
                        .getMethod("setValue", Object.class).invoke(null, val));
            }
        } catch (Exception ex) {
            log.debug("Set cell value [" + row.getRowNum() + "," + column + "] error: " + ex.toString());
            cell.setCellValue(val.toString());
        }
    }

    /**
     *
     * 设置公式
     *
     * @param row
     *            设置的行
     * @param column
     *            设置的列号
     * @param val
     *            设置的值
     */
    public void setCellFormula(Row row, int column, Object val) {
        Cell cell = row.getCell(column);
        if (null == cell) {
            cell = row.createCell(column);
        }
        try {
            if (val == null) {
                cell.setCellValue("");
            } else if (val instanceof String) {
                cell.setCellFormula((String) val);
            }
        } catch (Exception ex) {
            log.debug("Set cell formula [" + row.getRowNum() + "," + column + "] error: " + ex.toString());
            cell.setCellValue(val.toString());
        }
    }

    /**
     *
     * 合并单元格
     *
     * @param startX
     *            起始行数
     * @param endX
     *            结束行数
     * @param startY
     *            起始列数
     * @param endY
     *            结束列数
     * @param value
     *            void 合并后单元格塞值
     * @param flag
     *
     */
    public void mergeRegion(int startX, int endX, int startY, int endY, String value, boolean flag) {
        CellRangeAddress cra = new CellRangeAddress(startX, endX, startY, endY);
        // 在sheet里增加合并单元格
        sheet.addMergedRegion(cra);
        // 塞值
        Row row = sheet.getRow(startX);
        if (null == row) {
            row = sheet.createRow(startX);
        }
        // 设置背景色 边框 字号
        Cell cell = row.createCell(startY);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 14);
        if (flag) {
            style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            font.setFontHeightInPoints((short) 10);
        }
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);

        style.setFont(font);
        cell.setCellStyle(style);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, cra, sheet);
        setCellValue(row, startY, value);
    }

    /**
     *
     * 合并单元格并赋值
     *
     * @param startX
     *            起始行数
     * @param endX
     *            结束行数
     * @param startY
     *            起始列数
     * @param endY
     *            结束列数
     * @param value
     *            void 合并后单元格塞值
     */
    public void mergeRegion(int startX, int endX, int startY, int endY, String value) {
        CellRangeAddress cra = new CellRangeAddress(startX, endX, startY, endY);
        // 在sheet里增加合并单元格
        sheet.addMergedRegion(cra);
        // 塞值
        Row row = sheet.getRow(startX);
        if (null == row) {
            row = sheet.createRow(startX);
        }
        // 设置格式
        Cell cell = row.createCell(startY);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(style);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, cra, sheet);
        setCellValue(row, startY, value);
    }

    /**
     * 设置内容为强制换行
     *
     */
    public void setWrapText(int rowNum, int columnNum, String excelType) {
        if ("xls".equals(excelType)) {
            HSSFCellStyle cellStyle = (HSSFCellStyle) workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 10);
            cellStyle.setFont(font);
            cellStyle.setWrapText(true);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            Cell cell = getRow(rowNum).getCell(columnNum);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(new HSSFRichTextString(cell.getStringCellValue()));
        } else {
            XSSFCellStyle cellStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 10);
            cellStyle.setFont(font);
            cellStyle.setWrapText(true);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            Cell cell = getRow(rowNum).getCell(columnNum);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(new XSSFRichTextString(cell.getStringCellValue()));
        }

    }

    /**
     * 设置单元格是否锁定
     *
     */
    public void setCellLocked(int rowNum, int columnNum, boolean locked) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        Font dataFont = workbook.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        cellStyle.setFont(dataFont);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 设置为文本格式
        DataFormat format = workbook.createDataFormat();
        cellStyle.setDataFormat(format.getFormat("@"));
        cellStyle.setLocked(locked);
        Cell cell = getRow(rowNum).getCell(columnNum);
        cell.setCellStyle(cellStyle);
    }

    /**
     * 添加数据List<E>
     *
     * @param list
     *            数据列表
     * @param headerMap
     *            列表头LinkedHashMap key实体类字段 value表头列
     * @param alignMap
     *            对齐方式Map key实体类字段 value 对齐方式（1：靠左；2：居中；3：靠右）
     *
     */
    public <E> ExcelExport setDataList(List<E> list, Map<String, String> headerMap, Map<String, Integer> alignMap) {
        int num = 0;
        for (E e : list) {
            int colunm = 0;
            Row row = addRow();
            num++;
            addCell(row, colunm++, String.valueOf(num), 2, Class.class);
            StringBuilder sb = new StringBuilder();
            for (String col : headerMap.keySet()) {
                Object val;
                int align = 0;
                try {
                    val = Reflections.invokeGetter(e, col);
                } catch (Exception ex) {
                    log.debug(ex.toString());
                    val = "";
                }
                if ((alignMap != null) && (alignMap.size() > 0)) {
                    for (String alignCol : alignMap.keySet()) {
                        if (col.equals(alignCol)) {
                            align = alignMap.get(alignCol).intValue();
                        }
                    }
                }
                addCell(row, colunm++, val, align, Class.class);
                sb.append(new StringBuilder().append(val).append(", ").toString());
            }
            log.debug(new StringBuilder().append("Write success: [").append(row.getRowNum()).append("] ")
                    .append(sb.toString()).toString());
        }

        return this;
    }

    /**
     * 添加数据List<E>(可设置单元格格式)
     *
     * @param list
     *            数据列表
     *            列表头LinkedHashMap key实体类字段 value表头列
     *            对齐方式Map key实体类字段 value 对齐方式（1：靠左；2：居中；3：靠右）
     * @param typeMap
     *            单元格格式
     */
    public <E> ExcelExport setDataList(List<Map<String, Object>> list, Map<String, Integer> dataMap, boolean hasRowNum,
                                       String str, Map<String, Integer> typeMap) {
        int num = 1;
        for (Map m : list) {
            if ((m != null) && (m.size() > 0)) {
                int colunm = 0;
                Row row = addRow();
                row.setHeightInPoints(25);
                if (hasRowNum) {
                    addCell(row, colunm++, Integer.valueOf(num++), 2, Class.class);
                }
                StringBuilder sb = new StringBuilder();
                for (String col : dataMap.keySet()) {
                    Object val = null;
                    int align = 0;
                    try {
                        val = m.get(col);
                    } catch (Exception ex) {
                        log.debug(ex.toString());
                        if ("null".equals(str)) {
                            val = null;
                        } else {
                            val = "";
                        }
                    }
                    if (dataMap.get(col) != null) {
                        align = dataMap.get(col).intValue();
                    }
                    Cell cell;
                    if (null != val) {
                        cell = addCell(row, colunm++, val, align, Class.class);
                    } else {
                        cell = row.createCell(colunm++);
                        CellStyle style = styles.get("data" + (align >= 1 && align <= 3 ? align : ""));
                        cell.setCellStyle(style);
                    }
                    if (typeMap != null && !typeMap.isEmpty()) {
                        if (typeMap.containsKey(col)) {
                            CellStyle cellStyle = workbook.createCellStyle();
                            CellStyle style = styles.get("data" + (align >= 1 && align <= 3 ? align : ""));
                            cellStyle.cloneStyleFrom(style);
                            // 设置单元格格式为"文本"
                            DataFormat format = workbook.createDataFormat();
                            cellStyle.setDataFormat(format.getFormat("@"));
                            cell.setCellStyle(cellStyle);
                        }
                    }
                    sb.append(new StringBuilder().append(val).append(", ").toString());
                }
                log.debug(new StringBuilder().append("Write success: [").append(row.getRowNum()).append("] ")
                        .append(sb.toString()).toString());
            }
        }
        return this;

    }

    /**
     * 添加数据List<E>(可设置单元格格式)
     *
     * @param list
     *            数据列表
     *            列表头LinkedHashMap key实体类字段 value表头列
     *            对齐方式Map key实体类字段 value 对齐方式（1：靠左；2：居中；3：靠右）
     * @param typeMap
     *            单元格格式
     */
    public <E> ExcelExport setDataList(List<Map<String, Object>> list, Map<String, String> headerMap,
                                       Map<String, Integer> alignMap, boolean hasRowNum, Map<String, String> typeMap) {
        int num = 1;
        for (Map m : list) {
            if ((m != null) && (m.size() > 0)) {
                int colunm = 0;
                Row row = addRow();
                if (hasRowNum) {
                    addCell(row, colunm++, String.valueOf(num), 2, Class.class);
                }
                StringBuilder sb = new StringBuilder();
                for (String col : headerMap.keySet()) {
                    Object val;
                    int align = 0;
                    try {
                        val = m.get(col);
                    } catch (Exception ex) {
                        log.debug(ex.toString());
                        val = "";
                    }
                    if ((alignMap != null) && (alignMap.size() > 0)) {
                        for (String alignCol : alignMap.keySet()) {
                            if (col.equals(alignCol)) {
                                align = alignMap.get(alignCol).intValue();
                            }
                        }
                    }
                    Cell cell = addCell(row, colunm++, val, align, Class.class);
                    if (typeMap != null && !typeMap.isEmpty()) {
                        if (typeMap.containsKey(col)) {
                            CellStyle cellStyle = workbook.createCellStyle();
                            CellStyle style = styles.get("data" + (align >= 1 && align <= 3 ? align : ""));
                            cellStyle.cloneStyleFrom(style);
                            DataFormat df = workbook.createDataFormat();
                            if(ExcelDataType.MONEY.dataType.equals(typeMap.get(col))) {
                                cellStyle.setDataFormat(df.getFormat("#,##0.00"));
                                if(val != null && StringUtils.isNoneBlank(val.toString())) {
                                    cell.setCellValue(Double.parseDouble(val.toString()));
                                }
                            }
                            cell.setCellStyle(cellStyle);
                        }
                    }
                    sb.append(new StringBuilder().append(val).append(", ").toString());
                }
                log.debug(new StringBuilder().append("Write success: [").append(row.getRowNum()).append("] ")
                        .append(sb.toString()).toString());
            }
        }
        return this;

    }

    /**
     * 添加数据(List<Map<String, Object>>)
     *
     * @param list
     *            数据列表
     * @param headerMap
     *            列表头LinkedHashMap key实体类字段 value表头列
     * @param alignMap
     *            对齐方式Map key实体类字段 value 对齐方式（1：靠左；2：居中；3：靠右）
     * @param str
     *            区分标记
     *
     */
    public <E> ExcelExport setDataList(List<Map<String, Object>> list, Map<String, String> headerMap,
                                       Map<String, Integer> alignMap, String str) {
        int num = 1;
        for (Map m : list) {
            if ((m != null) && (m.size() > 0)) {
                int colunm = 0;
                Row row = addRow();
                addCell(row, colunm++, String.valueOf(num), 2, Class.class);
                StringBuilder sb = new StringBuilder();
                for (String col : headerMap.keySet()) {
                    Object val = null;
                    int align = 0;
                    try {
                        val = m.get(col);
                    } catch (Exception ex) {
                        log.debug(ex.toString());
                        val = "";
                    }
                    if ((alignMap != null) && (alignMap.size() > 0)) {
                        for (String alignCol : alignMap.keySet()) {
                            if (col.equals(alignCol)) {
                                align = alignMap.get(alignCol).intValue();
                            }
                        }
                    }
                    addCell(row, colunm++, val, align, Class.class);
                    sb.append(new StringBuilder().append(val).append(", ").toString());
                }
                log.debug(new StringBuilder().append("Write success: [").append(row.getRowNum()).append("] ")
                        .append(sb.toString()).toString());
            }
        }
        return this;
    }

    /**
     * 添加数据(List<Map<String, Object>>)
     *
     * @param list
     *            数据列表
     * @param headerMap
     *            列表头LinkedHashMap key实体类字段 value表头列
     * @param alignMap
     *            对齐方式Map key实体类字段 value 对齐方式（1：靠左；2：居中；3：靠右）
     * @param hasRowNum
     *            是否包含序号列(序号列在第一列)
     *
     */
    public <E> ExcelExport setDataList(List<Map<String, Object>> list, Map<String, String> headerMap,
                                       Map<String, Integer> alignMap, boolean hasRowNum) {
        int num = 1;
        for (Map m : list) {
            if ((m != null) && (m.size() > 0)) {
                int colnum = 0;
                Row row = addRow();
                if (hasRowNum) {
                    addCell(row, colnum++, String.valueOf(num), 2, Class.class);
                }
                StringBuilder sb = new StringBuilder();
                for (String col : headerMap.keySet()) {
                    Object val;
                    int align = 0;
                    try {
                        val = m.get(col);
                    } catch (Exception ex) {
                        log.debug(ex.toString());
                        val = "";
                    }
                    if ((alignMap != null) && (alignMap.size() > 0)) {
                        for (String alignCol : alignMap.keySet()) {
                            if (col.equals(alignCol)) {
                                align = alignMap.get(alignCol).intValue();
                            }
                        }
                    }
                    addCell(row, colnum++, val, align, Class.class);
                    sb.append(new StringBuilder().append(val).append(", ").toString());
                }
                log.debug(new StringBuilder().append("Write success: [").append(row.getRowNum()).append("] ")
                        .append(sb.toString()).toString());
            }
        }
        return this;
    }

    /**
     * 添加数据List<E>(只针对数据递增的模板文件)
     *
     * @param list
     *            数据列表
     * @param dataMap
     *            数据Map key实体类字段 value 对齐方式（1：靠左；2：居中；3：靠右）
     * @param hasRowNum
     *            是否包含序号列(序号列在第一列)
     *
     */
    public <E> ExcelExport setDataList(List<E> list, Map<String, Integer> dataMap, boolean hasRowNum) {
        int num = 0;
        for (E e : list) {
            int colnum = 0;
            Row row = addRow();
            row.setHeightInPoints(25);
            num++;
            if (hasRowNum) {
                addCell(row, colnum++, String.valueOf(num), 2, Class.class);
            }
            StringBuilder sb = new StringBuilder();
            for (String col : dataMap.keySet()) {
                Object val;
                int align = 0;
                try {
                    val = Reflections.invokeGetter(e, col);
                } catch (Exception ex) {
                    log.debug(ex.toString());
                    val = "";
                }
                if (dataMap.get(col) != null) {
                    align = dataMap.get(col).intValue();
                }
                addCell(row, colnum++, val, align, Class.class);
                sb.append(new StringBuilder().append(val).append(", ").toString());
            }
            log.debug(new StringBuilder().append("Write success: [").append(row.getRowNum()).append("] ")
                    .append(sb.toString()).toString());
        }

        return this;
    }

    /**
     * 添加数据(List<Map<String, Object>> 只针对数据递增的模板文件)
     *
     * @param list
     *            数据列表
     * @param dataMap
     *            数据Map key实体类字段 value 对齐方式（1：靠左；2：居中；3：靠右）
     * @param hasRowNum
     *            是否包含序号列(序号列在第一列)
     * @param str
     *            区分标记
     *
     */
    public <E> ExcelExport setDataList(List<Map<String, Object>> list, Map<String, Integer> dataMap, boolean hasRowNum,
                                       String str) {
        int num = 1;
        for (Map m : list) {
            if ((m != null) && (m.size() > 0)) {
                int colnum = 0;
                Row row = addRow();
                row.setHeightInPoints(25);
                if (hasRowNum) {
                    addCell(row, colnum++, Integer.valueOf(num++), 2, Class.class);
                }
                StringBuilder sb = new StringBuilder();
                for (String col : dataMap.keySet()) {
                    Object val;
                    int align = 0;
                    try {
                        val = m.get(col);
                    } catch (Exception ex) {
                        log.debug(ex.toString());
                        if ("null".equals(str)) {
                            val = null;
                        } else {
                            val = "";
                        }
                    }
                    if (dataMap.get(col) != null) {
                        align = dataMap.get(col).intValue();
                    }
                    if (null != val) {
                        addCell(row, colnum++, val, align, Class.class);
                    } else {
                        Cell cell = row.createCell(colnum++);
                        CellStyle style = styles.get("data" + (align >= 1 && align <= 3 ? align : ""));
                        cell.setCellStyle(style);
                    }
                    sb.append(new StringBuilder().append(val).append(", ").toString());
                }
                log.debug(new StringBuilder().append("Write success: [").append(row.getRowNum()).append("] ")
                        .append(sb.toString()).toString());
            }
        }
        return this;
    }

    /**
     * 创建并设置单元格样式--可加背景色--XSSF格式
     *
     * @param row
     * @param cellAlignCenter
     * @param color  void
     */
    public CellStyle handleCellStyle(Row row, int column, Integer cellAlignCenter, Color color) {
        Cell cell = null == row.getCell(column) ? row.createCell(column) : row.getCell(column);
        // 复制单元格样式
        CellStyle cellStyle = copyCellStyle(row, column, cellAlignCenter);
        // 设置背景色
        XSSFCellStyle styleTemp = ((XSSFCellStyle) cellStyle);
        if (null != color) {
            styleTemp.setFillForegroundColor(new XSSFColor(color));
        }
        styleTemp.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell.setCellStyle(styleTemp);
        return styleTemp;
    }

    /**
     * 创建并设置单元格样式--可加背景色--HSSF格式
     *
     * @param row
     * @param column
     * @param cellAlignCenter
     * @param index  颜色索引值
     */
    public CellStyle handleCellStyle(Row row, int column, Integer cellAlignCenter, Short index) {
        Cell cell = null == row.getCell(column) ? row.createCell(column) : row.getCell(column);
        // 复制单元格样式
        CellStyle cellStyle = copyCellStyle(row, column, cellAlignCenter);

        // 设置背景色
        HSSFCellStyle styleTemp = ((HSSFCellStyle) cellStyle);
        if (null != index) {
            styleTemp.setFillForegroundColor(index);
        }
        styleTemp.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell.setCellStyle(styleTemp);
        return cellStyle;
    }

    /**
     * 复制某个单元格样式
     * @param row
     *
     * @param row
     * @param align  void
     *
     */
    public CellStyle copyCellStyle(Row row, int column, Integer align) {
        CellStyle cellStyle = workbook.createCellStyle();
        CellStyle style = styles.get("data" + (align >= 1 && align <= 3 ? align : ""));
        cellStyle.cloneStyleFrom(style);
        return cellStyle;
    }

    /**
     * 输出数据流
     *
     * @param os
     *            输出数据流
     */
    public ExcelExport write(OutputStream os) throws IOException {
        wb.write(os);
        return this;
    }

    /**
     * 输出到客户端
     *
     * @param fileName
     *            输出文件名
     */
    public ExcelExport write(HttpServletResponse response, HttpServletRequest request, String fileName)
            throws IOException {
        response.reset();
        response.setCharacterEncoding("UTF-8");
        String agent = request.getHeader("User-Agent");
        boolean isMSIE = (agent != null && agent.indexOf("MSIE") != -1);
        // 处理部分IE无法识别的问题
        Boolean flag = agent.indexOf("like Gecko") > 0;
        if (isMSIE || flag) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        }
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        write(response.getOutputStream());
        return this;
    }

    /**
     * 输出到文件
     *
     *            输出文件名
     */
    public ExcelExport writeFile(String name) throws FileNotFoundException, IOException {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(name);
            this.write(os);
        } catch (Exception e) {
            if (null != os) {
                os.close();
            }
        }
        return this;
    }

    /**
     * 清理临时文件
     */
    public ExcelExport dispose() {
        wb.dispose();
        return this;
    }

    /**
     * 输出数据流（只针对于模板文件）
     *
     * @param os
     *            输出数据流
     */
    public ExcelExport writeTemplate(OutputStream os) throws IOException {
        workbook.write(os);
        os.flush();
        os.close();
        return this;
    }

    /**
     * 输出到客户端（只针对于模板文件）
     *
     * @param fileName
     *            输出文件名
     */
    public ExcelExport writeTemplate(HttpServletResponse response, HttpServletRequest request, String fileName)
            throws IOException {
        response.reset();
        response.setCharacterEncoding("UTF-8");
        String agent = request.getHeader("User-Agent");
        boolean isMSIE = (agent != null && agent.indexOf("MSIE") != -1);
        // 处理部分IE无法识别的问题
        Boolean flag = agent.indexOf("like Gecko") > 0;
        if (isMSIE || flag) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        }
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        writeTemplate(response.getOutputStream());
        return this;
    }

    /**
     * 输出到文件（只针对于模板文件）
     *
     *  输出文件名
     */
    public ExcelExport writeTemplateFile(String name) throws IOException {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(name);
            this.writeTemplate(os);
        } catch (Exception e) {
            if (null != os) {
                os.close();
            }
        }
        return this;
    }

    /**
     * 获取sheet
     *
     * @param headerNum
     * @param sheetIndex
     * @throws InvalidFormatException
     * @throws IOException  void
     *
     */
    public void getSheetAt(int headerNum, int sheetIndex)  {
        if (this.workbook.getNumberOfSheets() < sheetIndex) {
            throw new RuntimeException("文档中没有工作表!");
        }
        this.sheet = this.workbook.getSheetAt(sheetIndex);
        if (this.sheet.getLastRowNum() <= 0) {
            throw new RuntimeException("文档模板错误!");
        }
        this.rownum = headerNum + 1;
        this.styles = createStyles(workbook);
        log.debug("Initialize success.");
    }

    public Workbook getWorkbook() {
        return this.workbook;
    }

    public Sheet getSheet() {
        return this.sheet;
    }

}
