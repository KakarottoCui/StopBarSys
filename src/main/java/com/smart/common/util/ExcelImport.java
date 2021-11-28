package com.smart.common.util;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.smart.common.constant.ExcelDataType;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 导入Excel文件（支持“XLS”和“XLSX”格式）
 */
public class ExcelImport {

    private static Logger log = LoggerFactory.getLogger(ExcelImport.class);

    /**
     * 是否将数值类型数据使用string，防止数值类型为科学计数法
     */
    private boolean double2String = true;

    /**
     * 工作薄对象
     */
    private Workbook wb;

    /**
     * 工作表对象
     */
    private Sheet sheet;

    /**
     * 标题行号
     */
    private int headerNum;

    /**
     * 构造函数
     *
     *            导入文件，读取第一个工作表
     * @param headerNum
     *            标题行号，数据行号=标题行号+1
     * @throws InvalidFormatException
     * @throws IOException
     */
    public ExcelImport(String fileName, int headerNum) throws InvalidFormatException, IOException {
        this(new File(fileName), headerNum);
    }

    /**
     * 构造函数
     *
     *            导入文件对象，读取第一个工作表
     * @param headerNum
     *            标题行号，数据行号=标题行号+1
     * @throws InvalidFormatException
     * @throws IOException
     */
    public ExcelImport(File file, int headerNum) throws InvalidFormatException, IOException {
        this(file, headerNum, 0);
    }

    /**
     * 构造函数
     *
     *            导入文件
     * @param headerNum
     *            标题行号，数据行号=标题行号+1
     * @param sheetIndex
     *            工作表编号
     * @throws InvalidFormatException
     * @throws IOException
     */
    public ExcelImport(String fileName, int headerNum, int sheetIndex) throws InvalidFormatException, IOException {
        this(new File(fileName), headerNum, sheetIndex);
    }

    /**
     * 构造函数
     *
     *            导入文件对象
     * @param headerNum
     *            标题行号，数据行号=标题行号+1
     * @param sheetIndex
     *            工作表编号
     * @throws InvalidFormatException
     * @throws IOException
     */
    public ExcelImport(File file, int headerNum, int sheetIndex) throws InvalidFormatException, IOException {
        this(file.getName(), new FileInputStream(file), headerNum, sheetIndex);
    }

    /**
     * 构造函数
     *
     *            导入文件对象
     * @param headerNum
     *            标题行号，数据行号=标题行号+1
     * @param sheetIndex
     *            工作表编号
     * @throws InvalidFormatException
     * @throws IOException
     */
    public ExcelImport(MultipartFile multipartFile, int headerNum, int sheetIndex, boolean double2String)
            throws InvalidFormatException, IOException {
        this(multipartFile.getOriginalFilename(), multipartFile.getInputStream(), headerNum, sheetIndex);
        this.double2String = double2String;
    }

    /**
     * 构造函数
     *
     *            导入文件对象
     * @param headerNum
     *            标题行号，数据行号=标题行号+1
     * @param sheetIndex
     *            工作表编号
     * @throws InvalidFormatException
     * @throws IOException
     */
    public ExcelImport(MultipartFile multipartFile, int headerNum, int sheetIndex)
            throws InvalidFormatException, IOException {
        this(multipartFile.getOriginalFilename(), multipartFile.getInputStream(), headerNum, sheetIndex);
    }

