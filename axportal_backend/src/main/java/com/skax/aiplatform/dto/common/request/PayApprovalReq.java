package com.skax.aiplatform.dto.common.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayApprovalReq {
    private ApprovalInfo approvalInfo;
    private ApprovalTypeInfo approvalTypeInfo;
    private DisplayInfo displayInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalInfo {
        private String memberId;
        private String approvalType;
        private String approvalUniqueKey;
        private Integer approvalParamKey;
        private String approvalParamValue;
        private String afterProcessParamString;
        private String approvalItemString;
        private String approvalSummary;
        private Integer maxApprovalCount;
        private Integer currentApprovalCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalTypeInfo {
        private String typeNm;
        private List<ApprovalTarget> approvalTarget;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisplayInfo {
        private String typeNm; // 업무구분
        private String jkwNm; // 이름
        private String deptNm; // 부서
        private String prjNm; // 프로젝트명
        private String prjRoleNm; // 역할
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalTarget {
        private String prjNm;
        private String roleNm;
        private Long prjSeq;
        private Long roleSeq;
    }
}

