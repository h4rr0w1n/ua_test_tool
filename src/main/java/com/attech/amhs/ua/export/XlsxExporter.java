package com.attech.amhs.ua.export;

import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSession;
import com.attech.amhs.ua.model.TestSubcase;
import com.attech.amhs.ua.model.MessageLog;
import com.attech.amhs.ua.repository.TestCaseRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Service for exporting test session data to Excel/XLSX format
 */
public class XlsxExporter {

    private TestCaseRepository repository;
    private TestSession testSession;

    public XlsxExporter(TestCaseRepository repository, TestSession testSession) {
        this.repository = repository;
        this.testSession = testSession;
    }

    /**
     * Export test session data to XLSX file
     * 
     * @param filePath Path where to save the XLSX file
     * @throws IOException if export fails
     */
    public void export(String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        
        try {
            // Create sheets
            createSummarySheet(workbook);
            createTestCasesSheet(workbook);
            createTestSubcasesSheet(workbook);
            createMessagesSheet(workbook);
            createSessionSheet(workbook);
            
            // Write workbook to file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        } finally {
            workbook.close();
        }
    }

    /**
     * Create summary sheet with overall test statistics
     */
    private void createSummarySheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Summary");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int row = 0;
        
        // Title
        Cell titleCell = sheet.createRow(row++).createCell(0);
        titleCell.setCellValue("AMHS Test Session Summary");
        titleCell.setCellStyle(headerStyle);
        
        // Session timing
        row++;
        sheet.createRow(row).createCell(0).setCellValue("Session Start Time:");
        long startTime = testSession.getSessionStartTime();
        sheet.getRow(row).createCell(1).setCellValue(formatDate(startTime));
        row++;
        
        sheet.createRow(row).createCell(0).setCellValue("Session End Time:");
        long endTime = testSession.getSessionEndTime();
        sheet.getRow(row).createCell(1).setCellValue(formatDate(endTime));
        row++;
        
        sheet.createRow(row).createCell(0).setCellValue("Session Duration (ms):");
        sheet.getRow(row).createCell(1).setCellValue(testSession.getSessionDuration());
        row++;
        
        sheet.createRow(row).createCell(0).setCellValue("Session Duration (HH:mm:ss):");
        sheet.getRow(row).createCell(1).setCellValue(formatDuration(testSession.getSessionDuration()));
        row++;
        
        // Statistics
        row++;
        sheet.createRow(row).createCell(0).setCellValue("Test Statistics:");
        row++;
        
        int totalSubcases = repository.getSubcaseCount();
        int markedSubcases = repository.getMarkedSubcaseCount();
        int passedSubcases = repository.getPassedSubcaseCount();
        int failedSubcases = repository.getFailedSubcaseCount();
        
        sheet.createRow(row).createCell(0).setCellValue("Total Test Subcases:");
        sheet.getRow(row).createCell(1).setCellValue(totalSubcases);
        row++;
        
        sheet.createRow(row).createCell(0).setCellValue("Marked Subcases:");
        sheet.getRow(row).createCell(1).setCellValue(markedSubcases);
        row++;
        
        sheet.createRow(row).createCell(0).setCellValue("Passed Subcases:");
        sheet.getRow(row).createCell(1).setCellValue(passedSubcases);
        row++;
        
        sheet.createRow(row).createCell(0).setCellValue("Failed Subcases:");
        sheet.getRow(row).createCell(1).setCellValue(failedSubcases);
        row++;
        
        sheet.createRow(row).createCell(0).setCellValue("Pass Rate:");
        if (markedSubcases > 0) {
            double passRate = (double) passedSubcases / markedSubcases * 100;
            sheet.getRow(row).createCell(1).setCellValue(String.format("%.2f%%", passRate));
        } else {
            sheet.getRow(row).createCell(1).setCellValue("N/A");
        }
        row++;
        
