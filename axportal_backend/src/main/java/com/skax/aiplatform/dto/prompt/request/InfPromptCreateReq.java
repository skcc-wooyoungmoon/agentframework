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
public class InfPromptCreateReq {

    private String name;

    private String desc;

    private List<Message> messages;

    private String projectId;

    private boolean release;

    private List<Tag> tags;

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

        private String tag;

        private String versionId;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Variable {

        private String variable;

        private String validation;

        private boolean validationFlag;

        private boolean tokenLimitFlag;

        private int tokenLimit;

    }

}
