package com.skax.aiplatform.dto.home.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeImage {
    private String type;
    private String name;
    private String desc;
    private String id;
}
