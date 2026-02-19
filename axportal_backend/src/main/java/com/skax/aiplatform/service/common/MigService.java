package com.skax.aiplatform.service.common;

import java.util.List;
import java.util.Map;

import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.common.request.MigMasAsstSearchReq;
import com.skax.aiplatform.dto.common.request.MigMasSearchReq;
import com.skax.aiplatform.dto.common.response.MigMasRes;
import com.skax.aiplatform.dto.common.response.MigMasWithMapRes;
import com.skax.aiplatform.entity.deploy.GpoMigMas;

/**
 * 마이그레이션 관리 서비스
 */
public interface MigService {

    /**
     * 마이그레이션 정보 등록 (INSERT)
     * 
     * @param uuid        UUID (모델 ID 등)
     * @param asstNm      어시스트명 ("agent", "model" 등)
     * @param pgmDescCtnt 프로그램 설명 ("finetuning" 등)
     * @return 저장된 엔티티
     */
    GpoMigMas create(String uuid, String asstNm, String pgmDescCtnt);

    /**
     * 마이그레이션 상태 업데이트 (UPDATE)
     * DEL_YN을 1 로 변경 (삭제 처리)
     * 
     * @param uuid UUID
     * @return 업데이트된 엔티티
     */
    GpoMigMas updateToDeleted(String uuid);

    /**
     * 마이그레이션 상태 조회 (SELECT)
     * 존재 여부와 삭제 여부를 확인하여 활성 상태 반환
     * 
     * @param uuid UUID
     * @return true: 존재하고 삭제 안됨 (이행 전), false: 없거나 삭제됨 (이행 후)
     */
    boolean isActive(String uuid);

    /**
     * 에셋 검증 (Asset Validation)
     * 
     * <p>
     * UUID로 Lineage를 조회하고, 각 target_type에 따라 export 파일을 생성한 후
     * 파일을 읽어서 import 거래를 수행하고 검증합니다.
     * </p>
     * 
     * <h3>처리 과정:</h3>
     * <ol>
     * <li>UUID로 Lineage 조회 (downstream 방향)</li>
     * <li>target_type에 따라 각 target_key로 export 데이터 생성</li>
     * <li>/gapdat/migration_temp/{projectId}/ObjectType명_uuid.json 파일 생성 (있으면
     * 덮어쓰기)</li>
     * <li>생성된 파일을 읽어서 import 거래 호출</li>
     * <li>모든 거래가 성공했는지 확인</li>
     * </ol>
     * 
     * @param uuid      조회할 UUID
     * @param projectId 프로젝트 ID (폴더 구조 생성용)
     * @param type      객체 타입 (FILTER, SERVING_MODEL, APP, KNOWLEDGE)
     * @return 모든 검증이 성공하면 true, 실패하면 false
     * @throws BusinessException 검증 실패 시
     */
    boolean assetValidation(String uuid, String projectId, String type);

    /**
     * migration_temp 폴더를 migration 폴더로 복사
     * 
     * <p>
     * migration_temp/{projectName}/{type}/{id} 폴더를
     * migration/{projectName}/{type}/{id} 폴더로 전체 복사합니다.
     * </p>
     * 
     * <h3>복사 경로:</h3>
     * <ul>
     * <li>소스: {@code {baseDir}/migration_temp/{projectName}/{type}/{id}}</li>
     * <li>대상: {@code {baseDir}/migration/{projectName}/{type}/{id}}</li>
     * </ul>
     * 
     * <h3>OS별 경로:</h3>
     * <ul>
     * <li>Windows: {@code C:\gapdat\migration_temp\{projectName}\{type}\{id} →
     * C:\gapdat\migration\{projectName}\{type}\{id}}</li>
     * <li>Linux/Mac: {@code /gapdat/migration_temp/{projectName}/{type}/{id} →
     * /gapdat/migration/{projectName}/{type}/{id}}</li>
     * </ul>
     * 
     * @param type        객체 타입 (ObjectType enum)
     * @param id          객체 ID
     * @param projectName 프로젝트 이름
     * @return 복사된 대상 폴더 경로, 실패 시 null
     */
//     String copyFolderFromTempToMigration(ObjectType type, String id, String projectId, String projectName,
//             String assetName);

    /**
     * migration_temp 폴더를 migration 폴더로 복사 (dev 값을 prod로 업데이트)
     * 
     * <p>
     * 화면에서 받은 데이터를 기반으로 각 파일의 dev 값을 prod로 업데이트하고,
     * migration_temp 폴더를 migration 폴더로 복사한 후 DB에 저장합니다.
     * </p>
     * 
     * <h3>처리 과정:</h3>
     * <ol>
     * <li>각 타입별로 순회</li>
     * <li>각 항목의 id에 해당하는 파일을 찾아서 dev 값을 prod로 업데이트</li>
     * <li>migration_temp 폴더를 migration 폴더로 복사</li>
     * <li>GPO_MIG_MAS에 insert</li>
     * <li>GPO_MIG_MAS_MAP_MAS에 insert</li>
     * </ol>
     * 
     * @param type          객체 타입 (ObjectType enum)
     * @param id            객체 ID
     * @param projectId     프로젝트 ID
     * @param migrationData extractMigrationDataFromFolder의 반환 형태와 동일한 데이터
     * @param projectName   프로젝트 이름 (migDeployData.prjNm)
     * @param assetName     어시스트 이름 (migDeployData.name)
     * @return 복사된 대상 폴더 경로, 실패 시 null
     */
//     String copyFolderFromTempToMigration(ObjectType type, String id, String projectId,
//             Map<String, List<Map<String, Object>>> migrationData,
//             String projectName, String assetName);

