import { useState } from 'react';
import { UICode } from '@/components/UI/atoms/UICode';

import { UIButton2, UIToggle, UITypography, UIIcon2 } from '@/components/UI/atoms';
import { UIInput } from '@/components/UI/molecules/input';

import { UIArticle, UIFormField, UIGroup, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIList } from '@/components/UI/molecules';

import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';

import { UIUnitGroup } from '@/components/UI/molecules';
import { DesignLayout } from '../../components/DesignLayout';

export const DT_020101_P10: React.FC = () => {
  const [isLoaderDropdownOpen, setIsLoaderDropdownOpen] = useState(false);

  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음

  /* [251114_퍼블수정] 토글 영역 추가 */
  const [checked1, setChecked1] = useState(false);

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    //
  };

  const loaderOptions = [
    { value: 'RecursiveCharacter', label: 'RecursiveCharacter' },
    { value: 'RecursiveCharacter2', label: 'RecursiveCharacter2' },
    { value: 'RecursiveCharacter3', label: 'RecursiveCharacter3' },
  ];

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '지식 기본 설정' },
    { step: 2, label: '데이터 선택' },
    { step: 3, label: '선택 데이터 확인' },
    { step: 4, label: '청킹 설정' },
    { step: 5, label: '임베딩 설정' },
    { step: 6, label: '지식 등록' },
  ];

  // 지식 등록
  const [script, setScript] = useState(`from enum import Enum
    from typing import Any, Literal, Mapping, Optional, Union
    from uuid import UUID
    
    from langchain_core.embeddings import Embeddings
    from pydantic import BaseModel, ConfigDict, Field, HttpUrl, ValidationError
    
    
    class RetrievalMode(str, Enum):
        DENSE = "dense"
        SPARSE = "sparse"
        HYBRID = "hybrid"
        SEMANTIC = "semantic"
    
    
    class VectorDatabaseType(str, Enum):
        MILVUS = "Milvus"
        AZURE_AI_SEARCH = "AzureAISearch"
        OPENSEARCH = "OpenSearch"
        ELASTICSEARCH = "ElasticSearch"
    
    
    class BaseVectorDBInfo(BaseModel):
        type: VectorDatabaseType
    
        model_config = ConfigDict(extra="allow")  # Allow extra fields
    
    
    # ==============================
    # Connection info models for each VectorDB
    # ==============================
    class AzureAISearchInfo(BaseVectorDBInfo):
        type: Literal[VectorDatabaseType.AZURE_AI_SEARCH] = (
            VectorDatabaseType.AZURE_AI_SEARCH
        )
        endpoint: HttpUrl = Field(..., description="Azure AI Search endpoint URL")
        key: str = Field(..., description="Azure AI Search API key")
    
    
    class MilvusInfo(BaseVectorDBInfo):
        type: Literal[VectorDatabaseType.MILVUS] = VectorDatabaseType.MILVUS
        host: str = Field(..., description="Milvus host")
        port: str = Field(..., description="Milvus port")
        user: str = Field(..., description="Milvus username")
        password: str = Field(..., description="Milvus password")
        secure: str = Field(..., description="Use secure connection (true/false)")
        db_name: str = Field(..., description="Milvus database name")
    
    
    class OpenSearchInfo(BaseVectorDBInfo):
        type: Literal[VectorDatabaseType.OPENSEARCH] = VectorDatabaseType.OPENSEARCH
        endpoint: HttpUrl = Field(..., description="OpenSearch endpoint")
        user: str = Field(..., description="OpenSearch username")
        password: str = Field(..., description="OpenSearch password")
    
    
    class ElasticSearchInfo(BaseVectorDBInfo):
        type: Literal[VectorDatabaseType.ELASTICSEARCH] = VectorDatabaseType.ELASTICSEARCH
        endpoint: HttpUrl = Field(..., description="ElasticSearch endpoint")
        api_key: str = Field(..., description="ElasticSearch API key")
    
    
    VectorDBInfoUnion = Union[
        AzureAISearchInfo, MilvusInfo, OpenSearchInfo, ElasticSearchInfo
    ]
    
    
    class VectorDBInfo:
        """
        Wrapper for validated VectorDB connection info.
        The actual pydantic model (AzureAISearchInfo, etc.) is stored in .data.
        """
    
        def __init__(self, data: VectorDBInfoUnion):
            self.data = data
    
        @property
        def type(self) -> VectorDatabaseType:
            return self.data.type
    
        # Optional: type-safe accessors for each backend (raise error if wrong type)
        def as_azure(self) -> AzureAISearchInfo:
            if self.type != VectorDatabaseType.AZURE_AI_SEARCH:
                raise TypeError("VectorDB type is not 'azure_ai_search'")
            return self.data  # type: ignore[return-value]
    
        def as_milvus(self) -> MilvusInfo:
            if self.type != VectorDatabaseType.MILVUS:
                raise TypeError("VectorDB type is not 'milvus'")
            return self.data  # type: ignore[return-value]
    
        def as_opensearch(self) -> OpenSearchInfo:
            if self.type != VectorDatabaseType.OPENSEARCH:
                raise TypeError("VectorDB type is not 'opensearch'")
            return self.data  # type: ignore[return-value]
    
        def as_elasticsearch(self) -> ElasticSearchInfo:
            if self.type != VectorDatabaseType.ELASTICSEARCH:
                raise TypeError("VectorDB type is not 'elasticsearch'")
            return self.data  # type: ignore[return-value]
    
        @classmethod
        def from_any(cls, obj: Any) -> "VectorDBInfo":
            """
            Convert input into a standardized schema.
            Supports dict / Mapping / pydantic(v1/v2) / dataclass / normal object / to_dict()
            """
            payload: Optional[Mapping[str, Any]] = None
    
            if isinstance(obj, Mapping):
                payload = obj
            elif hasattr(obj, "to_dict") and callable(obj.to_dict):
                payload = obj.to_dict()
            elif hasattr(obj, "model_dump") and callable(obj.model_dump):  # pydantic v2
                payload = obj.model_dump()
            elif hasattr(obj, "dict") and callable(obj.dict):  # pydantic v1
                payload = obj.dict()
            elif hasattr(obj, "__dict__"):  # normal object / dataclass
                payload = vars(obj)
    
            if payload is None:
                raise TypeError("Cannot convert vectordb_info to dict.")
    
            # Mandatory: type check
            raw_type = payload.get("type")
            if raw_type is None:
                raise ValueError("'type' field is required in vectordb_info.")
    
            # Allow both string and Enum for type
            if isinstance(raw_type, str):
                try:
                    vtype = VectorDatabaseType(raw_type)
                except ValueError as e:
                    raise ValueError(f"Unsupported type: {raw_type!r}") from e
            elif isinstance(raw_type, VectorDatabaseType):
                vtype = raw_type
            else:
                raise TypeError(
                    f"'type' must be str or VectorDatabaseType (got: {type(raw_type)})"
                )
    
            # Validate strictly with the correct model
            try:
                if vtype == VectorDatabaseType.AZURE_AI_SEARCH:
                    model = AzureAISearchInfo(**payload)
                elif vtype == VectorDatabaseType.MILVUS:
                    model = MilvusInfo(**payload)
                elif vtype == VectorDatabaseType.OPENSEARCH:
                    model = OpenSearchInfo(**payload)
                elif vtype == VectorDatabaseType.ELASTICSEARCH:
                    model = ElasticSearchInfo(**payload)
                else:
                    raise AssertionError("unreachable")
            except ValidationError as e:
                raise ValueError(f"vectordb_info validation failed: {e}") from e
    
            return cls(model)
    
    
    class RetrievalOptions(BaseModel):
        retrieval_mode: RetrievalMode | None = Field(
            default=RetrievalMode.DENSE,
            title="Retrieval mode",
            description="Mode used for search. Note: 'semantic' can only be used with Azure AI Search.",
            examples=[RetrievalMode.DENSE],
        )
        top_k: int | None = Field(
            default=3,
            ge=1,
            title="Top K results",
            description="Return top K most relevant results.",
            examples=[3],
        )
        threshold: float | None = Field(
            default=None,
            ge=0.0,
            le=1.0,
            title="Vector search threshold",
            description="Return results with similarity above this threshold (0~1).",
            examples=[0.4],
        )
        filter: str | None = Field(
            default=None,
            title="Filter",
            description="Filter condition for search results. e.g. 'view_count gt 25'",
            examples=["view_count gt 25"],
        )
        file_ids: list[UUID] | None = Field(
            default=None,
            title="Target file ID list",
            description="Only search within documents corresponding to these file IDs.",
        )
    
        hybrid_dense_ratio: float | None = Field(
            default=None,
            gt=0.0,
            lt=1.0,
            title="Dense store ratio in hybrid search",
            description="Weight of dense store in hybrid search. Defaults to backend default if omitted.",
            examples=[0.6],
        )
    
        keywords: list[str] | None = Field(
            default=None,
            title="Keyword list",
            description="List of core keywords extracted from query. Used in sparse/hybrid. Overrides query_keywords. (['retirement pension', 'early termination condition'])",
            examples=["retirement pension", "early termination condition"],
        )
    
        # Allow extra fields passed by caller
        model_config = ConfigDict(extra="allow")
    
        @property
        def extra_fields(self) -> dict[str, Any]:
            extras = getattr(self, "model_extra", None)
            if extras is None:
                extras = getattr(self, "__pydantic_extra__", None)
            if not extras:
                return {}
            # remove fields with None value
            return {k: v for k, v in extras.items() if v is not None}
    
        @classmethod
        def from_any(cls, obj: Any) -> "RetrievalOptions":
            """
            Convert input into a standardized schema.
            Supports dict / Mapping / pydantic(v1/v2) / dataclass / normal object / to_dict()
            """
            data: Mapping[str, Any] | None = None
    
            if isinstance(obj, Mapping):
                data = obj
            elif hasattr(obj, "to_dict") and callable(obj.to_dict):
                data = obj.to_dict()
            elif hasattr(obj, "model_dump") and callable(obj.model_dump):  # pydantic v2
                data = obj.model_dump()
            elif hasattr(obj, "dict") and callable(obj.dict):  # pydantic v1
                data = obj.dict()
            elif hasattr(obj, "__dict__"):
                data = vars(obj)
    
            if data is None:
                raise TypeError("Cannot convert retrieval_options to dict.")
    
            # Instantiate (extra fields will be preserved with extra="allow")
            return cls(**data)
    
    
    class RetrievalDocument(BaseModel):
        content: str = Field(
            ...,
            title="Retrieved content",
            description="Text snippet of the document relevant to the query.",
        )
    
        metadata: dict = Field(
            ...,
            title="Document metadata",
            description="Metadata associated with the retrieved document, e.g. file path, name, format, etc.",
        )
    
        score: float = Field(
            ...,
            title="Retrieval score",
            description="Relevance score. Higher means more relevant to the query.",
        )
        model_config = ConfigDict(extra="allow")
    
    
    ############################ ADXP Template Fin ############################
    
    ###################### User Implementation(From Here) ######################
    from typing import Callable, Dict, List, Mapping
    
    from elasticsearch import AsyncElasticsearch
    
    Builder = Callable[..., Callable[[str], Dict[str, Any]]]
    
    TEXT_FIELD = "chunk_conts"
    VECTOR_FIELD = "chunk_embedding"
    
    
    def _build_sparse_query(*, query: str, options: RetrievalOptions, **_: Any):
        """
        Build a sparse vector KNN query body for Elasticsearch.
    
        Reference:
        https://www.elastic.co/docs/solutions/search/vector/knn
        """
    
        def body_func(query: str) -> Dict[str, Any]:
            return {
                "query": {
                    "match": {
                        TEXT_FIELD: query,
                    }
                },
                "size": options.top_k,
            }
    
        return body_func
    
    
    def _build_dense_query(
        *, query: str, options: RetrievalOptions, query_vector: List[float], **_: Any
    ):
        """
        Build a dense vector KNN query body for Elasticsearch.
    
        Reference:
        https://www.elastic.co/docs/reference/elasticsearch/rest-apis/retrievers/knn-retriever
        """
    
        def body_func(query: str) -> Dict[str, Any]:
            return {
                "knn": {
                    "field": VECTOR_FIELD,
                    "query_vector": query_vector,
                    "k": options.top_k,
                    "num_candidates": 100,
                },
                "size": options.top_k,
            }
    
        return body_func
    
    
    def _build_hybrid_query(
        *, query: str, options: RetrievalOptions, query_vector: List[float], **_: Any
    ):
        """
        Build a weighted Hybrid(linear) query body for Elasticsearch.
    
        Reference:
        https://www.elastic.co/docs/reference/elasticsearch/rest-apis/retrievers/linear-retriever
        """
    
        def body_func(query: str) -> Dict[str, Any]:
            hybrid_dense_ratio = options.hybrid_dense_ratio or 0.5
            return {
                "retriever": {
                    "linear": {
                        "retrievers": [
                            {
                                "retriever": {
                                    "knn": {
                                        "field": VECTOR_FIELD,
                                        "query_vector": query_vector,
                                        "k": options.top_k,
                                        "num_candidates": 100,
                                    }
                                },
                                "weight": hybrid_dense_ratio,
                            },
                            {
                                "retriever": {
                                    "standard": {
                                        "query": {
                                            "match": {
                                                TEXT_FIELD: query,
                                            }
                                        }
                                    }
                                },
                                "weight": (1.0 - hybrid_dense_ratio),
                            },
                        ],
                        "normalizer": "minmax",
                    }
                },
                "size": options.top_k,
            }
    
        return body_func
    
    
    QUERY_BUILDERS: dict[RetrievalMode, Builder] = {
        RetrievalMode.SPARSE: _build_sparse_query,
        RetrievalMode.DENSE: _build_dense_query,
        RetrievalMode.HYBRID: _build_hybrid_query,
    }
    
    
    async def get_relevant_documents(
        index_name: str,
        vectordb_info: Any,
        embeddings: Embeddings,
        query: str,
        retrieval_options: RetrievalOptions,
    ) -> List[RetrievalDocument]:
        db_info = VectorDBInfo.from_any(vectordb_info)
        es_info = db_info.as_elasticsearch()
        options = RetrievalOptions.from_any(retrieval_options)
    
        retrieval_mode = options.retrieval_mode or RetrievalMode.DENSE
        if retrieval_mode not in QUERY_BUILDERS:
            raise ValueError(
                f"Unsupported retrieval mode: {retrieval_mode}. "
                f"Available modes: {list(QUERY_BUILDERS.keys())}"
            )
    
        query_vector = (
            await embeddings.aembed_query(query)
            if retrieval_mode in [RetrievalMode.DENSE, RetrievalMode.HYBRID]
            else None
        )
    
        builder = QUERY_BUILDERS[retrieval_mode]
    
        query_dsl_func = builder(
            query=query,
            options=options,
            query_vector=query_vector,
        )
        query_dsl = query_dsl_func(query)
    
        es_client = AsyncElasticsearch(
            str(es_info.endpoint), api_key=es_info.api_key, verify_certs=False
        )
    
        try:
            async with es_client as es:
                response = await es.search(index=index_name, body=query_dsl)
                hits = response.get("hits", {}).get("hits", [])
                return [
                    RetrievalDocument(
                        content=f'{hit.get("_source", {}).get(TEXT_FIELD, "")}',
                        metadata={
                            k: v
                            for k, v in hit.get("_source", {}).items()
                            if k not in (TEXT_FIELD, VECTOR_FIELD)
                        },
                        score=hit.get("_score", 0),
                    )
                    for hit in hits
                ]
        except Exception as e:
            raise e
    
    `);

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'admin', label: '관리' }}
        initialSubMenu={{
          id: 'admin-roles',
          label: '역할 관리',
          icon: 'ico-lnb-menu-20-admin-role',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              기본 정보
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              지식 설정 진행 중...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='지식 생성' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={6} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={false}>
                    만들기
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        {/* 콘텐츠 영역 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader
            title='지식 등록'
            description='External Knowledge Repo 등록을 위한 정보들을 입력해주세요.'
            position='right'
            actions={
              <>
                <UIButton2 className='btn-tertiary-sky-blue' onClick={() => {}} style={{ width: '85px' }}>
                  테스트
                </UIButton2>
              </>
            }
          />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <div className='box-fill'>
                <UIUnitGroup gap={8} direction='column' align='start'>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                    <UIIcon2 className='ic-system-16-info-gray' />
                    <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                      정상 작동하는 ‘활성화’ 상태로 지식을 생성하려면 아래 사항을 반드시 확인해주세요.
                    </UITypography>
                  </div>
                  <div style={{ paddingLeft: '22px' }}>
                    <UIUnitGroup gap={8} direction='column' align='start'>
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {`선택한 임베딩 모델이 정상 동작하는 모델(File/API)인지, 모델 배포 목록에서 상태가 ‘이용가능’인지 확인해주세요.`}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {`선택한 벡터 DB가 맞는지 다시 한 번 확인해주세요. 기본 지식과 사용자 정의 지식은 사용하는 벡터 DB가 다를 수 있습니다.`}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {`Default Script를 수정한 경우, 입력한 Script 내용이 올바른지 확인해주세요.`}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {`사용자 정의 지식은 우측 상단 [테스트] 실행 결과가 실패한 상태에서 생성하면, 비활성화 상태로 생성되므로 테스트 성공 이후 등록 해 주세요.`}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                    </UIUnitGroup>
                  </div>
                </UIUnitGroup>
              </div>
            </UIArticle>

            {/* 임베딩모델A 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  임베딩 모델
                </UITypography>
                <UIDropdown
                  required={true}
                  value={'비정형 임베딩모델'}
                  readonly={true}
                  options={loaderOptions}
                  isOpen={isLoaderDropdownOpen}
                  onClick={() => setIsLoaderDropdownOpen(!isLoaderDropdownOpen)}
                  onSelect={() => {
                    setIsLoaderDropdownOpen(false);
                  }}
                />
              </UIFormField>
            </UIArticle>

            {/* 벡터 DB 입력 필드 */}
            <UIArticle>
              {/* [251120_퍼블수정] 부연설명 추가 */}
              <UIFormField gap={8} direction='column'>
                <UIGroup direction='column' gap={4}>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    벡터DB
                  </UITypography>
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    올바른 벡터DB를 선택해야 지식이 정상적으로 동작합니다. 기본 지식과 사용자 정의 지식은 사용하는 벡터DB가 다를 수 있으니, 선택한 벡터DB를 한 번 더 확인해주세요.
                  </UITypography>
                </UIGroup>
                <div>
                  {' '}
                  <UIInput.Text value={'비정형 데이터 플랫폼 Elastic Search'} placeholder='벡터 DB 입력' disabled={true} />
                </div>
              </UIFormField>
            </UIArticle>

            {/* 인덱스명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  인덱스명
                </UITypography>
                <div>
                  {' '}
                  <UIInput.Text value={'인덱스명 이름입니다'} placeholder='인덱스명 입력' disabled={true} />
                </div>
              </UIFormField>
            </UIArticle>

            {/* [251114_퍼블수정] 토글 영역 추가 */}
            <UIArticle>
              {/* [251201_퍼블수정] gap={8} > gap={4} 수정 */}
              <UIFormField gap={4} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Script
                </UITypography>
                {/* [251201_퍼블수정] 부연설명 추가 */}
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  토글을 끄면 시스템 기본 스크립트를 사용하고, 토글을 켜면 직접 편집한 스크립트를 사용합니다.
                </UITypography>
                <div className='flex' style={{ marginBottom: '4px' }}>
                  <UIToggle
                    size='medium'
                    checked={checked1}
                    onChange={() => {
                      setChecked1(!checked1);
                    }}
                  />
                </div>
                {/* 소스코드 영역 */}
                {/* [251114_퍼블수정] UICode 세로길이 리사이징가능하도록 수정(담당기획 : 퍼블까지만 일단반영필요 / 추후 개발쪽에 전달요청) 리사이징시 에디트영역 이슈로 maxHeight='700px' 추가 */}
                <UICode value={script} onChange={value => setScript(value)} language='python' theme='dark' width='100%' minHeight='700px' maxHeight='700px' readOnly={false} />
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: 80 }}>
                  이전
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
