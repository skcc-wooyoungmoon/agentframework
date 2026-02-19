import React, { useMemo, useState } from 'react';

import { UIButton2, UIIcon2, UILabel, UITypography } from '@/components/UI/atoms';
import { UIDataCnt } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIArticle, UIGroup, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const DP_050101_P02: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [searchValue, setSearchValue] = useState('');

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '분류 선택' },
    { step: 2, label: '배포 대상 선택' },
    { step: 3, label: '운영용 정보 입력' },
    { step: 4, label: '최종 정보 확인' },
  ];

  // 샘플 데이터
  const rowData = [
    {
      id: '1',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      isDefault: true,
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      isDefault: false,
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '3',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      isDefault: false,
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '4',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      isDefault: true,
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '5',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      isDefault: true,
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '이름',
        field: 'name' as any,
        width: 400,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '유형',
        field: 'description',
        minWidth: 272,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '기본설정',
        field: 'publicStatus',
        width: 120,
        cellRenderer: (params: any) => {
          return (
            <div className='flex gap-1 flex-wrap'>
              {params.data.isDefault === true ? (
                // <UILabel variant='line' intent='blue'>
                //   True
                // </UILabel>
                //  [251104_퍼블수정] : UILabel > UITextLabel 변경
                <UITextLabel intent='blue'>True</UITextLabel>
              ) : (
                <UITextLabel intent='gray'>False</UITextLabel>
                // <UILabel variant='line' intent='gray'>
                //   False
                // </UILabel>
              )}
            </div>
          );
        },
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [rowData]
  );

  // 샘플 데이터
  const rowData2 = [
    {
      id: '1',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      vectorDb: 'Milvus',
      modelName: 'GIP/text-embedding-3-large-new',
      indexName: '인덱스명',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      vectorDb: 'Milvus',
      modelName: 'GIP/text-embedding-3-large-new',
      indexName: '인덱스명',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '3',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      vectorDb: 'Milvus',
      modelName: 'GIP/text-embedding-3-large-new',
      indexName: '인덱스명',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '4',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      vectorDb: 'Milvus',
      modelName: 'GIP/text-embedding-3-large-new',
      indexName: '인덱스명',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '5',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      vectorDb: 'Milvus',
      modelName: 'GIP/text-embedding-3-large-new',
      indexName: '인덱스명',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs2: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '이름',
        field: 'name' as any,
        width: 272,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '상태',
        field: 'accountStatus' as any,
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const statusColors = {
            활성화: 'complete',
            실패: 'error',
            진행중: 'progress',
            취소: 'stop',
          } as const;
          return (
            <UILabel variant='badge' intent={statusColors[params.value as keyof typeof statusColors]}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '벡터DB',
        field: 'vectorDb',
        width: 144,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '임베딩 모델',
        field: 'modelName',
        width: 144,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '인덱스명',
        field: 'indexName',
        width: 144,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [rowData2]
  );

  // 샘플 데이터
  const rowData3 = [
    {
      id: '1',
      deployName: '콜센터 응대 특화모델',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',

      modelType: 'language',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      deployName: '콜센터 응대 특화모델',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      modelType: 'language',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '3',
      deployName: '콜센터 응대 특화모델',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      modelType: 'language',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '4',
      deployName: '콜센터 응대 특화모델',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      modelType: 'language',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '5',
      deployName: '콜센터 응대 특화모델',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      modelType: 'language',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs3: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '배포명',
        field: 'deployName' as any,
        width: 272,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '모델명',
        field: 'name' as any,
        width: 272,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '상태',
        field: 'accountStatus' as any,
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const statusColors = {
            활성화: 'complete',
            실패: 'error',
            진행중: 'progress',
            취소: 'stop',
          } as const;
          return (
            <UILabel variant='badge' intent={statusColors[params.value as keyof typeof statusColors]}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '모델유형',
        field: 'modelType' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [rowData3]
  );

  // 샘플 데이터
  const rowData4 = [
    {
      id: '1',
      deployName: '콜센터 응대 특화모델',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',

      modelType: 'language',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      deployName: '콜센터 응대 특화모델',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      modelType: 'language',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '3',
      deployName: '콜센터 응대 특화모델',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      modelType: 'language',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '4',
      deployName: '콜센터 응대 특화모델',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      modelType: 'language',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '5',
      deployName: '콜센터 응대 특화모델',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      modelType: 'language',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
  ];

  // 그리드 컬럼 정의 (배포 에이전트)
  const columnDefs4: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '배포명',
        field: 'deployName' as any,
        width: 272,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '빌더명',
        field: 'name' as any,
        width: 272,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '상태',
        field: 'accountStatus' as any,
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const statusColors = {
            활성화: 'complete',
            실패: 'error',
            진행중: 'progress',
            취소: 'stop',
          } as const;
          return (
            <UILabel variant='badge' intent={statusColors[params.value as keyof typeof statusColors]}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '배포유형',
        field: 'modelType' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [rowData4]
  );

  // 샘플 데이터 (가드레일)
  const rowData5 = [
    {
      id: '1',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '3',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '4',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '5',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
  ];

  // 그리드 컬럼 정의 (가드레일)
  const columnDefs5: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '이름',
        field: 'name' as any,
        width: 262,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '기본설정',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [rowData5]
  );

  // 샘플 데이터 (세이프티 필터 DB)
  const rowData6 = [
    {
      id: '1',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '3',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '4',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '5',
      name: '예적금 상품 Q&A 세트',
      accountStatus: '활성화',
      description: '대출 약관 관련 데이터세트',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
  ];

  // 그리드 컬럼 정의 (세이프티 필터 DB)
  const columnDefs6: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '분류',
        field: 'name' as any,
        width: 262,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '금지어',
        field: 'description',
        minWidth: 392,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [rowData6]
  );

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // 더보기 메뉴 설정

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'data', label: '데이터' }}
        initialSubMenu={{
          id: 'data-tools',
          label: '데이터도구',
          icon: 'ico-lnb-menu-20-data-storage',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              데이터 도구
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              Ingestion Tool 만들기 진행 중...
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
            <UIPopupHeader title='운영 배포' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} disabled={true}>
                    배포
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader
            title='배포 대상 선택'
            description='대상을 선택 후 다음 버튼을 누르면, 자동으로 배포을 위한 사전 검증 파일 생성 및 정합성 검증이 진행됩니다.'
            position='right'
          />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <div className='box-fill'>
                <UIUnitGroup gap={8} direction='column' align='start'>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                    <UIIcon2 className='ic-system-16-info-gray' />
                    <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                      정합성 검증은 운영 배포를 위해 생성된 사전 검증 파일이 포탈 형상과 동일한지 확인하는 절차를 의미합니다.
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
                                {`검증 결과 ‘일치'인 경우, 해당 파일을 기반으로 배포용 최종 파일을 생성합니다.`}
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
                                {`검증 결과 ‘불일치'인 경우, 해당 파일로 배포는 불가하며 포탈에서 파일을 다시 생성 후 재시도해주세요.`}
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

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  배포 정보
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-black'>
                  <table className='tbl-v'>
                    <colgroup>
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                    </colgroup>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            프로젝트명
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            대출 상품 추천
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            분류
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            백터 DB
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <UIGroup gap={8} direction='row' align='start'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={99} prefix='가드레일 총' unit='건' />
                      </div>
                    </UIGroup>
                  </div>
                  <div className='flex items-center gap-2'>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                        placeholder='이름, 설명 입력'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='single-select' rowData={rowData5} columnDefs={columnDefs5} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <UIGroup gap={8} direction='row' align='start'>
                      <div style={{ width: '180px', paddingRight: '8px' }}>
                        <UIDataCnt count={99} prefix='세이프티 필터 총' unit='건' />
                      </div>
                    </UIGroup>
                  </div>
                  <div className='flex items-center gap-2'>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                        placeholder='분류, 금지어 입력'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='single-select' rowData={rowData6} columnDefs={columnDefs6} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <UIGroup gap={8} direction='row' align='start'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={99} prefix='백터 DB 총' unit='건' />
                      </div>
                    </UIGroup>
                  </div>
                  <div className='flex items-center gap-2'>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                        placeholder='분류, 금지어 입력'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='single-select' rowData={rowData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <UIGroup gap={8} direction='row' align='start'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={99} prefix='지식 총' unit='건' />
                      </div>
                    </UIGroup>
                  </div>
                  <div className='flex items-center gap-2'>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                        placeholder='이름, 설명 입력'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='single-select' rowData={rowData2} columnDefs={columnDefs2} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <UIGroup gap={8} direction='row' align='start'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={99} prefix='배포 모델 총' unit='건' />
                      </div>
                    </UIGroup>
                  </div>
                  <div className='flex items-center gap-2'>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                        placeholder='배포명 입력'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='single-select' rowData={rowData3} columnDefs={columnDefs3} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <UIGroup gap={8} direction='row' align='start'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={99} prefix='배포 에이전트 총' unit='건' />
                      </div>
                    </UIGroup>
                  </div>
                  <div className='flex items-center gap-2'>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                        placeholder='배포명 입력'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='single-select' rowData={rowData4} columnDefs={columnDefs4} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                <UIButton2 className='btn-secondary-blue'>다음</UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
