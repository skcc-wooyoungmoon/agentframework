import { useState, useEffect } from 'react';

import { UIButton2, UITypography, UIRadio2 } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';
import { UIUnitGroup } from '@/components/UI/molecules';
import { useModal } from '@/stores/common/modal';
import { useUser } from '@/stores/auth/useUser';
import { KnowledgeChunkingSettingPage } from './KnowledgeChunkingSettingPage';
import { KnowledgeEmbeddingSettingPage } from './KnowledgeEmbeddingSettingPage';
import { KnowledgeRegistrationPage, KnowledgeRegistrationPageActions } from './KnowledgeRegistrationPage';
import { KnowledgeTestErrorAlert } from './KnowledgeTestErrorAlert';
import { v4 as uuidv4 } from 'uuid';
import { useCreateExternalKnowledge, useTestExternalKnowledge, useExecuteDataiku } from '@/services/knowledge/knowledge.services';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { KnowledgeDataSelectPage } from './KnowledgeDataSelectPage';
import { KnowledgeSelectedDataCheckPage } from './KnowledgeSelectedDataCheckPage';
import { useNavigate } from 'react-router-dom';

type KnowledgeCreatePopupProps = {
  isOpen: boolean;
  onClose: () => void;
  onComplete?: () => void;
  onPreviousStep: () => void;
};

