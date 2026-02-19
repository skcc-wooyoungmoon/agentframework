package com.skax.aiplatform.dto.data.response;

import com.skax.aiplatform.common.response.PageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalKnowledgeChunksRes {
	private PageResponse<Item> page;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Item {
		private String index;
		private String id;
		private Double score;
		private Object source;
	}
}
