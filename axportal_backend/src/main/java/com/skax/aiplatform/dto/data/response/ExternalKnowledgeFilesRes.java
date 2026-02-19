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
public class ExternalKnowledgeFilesRes {
	private PageResponse<Item> page;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Item {
		private String docPathAnony;
		private Long docCount;
		private String topIndex;
		private String topId;
		private Double topScore;
			private Object topSource; // _source 전체, 단 임베딩 필드는 서비스에서 제거
	}
}