        sheet.createRow(row).createCell(0).setCellValue("Messages Sent:");
        sheet.getRow(row).createCell(1).setCellValue(testSession.getMessageLogs().size());
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    /**
     * Create sheet with test case information
     */
    private void createTestCasesSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Test Cases");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(0);
        
        headerRow.createCell(0).setCellValue("Test Case ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Description");
        headerRow.createCell(3).setCellValue("Result");
        headerRow.createCell(4).setCellValue("Comments");
        headerRow.createCell(5).setCellValue("Subcase Count");
        
        for (int i = 0; i < 6; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        int row = 1;
        for (TestCase testCase : repository.getTestCasesList()) {
            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue(testCase.getId());
            dataRow.createCell(1).setCellValue(testCase.getName());
            dataRow.createCell(2).setCellValue(testCase.getDescription() != null ? testCase.getDescription() : "");
            
            String result = testCase.getResult();
            if (result == null || result.trim().isEmpty()) {
                boolean hasFail = false;
                boolean hasPass = false;
                for (TestSubcase sc : testCase.getSubcases()) {
                    if ("FAIL".equalsIgnoreCase(sc.getResult())) hasFail = true;
                    if ("PASS".equalsIgnoreCase(sc.getResult())) hasPass = true;
                }
                if (hasFail) result = "FAIL";
                else if (hasPass) result = "PASS";
                else result = "";
            }
            dataRow.createCell(3).setCellValue(result);
            
            dataRow.createCell(4).setCellValue(testCase.getComment() != null ? testCase.getComment() : "");
            dataRow.createCell(5).setCellValue(testCase.getSubcases().size());
        }
        
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create sheet with test subcase information
     */
    private void createTestSubcasesSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Test Subcases");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(0);
        
        headerRow.createCell(0).setCellValue("Test Case ID");
        headerRow.createCell(1).setCellValue("Subcase ID");
        headerRow.createCell(2).setCellValue("Subcase Name");
        headerRow.createCell(3).setCellValue("Description");
        headerRow.createCell(4).setCellValue("Result");
        headerRow.createCell(5).setCellValue("Comments");
        headerRow.createCell(6).setCellValue("Start Time");
        headerRow.createCell(7).setCellValue("End Time");
        headerRow.createCell(8).setCellValue("Duration (ms)");
        headerRow.createCell(9).setCellValue("Marked");
        
        for (int i = 0; i < 10; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        int row = 1;
        for (TestCase testCase : repository.getTestCasesList()) {
            for (TestSubcase subcase : testCase.getSubcases()) {
                Row dataRow = sheet.createRow(row++);
                dataRow.createCell(0).setCellValue(testCase.getId());
                dataRow.createCell(1).setCellValue(subcase.getId());
                dataRow.createCell(2).setCellValue(subcase.getName());
                dataRow.createCell(3).setCellValue(subcase.getDescription() != null ? subcase.getDescription() : "");
                dataRow.createCell(4).setCellValue(subcase.getResult() != null ? subcase.getResult() : "");
                dataRow.createCell(5).setCellValue(subcase.getComment() != null ? subcase.getComment() : "");
                dataRow.createCell(6).setCellValue(formatDate(subcase.getStartTime()));
                dataRow.createCell(7).setCellValue(formatDate(subcase.getEndTime()));
                dataRow.createCell(8).setCellValue(subcase.getDuration());
                dataRow.createCell(9).setCellValue(subcase.isMarked() ? "Yes" : "No");
            }
        }
        
        for (int i = 0; i < 10; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create sheet with message log information
     */
    private void createMessagesSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Messages");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(0);
        
        headerRow.createCell(0).setCellValue("Timestamp");
        headerRow.createCell(1).setCellValue("Test Case");
        headerRow.createCell(2).setCellValue("Test Subcase");
        headerRow.createCell(3).setCellValue("Sender");
        headerRow.createCell(4).setCellValue("Recipient");
        headerRow.createCell(5).setCellValue("Subject");
        headerRow.createCell(6).setCellValue("Priority");
        headerRow.createCell(7).setCellValue("Success");
        headerRow.createCell(8).setCellValue("Error");
        headerRow.createCell(9).setCellValue("Content");
        headerRow.createCell(10).setCellValue("X.400 Payload");
        
        for (int i = 0; i < 11; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        int row = 1;
        for (MessageLog log : testSession.getMessageLogs()) {
            // Skip received messages - only export sent messages
            if (log.isReceived()) {
                continue;
            }
            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue(formatDate(log.getTimestamp()));
            dataRow.createCell(1).setCellValue(log.getTestCaseId() != null ? log.getTestCaseId() : "");
            dataRow.createCell(2).setCellValue(log.getTestSubcaseId() != null ? log.getTestSubcaseId() : "");
            dataRow.createCell(3).setCellValue(log.getSender() != null ? log.getSender() : "");
            dataRow.createCell(4).setCellValue(log.getRecipient() != null ? log.getRecipient() : "");
            dataRow.createCell(5).setCellValue(log.getSubject() != null ? log.getSubject() : "");
            dataRow.createCell(6).setCellValue(log.getPriority() != null ? log.getPriority() : "");
            dataRow.createCell(7).setCellValue(log.isSuccess() ? "Success" : "Failed");
            dataRow.createCell(8).setCellValue(log.getErrorMessage() != null ? log.getErrorMessage() : "");
            dataRow.createCell(9).setCellValue(log.getContent() != null ? log.getContent() : "");
            dataRow.createCell(10).setCellValue(log.getX400Payload() != null ? log.getX400Payload() : "");
        }
        
        for (int i = 0; i < 11; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create sheet with session details
     */
    private void createSessionSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Session Details");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        int row = 0;
        
        Cell titleCell = sheet.createRow(row++).createCell(0);
        titleCell.setCellValue("Session Details");
        titleCell.setCellStyle(headerStyle);
        
        row++;
        sheet.createRow(row).createCell(0).setCellValue("Session Identifier:");
        sheet.getRow(row).createCell(1).setCellValue(System.getProperty("user.name") + "_" + 
                                                    System.currentTimeMillis());
        row++;
        
        sheet.createRow(row).createCell(0).setCellValue("Export Date:");
        sheet.getRow(row).createCell(1).setCellValue(formatDate(System.currentTimeMillis()));
        row++;
        
        int testedCases = 0;
        for (TestCase testCase : repository.getTestCasesList()) {
            boolean isTested = testCase.isMarked();
            if (!isTested) {
                for (TestSubcase subcase : testCase.getSubcases()) {
                    if (subcase.isMarked()) {
                        isTested = true;
                        break;
                    }
                }
            }
            if (isTested) testedCases++;
        }
        
        int totalCases = Math.max(20, repository.getTestCaseCount());
        sheet.createRow(row).createCell(0).setCellValue("Tested / Total Test Cases:");
        sheet.getRow(row).createCell(1).setCellValue(testedCases + " / " + totalCases);
        row++;
        
        int testedSubcases = repository.getMarkedSubcaseCount();
        int totalSubcases = repository.getSubcaseCount();
        sheet.createRow(row).createCell(0).setCellValue("Tested / Total Subcases:");
        sheet.getRow(row).createCell(1).setCellValue(testedSubcases + " / " + totalSubcases);
        row++;
        
        sheet.createRow(row).createCell(0).setCellValue("Total Messages Sent:");
        sheet.getRow(row).createCell(1).setCellValue(testSession.getMessageLogs().size());
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    /**
     * Create header cell style
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    /**
     * Create data cell style
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    /**
     * Format timestamp to readable date string
     */
    private String formatDate(long timestamp) {
        if (timestamp == 0) {
            return "N/A";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }

    /**
     * Format duration in milliseconds to HH:mm:ss format
     */
    private String formatDuration(long durationMs) {
        if (durationMs == 0) {
            return "0s";
        }
        long totalSeconds = durationMs / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
