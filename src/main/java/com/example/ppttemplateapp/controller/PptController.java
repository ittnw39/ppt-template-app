package com.example.ppttemplateapp.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.example.ppttemplateapp.service.PptService;
import com.example.ppttemplateapp.repository.ReportRepository;
import com.example.ppttemplateapp.model.Report;
import com.example.ppttemplateapp.DTO.ReportDto;

@RestController
@RequestMapping("/ppt")
public class PptController {

    private final PptService pptService;
    private final ReportRepository reportRepository;

    public PptController(PptService pptService, ReportRepository reportRepository) {
        this.pptService = pptService;
        this.reportRepository = reportRepository;
    }

    // 데이터베이스 연결 테스트
    @GetMapping("/db-test")
    public ResponseEntity<Map<String, Object>> testDatabaseConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 총 레코드 수 확인
            long count = reportRepository.count();
            
            // 모든 레포트 조회
            List<Report> reports = reportRepository.findAll();
            
            response.put("status", "SUCCESS");
            response.put("message", "데이터베이스 연결 성공!");
            response.put("total_records", count);
            response.put("reports", reports);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "데이터베이스 연결 실패: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 테스트 데이터 생성 (POST)
    @PostMapping("/create-test-data")
    public ResponseEntity<Map<String, Object>> createTestData(
            @RequestParam(defaultValue = "테스트 보고서") String title,
            @RequestParam(defaultValue = "이것은 테스트 데이터입니다.") String content) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Report report = Report.builder()
                    .title(title)
                    .content(content)
                    .build();
            
            Report savedReport = reportRepository.save(report);
            
            response.put("status", "SUCCESS");
            response.put("message", "테스트 데이터 생성 성공!");
            response.put("created_report", savedReport);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "테스트 데이터 생성 실패: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 테스트 데이터 생성 (GET - 브라우저 테스트용)
    @GetMapping("/create-test-data")
    public ResponseEntity<Map<String, Object>> createTestDataGet(
            @RequestParam(defaultValue = "테스트 보고서") String title,
            @RequestParam(defaultValue = "이것은 테스트 데이터입니다.") String content) {
        
        // POST 메서드와 동일한 로직 재사용
        return createTestData(title, content);
    }

    // 모든 보고서 조회
    @GetMapping("/reports")
    public ResponseEntity<List<Report>> getAllReports() {
        try {
            List<Report> reports = reportRepository.findAll();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 템플릿 분석
    @GetMapping("/analyze-template")
    public ResponseEntity<String> analyzeTemplate() {
        try {
            String analysis = pptService.analyzePlaceholders();
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("템플릿 분석 중 오류 발생: " + e.getMessage());
        }
    }

    // PPT 생성 테스트 (ID 없이)
    @PostMapping("/test-ppt-generation")
    public ResponseEntity<byte[]> testPptGeneration(
            @RequestParam(defaultValue = "테스트 제목") String title,
            @RequestParam(defaultValue = "테스트 내용입니다.") String content) {
        
        try {
            Map<String, String> testData = new HashMap<>();
            testData.put("title", title);
            testData.put("content", content);
            
            byte[] pptBytes = pptService.generatePptFromData(testData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType
                .parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation"));
            headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename("test_report.pptx")
                .build());

            return new ResponseEntity<>(pptBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PPT 생성 테스트 (GET - 브라우저 테스트용)
    @GetMapping("/test-ppt-generation")
    public ResponseEntity<byte[]> testPptGenerationGet(
            @RequestParam(defaultValue = "테스트 제목") String title,
            @RequestParam(defaultValue = "테스트 내용입니다.") String content) {
        
        return testPptGeneration(title, content);
    }

    // 템플릿 분석 (HTML 형태로 보기 좋게)
    @GetMapping("/analyze-template-html")
    public ResponseEntity<String> analyzeTemplateHtml() {
        try {
            String analysis = pptService.analyzePlaceholders();
            String htmlResponse = "<html><head><meta charset='UTF-8'><title>템플릿 분석</title></head>" +
                    "<body style='font-family: malgun gothic, arial; margin: 20px;'>" +
                    "<h2>🔍 PPTX 템플릿 분석 결과</h2>" +
                    "<pre style='background: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto;'>" +
                    analysis +
                    "</pre>" +
                    "<hr>" +
                    "<h3>📝 테스트 방법</h3>" +
                    "<p><strong>1. 테스트 데이터 생성:</strong><br>" +
                    "<a href='/ppt/create-test-data?title=성과보고서&content=2025년1월프로젝트성과입니다'>" +
                    "/ppt/create-test-data?title=성과보고서&content=2025년1월프로젝트성과입니다</a></p>" +
                    "<p><strong>2. PPT 직접 생성:</strong><br>" +
                    "<a href='/ppt/test-ppt-generation?title=성과보고서&content=2025년1월프로젝트성과입니다'>" +
                    "/ppt/test-ppt-generation?title=성과보고서&content=2025년1월프로젝트성과입니다</a></p>" +
                    "<p><strong>3. 데이터베이스 확인:</strong><br>" +
                    "<a href='/ppt/reports'>/ppt/reports</a></p>" +
                    "</body></html>";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.add("Content-Language", "ko-KR");
            
            return new ResponseEntity<>(htmlResponse, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("<html><body><h2>오류 발생</h2><p>템플릿 분석 중 오류: " + e.getMessage() + "</p></body></html>");
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFromDb(@PathVariable Integer id) throws Exception {
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("보고서를 찾을 수 없습니다"));

        ReportDto dto = ReportDto.fromEntity(report);
        byte[] pptBytes = pptService.generatePptFromData(dto.toMap());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType
            .parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation"));
        headers.setContentDisposition(ContentDisposition
            .attachment()
            .filename("report_" + id + ".pptx")
            .build());

        return new ResponseEntity<>(pptBytes, headers, HttpStatus.OK);
    }
}