    /**
     * migration_temp 폴더를 migration 폴더로 복사 (JSON 파일로 통합)
     * 
     * <p>
     * 화면에서 받은 데이터를 기반으로 각 파일의 dev 값을 prod로 업데이트하고,
     * migration_temp 폴더의 모든 JSON 파일을 하나의 JSON 파일로 합친 후 DB에 저장합니다.
     * </p>
     * 
     * <h3>처리 과정:</h3>
     * <ol>
     * <li>각 파일의 dev 값을 prod로 업데이트 (migrationData 있으면)</li>
     * <li>SERVING_MODEL 선처리 (MODEL 물리 파일 복사)</li>
     * <li>폴더 내 모든 JSON 파일을 읽어서 정렬 (PROJECT 우선, num 순서)</li>
     * <li>하나의 JSON 파일로 통합 ({projectId}_{type}_{id}.json)</li>
     * <li>DB에 마이그레이션 정보 저장</li>
     * </ol>
     * 
     * @param type          객체 타입 (ObjectType enum)
     * @param id            객체 ID
     * @param projectId     프로젝트 ID
     * @param migrationData extractMigrationDataFromFolder의 반환 형태와 동일한 데이터 (null 가능)
     * @param projectName   프로젝트 이름
     * @param assetName     에셋 이름
     * @return 생성된 JSON 파일 경로, 실패 시 null
     */
    String copyFolderFromTempToMigrationAsJson(ObjectType type, String id, String projectId,
            Map<String, List<Map<String, Object>>> migrationData,
            String projectName, String assetName);

    /**
     * 통합 JSON 파일을 읽어서 import 거래 수행
     * 
     * <p>
     * 통합 JSON 파일을 읽어서 배열의 각 항목을 순서대로 import 수행합니다.
     * </p>
     * 
     * <h3>처리 과정:</h3>
     * <ol>
     * <li>JSON 파일 읽기 (배열 형식)</li>
     * <li>각 항목을 순서대로 처리 (PROJECT 우선, num 순서)</li>
     * <li>존재 여부 확인 및 delete</li>
     * <li>Import 수행</li>
     * </ol>
     * 
     * @param projectId 프로젝트 ID
     * @param jsonFilePath 통합 JSON 파일 경로 ({projectId}_{type}_{id}.json)
     * @return import 성공 여부 (모든 항목이 성공하면 true)
     */
    boolean importFromJsonFile(String projectId, String jsonFilePath);


    /**
     * 폴더에서 마이그레이션 데이터 추출
     * 
     * <p>
     * migration_temp/{projectName}/{type}/{id} 폴더의 JSON 파일들을 읽어서
     * 타입별로 필요한 정보를 추출하고, DB에서 매핑 정보를 조회하여 보완합니다.
     * </p>
     * 
     * <h3>처리 과정:</h3>
     * <ol>
     * <li>폴더에서 모든 .json 파일 읽기</li>
     * <li>파일명 파싱: {TYPE}_{ID}.json</li>
     * <li>타입별로 정보 추출:
     * <ul>
     * <li>KNOWLEDGE: script, index_name을 {value, value} 형태로</li>
     * <li>VECTOR_DB: connection_info의 각 필드</li>
     * </ul>
     * </li>
     * <li>DB에서 GPO_MIG_MAS_MAP_MAS 조회하여 값 보완</li>
     * </ol>
     * 
     * @param projectName 프로젝트 이름
     * @param type        객체 타입 (ObjectType enum)
     * @param id          객체 ID
     * @return 타입별로 추출한 정보를 담은 Map (타입별로 리스트 형태)
     */
    Map<String, List<Map<String, Object>>> extractMigrationDataFromFolder(String projectName, ObjectType type,
            String id);

    /**
     * 폴더에서 Import 및 선처리/후처리 수행
     * 
     * <p>
     * migration/{projectId}/{type}/{id} 폴더의 모든 JSON 파일을 읽어서
     * 선처리를 수행한 후, 존재하면 delete하고 import를 수행하고, 타입별 후처리 함수를 실행합니다.
     * </p>
     * 
     * <h3>처리 과정:</h3>
     * <ol>
     * <li>타입별 선처리 함수 실행 (Import 전)</li>
     * <li>migration/{projectId}/{type}/{id} 폴더 경로 생성</li>
     * <li>폴더 내 모든 .json 파일 읽기</li>
     * <li>각 파일에 대해 존재 여부 확인 및 delete</li>
     * <li>파일 내용을 읽어서 import 수행</li>
     * <li>타입별 후처리 함수 실행 (Import 후)</li>
     * </ol>
     * 
     * @param projectId 프로젝트 ID
     * @param type      객체 타입 (ObjectType enum)
     * @param id        객체 ID
     * @return Import 및 선처리/후처리 결과 Map (preprocess, importSuccess, postprocess 포함)
     */
    Map<String, Object> importAndMigration(String projectId, ObjectType type, String id);

    /**
     * 운영 이행 관리 조회 (페이지네이션)
     * 
     * @param request 조회 요청 DTO
     * @return 페이지네이션된 운영 이행 목록
     */
    PageResponse<MigMasRes> searchMigMas(MigMasSearchReq request);

    /**
     * 운영 이행 관리 조회 (조인 결과)
     * 
     * <p>
     * GPO_MIG_MAS와 GPO_MIG_ASST_MAP_MAS를 조인하여 모든 정보를 조회합니다.
     * </p>
     * 
     * @param request 조회 요청 DTO
     * @return 조인된 운영 이행 목록
     */
    List<MigMasWithMapRes> findAllMigMasWithMap(MigMasAsstSearchReq request);
}
