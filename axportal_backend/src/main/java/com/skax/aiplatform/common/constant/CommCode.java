package com.skax.aiplatform.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * 공통 코드관리를 다음과 같이 간소화 해서 사용합니다.
 * 아래 주석에는 사용하는 방법에 대한 간략한 예시가 있습니다.
 * 다양한 방식으로 사용 하세요
 */
public final class CommCode {

    private CommCode() {
        // 유틸리티 클래스는 인스턴스화 방지
    }

    /**
     * 사용자 재직 상태 사용 예시
     * WorkStatus workStatus = WorkStatus.ACTIVE;
     * String description = workStatus.getDescription(); // "재직" 반환
     */
    @Getter
    @RequiredArgsConstructor
    public enum WorkStatus {
        EMPLOYED("재직"),
        RESIGNED("퇴사");

        private final String description;
    }

    public enum AuthorityStatus {
        ACTIVE,    // 활성화
        INACTIVE,  // 비활성화
        DEPRECATED // 폐기됨
    }
    /**
     * 권한 타입
     */
    public enum AuthorityType {
        SYSTEM,    // 시스템 권한
        PROJECT,   // 프로젝트 권한
        MODEL,     // 모델 권한
        AGENT,     // 에이전트 권한
        DATA,      // 데이터 권한
        USER,      // 사용자 권한
        CUSTOM     // 커스텀 권한
    }

    /**
     * 프로젝트 상태 사용 예시
     * ProjectStatus status = ProjectStatus.IN_PROGRESS;
     * String description = status.getDescription(); // "진행중" 반환
     */
    @Getter
    @RequiredArgsConstructor
    public enum ProjectStatus {
        ONGOING("진행중"),
        COMPLETED("종료");

        private final String description;
    }

    /**
     * 역할 유형 사용 예시
     * CommCode.RoleType roleType = CommCode.RoleType.DEFAULT;
     * String description = roleType.getDescription(); // "사전 정의" 반환
     */
    @Getter
    @RequiredArgsConstructor
    public enum RoleType {
        DEFAULT("사전 정의"),
        CUSTOM("사용자 정의");

        private final String description;
    }

    /**
     * 역할 상태 사용 예시
     * CommCode.RoleStatus status = CommCode.RoleStatus.ACTIVE;
     * String statusDesc = status.getDescription(); // "사용중" 반환
     */
    @Getter
    @RequiredArgsConstructor
    public enum RoleStatus {
        ACTIVE("사용중"),
        INACTIVE("미사용");

        private final String description;
    }

    /**
     * 공통 활성 상태 enum
     * 활성 상태 사용 예시
     * CommCode.ActiveStatus activeStatus = CommCode.ActiveStatus.PENDING;
     * String activeDesc = activeStatus.getDescription(); // "대기" 반환
     */
    @Getter
    @RequiredArgsConstructor
    public enum ActiveStatus {
        ACTIVE("활성"),
        INACTIVE("비활성"),
        PENDING("대기"),
        DELETED("삭제됨");

        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum UserStatus {
        EMPLOY("재직"),
        RETIRE("퇴사");
        private final String description;
    }

    /**
     * 엔티티 상태 enum
     * if (entity.getStatus() == CommCode.EntityStatus.DELETED) {
     * // 삭제된 엔티티 처리 로직
     * }
     */
    @Getter
    @RequiredArgsConstructor
    public enum EntityStatus {
        CREATED("생성됨"),
        UPDATED("수정됨"),
        DELETED("삭제됨"),
        ARCHIVED("보관됨");

        private final String description;
    }

    /**
     * 처리 상태 enum 아래와 같이 사용도 가능함
     * import static com.skax.aiplatform.common.constant.CommCode.ProcessStatus.*;
     * switch (processStatus) {
     * case PENDING:
     */
    @Getter
    @RequiredArgsConstructor
    public enum ProcessStatus {
        PENDING("대기"),
        PROCESSING("처리중"),
        COMPLETED("완료"),
        FAILED("실패"),
        CANCELED("취소됨");

        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum AlarmStatus {
        COMPLETED("완료"),
        APPROVAL("승인"),
        REJECT("반려"),
        FAILED("실패"),
        CANCELED("취소됨"),
        REQUEST("요청");
        private final String description;
    }

}
