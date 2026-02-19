package com.skax.aiplatform.dto.prompt.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GuardRailPromptCreateReq {

    @JsonProperty("name")
    private String name;

    @JsonProperty("desc")
    private String desc;

    @JsonProperty("messages")
    private List<Message> messages;

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("release")
    private boolean release;

    @JsonProperty("tags")
    private List<Tag> tags;

    @JsonProperty("variables")
    private List<Variable> variables;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {

        @JsonProperty("message")
        private String message;

        @JsonProperty("mtype")
        private int mtype;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tag {

        @JsonProperty("tag")
        private String tag;

        @JsonProperty("version_id")
        private String versionId;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Variable {

        @JsonProperty("variable")
        private String variable;

        @JsonProperty("validation")
        private String validation;

        @JsonProperty("validation_flag")
        private boolean validationFlag;

        @JsonProperty("token_limit_flag")
        private boolean tokenLimitFlag;

        @JsonProperty("token_limit")
        private int tokenLimit;

    }

}






