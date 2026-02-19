package com.skax.aiplatform.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 메뉴 경로 및 리소스 타입 매핑 담당 클래스
 * 
 * <p>API 엔드포인트를 메뉴 구조에 맞게 매핑하고 리소스 타입을 추출합니다.</p>
 * 
 * @author sonmunwoo
 * @since 2025-10-19
 * @version 1.0.0
 */
@Slf4j
@Component
public class MenuPathResolver {

    /**
     * 메서드명에서 액션 추출 (HTTP 메서드 형태로 매핑)
     */
    public String getActionFromMethod(String methodName, String targetAsset) {
        // targetAsset에 "byid"가 포함되어 있으면 GET_DETAIL 반환
        if (targetAsset != null && targetAsset.toLowerCase().contains("byid")) {
            return "GET_DETAIL";
        }
        
        // 실제 HTTP 메서드 우선 확인
        String httpMethod = getActualHttpMethod();
        if (httpMethod != null) {
            switch (httpMethod.toUpperCase()) {
                case "GET": return "GET";
                case "POST": return "POST";
                case "PUT": return "PUT";
                case "DELETE": return "DELETE";
                case "PATCH": return "PATCH";
                default: break;
            }
        }
        
        // HTTP 메서드를 직접 파악할 수 없는 경우 메서드명으로 추론
        if (methodName.startsWith("get") || methodName.startsWith("find") || methodName.startsWith("retrieve")) {
            return "GET";
        } else if (methodName.startsWith("create") || methodName.startsWith("save") || methodName.startsWith("add")) {
            return "POST";
        } else if (methodName.startsWith("update") || methodName.startsWith("modify") || methodName.startsWith("edit")) {
            return "PUT";
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return "DELETE";
        } else if (methodName.startsWith("invoke") || methodName.startsWith("execute") || methodName.startsWith("run")) {
            return "POST";
        }
        return "API_CALL";
    }

    /**
     * Controller 메서드명에서 액션 추출
     */
    public String getActionFromControllerMethod(String methodName, String targetAsset) {
        // targetAsset에 "byid"가 포함되어 있으면 GET_DETAIL 반환
        if (targetAsset != null && targetAsset.toLowerCase().contains("byid")) {
            return "GET_DETAIL";
        }
        
        // 실제 HTTP 메서드 우선 확인
        String httpMethod = getActualHttpMethod();
        if (httpMethod != null) {
            switch (httpMethod.toUpperCase()) {
                case "GET": return "GET";
                case "POST": return "POST";
                case "PUT": return "PUT";
                case "DELETE": return "DELETE";
                case "PATCH": return "PATCH";
                default: break;
            }
        }
        
        // HTTP 메서드를 직접 파악할 수 없는 경우 메서드명으로 추론
        if (methodName.startsWith("get") || methodName.startsWith("find") || 
            methodName.startsWith("retrieve") || methodName.startsWith("list") || 
            methodName.startsWith("search")) {
            return "GET";
        } else if (methodName.startsWith("create") || methodName.startsWith("save") || 
                   methodName.startsWith("add") || methodName.startsWith("register") || 
                   methodName.startsWith("post")) {
            return "POST";
        } else if (methodName.startsWith("update") || methodName.startsWith("modify") || 
                   methodName.startsWith("edit") || methodName.startsWith("change") || 
                   methodName.startsWith("put") || methodName.startsWith("patch")) {
            return "PUT";
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return "DELETE";
        } else if (methodName.startsWith("login") || methodName.startsWith("logout") || 
                   methodName.startsWith("authenticate")) {
            return "POST";
        } else if (methodName.startsWith("upload") || methodName.startsWith("download")) {
            return methodName.startsWith("upload") ? "POST" : "GET";
        } else if (methodName.startsWith("export") || methodName.startsWith("import")) {
            return methodName.startsWith("export") ? "GET" : "POST";
        }
        return "API_REQUEST";
    }

