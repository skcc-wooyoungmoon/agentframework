package com.skax.aiplatform.client.ione.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * API VO (상세 정보)
 * 
 * @author system
 * @since 2025-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntfApiVo {
    
    private String apiSysId;
    private String apiId;
    private String apiName;
    private String apiDesc;
    private String taskId;
    private String taskName;
    private String apiGtwPath;
    private String useStartDatetime;
    private String useEndDatetime;
    private String apiSvcPathTyp;
    private String apiSvcPathSetPath;
    private String apiSvcPathStripPrefix;
    private String apiSvcPathRewritePathFrom;
    private String apiSvcPathRewritePathTo;
    private String prefixPath;
    private List<KeyValuePair> routeCookieList;
    private List<KeyValuePair> routeHeaderList;
    private List<KeyValuePair> routeQueryParamList;
    private List<Object> routeMethodList;
    private List<Object> routeHostList;
    private String routePathPattern;
    private String routePathPatternIsDeny;
    private List<Object> routeRemoteAddressList;
    private String routeRemoteAddressEnableXff;
    private List<KeyValuePair> mediationSetRequestHeaderList;
    private List<KeyValuePair> mediationSetResponseHeaderList;
    private List<KeyValuePair> mediationAddRequestHeaderList;
    private List<KeyValuePair> mediationAddResponseHeaderList;
    private List<KeyValuePair> mediationAddRequestParameterList;
    private List<KeyValuePair> mediationMapRequestHeaderList;
    private List<Object> mediationRemoveRequestHeaderList;
    private List<Object> mediationRemoveRequestParameterList;
    private List<Object> mediationRemoveResponseHeaderList;
    private String mediationEnableFallbackHeader;
    private List<Object> mediationDedupeResponseHeaderList;
    private String apiSvcUrl;
    private String trafficRequestSize;
    private String trafficConnectTimeout;
    private String trafficResponseTimeout;
    private String trafficRateLimiterTyp;
    private String trafficRateLimiterCapacity;
    private String trafficRateLimiterRefillTokens;
    private String trafficRateLimiterRefillDurationMs;
    private String trafficRateLimiterIp;
    private String trafficRateLimiterPath;
    private String trafficRateLimiterIsXFF;
    private String trafficRateLimiterDenyEmptyKey;
    private String trafficRateLimiterHeaderKey;
    private String trafficRateLimiterHeaderValue;
    private String trafficRateLimiterQueryKey;
    private String trafficRateLimiterQueryValue;
    private String errorRetriesCount;
    private List<Object> errorRetriesMethods;
    private List<Object> errorRetriesSeriesList;
    private List<Object> errorRetriesStatusList;
    private String errorRetriesFirstBackoff;
    private String errorRetriesMaxBackoff;
    private String errorRetriesFactor;
    private String errorRetriesBasedOnPreviousValue;
    private String errorNoticeWaitTime;
    private List<Object> errorNoticeStatusList;
    private String errorCctBrkId;
    private String errorCctBrkFallbackApiSysId;
    private String errorCctBrkFallbackApiId;
    private List<Object> errorCctBrkStatusList;
    private String cachePathPatterns;
    private String cacheExpireTime;
    private String authTyp;
    private String authCondTyp;
    private String authCondItem;
    private String authCondItemKey;
    private String authCondItemValue;
    private List<Object> authChnlList;
    private String apiSvcGrpSysId;
    private String apiSvcGrpId;
    private String apiVerSysId;
    private String apiVerId;
    private String apiSvcGrpDesc;
    private String apiSvrGrpSysId;
    private String apiSvrGrpId;
    private String apiSvrGrpDesc;
    private String apiSvrIp;
    private String createDate;
    private String creator;
    private String modifyDate;
    private String modifier;
}
