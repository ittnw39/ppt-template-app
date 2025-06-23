package com.example.ppttemplateapp.service;

import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

@Service
public class PptService {

    private static final Logger logger = LoggerFactory.getLogger(PptService.class);
    private final String templatePath = "templates/template.pptx";

    public byte[] generatePptFromData(Map<String, String> data) throws IOException {
        logger.info("PPT 생성 시작: 데이터 = {}", data);
        
        ClassPathResource resource = new ClassPathResource(templatePath);
        if (!resource.exists()) {
            throw new IOException("템플릿 파일을 찾을 수 없습니다: " + templatePath);
        }
        
        try (InputStream templateInputStream = resource.getInputStream();
             XMLSlideShow ppt = new XMLSlideShow(templateInputStream);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            int slideCount = 0;
            int replacementCount = 0;
            
            for (XSLFSlide slide : ppt.getSlides()) {
                slideCount++;
                logger.debug("슬라이드 {} 처리 중", slideCount);
                
                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape textShape) {
                        for (XSLFTextParagraph para : textShape.getTextParagraphs()) {
                            for (XSLFTextRun run : para.getTextRuns()) {
                                String original = run.getRawText();
                                if (original != null && original.contains("${")) {
                                    logger.debug("원본 텍스트: {}", original);
                                    String replaced = replacePlaceholders(original, data);
                                    run.setText(replaced);
                                    logger.debug("변경된 텍스트: {}", replaced);
                                    replacementCount++;
                                }
                            }
                        }
                    }
                }
            }

            logger.info("PPT 생성 완료: {} 슬라이드, {} 개 텍스트 치환", slideCount, replacementCount);
            ppt.write(out);
            return out.toByteArray();
        }
    }

    private String replacePlaceholders(String text, Map<String, String> data) {
        String result = text;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            if (result.contains(placeholder)) {
                result = result.replace(placeholder, entry.getValue());
                logger.debug("치환: {} -> {}", placeholder, entry.getValue());
            }
        }
        return result;
    }
    
    /**
     * 템플릿에서 플레이스홀더를 확인하는 메서드
     */
    public String analyzePlaceholders() {
        StringBuilder analysis = new StringBuilder();
        analysis.append("=== 템플릿 분석 결과 ===\n");
        
        try {
            ClassPathResource resource = new ClassPathResource(templatePath);
            if (!resource.exists()) {
                return "템플릿 파일을 찾을 수 없습니다: " + templatePath;
            }
            
            try (InputStream templateInputStream = resource.getInputStream();
                 XMLSlideShow ppt = new XMLSlideShow(templateInputStream)) {
                
                int slideCount = 0;
                int totalTextShapes = 0;
                int placeholderCount = 0;
                
                for (XSLFSlide slide : ppt.getSlides()) {
                    slideCount++;
                    analysis.append("\n슬라이드 ").append(slideCount).append(":\n");
                    
                    int shapeCount = 0;
                    for (XSLFShape shape : slide.getShapes()) {
                        shapeCount++;
                        
                        if (shape instanceof XSLFTextShape textShape) {
                            totalTextShapes++;
                            analysis.append("  [텍스트박스 ").append(shapeCount).append("]\n");
                            
                            // 텍스트박스의 전체 텍스트 가져오기
                            String fullText = textShape.getText();
                            if (fullText != null && !fullText.trim().isEmpty()) {
                                analysis.append("    전체 텍스트: ").append(fullText.trim()).append("\n");
                            }
                            
                            // 각 문단별 상세 분석
                            int paraCount = 0;
                            for (XSLFTextParagraph para : textShape.getTextParagraphs()) {
                                paraCount++;
                                
                                for (XSLFTextRun run : para.getTextRuns()) {
                                    String text = run.getRawText();
                                    if (text != null && !text.trim().isEmpty()) {
                                        if (text.contains("${")) {
                                            analysis.append("    ✓ 플레이스홀더: ").append(text).append("\n");
                                            placeholderCount++;
                                        } else {
                                            analysis.append("    - 일반 텍스트: ").append(text.trim()).append("\n");
                                        }
                                        
                                        // 폰트 정보
                                        if (run.getFontFamily() != null) {
                                            analysis.append("      폰트: ").append(run.getFontFamily());
                                            if (run.getFontSize() != null) {
                                                analysis.append(", 크기: ").append(run.getFontSize()).append("pt");
                                            }
                                            analysis.append("\n");
                                        }
                                    }
                                }
                            }
                        } else {
                            analysis.append("  [기타 도형 ").append(shapeCount).append("]: ")
                                    .append(shape.getClass().getSimpleName()).append("\n");
                        }
                    }
                    
                    if (shapeCount == 0) {
                        analysis.append("  (빈 슬라이드)\n");
                    }
                }
                
                analysis.append("\n=== 요약 ===\n");
                analysis.append("총 슬라이드 수: ").append(slideCount).append("\n");
                analysis.append("총 텍스트박스 수: ").append(totalTextShapes).append("\n");
                analysis.append("총 플레이스홀더 수: ").append(placeholderCount).append("\n");
                
            }
        } catch (IOException e) {
            analysis.append("분석 중 오류 발생: ").append(e.getMessage());
            logger.error("템플릿 분석 오류", e);
        }
        
        return analysis.toString();
    }
}