    /**
     * 클라이언트명에서 리소스 타입 추출
     */
    public String getResourceTypeFromClient(String className) {
        String clientName = className.toLowerCase();
        
        // 에이전트 관련
        if (clientName.contains("agent")) {
            if (clientName.contains("builder")) return "agentBuilder";
            else if (clientName.contains("tools")) return "agentTools";
            else if (clientName.contains("eval")) return "agentEval";
            return "agentBuilder";
        }
        
        // 모델 관련
        else if (clientName.contains("model")) {
            if (clientName.contains("catalog") || clientName.contains("ctlg")) return "modelCtlg";
            else if (clientName.contains("eval")) return "modelEval";
            else if (clientName.contains("deploy")) return "modelDeploy";
            else if (clientName.contains("finetun")) return "fineTuning";
            return "modelCtlg";
        }
        
        // 데이터 관련
        else if (clientName.contains("data")) {
            if (clientName.contains("storage") || clientName.contains("stor")) return "dataStor";
            else if (clientName.contains("catalog") || clientName.contains("ctlg")) return "dataCtlg";
            else if (clientName.contains("tools")) return "dataTools";
            return "dataStor";
        }
        
        // 인증 관련
        else if (clientName.contains("auth")) return "auth";
        
        // 지식 관련
        else if (clientName.contains("knowledge") || clientName.contains("queries")) return "dataCtlg";
        
        // 평가 관련
        else if (clientName.contains("evaluation") || clientName.contains("eval")) return "modelEval";
        
        // 프롬프트 관련
        else if (clientName.contains("prompt")) {
            if (clientName.contains("infer")) return "inferPrompt";
            else if (clientName.contains("fewshot")) return "fewShot";
            return "inferPrompt";
        }
        
        // 기본값
        return "common";
    }

    /**
     * Controller 클래스명에서 리소스 타입 추출
     */
    public String getResourceTypeFromController(String className) {
        String controllerName = className.replace("Controller", "").toLowerCase();
        
        // URL 경로 기반 매핑 (우선순위 높음)
        String resourceTypeFromUrl = getResourceTypeFromUrl();
        if (resourceTypeFromUrl != null) {
            return resourceTypeFromUrl;
        }
        
        // 권한 관련
        if (controllerName.contains("auth") || controllerName.contains("login")) return "auth";
        else if (controllerName.contains("user") && !controllerName.contains("usage")) return "users";
        else if (controllerName.contains("group")) return "groups";
        
        // 홈 관련
        else if (controllerName.contains("modelgarden")) return "modelGarden";
        else if (controllerName.contains("project")) return "project";
        
        // 데이터 관련
        else if (controllerName.contains("datastor") || controllerName.contains("datastorage")) return "dataStor";
        else if (controllerName.contains("datactlg") || controllerName.contains("datacatalog")) return "dataCtlg";
        else if (controllerName.contains("datatool")) return "dataTools";
        
        // 모델 관련
        else if (controllerName.contains("modelctlg") || controllerName.contains("modelcatalog")) return "modelCtlg";
        else if (controllerName.contains("modeleval") || controllerName.contains("modelevaluation")) return "modelEval";
        else if (controllerName.contains("finetuning") || controllerName.contains("finetune")) return "fineTuning";
        
        // 플레이그라운드
        else if (controllerName.contains("playground") || controllerName.contains("pg")) return "pg";
        
        // 프롬프트 관련
        else if (controllerName.contains("inferprompt")) return "inferPrompt";
        else if (controllerName.contains("fewshot")) return "fewShot";
        
        // 에이전트 관련
        else if (controllerName.contains("agentbuilder")) return "agentBuilder";
        else if (controllerName.contains("agenttools") || controllerName.contains("agenttool")) return "agentTools";
        else if (controllerName.contains("agenteval") || controllerName.contains("agentevaluation")) return "agentEval";
        
        // 배포 관련
        else if (controllerName.contains("modeldeploy")) return "modelDeploy";
        else if (controllerName.contains("agentdeploy")) return "agentDeploy";
        else if (controllerName.contains("apikey")) return "apiKey";
        
        // 관리 관련
        else if (controllerName.contains("usermgmt") || controllerName.contains("usermanagement")) return "userMgmt";
        else if (controllerName.contains("rolemgmt") || controllerName.contains("rolemanagement")) return "roleMgmt";
        else if (controllerName.contains("groupmgmt") || controllerName.contains("groupmanagement")) return "groupMgmt";
        else if (controllerName.contains("approvmgmt") || controllerName.contains("approvalmanagement")) return "approvMgmt";
        else if (controllerName.contains("resrcmgmt") || controllerName.contains("resourcemanagement")) return "resrcMgmt";
        else if (controllerName.contains("userusage") || controllerName.contains("userusagemgmt")) return "userUsageMgmt";
        else if (controllerName.contains("serviceusage") || controllerName.contains("serviceusagemgmt")) return "serviceUsageMgmt";
        else if (controllerName.contains("secmgmt") || controllerName.contains("securitymanagement")) return "secMgmt";
        else if (controllerName.contains("noticemgmt") || controllerName.contains("noticemanagement")) return "noticeMgmt";
        
        // 공통 관련
        else if (controllerName.contains("notice")) return "notice";
        else if (controllerName.contains("eapproval")) return "eApproval";
        
        // 프로젝트 관리 관련
        else if (controllerName.contains("projectmgmt") || controllerName.contains("projectmanagement")) return "project";
        
        // 기본값
        return "common";
    }