export const KnowledgeCreatePopup: React.FC<KnowledgeCreatePopupProps> = ({ isOpen, onClose, onComplete, onPreviousStep }) => {
  const navigate = useNavigate();
  const { openAlert, openModal } = useModal();
  const { user } = useUser();
  // 공통 팝업 훅
  const { showCancelConfirm } = useCommonPopup();

  const createKnowledgeMutation = useCreateExternalKnowledge();
  const testKnowledgeMutation = useTestExternalKnowledge();
  // Dataiku 실행 mutation
  const executeDataikuMutation = useExecuteDataiku({
    onSuccess: () => {
      // console.log('Dataiku 실행 성공');
    },
    onError: /* async (error: any) */ () => {
      // console.error(`Dataiku 실행 중 오류가 발생했습니다.\n${error?.response?.data?.message || error?.message || '알 수 없는 오류'}`);
      // await openAlert({
      //   title: '오류',
      //   message: `Dataiku 실행 중 오류가 발생했습니다.\n${error?.response?.data?.message || error?.message || '알 수 없는 오류'}`,
      // });
    },
  });


  const [currentStep, setCurrentStep] = useState(1);
  const [knowledgeType, setKnowledgeType] = useState('option1');
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');

  // 지식 ID (UUID) - 팝업 열릴 때 생성
  const [knowledgeId, setKnowledgeId] = useState('');
  const [indexName, setIndexName] = useState('');

  // 데이터선택 (Step2, Step3)
  const [selectedItems, setSelectedItems] = useState<any[]>([]); // 선택된 항목들 (id 기준으로 중복 제거)
  const [selectedItemsMap, setSelectedItemsMap] = useState<Map<string, any>>(new Map()); // id를 key로 하는 Map
  // const [selectedSourceSystem, setSelectedSourceSystem] = useState('전체'); // 원천시스템 
  // const [currentPage, setCurrentPage] = useState(1); // 데이터선택 페이지


  // 청킹 설정
  const [chunkingMethod, setChunkingMethod] = useState('');
  const [chunkingMethodId, setChunkingMethodId] = useState('');
  const [chunkSize, setChunkSize] = useState('');
  const [sentenceOverlap, setSentenceOverlap] = useState('');
  const defaultChunkSize = 300; // 기본값 300
  const defaultSentenceOverlap = 0; // 기본값 0

  // 임베딩 설정
  const [embeddingModel, setEmbeddingModel] = useState('');
  const [embeddingModelId, setEmbeddingModelId] = useState('');
  const [vectorDB, setVectorDB] = useState('');
  const [vectorDBId, setVectorDBId] = useState('');
  const [syncEnabled, setSyncEnabled] = useState(false);
  const [syncTargets, setSyncTargets] = useState<string[]>([]);
  const [toggleChecked, setToggleChecked] = useState(false);

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


class VectorDatabaseType(str, Enum):
    MILVUS = "Milvus"
    ELASTICSEARCH = "ElasticSearch"


class BaseVectorDBInfo(BaseModel):
    type: VectorDatabaseType

    model_config = ConfigDict(extra="allow")  # Allow extra fields


# ==============================
# Connection info models for each VectorDB
# ==============================
class MilvusInfo(BaseVectorDBInfo):
    type: Literal[VectorDatabaseType.MILVUS] = VectorDatabaseType.MILVUS
    host: str = Field(..., description="Milvus host")
    port: str = Field(..., description="Milvus port")
    user: str = Field(..., description="Milvus username")
    password: str = Field(..., description="Milvus password")
    secure: str = Field(..., description="Use secure connection (true/false)")
    db_name: str = Field(..., description="Milvus database name")


class ElasticSearchInfo(BaseVectorDBInfo):
    type: Literal[VectorDatabaseType.ELASTICSEARCH] = VectorDatabaseType.ELASTICSEARCH
    endpoint: HttpUrl = Field(..., description="ElasticSearch endpoint")
    api_key: str = Field(..., description="ElasticSearch API key")


VectorDBInfoUnion = Union[MilvusInfo, ElasticSearchInfo]


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
    def as_milvus(self) -> MilvusInfo:
        if self.type != VectorDatabaseType.MILVUS:
            raise TypeError("VectorDB type is not 'milvus'")
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
            if vtype == VectorDatabaseType.ELASTICSEARCH:
                model = ElasticSearchInfo(**payload)
            elif vtype == VectorDatabaseType.MILVUS:
                model = MilvusInfo(**payload)
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


class RetrievalException(Exception):
    """검색 및 데이터 정제 에러"""

    pass


def _build_sparse_query(*, query: str, options: RetrievalOptions, **_: Any):
    """
    Build a text search query body for Elasticsearch.

    Reference:
    
https://www.elastic.co/docs/reference/query-languages/query-dsl/query-dsl-match-query

    """

    def body_func() -> Dict[str, Any]:
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
    
https://www.elastic.co/docs/reference/query-languages/query-dsl/query-dsl-knn-query

    """

    def body_func() -> Dict[str, Any]:
        return {
            "knn": {
                "field": VECTOR_FIELD,
                "query_vector": query_vector,
                "k": 50,
                "num_candidates": 100,
                "similarity": (options.threshold * 2) - 1,  ## cosine scale
            },
            "size": options.top_k,
        }

    return body_func


def _build_hybrid_query(
    *, query: str, options: RetrievalOptions, query_vector: List[float], **_: Any
):
    """
    Build a weighted Hybrid query body for Elasticsearch.

    Reference:
    ----------------------------------------------------------------------
    """

    def body_func() -> Dict[str, Any]:
        hybrid_dense_ratio = options.hybrid_dense_ratio or 0.5
        return {
            "query": {
                "script_score": {
                    "query": {
                        "bool": {
                            "must": [
                                {
                                    "knn": {
                                        "field": VECTOR_FIELD,
                                        "query_vector": query_vector,
                                        "k": 50,
                                        "num_candidates": 100,
                                        "similarity": (options.threshold * 2)
                                        - 1,  ## cosine scale
                                    },
                                }
                            ],
                            "should": [{"match": {TEXT_FIELD: query}}],
                        }
                    },
                    "script": {
                        "source": """
                            /**********************************************************
                            *   Hybrid Score (BM25 + Vector Similarity)
                            *   - BM25:     log1p 정규화 (스케일 안정화)
                            *   - Vector:   cosine → 0~1 정규화
                            *   - Fusion:   normalized weighted sum
                            **********************************************************/

                        // 1) 텍스트 점수 정규화 (log1p 방식)
                        double bm25_raw = _score;
                        double bm25_norm = Math.log(1 + bm25_raw);  
                        
                        // 2) 벡터 유사도 계산 및 정규화
                        double vec_raw = cosineSimilarity(params.query_vector, params.vector_field);
                        double vec_norm = (vec_raw + 1.0) / 2.0;    

                        // 3) 두 스코어의 가중합
                        double f_score = params.weight_text * bm25_norm + params.weight_vector * vec_norm;

                        return Math.max(f_score, 0.0);
                        """,
                        "params": {
                            "vector_field": VECTOR_FIELD,
                            "query_vector": query_vector,
                            "weight_text": (1 - hybrid_dense_ratio),
                            "weight_vector": hybrid_dense_ratio
                        },
                    },
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
    options.threshold = options.threshold or 0.3

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
    query_dsl = query_dsl_func()

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
        raise RetrievalException from e

`);

  // 테스트 통과 여부
  const [, setIsTestPassed] = useState(false);

  // 팝업 열릴 때 초기화
  useEffect(() => {
    if (isOpen && !knowledgeId) {
      const newUuid = uuidv4();
      setKnowledgeId(newUuid);

      // 기본 지식일 때만 인덱스명 자동 생성, 사용자 정의 지식은 입력받음
      if (knowledgeType === 'option1') {
        const newIndexName = `gaf_default_rag_${newUuid}`;
        setIndexName(newIndexName);
      } else {
        setIndexName(''); // 사용자 정의 지식은 빈 값으로 시작
      }
    }
  }, [isOpen, knowledgeId, knowledgeType]);

  // 지식 유형 변경 시 인덱스명 처리
  useEffect(() => {
    if (knowledgeId) {
      if (knowledgeType === 'option1') {
        // 기본 지식으로 변경: 자동 생성
        const newIndexName = `gaf_default_rag_${knowledgeId}`;
        setIndexName(newIndexName);
      } else {
        // 사용자 정의 지식으로 변경: 빈 값으로 초기화 (사용자가 직접 입력)
        setIndexName('');
      }
    }
  }, [knowledgeType, knowledgeId]);

  const getStepperItems = () => {
    if (knowledgeType === 'option2') {
      return [
        { step: 1, label: '지식 기본 설정' },
        { step: 2, label: '지식 등록' },
      ];
    }
    return [
      { step: 1, label: '지식 기본 설정' },
      { step: 2, label: '데이터 선택' },
      { step: 3, label: '선택 데이터 확인' },
      { step: 4, label: '청킹 설정' },
      { step: 5, label: '임베딩 설정' },
      { step: 6, label: '지식 등록' },
    ];
  };

  const stepperItems = getStepperItems();

  const getDisplayStep = () => {
    if (knowledgeType === 'option2') {
      // 사용자 정의 지식: step 1 -> step 6로 바로 진행
      if (currentStep === 1) return 1;
      if (currentStep === 6) return 2; // 실제 step 6를 표시상 step 2로
    }
    return currentStep;
  };

  const isStepValid = () => {
    switch (currentStep) {
      case 1:
        return name.trim().length > 0 && description.trim().length > 0;
      case 2:
        return selectedItems.length > 0;
      case 3:
        return selectedItems.length > 0;
      case 4:
        return true;
      case 5:
        // syncEnabled가 true인데 syncTargets가 비어있으면 false
        if (syncEnabled && syncTargets.length === 0) {
          return false;
        }
        return true;
      case 6:
        return true;
      default:
        return false;
    }
  };

  const buildKnowledgeData = () => {
    return {
      knwId: knowledgeId,
      knwNm: name.trim(),
      description: description.trim(),
      knowledgeType: knowledgeType === 'option2' ? 'custom' : 'external', // 백엔드에 전달할 값
      chunkId: chunkingMethodId,
      chunkNm: chunkingMethod,
      chunkSize: chunkSize === '' || chunkSize === null || chunkSize === undefined ? defaultChunkSize : Number(chunkSize),
      sentenceOverlap: sentenceOverlap === '' || sentenceOverlap === null || sentenceOverlap === undefined ? defaultSentenceOverlap : Number(sentenceOverlap),
      embModelId: embeddingModelId,
      embeddingModel,
      vectorDbId: vectorDBId,
      vectorDB,
      ragChunkIndexNm: indexName,
      syncEnabled,
      syncTargets,
      script,
      createdBy: user.userInfo.jkwNm || 'system', // 사용자 이름
      selectedItems: selectedItems, // 선택된 데이터
    };
  };

  const handleTest = async () => {
    const knowledgeData = buildKnowledgeData();

    // 필수 값 검증
    if (!knowledgeData.vectorDB || !knowledgeData.embeddingModel || !knowledgeData.ragChunkIndexNm || !knowledgeData.script) {
      await openAlert({
        title: '테스트 실패',
        message: '필수 설정값이 누락되었습니다.',
      });
      setIsTestPassed(false);
      return;
    }

    try {
      // ADXP API 테스트 호출
      const defaultQuery = '';
      const defaultRetrievalOptions = '{"topk":3}';

      const response = await testKnowledgeMutation.mutateAsync({
        embeddingModel: knowledgeData.embeddingModel,
        vectorDB: knowledgeData.vectorDB,
        vectorDbId: knowledgeData.vectorDbId || '',
        indexName: knowledgeData.ragChunkIndexNm,
        script: knowledgeData.script,
        query: defaultQuery,
        retrievalOptions: defaultRetrievalOptions,
      });

      // 응답 데이터 추출 (Response<T> 구조: { data: T, success: boolean, ... })
      const testResult = response?.data || response;

      // 200 응답이지만 status가 "error"인 경우 (테스트 실패)
      if (testResult?.status === 'error' || (!testResult?.success && testResult?.status === 'error')) {
        const detail = testResult?.detail || testResult?.message || '알 수 없는 오류가 발생했습니다.';
        setIsTestPassed(false);

        await openModal({
          type: '2xsmall',
          title: '안내',
          body: <KnowledgeTestErrorAlert detail={detail} />,
          showFooter: true,
          confirmText: '확인',
        });
        return;
      }

      // 성공 케이스
      await openAlert({
        title: '안내',
        message: '테스트를 성공하였습니다.',
      });
      setIsTestPassed(true);
    } catch (error: any) {
      setIsTestPassed(false);

      const status = error?.response?.status;

      // 400 에러만 특별 처리 (입력값 오류로 인한 테스트 실패)
      // 백엔드에서 대부분 200으로 변환해주지만, 혹시 모를 경우 대비
      if (status === 400) {
        const detail = error?.response?.data?.detail || error?.response?.data?.message || '알 수 없는 오류가 발생했습니다.';
        const errorCode = error?.response?.data?.code?.toString();
        const errorType = 'Bad Request';

        await openModal({
          type: '2xsmall',
          title: '안내',
          body: <KnowledgeTestErrorAlert detail={detail} errorCode={errorCode} errorType={errorType} />,
          showFooter: true,
          confirmText: '확인',
        });
        return;
      }

      // 나머지 에러는 그대로 throw (401, 403, 500, 네트워크 에러 등)
      // 전역 에러 핸들러나 axios interceptor에서 처리됨
      throw error;
    }
  };

  const handleClose = () => {
    showCancelConfirm({
      onConfirm: () => {
        onClose();
        setCurrentStep(1);
        setKnowledgeType('option1');
        setName('');
        setDescription('');
        setKnowledgeId('');
        setIndexName('');
        setChunkingMethod('');
        setChunkingMethodId('');
        setChunkSize('');
        setSentenceOverlap('');
        setToggleChecked(false);
        setEmbeddingModel('');
        setEmbeddingModelId('');
        setVectorDB('');
        setVectorDBId('');
        setSyncEnabled(false);
        setSyncTargets([]);
        setIsTestPassed(false);

        // step2, step3 초기화 
        setSelectedItems([]);
        setSelectedItemsMap(new Map());
      },
    });
  };

  const handlePrevious = () => {
    if (currentStep === 1) {
      onPreviousStep();
      return;
    }

    if (knowledgeType === 'option2') {
      // 사용자 정의 지식: step 1 -> step 6
      if (currentStep === 6) {
        setCurrentStep(1); // step 6에서 이전 -> step 1 (기본 설정)
      } else {
        handleClose();
      }
    } else {
      if (currentStep > 1) {
        setCurrentStep(prev => prev - 1);
      } else {
        handleClose();
      }
    }
  };

  const handleNext = () => {
    if (knowledgeType === 'option2') {
      // 사용자 정의 지식: step 1 -> step 6로 바로 진행
      if (currentStep === 1) {
        setCurrentStep(6); // step 1에서 다음 -> step 6 (지식 등록)
      }
    } else {
      if (currentStep < 6) {
        setCurrentStep(prev => prev + 1);
      }
    }
  };

  const handleCreate = async () => {
    const knowledgeData = buildKnowledgeData();

    // 필수 항목 검증
    const missingFields: string[] = [];

    // 공통 필수 항목
    if (!knowledgeData.knwNm) missingFields.push('지식명');
    if (!knowledgeData.description) missingFields.push('설명');

    // 지식 유형에 따른 필수 항목 검증
    if (knowledgeType === 'option2') {
      // 사용자 정의 지식
      if (!knowledgeData.embeddingModel) missingFields.push('임베딩 모델');
      if (!knowledgeData.vectorDB) missingFields.push('벡터DB');
      if (!knowledgeData.ragChunkIndexNm) missingFields.push('인덱스명');
      if (!knowledgeData.script) missingFields.push('스크립트');
    } else {
      // 기본 지식
      if (!knowledgeData.selectedItems || knowledgeData.selectedItems.length === 0) missingFields.push('데이터 선택');
      if (!knowledgeData.chunkNm) missingFields.push('청킹 방법');
      if (!knowledgeData.embeddingModel) missingFields.push('임베딩 모델');
      if (!knowledgeData.vectorDB) missingFields.push('벡터DB');
      if (!knowledgeData.script) missingFields.push('스크립트');
    }

    if (missingFields.length > 0) {
      await openAlert({
        title: '필수 항목 누락',
        message: `다음 항목을 입력해주세요:\n\n${missingFields.join('\n')}`,
      });
      return;
    }

    //console.log('knowledgeData : ', knowledgeData);

    // 공통 완료 처리 함수(이벤트 발생, state 초기화, 팝업 닫기)
    const handleComplete = (knowledgeIdForDetailPage: string | undefined) => {
      // CustomEvent 발생 (리스트 새로고침)
      //window.dispatchEvent(new CustomEvent('knowledge-created'));

      // 상세페이지 이동 
      navigate(`/data/dataCtlg/knowledge/detail/${knowledgeIdForDetailPage}`);

      // state 초기화
      setCurrentStep(1);
      setKnowledgeType('option1');
      setName('');
      setDescription('');
      setKnowledgeId('');
      setIndexName('');
      setChunkingMethod('');
      setChunkingMethodId('');
      setChunkSize('');
      setSentenceOverlap('');
      setEmbeddingModel('');
      setEmbeddingModelId('');
      setVectorDB('');
      setVectorDBId('');
      setSyncEnabled(false);
      setSyncTargets([]);
      setToggleChecked(false);
      setIsTestPassed(false);
      setSelectedItems([]);
      setSelectedItemsMap(new Map());

      // 팝업 닫기
      onComplete?.();
    };


    // 지식 생성 및 Dataiku 실행 요청 
    try {
      // 1. 지식 생성 API 호출
      const createResponse = await createKnowledgeMutation.mutateAsync(knowledgeData);

      // 2. 생성된 지식 ID 추출 
      const createdKnowledgeId = createResponse?.data?.expKnwId;  // ADXP 

      // 상세조회 이동을 위한 Knowledge Id
      // 기본 지식인 경우 생성된 Knowledge Id, 사용자 정의 지식인 경우 ADXP Knowledge Id
      const knowledgeIdForDetailPage = knowledgeType === 'option1' ? createResponse?.data?.knwId : createdKnowledgeId;
      // 3. 기본 지식이고 선택된 데이터가 있는 경우에만 Dataiku 실행
      if (knowledgeType === 'option1' && selectedItems.length > 0) {
        try {
          // Dataiku 실행 요청 데이터 구성
          const dataikuRequestData = {
            knowledgeId: createdKnowledgeId,
            selectedDatasets: selectedItems.map(item => ({
              datasetCardId: item.datasetCardId,
              datasetCardName: item.datasetCardName || item.name,
              datasetCd: item.datasetCd,
              originSystemCd: item.originSystemCd,
              originSystemName: item.originSystemName || item.depth,
            })),
          };

          //console.log('🚀 Dataiku 실행 요청:', dataikuRequestData);

          // 4. Dataiku 실행 API 호출
          await executeDataikuMutation.mutateAsync(dataikuRequestData);

          // 완료 alert 표시
          await openAlert({
            title: '완료',
            message: '지식 만들기를 완료하였습니다.',
          });
        } catch (dataikuError: any) {
          console.error('Dataiku 실행 실패:', dataikuError);
          // 지식은 생성되었으므로 상세페이지로 이동 및 팝업 닫기
          handleComplete(knowledgeIdForDetailPage);
          return;
        }
      } else {
        // 사용자 정의 지식 일 경우 (Dataiku 실행 없이 완료)
        await openAlert({
          title: '완료',
          message: '지식 만들기를 완료하였습니다.',
        });
      }
      // 성공 시 공통 완료 처리
      handleComplete(knowledgeIdForDetailPage);

      // 지식 생성 자체가 실패한 경우 
    } catch (error: any) {
      console.error('지식 생성 실패:', error);
      return;
    }
  };

  const getStepTitle = () => {
    switch (currentStep) {
      case 1:
        return '지식 기본 설정';
      case 2:
        return '데이터 선택';
      case 3:
        return '선택 데이터 확인';
      case 4:
        return '청킹 설정';
      case 5:
        return '임베딩 설정';
      case 6:
        return '지식 등록';
      default:
        return '';
    }
  };

  const getStepDescription = () => {
    switch (currentStep) {
      case 1:
        return '지식 유형 선택 후 선택한 유형에 알맞는 기본 메타 정보를 입력해주세요.';
      case 2:
        return '지식에 추가할 지식 데이터를 선택해 주세요.';
      case 3:
        return '';
      case 4:
        return '파싱 완료된 데이터의 청킹 방법을 설정해주세요.';
      case 5:
        return '청킹 완료된 데이터를 임베딩할 모델과 벡터DB를 설정해주세요.';
      case 6:
        return '최종 지식 정보를 확인하고 등록해주세요.';
      default:
        return '';
    }
  };

  const renderStepContent = () => {
    switch (currentStep) {
      case 1:
        return (
          <>
            <UIArticle>
              <UIFormField gap={12} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  지식 유형 선택
                </UITypography>
                <UIUnitGroup gap={12} direction='column' align='start'>
                  <UIRadio2 name='knowledgeType' value='option1' label='기본 지식' checked={knowledgeType === 'option1'} onChange={(_checked, value) => setKnowledgeType(value)} />
                  <UIRadio2
                    name='knowledgeType'
                    value='option2'
                    label='사용자 정의 지식'
                    checked={knowledgeType === 'option2'}
                    onChange={(_checked, value) => setKnowledgeType(value)}
                  />
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  이름
                </UITypography>
                <UIInput.Text value={name} onChange={e => setName(e.target.value)} placeholder='이름 입력' maxLength={30} />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  설명
                </UITypography>
                <UITextArea2 value={description} onChange={e => setDescription(e.target.value)} placeholder='설명 입력' maxLength={100} />
              </UIFormField>
            </UIArticle>
          </>
        );
      case 2:
        return (
          <KnowledgeDataSelectPage
            selectedItems={selectedItems}
            setSelectedItems={setSelectedItems}
            selectedItemsMap={selectedItemsMap}
            setSelectedItemsMap={setSelectedItemsMap}
            // searchValue={searchValue}
            // setSearchValue={setSearchValue}
            // searchInputValue={searchInputValue}
            // setSearchInputValue={setSearchInputValue}
            // selectedSourceSystem={selectedSourceSystem}
            // setSelectedSourceSystem={setSelectedSourceSystem}
            // currentPage={currentPage}
            // setCurrentPage={setCurrentPage}
            isOpen={isOpen}
          />
        );
      case 3:
        return (
          <KnowledgeSelectedDataCheckPage
            selectedItems={selectedItems}
            setSelectedItems={setSelectedItems}
            selectedItemsMap={selectedItemsMap}
            setSelectedItemsMap={setSelectedItemsMap}
          />
        );
      case 4:
        return (
          <KnowledgeChunkingSettingPage
            chunkingMethod={chunkingMethod}
            chunkingMethodId={chunkingMethodId}
            chunkSize={chunkSize}
            sentenceOverlap={sentenceOverlap}
            onChunkingMethodChange={(value, id) => {
              setChunkingMethod(value);
              setChunkingMethodId(id);
            }}
            onChunkSizeChange={setChunkSize}
            onSentenceOverlapChange={setSentenceOverlap}
          />
        );
      case 5:
        return (
          <KnowledgeEmbeddingSettingPage
            embeddingModel={embeddingModel}
            embeddingModelId={embeddingModelId}
            vectorDB={vectorDB}
            vectorDBId={vectorDBId}
            syncEnabled={syncEnabled}
            syncTargets={syncTargets}
            isCustomKnowledge={knowledgeType === 'option2'}
            indexName={indexName}
            onEmbeddingModelChange={(value, id) => {
              setEmbeddingModel(value);
              setEmbeddingModelId(id);
            }}
            onVectorDBChange={(value, id) => {
              setVectorDB(value);
              setVectorDBId(id);
            }}
            onSyncEnabledChange={setSyncEnabled}
            onSyncTargetsChange={setSyncTargets}
            onIndexNameChange={setIndexName}
          />
        );
      case 6:
        return (
          <KnowledgeRegistrationPage
            embeddingModel={embeddingModel}
            vectorDB={vectorDB}
            indexName={indexName}
            script={script}
            isCustomKnowledge={knowledgeType === 'option2'}
            embeddingModelId={embeddingModelId}
            vectorDBId={vectorDBId}
            toggleChecked={toggleChecked}
            onToggleChange={setToggleChecked}
            onScriptChange={setScript}
            onTest={handleTest}
            onEmbeddingModelChange={(value, id) => {
              setEmbeddingModel(value);
              setEmbeddingModelId(id);
            }}
            onVectorDBChange={(value, id) => {
              setVectorDB(value);
              setVectorDBId(id);
            }}
            onIndexNameChange={setIndexName}
          />
        );
      default:
        return null;
    }
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='지식 생성' description='' position='left' />
          <UIPopupBody>
            <UIArticle>
              <UIStepper currentStep={getDisplayStep()} items={stepperItems} direction='vertical' />
            </UIArticle>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={currentStep < 6 || (currentStep === 6 && indexName === '')} onClick={handleCreate}>
                  만들기
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupHeader
          title={getStepTitle()}
          description={getStepDescription()}
          position='right'
          actions={knowledgeType === 'option2' && currentStep === 6 ? <KnowledgeRegistrationPageActions onTest={handleTest} /> : undefined}
        />

        <UIPopupBody>{renderStepContent()}</UIPopupBody>

        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-gray' onClick={handlePrevious}>
                이전
              </UIButton2>
              {currentStep < 6 && (
                <UIButton2 className='btn-secondary-blue' onClick={handleNext} disabled={!isStepValid()}>
                  다음
                </UIButton2>
              )}
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
};
