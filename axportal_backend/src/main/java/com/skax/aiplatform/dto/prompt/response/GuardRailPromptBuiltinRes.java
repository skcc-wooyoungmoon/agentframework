package com.skax.aiplatform.dto.prompt.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardRailPromptBuiltinRes {
    
    private List<BuiltinPrompt> data;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuiltinPrompt {
        private String name;
        private String uuid;
        private List<Message> messages;
        private List<Variable> variables;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private int mtype;
        private String message;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Variable {
        private String variable;
        private boolean validationFlag;
        private String validation;
        private boolean tokenLimitFlag;
        private int tokenLimit;
    }
}