    /**
     * URL 경로에서 리소스 타입 추출
     */
    private String getResourceTypeFromUrl() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String uri = request.getRequestURI().toLowerCase();
                
                // 파인튜닝 관련
                if (uri.contains("/finetuning/")) return "fineTuning";
                
                // 모델 배포 관련 (우선순위 높음)
                if (uri.contains("/modeldeploy") || uri.contains("/model-deploy")) return "modelDeploy";
                
                // 모델 관련
                if (uri.contains("/model/")) {
                    if (uri.contains("/catalog") || uri.contains("/ctlg")) return "modelCtlg";
                    else if (uri.contains("/eval")) return "modelEval";
                    else if (uri.contains("/deploy")) return "modelDeploy";
                    return "modelCtlg";
                }
                
                // 데이터 관련
                if (uri.contains("/data/")) {
                    if (uri.contains("/storage") || uri.contains("/stor")) return "dataStor";
                    else if (uri.contains("/catalog") || uri.contains("/ctlg")) return "dataCtlg";
                    else if (uri.contains("/tools")) return "dataTools";
                    return "dataStor";
                }
                
                // 데이터 도구 관련 (우선순위 높음)
                if (uri.contains("/datatool/")) return "dataTools";
                
                // 에이전트 관련
                if (uri.contains("/agent/")) {
                    if (uri.contains("/builder")) return "agentBuilder";
                    else if (uri.contains("/tools")) return "agentTools";
                    else if (uri.contains("/eval")) return "agentEval";
                    return "agentBuilder";
                }
                
                // 프롬프트 관련
                if (uri.contains("/prompt/")) {
                    if (uri.contains("/infer") || uri.contains("/inferprompt")) return "inferPrompt";
                    else if (uri.contains("/fewshot")) return "fewShot";
                    return "inferPrompt";
                }
                
                // 프로젝트 관련
                if (uri.contains("/project")) return "project";
                
                // 배포 관련
                if (uri.contains("/modeldeploy")) return "modelDeploy";
                else if (uri.contains("/agentdeploy")) return "agentDeploy";
                else if (uri.contains("/deploy/")) {
                    if (uri.contains("/model")) return "modelDeploy";
                    else if (uri.contains("/agent")) return "agentDeploy";
                    return "modelDeploy";
                }
                
                // 관리 관련
                if (uri.contains("/admin/")) {
                    if (uri.contains("/user-usage-mgmt")) return "userUsageMgmt";
                    else if (uri.contains("/service-usage-mgmt")) return "serviceUsageMgmt";
                    else if (uri.contains("/notice-mgmt") || uri.contains("/notices")) return "noticeMgmt";
                    else if (uri.contains("/user-mgmt")) return "userMgmt";
                    else if (uri.contains("/role-mgmt")) return "roleMgmt";
                    else if (uri.contains("/group-mgmt")) return "groupMgmt";
                    return "userMgmt";
                }
                
