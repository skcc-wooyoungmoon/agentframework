package com.skax.aiplatform.client.ione.api.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 정보 결과
 * 
 * @author system
 * @since 2025-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiInfoResult {
    
    @JsonProperty("apiSysId")
    private String apiSysId;
    
    @JsonProperty("apiId")
    private String apiId;
    
    @JsonProperty("apiName")
    private String apiName;
    
    @JsonProperty("apiDesc")
    private String apiDesc;
    
    @JsonProperty("taskId")
    private String taskId;
    
    @JsonProperty("taskName")
    private String taskName;
    
    @JsonProperty("apiGtwPath")
    private String apiGtwPath;
    
    @JsonProperty("useStartDatetime")
    private String useStartDatetime;
    
    @JsonProperty("useEndDatetime")
    private String useEndDatetime;
    
    @JsonProperty("apiSvcPathTyp")
    private String apiSvcPathTyp;
    
    @JsonProperty("apiSvcPathSetPath")
    private String apiSvcPathSetPath;
    
    @JsonProperty("apiSvcPathStripPrefix")
    private String apiSvcPathStripPrefix;
    
    @JsonProperty("apiSvcPathRewritePathFrom")
    private String apiSvcPathRewritePathFrom;
    
    @JsonProperty("apiSvcPathRewritePathTo")
    private String apiSvcPathRewritePathTo;
    
    @JsonProperty("prefixPath")
    private String prefixPath;
    
    @JsonProperty("routeCookieList")
    private List<KeyValuePair> routeCookieList;
    
    @JsonProperty("routeHeaderList")
    private List<KeyValuePair> routeHeaderList;
    
    @JsonProperty("routeQueryParamList")
    private List<KeyValuePair> routeQueryParamList;
    
    @JsonProperty("routeMethodList")
    private List<MethodItem> routeMethodList;
    
    @JsonProperty("routeHostList")
    private List<HostItem> routeHostList;
    
    @JsonProperty("routePathPattern")
    private String routePathPattern;
    
    @JsonProperty("routePathPatternIsDeny")
    private Boolean routePathPatternIsDeny;
    
    @JsonProperty("routeRemoteAddressList")
    private List<RemoteAddressItem> routeRemoteAddressList;
    
    @JsonProperty("routeRemoteAddressEnableXff")
    private Boolean routeRemoteAddressEnableXff;
    
    @JsonProperty("mediationSetRequestHeaderList")
    private List<KeyValuePair> mediationSetRequestHeaderList;
    
    @JsonProperty("mediationSetResponseHeaderList")
    private List<KeyValuePair> mediationSetResponseHeaderList;
    
    @JsonProperty("mediationAddRequestHeaderList")
    private List<KeyValuePair> mediationAddRequestHeaderList;
    
    @JsonProperty("mediationAddResponseHeaderList")
    private List<KeyValuePair> mediationAddResponseHeaderList;
    
    @JsonProperty("mediationAddRequestParameterList")
    private List<KeyValuePair> mediationAddRequestParameterList;
    
    @JsonProperty("mediationMapRequestHeaderList")
    private List<KeyValuePair> mediationMapRequestHeaderList;
    
    @JsonProperty("mediationRemoveRequestHeaderList")
    private List<RemoveItem> mediationRemoveRequestHeaderList;
    
    @JsonProperty("mediationRemoveRequestParameterList")
    private List<RemoveItem> mediationRemoveRequestParameterList;
    
    @JsonProperty("mediationRemoveResponseHeaderList")
    private List<RemoveItem> mediationRemoveResponseHeaderList;
    
    @JsonProperty("mediationEnableFallbackHeader")
    private Boolean mediationEnableFallbackHeader;
    
    @JsonProperty("mediationDedupeResponseHeaderList")
    private List<RemoveItem> mediationDedupeResponseHeaderList;
    
    @JsonProperty("apiSvcUrl")
    private String apiSvcUrl;
    
    @JsonProperty("trafficRequestSize")
    private String trafficRequestSize;
    
    @JsonProperty("trafficConnectTimeout")
    private String trafficConnectTimeout;
    
    @JsonProperty("trafficResponseTimeout")
    private String trafficResponseTimeout;
    
    @JsonProperty("trafficRateLimiterTyp")
    private String trafficRateLimiterTyp;
    
    @JsonProperty("trafficRateLimiterCapacity")
    private String trafficRateLimiterCapacity;
    
    @JsonProperty("trafficRateLimiterRefillTokens")
    private String trafficRateLimiterRefillTokens;
    
    @JsonProperty("trafficRateLimiterRefillDurationMs")
    private String trafficRateLimiterRefillDurationMs;
    
    @JsonProperty("trafficRateLimiterIp")
    private String trafficRateLimiterIp;
    
    @JsonProperty("trafficRateLimiterPath")
    private String trafficRateLimiterPath;
    
    @JsonProperty("trafficRateLimiterIsXFF")
    private Boolean trafficRateLimiterIsXFF;
    
    @JsonProperty("trafficRateLimiterDenyEmptyKey")
    private Boolean trafficRateLimiterDenyEmptyKey;
    
    @JsonProperty("trafficRateLimiterHeaderKey")
    private String trafficRateLimiterHeaderKey;
    
    @JsonProperty("trafficRateLimiterHeaderValue")
    private String trafficRateLimiterHeaderValue;
    
    @JsonProperty("trafficRateLimiterQueryKey")
    private String trafficRateLimiterQueryKey;
    
    @JsonProperty("trafficRateLimiterQueryValue")
    private String trafficRateLimiterQueryValue;
    
    @JsonProperty("errorRetriesCount")
    private String errorRetriesCount;
    
    @JsonProperty("errorRetriesMethods")
    private List<MethodItem> errorRetriesMethods;
    
    @JsonProperty("errorRetriesSeriesList")
    private List<SeriesItem> errorRetriesSeriesList;
    
    @JsonProperty("errorRetriesStatusList")
    private List<StatusItem> errorRetriesStatusList;
    
    @JsonProperty("errorRetriesFirstBackoff")
    private String errorRetriesFirstBackoff;
    
    @JsonProperty("errorRetriesMaxBackoff")
    private String errorRetriesMaxBackoff;
    
    @JsonProperty("errorRetriesFactor")
    private String errorRetriesFactor;
    
    @JsonProperty("errorRetriesBasedOnPreviousValue")
    private Boolean errorRetriesBasedOnPreviousValue;
    
    @JsonProperty("errorNoticeWaitTime")
    private String errorNoticeWaitTime;
    
    @JsonProperty("errorNoticeStatusList")
    private List<StatusItem> errorNoticeStatusList;
    
    @JsonProperty("errorCctBrkId")
    private String errorCctBrkId;
    
    @JsonProperty("errorCctBrkFallbackApiSysId")
    private String errorCctBrkFallbackApiSysId;
    
    @JsonProperty("errorCctBrkFallbackApiId")
    private String errorCctBrkFallbackApiId;
    
    @JsonProperty("errorCctBrkStatusList")
    private List<StatusItem> errorCctBrkStatusList;
    
    @JsonProperty("cachePathPatterns")
    private String cachePathPatterns;
    
    @JsonProperty("cacheExpireTime")
    private String cacheExpireTime;
    
    @JsonProperty("authTyp")
    private String authTyp;
    
    @JsonProperty("authCondTyp")
    private String authCondTyp;
    
    @JsonProperty("authCondItem")
    private String authCondItem;
    
    @JsonProperty("authCondItemKey")
    private String authCondItemKey;
    
    @JsonProperty("authCondItemValue")
    private String authCondItemValue;
    
    @JsonProperty("authChnlList")
    private List<ChannelItem> authChnlList;
    
    @JsonProperty("apiSvcGrpSysId")
    private String apiSvcGrpSysId;
    
    @JsonProperty("apiSvcGrpId")
    private String apiSvcGrpId;
    
    @JsonProperty("apiVerSysId")
    private String apiVerSysId;
    
    @JsonProperty("apiVerId")
    private String apiVerId;
    
    @JsonProperty("apiSvcGrpDesc")
    private String apiSvcGrpDesc;
    
    @JsonProperty("apiSvrGrpSysId")
    private String apiSvrGrpSysId;
    
    @JsonProperty("apiSvrGrpId")
    private String apiSvrGrpId;
    
    @JsonProperty("apiSvrGrpDesc")
    private String apiSvrGrpDesc;
    
    @JsonProperty("apiSvrIp")
    private String apiSvrIp;
    
    @JsonProperty("createDate")
    private String createDate;
    
    @JsonProperty("creator")
    private String creator;
    
    @JsonProperty("modifyDate")
    private String modifyDate;
    
    @JsonProperty("modifier")
    private String modifier;
    
    /**
     * HTTP Method 아이템
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MethodItem {
        @JsonProperty("method")
        private String method;
    }
    
    /**
     * Host 아이템
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HostItem {
        @JsonProperty("host")
        private String host;
    }
    
    /**
     * Remote Address 아이템
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RemoteAddressItem {
        @JsonProperty("remoteAddress")
        private String remoteAddress;
    }
    
    /**
     * 제거 아이템 (key만 포함)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RemoveItem {
        @JsonProperty("key")
        private String key;
    }
    
    /**
     * HTTP Status Series 아이템
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SeriesItem {
        @JsonProperty("series")
        private String series;
    }
    
    /**
     * HTTP Status 아이템
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatusItem {
        @JsonProperty("status")
        private String status;
    }
    
    /**
     * Channel 아이템
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChannelItem {
        @JsonProperty("chnlCode")
        private String chnlCode;
    }
}
