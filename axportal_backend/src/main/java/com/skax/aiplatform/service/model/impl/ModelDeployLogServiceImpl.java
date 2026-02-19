package com.skax.aiplatform.service.model.impl;

import com.skax.aiplatform.client.sktai.history.dto.response.ModelHistoryRead;
import com.skax.aiplatform.client.sktai.history.service.SktaiHistoryService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.log.response.ModelHistoryRecordRes;
import com.skax.aiplatform.dto.log.response.ModelHistoryRes;
import com.skax.aiplatform.mapper.log.ModelLogMapper;
import com.skax.aiplatform.service.model.ModelDeployLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 모델 배포 로그 서비스 구현체
 *
 * <p>
 * 모델 배포와 관련된 사용 이력 및 로그를 조회하는 서비스 구현체입니다.
 * SKTAI History API를 통해 모델 사용 이력을 조회합니다.
 * </p>
 *
 * @author TaeYounChung
 * @version 1.0.0
 * @since 2025-09-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModelDeployLogServiceImpl implements ModelDeployLogService {

    private final SktaiHistoryService sktaiHistoryService;
    private final ModelLogMapper modelLogMapper;

    @Override
    public PageResponse<ModelHistoryRecordRes> getModelHistoryList(String fields, Boolean errorLogs, String fromDate, String toDate, Integer page, Integer size, String filter, String search, String sort) {

        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service ModelDeployLogServiceImpl.getModelHistoryList ]");
        log.info("Request Parameters - fields: {}, errorLogs: {}, fromDate: {}, toDate: {}, page: {}, size: {}, filter: {}, search: {}, sort: {}", fields, errorLogs, fromDate, toDate, page, size, filter, search, sort);
        log.info("-----------------------------------------------------------------------------------------");

        try {
            // SKTAI History API 호출
            ModelHistoryRead response = sktaiHistoryService.getModelHistoryList(fields, errorLogs, fromDate, toDate, page, size, filter, search, sort);

            log.info("SKTAI History API 호출 성공 - 총 {}건 조회", response.getPayload() != null && response.getPayload().getPagination() != null ? response.getPayload().getPagination().getTotal() : 0);

            ModelHistoryRes modelHistoryRes = modelLogMapper.toModelHistoryRes(response);

            return PaginationUtils.toPageResponseFromAdxp(response.getPayload(), modelHistoryRes.getData());

        } catch (BusinessException e) {
            log.error("모델 사용 이력 조회 실패 (BusinessException) - fromDate: {}, toDate: {}, filter: {}, search: {}, errorCode: {}", fromDate, toDate, filter, search, e.getErrorCode(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("모델 사용 이력 조회 실패 (RuntimeException) - fromDate: {}, toDate: {}, filter: {}, search: {}", fromDate, toDate, filter, search, e);
            throw e;
        }
    }

    @Override
    public byte[] generateCsvData(ModelHistoryRes modelHistoryRes) {
        log.info("CSV 데이터 생성 시작 - 총 {}건의 레코드", modelHistoryRes.getData() != null ? modelHistoryRes.getData().size() : 0);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {

            // BOM 추가 (Excel에서 한글 깨짐 방지)
            baos.write(0xEF);
            baos.write(0xBB);
            baos.write(0xBF);

            // CSV 헤더 작성
            writeCsvHeader(writer);

            // 데이터 행 작성
            List<ModelHistoryRecordRes> data = modelHistoryRes.getData();
            if (data != null && !data.isEmpty()) {
                for (int i = 0; i < data.size(); i++) {
                    writeCsvRecord(writer, data.get(i), i + 1);
                }
            }

            writer.flush();
            byte[] csvBytes = baos.toByteArray();

            log.info("CSV 데이터 생성 완료 - {} bytes", csvBytes.length);
            return csvBytes;

        } catch (IOException e) {
            log.error("CSV 데이터 생성 중 오류 발생", e);
            throw new RuntimeException("CSV 데이터 생성에 실패했습니다.", e);
        }
    }

    /**
     * CSV 헤더 작성
     */
    private void writeCsvHeader(OutputStreamWriter writer) throws IOException {
        writer.write("NO,요청일시,상태,총 소요시간(ms),내용,호출 Key,모델명,사용자,완성 토큰,프롬프트 토큰,총 토큰\n");
    }

    /**
     * CSV 레코드 작성
     */
    private void writeCsvRecord(OutputStreamWriter writer, ModelHistoryRecordRes record, int rowNumber) throws IOException {
        // 내용 (request + response) - 3만자 단위로 분할
        String content = String.format("request: %s\nresponse: %s", record.getInputJson() != null ? record.getInputJson() : "", record.getOutputJson() != null ? record.getOutputJson() : "");

        final int CHUNK_SIZE = 30000; // 3만자 단위
        int contentLength = content.length();

        // 첫 번째 행 작성 (모든 컬럼 포함)
        writeCsvRecordRow(writer, record, rowNumber, contentLength > 0 ? content.substring(0, Math.min(CHUNK_SIZE, contentLength)) : "");

        // 나머지 내용이 있으면 추가 행 작성 (내용 컬럼만)
        if (contentLength > CHUNK_SIZE) {
            int startIndex = CHUNK_SIZE;
            while (startIndex < contentLength) {
                int endIndex = Math.min(startIndex + CHUNK_SIZE, contentLength);
                String chunk = content.substring(startIndex, endIndex);
                writeCsvRecordContentOnly(writer, chunk);
                startIndex = endIndex;
            }
        }
    }

    /**
     * CSV 레코드의 전체 행 작성 (모든 컬럼 포함)
     */
    private void writeCsvRecordRow(OutputStreamWriter writer, ModelHistoryRecordRes record, int rowNumber, String contentChunk) throws IOException {
        // NO (순번) - 데이터 순서대로 번호 부여
        writer.write(escapeCsvValue(String.valueOf(rowNumber)));
        writer.write(",");

        // 요청일시
        writer.write(escapeCsvValue(record.getRequestTime()));
        writer.write(",");

        // 상태
        String status = record.getErrorMessage() != null && !record.getErrorMessage().isEmpty() ? "실패" : "정상";
        writer.write(escapeCsvValue(status));
        writer.write(",");

        // 총 소요시간(ms)
        String elapsedTime = record.getElapsedTime() != null ? String.valueOf(Math.floor(record.getElapsedTime() * 100)) : "";
        writer.write(escapeCsvValue(elapsedTime));
        writer.write(",");

        // 내용 (첫 3만자)
        writer.write(escapeCsvValue(contentChunk));
        writer.write(",");

        // 호출 Key
        writer.write(escapeCsvValue(record.getApiKey()));
        writer.write(",");

        // 모델명
        writer.write(escapeCsvValue(record.getModelName()));
        writer.write(",");

        // 사용자
        writer.write(escapeCsvValue(record.getUser()));
        writer.write(",");

        // 완성 토큰
        writer.write(escapeCsvValue(record.getCompletionTokens() != null ? record.getCompletionTokens().toString() : ""));
        writer.write(",");

        // 프롬프트 토큰
        writer.write(escapeCsvValue(record.getPromptTokens() != null ? record.getPromptTokens().toString() : ""));
        writer.write(",");

        // 총 토큰
        writer.write(escapeCsvValue(record.getTotalTokens() != null ? record.getTotalTokens().toString() : ""));
        writer.write("\n");
    }

    /**
     * CSV 레코드의 내용만 작성하는 행 (나머지 컬럼은 빈 값)
     */
    private void writeCsvRecordContentOnly(OutputStreamWriter writer, String contentChunk) throws IOException {
        // NO (빈 값)
        writer.write(escapeCsvValue(""));
        writer.write(",");

        // 요청일시 (빈 값)
        writer.write(escapeCsvValue(""));
        writer.write(",");

        // 상태 (빈 값)
        writer.write(escapeCsvValue(""));
        writer.write(",");

        // 총 소요시간(ms) (빈 값)
        writer.write(escapeCsvValue(""));
        writer.write(",");

        // 내용 (다음 3만자)
        writer.write(escapeCsvValue(contentChunk));
        writer.write(",");

        // 호출 Key (빈 값)
        writer.write(escapeCsvValue(""));
        writer.write(",");

        // 모델명 (빈 값)
        writer.write(escapeCsvValue(""));
        writer.write(",");

        // 사용자 (빈 값)
        writer.write(escapeCsvValue(""));
        writer.write(",");

        // 완성 토큰 (빈 값)
        writer.write(escapeCsvValue(""));
        writer.write(",");

        // 프롬프트 토큰 (빈 값)
        writer.write(escapeCsvValue(""));
        writer.write(",");

        // 총 토큰 (빈 값)
        writer.write(escapeCsvValue(""));
        writer.write("\n");
    }

    /**
     * CSV 값 이스케이프 처리
     */
    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }

        // 값에 쉼표, 따옴표, 줄바꿈이 포함된 경우 따옴표로 감싸고 내부 따옴표는 두 개로 변환
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}