                // 권한 관련
                if (uri.contains("/auth/")) {
                    if (uri.contains("/login")) return "login";
                    else if (uri.contains("/logout")) return "logout";
                    return "auth";
                }
                
                // 공지사항 관련
                if (uri.contains("/notice")) return "notice";
            }
        } catch (ClassCastException e) {
            log.debug("URL에서 리소스 타입 추출 실패 (ClassCastException): {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.debug("URL에서 리소스 타입 추출 실패 (IllegalStateException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.debug("URL에서 리소스 타입 추출 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("URL에서 리소스 타입 추출 실패 (RuntimeException): {}", e.getMessage());
        }
        return null;
    }

    /**
     * 프론트엔드 URL 경로를 한글 메뉴명으로 변환
     * 
     * <p>'/' 기준으로 경로를 분리하여 1단계, 2단계 메뉴를 각각 매핑합니다.
     * 매핑이 없는 경우 영문을 그대로 사용합니다.</p>
     * 
     * @param frontendPath 프론트엔드 URL 경로 (예: "/home/dashboard", "/admin/noticeMgmt")
     * @return 한글 메뉴명 (예: "홈 > 대시보드", "관리 > 공지사항관리") 또는 부분 매핑 결과
     */
    public String convertFrontendPathToKorean(String frontendPath) {
        if (frontendPath == null || frontendPath.isEmpty()) {
            return frontendPath;
        }
        
        // URL 경로를 소문자로 변환
        String path = frontendPath.toLowerCase().trim();
        
        // 루트 경로("/") 또는 빈 경로는 null 반환
        if (path.equals("/") || path.equals("") || path.equals("/index.html")) {
            log.debug("루트 경로는 메뉴 경로로 저장하지 않음: {}", frontendPath);
            return null;
        }
        
        // '/'로 경로 분리
        String[] pathSegments = path.split("/");
        StringBuilder result = new StringBuilder();
        int segmentCount = 0; // 처리된 세그먼트 개수
        
        for (int i = 0; i < pathSegments.length; i++) {
            if (pathSegments[i].isEmpty()) continue;
            
            // 1단계(1deps)와 2단계(2deps)까지만 처리
            if (segmentCount >= 2) {
                break;
            }
            
            String segment = pathSegments[i];
            String mappedSegment;
            
            // 첫 번째 세그먼트는 1단계 메뉴 매핑 시도
            if (result.length() == 0) {
                mappedSegment = mapFirstLevelMenu(segment);
            } else {
                // 두 번째 이후는 2단계 메뉴 매핑 시도
                mappedSegment = mapSecondLevelMenu(segment);
            }
            
            if (result.length() > 0) {
                result.append(" > ");
            }
            result.append(mappedSegment);
            segmentCount++;
        }
        
        String finalResult = result.toString();
        log.debug("메뉴 경로 변환: {} → {}", frontendPath, finalResult);
        return finalResult;
    }
    
    /**
     * 1단계 메뉴 세그먼트를 한글로 매핑
     * 매핑 없으면 영문 그대로 반환
     */
    private String mapFirstLevelMenu(String segment) {
        String lower = segment.toLowerCase();
        
        switch (lower) {
            case "home": return "홈";
            case "data": return "데이터";
            case "model": return "모델";
            case "training": return "학습";
            case "deploy": return "배포";
            case "admin": return "관리";
            case "log": return "로그";
            case "auth": return "권한";
            case "prompt": return "프롬프트";
            case "agent": return "에이전트";
            case "playground": 
            case "pg": return "플레이그라운드";
            case "notice": return "공통";
            case "eapproval":
            case "approval": return "공통";
            case "alarm": return "공통";
            case "eval":
            case "evaluation": return "평가";
            case "ide": return "IDE";
            case "resource": return "자원관리";
            case "login": return "로그인";
            case "logout": return "로그아웃";
            default:
                // 매핑 없으면 원본 영문 그대로 반환
                log.debug("1단계 메뉴 매핑 없음, 영문 사용: {}", segment);
                return segment;
        }
    }
    
    /**
     * 2단계 메뉴 세그먼트를 한글로 매핑
     * 매핑 없으면 영문 그대로 반환
     */
    private String mapSecondLevelMenu(String segment) {
        String lower = segment.toLowerCase();
        
        switch (lower) {
            // 홈 하위
            case "dashboard": return "대시보드";
            case "modelgarden": return "모델가든";
            case "project": return "프로젝트";
            
            // 권한 하위
            case "login": return "로그인";
            case "logout": return "로그아웃";
            case "signup": return "회원가입";
            case "user": return "회원가입";
            case "group": return "그룹관리";
            
            // 데이터 하위
            case "storage":
            case "datastorage":
            case "datastor": return "데이터 저장소";
            case "catalog":
            case "datacatalog":
            case "datactlg": return "데이터 카탈로그";
            case "tool":
            case "datatool": return "데이터도구";
            case "dataset": return "데이터세트";
            case "knowledge": return "지식베이스";
            case "structured": return "정형데이터";
            case "unstructured": return "비정형데이터";
            case "upload": return "업로드";
            case "rag": return "RAG";
            
            // 모델 하위
            case "eval": return "모델 평가";
            case "finetuning":
            case "finetune": return "파인튜닝";
            case "garden": return "모델가든";
            
            // 프롬프트 하위
            case "infer": return "추론 프롬프트";
            case "fewshot": return "퓨샷";
            case "guardrail": return "가드레일";
            case "workflow": return "워크플로우";
            
            // 에이전트 하위
            case "builder": return "에이전트 빌더";
            case "mcp": return "MCP서버";
            
            // 배포 하위
            case "model": return "모델 배포";
            case "agent":
            case "agentdeploy": return "에이전트 배포";
            case "apikey":
            case "api-key":  return "API Key";
            case "api-key-mgmt": return "API Key 관리";
            case "safety":
            case "safetyfilter": return "안전필터";
            
            // 관리 하위
            case "user-mgmt":
            case "usermanagement": return "사용자 관리";
            case "role-mgmt":
            case "rolemanagement": return "역할 관리";
            case "group-mgmt":
            case "groupmanagement": return "그룹 관리";
            case "approval-mgmt":
            case "approvalmanagement": return "권한승인 관리";
            case "resource-mgmt":
            case "resourcemanagement": return "자원 관리";
            case "user-usage":
            case "userusage": return "사용자 이용 현황";
            case "service-usage":
            case "serviceusage": return "서비스 이용 현황";
            case "security-mgmt":
            case "securitymanagement": return "보안 관리";
            case "notice-mgmt":
            case "noticemanagement":
            case "noticemgmt": return "공지사항관리";
            case "project-mgmt":
            case "projectmanagement": return "프로젝트 관리";
            
            // 로그 하위
            case "history": return "이력";
            
            // 공통 하위
            case "notice": return "공지사항";
            case "eapproval":
            case "approval": return "전자결재";
            case "alarm": return "알림";
            
            // 기타
            case "preset": return "프리셋 관리";
            
            default:
                // 매핑 없으면 원본 영문 그대로 반환
                log.debug("2단계 메뉴 매핑 없음, 영문 사용: {}", segment);
                return segment;
        }
    }

    /**
     * 실제 HTTP 메서드 파악
     */
    private String getActualHttpMethod() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getMethod();
            }
        } catch (ClassCastException e) {
            log.debug("HTTP 메서드 파악 실패 (ClassCastException): {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.debug("HTTP 메서드 파악 실패 (IllegalStateException): {}", e.getMessage());
        } catch (NullPointerException e) {
            log.debug("HTTP 메서드 파악 실패 (NullPointerException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.debug("HTTP 메서드 파악 실패 (RuntimeException): {}", e.getMessage());
        }
        return null;
    }
}