    /**
     * 构造函数
     *
     *            导入文件对象
     * @param headerNum
     *            标题行号，数据行号=标题行号+1
     * @param sheetIndex
     *            工作表编号
     * @throws InvalidFormatException
     * @throws IOException
     */
    public ExcelImport(String fileName, InputStream is, int headerNum, int sheetIndex)
            throws InvalidFormatException, IOException {
        if (StringUtils.isBlank(fileName)) {
            throw new RuntimeException("导入文档为空!");
        } else if (fileName.toLowerCase().endsWith("xls")) {
            this.wb = new HSSFWorkbook(is);
        } else if (fileName.toLowerCase().endsWith("xlsx")) {
            this.wb = new XSSFWorkbook(is);
        } else if (fileName.toLowerCase().endsWith("xlsm")) {
            this.wb = new XSSFWorkbook(is);
        } else {
            throw new RuntimeException("文档格式不正确!");
        }
        if (this.wb.getNumberOfSheets() < sheetIndex) {
            throw new RuntimeException("文档中没有工作表!");
        }
        this.sheet = this.wb.getSheetAt(sheetIndex);
        if (this.sheet.getLastRowNum() < 0) {
            throw new RuntimeException("文档中无数据!");
        }
        this.headerNum = headerNum;
        is.close();
        log.debug("Initialize success.");
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
     * 获取数据行号
     *
     * @return
     */
    public int getDataRowNum() {
        return headerNum + 1;
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
     * 获取最后一个列号
     *
     * @return
     */
    public int getLastCellNum() {
        return this.getRow(headerNum).getLastCellNum();
    }

    /**
     * 获取单元格值
     *
     * @param row
     *            获取的行
     * @param column
     *            获取单元格列号
     * @return 单元格值
     */
    public Object getCellValue(Row row, int column) {
        Object val = "";
        try {
            Cell cell = row.getCell(column);
            if (cell != null) {
                if (cell.getCellType() == CellType.NUMERIC) {
                    short format = cell.getCellStyle().getDataFormat();
                    SimpleDateFormat sdf;
                    if (format == 184) { // 日期
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    } else { // 不是日期格式
                        // 根据数据来判断是否使用转换
                        val = cell.getNumericCellValue();
                        // 如果数据包含E并且需要转换，则转换
                        if (double2String && String.valueOf(val).contains("E")) {
                            val = new java.text.DecimalFormat("0").format(cell.getNumericCellValue());
                        }
                        return val;
                    }
                    double value = cell.getNumericCellValue();
                    Date date = DateUtil.getJavaDate(value);
                    if (date == null || "".equals(date)) {
                        val = "";
                    }
                    try {
                        val = sdf.format(date);
                    } catch (Exception e) {
                        e.printStackTrace();
                        val = "";
                    }
                } else if (cell.getCellType() == CellType.STRING) {
                    val = cell.getStringCellValue();
                } else if (cell.getCellType() == CellType.FORMULA) {
                    try {
                        val = cell.getStringCellValue();
                    } catch (Exception e) {
                        val = cell.getCellFormula();
                    }
                } else if (cell.getCellType() == CellType.BOOLEAN) {
                    val = cell.getBooleanCellValue();
                } else if (cell.getCellType() == CellType.ERROR) {
                    val = cell.getErrorCellValue();
                }
            }
        } catch (Exception e) {
            return val;
        }
        return val;
    }

    /**
     * 获取某几列数据
     */

    public List<Map<String, Object>> getDataListByColumn(Map<String, String> headerMap, int[] columnNumber) {
        List dataList = new ArrayList<Map<String, Object>>();
        for (int i = getDataRowNum(); i < getLastDataRowNum(); i++) {
            Row row = getRow(i);
            StringBuilder sb = new StringBuilder();
            Map<String, Object> e = new HashMap<>();
            int number = 0;
            for (String entityName : headerMap.keySet()) {
                Object val = getCellValue(row, columnNumber[number++]);
                if (StringUtils.isNotBlank(val.toString())) {
                    String valType = headerMap.get(entityName);
                    try {
                        if (ExcelDataType.STRING.dataType.equals(valType)) {
                            String s = String.valueOf(val.toString());
                            if (StringUtils.endsWith(s, ".0")) {
                                val = StringUtils.substringBefore(s, ".0");
                            } else {
                                val = String.valueOf(val.toString());
                            }
                            val = StringUtils.trim((String) val);
                        } else if (ExcelDataType.INTEGER.dataType.equals(valType)) {
                            val = Double.valueOf(val.toString()).intValue();
                        } else if (ExcelDataType.LONG.dataType.equals(valType)) {
                            val = Double.valueOf(val.toString()).longValue();
                        } else if (ExcelDataType.DOUBLE.dataType.equals(valType)) {
                            val = Double.valueOf(val.toString());
                        } else if (ExcelDataType.BIG_DECIMAL.dataType.equals(valType)) {
                            val = BigDecimal.valueOf(Double.valueOf(val.toString()));
                        } else if (ExcelDataType.FLOAT.dataType.equals(valType)) {
                            val = Float.valueOf(val.toString());
                        } else if (ExcelDataType.TIMESTAMP.dataType.equals(valType)) {
                            if (val.toString().split("-").length > 1) {
                                val = Timestamp.valueOf(val.toString());
                            } else {
                                val = DateUtil.getJavaDate((Double) val);
                                val = new Timestamp(((Date) val).getTime());
                            }
                        } else if (ExcelDataType.DATE.dataType.equals(valType)) {
                            val = DateUtil.getJavaDate((Double) val);
                        }
                    } catch (Exception ex) {
                        log.info("Get cell value [" + i + "," + number +
                                "] error: " + ex.toString());

                        val = null;
                    }
                    if (val == null) {
                        val = "";
                    }
                    e.put(entityName, val);
                }else{
                    e.put(entityName, "");
                }
                sb.append(val).append(", ");
            }
            dataList.add(e);
        }
        return dataList;
    }


    /**
     * 获取导入数据列表
     *
     * @param cls
     *            导入对象类型
     * @param headerMap
     *            表头对象LinkedHashMap 字段名，字段类型
     * @param macroMap
     *            字典对象Map 字段名,字典类型
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     *             List<E>
     */
    public <E> List<E> getDataList(Class<E> cls, Map<String, String> headerMap, Map<String, String> macroMap)
            throws InstantiationException, IllegalAccessException {
        List dataList = new ArrayList();
        for (int i = getDataRowNum(); i < getLastDataRowNum(); i++) {
            Object e = cls.newInstance();
            int column = 0;
            Row row = getRow(i);
            StringBuilder sb = new StringBuilder();
            for (String entityName : headerMap.keySet()) {
                Object val = getCellValue(row, column++);
                if (StringUtils.isNotBlank(val.toString())) {

                    String valType = headerMap.get(entityName);
                    try {
                        if (ExcelDataType.STRING.dataType.equals(valType)) {
                            String s = String.valueOf(val.toString());
                            if (StringUtils.endsWith(s, ".0")) {
                                val = StringUtils.substringBefore(s, ".0");
                            } else {
                                val = String.valueOf(val.toString());
                            }
                            val = StringUtils.trim((String) val).replaceAll("\\u00A0+", "");

                        } else if (ExcelDataType.INTEGER.dataType.equals(valType)) {
                            val = Integer.valueOf(Double.valueOf(val.toString()).intValue());
                        } else if (ExcelDataType.LONG.dataType.equals(valType)) {
                            val = Long.valueOf(Double.valueOf(val.toString()).longValue());
                        } else if (ExcelDataType.DOUBLE.dataType.equals(valType)) {
                            val = Double.valueOf(val.toString());
                        } else if (ExcelDataType.BIG_DECIMAL.dataType.equals(valType)) {
                            val = BigDecimal.valueOf(Double.valueOf(val.toString()));
                        } else if (ExcelDataType.FLOAT.dataType.equals(valType)) {
                            val = Float.valueOf(val.toString());
                        } else if (ExcelDataType.TIMESTAMP.dataType.equals(valType)) {
                            if(val.toString().split("-").length > 1) {
                                val = Timestamp.valueOf(val.toString());
                            }else {
                                val = DateUtil.getJavaDate(((Double) val).doubleValue());
                                val = new Timestamp(((Date)val).getTime());
                            }
                        }else if (ExcelDataType.DATE.dataType.equals(valType)) {
                            val = DateUtil.getJavaDate(((Double) val).doubleValue());
                        }
                    } catch (Exception ex) {
                        log.info(new StringBuilder().append("Get cell value [").append(i).append(",").append(column)
                                .append("] error: ").append(ex.toString()).toString());

                        val = null;
                    }
                    Reflections.invokeSetter(e, entityName, val);
                }
                sb.append(new StringBuilder().append(val).append(", ").toString());
            }
            dataList.add(e);
        }
        return dataList;
    }

    /**
     * 获取导入数据列表
     *
     * @param headerMap
     *            表头对象LinkedHashMap 字段名，字段类型
     * @param macroMap
     *            字典对象Map 字段名,字典类型
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     *             List<Map<String,String>>
     */
    public List<Map<String, String>> getDataList(Map<String, String> headerMap, Map<String, String> macroMap) {
        List dataList = new ArrayList();
        for (int i = getDataRowNum(); i < getLastDataRowNum(); i++) {
            int column = 0;
            Row row = getRow(i);
            StringBuilder sb = new StringBuilder();
            Map valMap = new HashMap(headerMap.size());
            for (String entityName : headerMap.keySet()) {
                Object val = getCellValue(row, column++);
                if (val != null) {
                    String valType = headerMap.get(entityName);
                    try {
                        if (ExcelDataType.STRING.dataType.equals(valType)) {
                            String s = String.valueOf(val.toString());
                            if (StringUtils.endsWith(s, ".0")) {
                                val = StringUtils.substringBefore(s, ".0");
                            } else {
                                val = String.valueOf(val.toString());
                            }
                        } else if (ExcelDataType.INTEGER.dataType.equals(valType)) {
                            val = Integer.valueOf(Double.valueOf(val.toString()).intValue());
                        } else if (ExcelDataType.LONG.dataType.equals(valType)) {
                            val = Long.valueOf(Double.valueOf(val.toString()).longValue());
                        } else if (ExcelDataType.DOUBLE.dataType.equals(valType)) {
                            val = Double.valueOf(val.toString());
                        } else if (ExcelDataType.BIG_DECIMAL.dataType.equals(valType)) {
                            val = BigDecimal.valueOf(Double.valueOf(val.toString()));
                        } else if (ExcelDataType.FLOAT.dataType.equals(valType)) {
                            val = Float.valueOf(val.toString());
                        } else if (ExcelDataType.DATE.dataType.equals(valType)) {
                            val = DateUtil.getJavaDate(((Double) val).doubleValue());
                        }
                    } catch (Exception ex) {
                        log.debug(new StringBuilder().append("Get cell value [").append(i).append(",").append(column)
                                .append("] error: ").append(ex.toString()).toString());

                        val = "";
                    }
                }
                valMap.put(entityName, val);
                sb.append(new StringBuilder().append(val).append(", ").toString());
            }
            dataList.add(valMap);
            log.debug(new StringBuilder().append("Read success: [").append(i).append("] ").append(sb.toString())
                    .toString());
        }
        return dataList;
    }

    /**
     * 获取导入数据列表
     *
     * @param headerList
     *            数据字段集合
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     *             List<Map<String,String>>
     */
    public List<Map<String, String>> getDataList(List<String> headerList) {
        List dataList = new ArrayList();
        for (int i = getDataRowNum(); i < getLastDataRowNum(); i++) {
            int column = 0;
            Row row = getRow(i);
            StringBuilder sb = new StringBuilder();
            Map valMap = new HashMap(headerList.size());
            for (String key : headerList) {
                Object val = getCellValue(row, column++);
                try {
                    val = String.valueOf(val.toString());
                } catch (Exception ex) {
                    log.debug(new StringBuilder().append("Get cell value [").append(i).append(",").append(column)
                            .append("] error: ").append(ex.toString()).toString());

                    val = "";
                }
                valMap.put(key, val.toString());
                sb.append(new StringBuilder().append(val).append(", ").toString());
            }
            dataList.add(valMap);
            log.debug(new StringBuilder().append("Read success: [").append(i).append("] ").append(sb.toString())
                    .toString());
        }
        return dataList;
    }

    /**
     * 导入测试
     */
    public static void main(String[] args) throws Throwable {
        ExcelImport ei = new ExcelImport("C:\\Users\\xu.x.zhang\\Desktop\\scrap.xlsx", 1);
        for (int i = 0; i < ei.getLastDataRowNum(); i++) {
            Row row = ei.getRow(i);
            for (int j = ei.getDataRowNum(); j < ei.getLastCellNum(); j++) {
                Object val = ei.getCellValue(row, j);
                System.out.print(val + ", ");
            }
            System.out.print("\n");
        }
    }
}
