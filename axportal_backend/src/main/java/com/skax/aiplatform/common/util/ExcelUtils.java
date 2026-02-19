package com.skax.aiplatform.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.extern.slf4j.Slf4j;

/**
 * Excel 파일 생성 유틸리티 클래스
 * 
 * <p>Apache POI를 사용하여 Excel 파일을 생성하는 유틸리티를 제공합니다.</p>
 * 
 * @author sonmunwoo
 * @since 2025-09-20
 * @version 1.0.0
 */
@Slf4j
public class ExcelUtils {

    private ExcelUtils() {
        throw new UnsupportedOperationException("이 클래스는 유틸리티 클래스이므로 인스턴스화할 수 없습니다.");
    }


    /**
     * 헤더 스타일 생성
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        
        // 폰트 설정
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        
        // 스타일 설정
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // 테두리 설정
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }

    /**
     * 데이터 스타일 생성
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 정렬 설정
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // 텍스트 줄바꿈 허용
        style.setWrapText(true);
        
        // 테두리 설정
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }



    /**
     * 셀 생성 및 값 설정
     */
    private static void createCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }


    /**
     * 동적 헤더와 데이터로 Excel 파일 생성
     * 
     * @param headers 헤더 정보 목록
     * @param data 데이터 목록
     * @return Excel 파일의 바이트 배열
     * @throws IOException Excel 생성 중 오류 발생 시
     */
    public static byte[] createDynamicExcel(List<HeaderInfo> headers, List<Map<String, Object>> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("사용자 활동 로그");
            
            // 헤더 스타일 생성
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // 데이터 스타일 생성
            CellStyle dataStyle = createDataStyle(workbook);
            
            // 동적 헤더 행 생성
            createDynamicHeaderRow(sheet, headers, headerStyle);
            
            // 동적 데이터 행 생성
            createDynamicDataRows(sheet, headers, data, dataStyle);
            
            // 컬럼 너비 설정 (헤더 정보의 width 사용)
            setColumnWidths(sheet, headers);
            
            // Excel 파일을 바이트 배열로 변환
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            byte[] excelData = outputStream.toByteArray();
            log.info("동적 Excel 파일 생성 완료: {}건, {}bytes", data.size(), excelData.length);
            
            return excelData;
        }
    }

    /**
     * 동적 헤더 행 생성
     */
    private static void createDynamicHeaderRow(Sheet sheet, List<HeaderInfo> headers, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        headerRow.setHeight((short) 600); // 행 높이 설정
        
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i).getHeaderName());
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * 동적 데이터 행 생성
     */
    private static void createDynamicDataRows(Sheet sheet, List<HeaderInfo> headers, 
                                            List<Map<String, Object>> data, CellStyle dataStyle) {
        for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex + 1);
            Map<String, Object> rowData = data.get(rowIndex);
            
            for (int colIndex = 0; colIndex < headers.size(); colIndex++) {
                String fieldName = headers.get(colIndex).getField();
                Object value = rowData.get(fieldName);
                String cellValue = value != null ? value.toString() : "";
                
                // errCode 필드인 경우 성공/실패로 변환
                if ("errCode".equals(fieldName)) {
                    String errCode = cellValue;
                    if (errCode == null || errCode.trim().isEmpty()) {
                        cellValue = "미확인";
                    } else if ("200".equals(errCode) || "200".equals(errCode.trim())) {
                        cellValue = "성공";
                    } else {
                        cellValue = "실패";
                    }
                    
                }
                
                createCell(row, colIndex, cellValue, dataStyle);
            }
        }
    }

    /**
     * 컬럼 너비 설정 (헤더 정보의 width 사용)
     */
    private static void setColumnWidths(Sheet sheet, List<HeaderInfo> headers) {
        for (int i = 0; i < headers.size(); i++) {
            HeaderInfo header = headers.get(i);
            if (header.getWidth() != null && header.getWidth() > 0) {
                // width 값을 POI의 단위로 변환 (1 = 약 1/256 문자)
                int poiWidth = header.getWidth() * 256 / 8; // 대략적인 변환
                sheet.setColumnWidth(i, poiWidth);
            } else {
                // width가 없으면 자동 조정
                sheet.autoSizeColumn(i);
                
                // 최대 너비 제한
                int currentWidth = sheet.getColumnWidth(i);
                int maxWidth = 15000;
                if (currentWidth > maxWidth) {
                    sheet.setColumnWidth(i, maxWidth);
                }
            }
        }
    }

    /**
     * 헤더 정보 내부 클래스
     */
    public static class HeaderInfo {
        private String headerName;
        private String field;
        private Integer width;
        
        public String getHeaderName() {
            return headerName;
        }
        
        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }
        
        public String getField() {
            return field;
        }
        
        public void setField(String field) {
            this.field = field;
        }
        
        public Integer getWidth() {
            return width;
        }
        
        public void setWidth(Integer width) {
            this.width = width;
        }
    }
}
