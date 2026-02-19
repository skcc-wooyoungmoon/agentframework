package com.skax.aiplatform.dto.model.request;

public class FineTuningEnum {
   
    
    public enum tuningType {
        QLoRa("QLoRa"),
        LoRa("LoRa"),
        Full("Full");

        private final String value;

        tuningType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    
    public enum modelType {
        MT01("생성"),
        MT02("분류"),
        MT03("기타"),
        MT04("회귀");

        private final String value;

        modelType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

}