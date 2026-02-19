import { useMemo, useState, useEffect } from 'react';

import { designRouteConfig } from '../routes/design-route.config';

// 타입정의
interface PageData {
  id: string;
  path: string;
  label: string;
  depth: string;
  screenName: string;
  screenId: string;
  type: string;
  status: string;
  element?: React.ReactNode;
}

interface GroupedPage {
  lv1Korean: string;
  lv1English: string;
  pages: PageData[];
}

interface BusinessMapping {
  [key: string]: {
    eng: string;
    lv2: {
      [key: string]: string;
    };
  };
}

const _modalList = [
  'LG_010101_P02',
  'LG_010101_P03',
  'DT_020302_P06',
  'MD_030101_P07',
  'DT_030101_P04',
  'DT_020302_P04',
  'MD_040101_P03',
  'MD_040101_P02',
  'MD_040101_P01',
  'PR_010102_P03',
  'DP_040102_P02',
  'CM_020101_P02',
  'HM_010101_P04',
  'PR_030101_P02',
  'DT_020101_P08',
  'DT_020303_P06',
  'DT_010102_P01',
  'DT_020303_P01',
  'LG_010101_P05',
  'AG_030101_P03',
  'MD_030102_P02',
  'MD_030103_P01',
  'DT_030101_P07',
  'AG_010102_P20',
  'AG_010102_P26',
  'AG_010102_P34',
  'LG_010101_P06',
  'AG_010102_P21',
  'AG_010102_P22',
  'AG_010102_P23',
  'AG_010102_P25',
  'AG_010102_P32',
  'AG_010102_P35',
  'EV_010101_P01',
  'EV_010201_P01',
  'EV_010301_P01',
  'MD_050102_P01',
  'HM_060101_P02',
  'HM_060101_P06',
  'AG_010102_P36',
];

export const UIPageList = () => {
  const [search, setSearch] = useState('');
  const [expandedGroups, setExpandedGroups] = useState<Set<string>>(new Set());
  const [isGuideVisible, setIsGuideVisible] = useState(false);

  // UIPageList에서는 브라우저 세로 스크롤을 허용
  useEffect(() => {
    document.body.style.overflowY = 'auto';
    document.documentElement.style.overflowY = 'auto';

    return () => {
      document.body.style.overflowY = 'hidden';
      document.documentElement.style.overflowY = 'hidden';
    };
  }, []);

  // 그룹 펼침/접힘 토글
  const toggleGroup = (lv1Korean: string) => {
    setExpandedGroups(prev => {
      const newSet = new Set(prev);
      if (newSet.has(lv1Korean)) {
        newSet.delete(lv1Korean);
      } else {
        newSet.add(lv1Korean);
      }
      return newSet;
    });
  };

  // 업무구분 매핑 객체
  const businessMapping: BusinessMapping = useMemo(
    () => ({
      가이드: {
        eng: 'guide',
        lv2: {
          '퍼블리싱 가이드': 'publishingGuide',
          '데이터그룹 페이지 가이드': 'dataGroupGuide',
        },
      },
      공통: {
        eng: 'common',
        lv2: {
          '공통 오류': 'commonError',
          전자결재: 'eApproval',
          권한: 'auth',
          회원가입: 'signUp',
        },
      },
      홈: {
        eng: 'home',
        lv2: {
          모델가든: 'modelGarden',
          IDE: 'ide',
        },
      },
      데이터: {
        eng: 'data',
        lv2: {
          '데이터 탐색': 'dataStor',
          '지식/학습 데이터 관리': 'dataCtlg',
          데이터도구: 'dataTools',
        },
      },
      모델: {
        eng: 'model',
        lv2: {
          '모델 관리': 'modelCtlg',
          '모델 평가': 'modelEval',
          파인튜닝: 'fineTuning',
        },
      },
      플레이그라운드: {
        eng: 'pg',
        lv2: {
          플레이그라운드: 'pg',
        },
      },
      프롬프트: {
        eng: 'prompt',
        lv2: {
          '추론 프롬프트': 'inferPrompt',
          퓨샷: 'fewShot',
        },
      },
      에이전트: {
        eng: 'agent',
        lv2: {
          빌더: 'builder',
          도구: 'tools',
          '에이전트 평가': 'agentEval',
        },
      },
      배포: {
        eng: 'deploy',
        lv2: {
          '모델 배포': 'modelDeploy',
          '에이전트 배포': 'agentDeploy',
          'API KEY': 'apiKey',
        },
      },
      관리: {
        eng: 'admin',
        lv2: {
          '사용자 관리': 'userMgmt',
          '역할 관리': 'roleMgmt',
          '권한승인 관리': 'approvMgmt',
          '자원 관리': 'resrcMgmt',
          '그룹 관리': 'groupMgmt',
          '접속 관리': 'accessMgmt',
          '사용이력 관리': 'usageMgmt',
          '보안 관리': 'secMgmt',
        },
      },
      로그: {
        eng: 'log',
        lv2: {
          '에이전트사용 로그': 'agentDeployLog',
          시스템로그: 'systemLog',
          사용로그: 'usageLog',
        },
      },
      모니터링: {
        eng: 'monitoring',
        lv2: {
          '모델배포 모니터링': 'modelDeploy',
          '에이전트사용 로그 모니터링': 'agentDeploy',
          'API Key 모니터링': 'apiKey',
          '파인튜닝 목록': 'finetuning',
        },
      },
      로그인: {
        eng: 'login',
        lv2: {
          로그인: 'login',
        },
      },
      공지사항: {
        eng: 'notice',
        lv2: {
          공지사항: 'notice',
        },
      },
    }),
    []
  );

  // 페이지별 다중 Depth 정보 추출 함수
  const extractLevelInfo = (depth: string, screenName: string) => {
    const parts = depth.split(' > ');

    // 첫 번째 레벨(LV1) 추출
    const lv1Korean = parts[0] || depth.split(' ')[0];

    // 특별 케이스 처리
    let lv1 = lv1Korean;
    if (lv1Korean === '회원가입') {
      lv1 = '공통';
    } else if (lv1Korean === '공지사항') {
      lv1 = '공통';
    }

    // 각 depth별 정보 추출
    const lv2Korean =
      parts.length >= 2
        ? parts[1].replace(/\(TAB\)\s*/, '').trim()
        : lv1Korean === '회원가입'
          ? '회원가입'
          : lv1Korean === '공지사항'
            ? '공지사항'
            : lv1Korean === '로그' && screenName === '목록'
              ? '로그'
              : lv1Korean === '모니터링' && screenName === '목록'
                ? '모니터링'
                : lv1Korean === '플레이그라운드'
                  ? '플레이그라운드'
                  : lv1Korean === '로그인'
                    ? '-'
                    : screenName;

    const lv3Korean = parts.length >= 3 ? parts[2].trim() : '-';
    const lv4Korean = parts.length >= 4 ? parts[3].trim() : '-';
    const lv5Korean = parts.length >= 5 ? parts[4].trim() : '-';

    return { lv1, lv2Korean, lv3Korean, lv4Korean, lv5Korean };
  };

  // designRouteConfig를 직접 사용하여 페이지 목록 생성
  const pageList = designRouteConfig;

  // 필터링된 페이지 목록
  const filteredPages = useMemo(() => {
    return pageList.filter(page => {
      // hidden depth를 가진 페이지는 리스트에서 제외
      if (page.depth === 'hidden') return false;
      // hidden status를 가진 페이지는 집계에서 제외
      if (page.status === 'hidden') return false;
      // 삭제 페이지는 집계에서 제외
      if (page.status === '3') return false;

      if (search === '') return true;

      return (
        page.screenName.toLowerCase().includes(search.toLowerCase()) ||
        page.path.toLowerCase().includes(search.toLowerCase()) ||
        page.screenId.toLowerCase().includes(search.toLowerCase()) ||
        page.depth.toLowerCase().includes(search.toLowerCase())
      );
    });
  }, [pageList, search]);

  // 가이드와 페이지 분리하여 그룹화
  const groupedPages = useMemo(() => {
    const guideGroup: GroupedPage[] = [];
    const pageGroups: { [key: string]: GroupedPage } = {};

    filteredPages.forEach(page => {
      const { lv1 } = extractLevelInfo(page.depth, page.screenName);
      const lv1Eng = businessMapping[lv1]?.eng || '';

      if (lv1 === '가이드') {
        // 가이드 그룹 처리
        let guideGroupItem = guideGroup.find(g => g.lv1Korean === '가이드');
        if (!guideGroupItem) {
          guideGroupItem = {
            lv1Korean: '가이드',
            lv1English: lv1Eng,
            pages: [],
          };
          guideGroup.push(guideGroupItem);
        }
        guideGroupItem.pages.push(page);
      } else {
        // 페이지 그룹 처리
        if (!pageGroups[lv1]) {
          pageGroups[lv1] = {
            lv1Korean: lv1,
            lv1English: lv1Eng,
            pages: [],
          };
        }
        pageGroups[lv1].pages.push(page);
      }
    });

    // 검색 중일 때는 빈 그룹 제거
    const filteredPageGroups = search !== '' ? Object.values(pageGroups).filter(group => group.pages.length > 0) : Object.values(pageGroups);

    // 페이지 그룹 정렬
    const sortedPageGroups = filteredPageGroups.sort((a: GroupedPage, b: GroupedPage) => {
      const order = ['공통', '홈', '데이터', '모델', '플레이그라운드', '프롬프트', '에이전트', '배포', '관리', '로그', '모니터링', '로그인', '공지사항', '평가'];
      const aIndex = order.indexOf(a.lv1Korean);
      const bIndex = order.indexOf(b.lv1Korean);
      return (aIndex === -1 ? 999 : aIndex) - (bIndex === -1 ? 999 : bIndex);
    });

    // 검색 중일 때는 빈 가이드 그룹도 제거
    const filteredGuideGroup = search !== '' && guideGroup.length > 0 && guideGroup[0].pages.length === 0 ? [] : guideGroup;

    // 가이드 그룹을 맨 앞에, 페이지 그룹들을 뒤에 배치
    return [...filteredGuideGroup, ...sortedPageGroups];
  }, [filteredPages, businessMapping, search]);

  // groupedPages가 변경될 때마다 그룹 상태 설정
  useEffect(() => {
    if (groupedPages.length > 0) {
      if (search !== '') {
        // 검색 시에는 모든 그룹을 열린 상태로 설정
        const allGroupNames = new Set(groupedPages.map(group => group.lv1Korean));
        setExpandedGroups(allGroupNames);
      } else {
        // 검색이 아닐 때는 모든 그룹을 닫힌 상태로 설정
        setExpandedGroups(new Set());
      }
    }
  }, [groupedPages, search]);

  // 페이지 이동
  const handleNavigate = (path: string) => {
    // path에서 화면ID만 추출 (ex: "prompt/PR_020101" → "PR_020101")
    const screenId = path.split('/').pop();

    if (!screenId) return;

    if (_modalList.includes(screenId)) {
      // _modals 에 포함된 화면ID일 경우 다른 링크로 이동
      window.open(`/design/modal-list?id=${screenId}`, '_blank');
    } else {
      // 기본 이동
      window.open(`/design/${path}`, '_blank');
    }
  };

  // 상태별 배경색 반환 함수
  const getStatusBackgroundColor = (status: string) => {
    switch (status) {
      case '0':
        return '#ffffff';
      case '1':
        return '#c0e2ff';
      case '2':
        return '#d6f9c7';
      default:
        return '#ffffff';
    }
  };

  return (
    <div
      style={{
        fontFamily: 'Arial, sans-serif',
        padding: '20px',
        backgroundColor: '#ffffff',
        minHeight: '100vh',
      }}
    >
      {/* 헤더 네비게이션 */}
      <div
        style={{
          marginBottom: '20px',
          padding: '10px 0',
          borderBottom: '1px solid #ddd',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
        }}
      >
        <h1
          style={{
            fontSize: '24px',
            margin: 0,
            color: '#333',
          }}
        >
          신한 AI - Page List
        </h1>

        <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
          <input
            type='text'
            placeholder='검색: 화면명, 경로, 화면ID'
            value={search}
            onChange={e => setSearch(e.target.value)}
            style={{
              padding: '8px 12px',
              border: '1px solid #ddd',
              borderRadius: '4px',
              fontSize: '14px',
              width: '300px',
              backgroundColor: 'white',
            }}
          />
          <span
            style={{
              fontSize: '14px',
              color: '#666',
              whiteSpace: 'nowrap',
            }}
          >
            총 {filteredPages.filter(page => !page.depth.startsWith('가이드')).length}개 페이지 ({groupedPages.filter(g => g.lv1Korean !== '가이드').length}개 그룹)
          </span>
        </div>
      </div>

      {/* 작업 정보 */}
      <div
        className='work-info'
        style={{
          display: 'flex',
          gap: '30px',
          marginBottom: '15px',
          padding: '20px 0',
          backgroundColor: '#ffffff',
          justifyContent: 'center',
          alignItems: 'center',
        }}
      >
        {(() => {
          const allPages = filteredPages.filter(page => !page.depth.startsWith('가이드'));
          const statusCounts = allPages.reduce(
            (acc, page) => {
              acc[page.status] = (acc[page.status] || 0) + 1;
              return acc;
            },
            {} as Record<string, number>
          );

          return (
            <>
              <dl
                className='work-item'
                style={{
                  margin: 0,
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  textAlign: 'center',
                }}
              >
                <dt style={{ margin: 0 }}>
                  <span style={{ fontSize: '18px', color: '#666' }}>전체 : </span>
                </dt>
                <dd style={{ margin: 0 }}>
                  <strong className='all' style={{ fontSize: '24px', color: '#333' }}>
                    {allPages.length}
                  </strong>
                </dd>
              </dl>
              <dl
                className='work-item'
                style={{
                  margin: 0,
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  textAlign: 'center',
                }}
              >
                <dt style={{ margin: 0 }}>
                  <span style={{ fontSize: '18px', color: '#666' }}>검수요청 : </span>
                </dt>
                <dd style={{ margin: 0 }}>
                  <strong className='confirm' style={{ fontSize: '24px', color: '#0066cc' }}>
                    {statusCounts['1'] || 0}
                  </strong>
                </dd>
              </dl>
              <dl
                className='work-item'
                style={{
                  margin: 0,
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  textAlign: 'center',
                }}
              >
                <dt style={{ margin: 0 }}>
                  <span style={{ fontSize: '18px', color: '#666' }}>검수완료 : </span>
                </dt>
                <dd style={{ margin: 0 }}>
                  <strong className='complete' style={{ fontSize: '24px', color: '#28a745' }}>
                    {statusCounts['2'] || 0}
                  </strong>
                </dd>
              </dl>
              <dl
                className='work-item'
                style={{
                  margin: 0,
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  textAlign: 'center',
                }}
              >
                <dt style={{ margin: 0 }}>
                  <span style={{ fontSize: '18px', color: '#666' }}>작업완료 : </span>
                </dt>
                <dd style={{ margin: 0 }}>
                  <strong className='wait' style={{ fontSize: '24px', color: '#ff7c00' }}>
                    {statusCounts['2'] || 0}
                  </strong>
                </dd>
              </dl>
              <dl
                className='work-item'
                style={{
                  margin: 0,
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  textAlign: 'center',
                }}
              >
                <dt style={{ margin: 0 }}>
                  <span style={{ fontSize: '18px', color: '#666' }}>진척률 : </span>
                </dt>
                <dd style={{ margin: 0 }}>
                  <strong className='progress' style={{ fontSize: '24px', color: '#007bff' }}>
                    {allPages.length > 0 ? Math.round(((statusCounts['2'] || 0) / allPages.length) * 100) : 0}%
                  </strong>
                </dd>
              </dl>
            </>
          );
        })()}
      </div>

      {/* 상태별 범례 */}
      <ul
        className='display-work-list'
        style={{
          display: 'flex',
          gap: '25px',
          listStyle: 'none',
          padding: 0,
          margin: '0 0 25px 0',
          fontSize: '18px',
          justifyContent: 'center',
        }}
      >
        <li style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <em
            className='box-white'
            style={{
              width: '20px',
              height: '20px',
              backgroundColor: '#ffffff',
              border: '1px solid #ddd',
              display: 'inline-block',
            }}
          ></em>
          <span>미작업</span>
        </li>
        <li style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <em
            className='progress-confirm'
            style={{
              width: '20px',
              height: '20px',
              backgroundColor: '#c0e2ff',
              border: '1px solid #0066cc',
              display: 'inline-block',
            }}
          ></em>
          <span>검수요청</span>
        </li>
        <li style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <em
            className='progress-complete'
            style={{
              width: '20px',
              height: '20px',
              backgroundColor: '#d6f9c7',
              border: '1px solid #28a745',
              display: 'inline-block',
            }}
          ></em>
          <span>검수완료</span>
        </li>
        <li style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <em
            className='progress-start'
            style={{
              width: '20px',
              height: '20px',
              backgroundColor: 'rgb(255 124 0 / 20%)',
              border: '1px solid #ff7c00',
              display: 'inline-block',
            }}
          ></em>
          <span>작업완료</span>
        </li>
      </ul>

      {/* 페이지 그룹 */}
      <div>
        <h2
          style={{
            fontSize: '20px',
            fontWeight: 'bold',
            color: '#333',
            padding: '10px 0',
            borderBottom: '2px solid #0066cc',
          }}
        >
          PAGE
        </h2>
        <div style={{ backgroundColor: 'white', border: '1px solid #ddd' }}>
          {groupedPages
            .filter(g => g.lv1Korean !== '가이드')
            .map(group => {
              return (
                <div key={group.lv1Korean}>
                  <div style={{ borderBottom: '1px solid #eee' }}>
                    {/* 그룹 헤더 (아코디언 버튼) */}
                    <div
                      className='accordion-header'
                      onClick={() => toggleGroup(group.lv1Korean)}
                      style={{
                        padding: '16px 20px',
                        backgroundColor: '#f8f9fa',
                        borderBottom: expandedGroups.has(group.lv1Korean) ? '1px solid #ddd' : 'none',
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        fontWeight: 'bold',
                        fontSize: '16px',
                        color: '#333',
                      }}
                      onMouseOver={e => {
                        if (!expandedGroups.has(group.lv1Korean)) {
                          (e.currentTarget as HTMLElement).style.backgroundColor = '#e9ecef';
                        }
                      }}
                      onMouseOut={e => {
                        if (!expandedGroups.has(group.lv1Korean)) {
                          (e.currentTarget as HTMLElement).style.backgroundColor = '#f8f9fa';
                        }
                      }}
                    >
                      <div
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'space-between',
                          width: '100%',
                        }}
                      >
                        <div>
                          {group.lv1Korean !== '가이드' && (
                            <span
                              style={{
                                fontSize: '11px',
                                color: '#666',
                                backgroundColor: '#f0f0f0',
                                padding: '2px 6px',
                                borderRadius: '4px',
                                marginRight: '8px',
                                fontWeight: 'normal',
                              }}
                            >
                              1Depth
                            </span>
                          )}
                          <span style={{ marginRight: '8px', fontSize: '16px' }}>{group.lv1Korean}</span>
                          <span
                            style={{
                              fontSize: '14px',
                              color: '#666',
                              fontWeight: 'normal',
                            }}
                          >
                            ({group.lv1English})
                          </span>
                        </div>

                        {/* 그룹별 상태 정보 (가이드 제외) */}
                        {group.lv1Korean !== '가이드' && (
                          <div
                            style={{
                              display: 'flex',
                              gap: '20px',
                              fontSize: '13px',
                              color: '#666',
                              marginRight: '40px',
                              alignItems: 'center',
                            }}
                          >
                            {(() => {
                              const groupStatusCounts = group.pages.reduce(
                                (acc, page) => {
                                  acc[page.status] = (acc[page.status] || 0) + 1;
                                  return acc;
                                },
                                {} as Record<string, number>
                              );

                              const totalGroupPages = group.pages.length;
                              const progressRate = totalGroupPages > 0 ? Math.round(((groupStatusCounts['2'] || 0) / totalGroupPages) * 100) : 0;

                              return (
                                <>
                                  <span
                                    style={{
                                      minWidth: '50px',
                                      textAlign: 'left',
                                    }}
                                  >
                                    전체:{' '}
                                    <strong
                                      style={{
                                        color: '#333',
                                        fontSize: '14px',
                                        display: 'inline-block',
                                        minWidth: '20px',
                                        textAlign: 'right',
                                      }}
                                    >
                                      {totalGroupPages}
                                    </strong>
                                  </span>
                                  <span
                                    style={{
                                      minWidth: '70px',
                                      textAlign: 'left',
                                    }}
                                  >
                                    검수요청:{' '}
                                    <strong
                                      style={{
                                        color: '#0066cc',
                                        fontSize: '14px',
                                        display: 'inline-block',
                                        minWidth: '20px',
                                        textAlign: 'right',
                                      }}
                                    >
                                      {groupStatusCounts['1'] || 0}
                                    </strong>
                                  </span>
                                  <span
                                    style={{
                                      minWidth: '70px',
                                      textAlign: 'left',
                                    }}
                                  >
                                    검수완료:{' '}
                                    <strong
                                      style={{
                                        color: '#28a745',
                                        fontSize: '14px',
                                        display: 'inline-block',
                                        minWidth: '20px',
                                        textAlign: 'right',
                                      }}
                                    >
                                      {groupStatusCounts['2'] || 0}
                                    </strong>
                                  </span>
                                  <span
                                    style={{
                                      minWidth: '70px',
                                      textAlign: 'left',
                                    }}
                                  >
                                    작업완료:{' '}
                                    <strong
                                      style={{
                                        color: '#ff7c00',
                                        fontSize: '14px',
                                        display: 'inline-block',
                                        minWidth: '20px',
                                        textAlign: 'right',
                                      }}
                                    >
                                      {groupStatusCounts['2'] || 0}
                                    </strong>
                                  </span>
                                  <span
                                    style={{
                                      minWidth: '70px',
                                      textAlign: 'left',
                                    }}
                                  >
                                    진척률:{' '}
                                    <strong
                                      style={{
                                        color: '#007bff',
                                        fontSize: '14px',
                                        display: 'inline-block',
                                        minWidth: '40px',
                                        textAlign: 'right',
                                      }}
                                    >
                                      {progressRate}%
                                    </strong>
                                  </span>
                                </>
                              );
                            })()}
                          </div>
                        )}
                      </div>
                      <div
                        style={{
                          width: '24px',
                          height: '24px',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          borderRadius: '4px',
                          backgroundColor: 'transparent',
                          transition: 'all 0.2s ease',
                          transform: expandedGroups.has(group.lv1Korean) ? 'rotate(180deg)' : 'rotate(0deg)',
                          pointerEvents: 'none',
                        }}
                      >
                        <svg width='12' height='8' viewBox='0 0 12 8' fill='none' xmlns='http://www.w3.org/2000/svg'>
                          <path d='M1 1.5L6 6.5L11 1.5' stroke='#666' strokeWidth='2' strokeLinecap='round' strokeLinejoin='round' />
                        </svg>
                      </div>
                    </div>

                    {/* 그룹 내용 (펼쳐졌을 때만 표시) */}
                    {expandedGroups.has(group.lv1Korean) && (
                      <div>
                        {/* 테이블 헤더 */}
                        <table
                          style={{
                            width: '100%',
                            borderCollapse: 'collapse',
                            fontSize: '15px',
                          }}
                        >
                          <thead>
                            <tr style={{ backgroundColor: '#ffffff' }}>
                              {group.lv1Korean !== '가이드' && (
                                <>
                                  <th
                                    style={{
                                      ...headerStyle,
                                      backgroundColor: '#ffffff',
                                      fontWeight: '600',
                                      width: '60px',
                                    }}
                                  >
                                    No
                                  </th>
                                  <th
                                    style={{
                                      ...headerStyle,
                                      backgroundColor: '#ffffff',
                                      fontWeight: '600',
                                      width: '180px',
                                      maxWidth: '180px',
                                    }}
                                  >
                                    화면ID
                                  </th>
                                </>
                              )}
                              <th
                                style={{
                                  ...headerStyle,
                                  backgroundColor: '#ffffff',
                                  fontWeight: '600',
                                }}
                              >
                                {group.lv1Korean === '가이드' ? '페이지명' : '2Depth'}
                              </th>
                              {group.lv1Korean !== '가이드' && (
                                <>
                                  <th
                                    style={{
                                      ...headerStyle,
                                      backgroundColor: '#ffffff',
                                      fontWeight: '600',
                                    }}
                                  >
                                    3Depth
                                  </th>
                                  <th
                                    style={{
                                      ...headerStyle,
                                      backgroundColor: '#ffffff',
                                      fontWeight: '600',
                                    }}
                                  >
                                    4Depth
                                  </th>
                                  <th
                                    style={{
                                      ...headerStyle,
                                      backgroundColor: '#ffffff',
                                      fontWeight: '600',
                                    }}
                                  >
                                    5Depth
                                  </th>
                                  <th
                                    style={{
                                      ...headerStyle,
                                      backgroundColor: '#ffffff',
                                      fontWeight: '600',
                                    }}
                                  >
                                    화면명
                                  </th>
                                </>
                              )}
                              {group.lv1Korean !== '가이드' && (
                                <th
                                  style={{
                                    ...headerStyle,
                                    backgroundColor: '#ffffff',
                                    fontWeight: '600',
                                    width: '80px',
                                    maxWidth: '80px',
                                  }}
                                >
                                  타입
                                </th>
                              )}
                              <th
                                style={{
                                  ...headerStyle,
                                  backgroundColor: '#ffffff',
                                  fontWeight: '600',
                                  width: '100px',
                                  maxWidth: '100px',
                                }}
                              >
                                링크
                              </th>
                              {group.lv1Korean !== '가이드' && (
                                <th
                                  style={{
                                    ...headerStyle,
                                    backgroundColor: '#ffffff',
                                    fontWeight: '600',
                                    width: '80px',
                                    maxWidth: '80px',
                                  }}
                                >
                                  상태
                                </th>
                              )}
                            </tr>
                          </thead>
                          <tbody>
                            {group.pages.map((page: PageData, pageIndex: number) => {
                              const { lv1, lv2Korean, lv3Korean, lv4Korean, lv5Korean } = extractLevelInfo(page.depth, page.screenName);
                              const lv2Eng = businessMapping[lv1]?.lv2[lv2Korean] || '';

                              return (
                                <tr
                                  key={page.screenId}
                                  style={{
                                    borderBottom: '1px solid #f0f0f0',
                                    backgroundColor: group.lv1Korean === '가이드' ? '#ffffff' : getStatusBackgroundColor(page.status),
                                    cursor: 'pointer',
                                  }}
                                  onMouseEnter={e => {
                                    (e.currentTarget as HTMLElement).style.backgroundColor = '#e2e6ea';
                                  }}
                                  onMouseLeave={e => {
                                    (e.currentTarget as HTMLElement).style.backgroundColor = group.lv1Korean === '가이드' ? '#ffffff' : getStatusBackgroundColor(page.status);
                                  }}
                                >
                                  {group.lv1Korean !== '가이드' && (
                                    <>
                                      <td
                                        style={{
                                          ...cellStyle,
                                          width: '60px',
                                        }}
                                      >
                                        {pageIndex + 1}
                                      </td>
                                      <td
                                        style={{
                                          ...cellStyle,
                                          textAlign: 'left',
                                          width: '180px',
                                          maxWidth: '180px',
                                          overflow: 'hidden',
                                          textOverflow: 'ellipsis',
                                          whiteSpace: 'nowrap',
                                        }}
                                      >
                                        {page.screenId}
                                      </td>
                                    </>
                                  )}
                                  <td
                                    style={{
                                      ...cellStyle,
                                      textAlign: 'left',
                                    }}
                                  >
                                    <div>{lv2Korean}</div>
                                    {group.lv1Korean !== '가이드' && (
                                      <div
                                        style={{
                                          fontSize: '13px',
                                          color: '#666',
                                          marginTop: '2px',
                                        }}
                                      >
                                        {lv2Eng}
                                      </div>
                                    )}
                                  </td>
                                  {group.lv1Korean !== '가이드' && (
                                    <>
                                      <td
                                        style={{
                                          ...cellStyle,
                                          textAlign: 'left',
                                        }}
                                      >
                                        {lv3Korean}
                                      </td>
                                      <td
                                        style={{
                                          ...cellStyle,
                                          textAlign: 'left',
                                        }}
                                      >
                                        {lv4Korean}
                                      </td>
                                      <td
                                        style={{
                                          ...cellStyle,
                                          textAlign: 'left',
                                        }}
                                      >
                                        {lv5Korean}
                                      </td>
                                      <td
                                        style={{
                                          ...cellStyle,
                                          textAlign: 'left',
                                        }}
                                      >
                                        {page.screenName}
                                      </td>
                                    </>
                                  )}
                                  {group.lv1Korean !== '가이드' && (
                                    <td
                                      style={{
                                        ...cellStyle,
                                        width: '80px',
                                        maxWidth: '80px',
                                        overflow: 'hidden',
                                        textOverflow: 'ellipsis',
                                        whiteSpace: 'nowrap',
                                      }}
                                    >
                                      {page.type}
                                    </td>
                                  )}
                                  <td
                                    style={{
                                      ...cellStyle,
                                      width: '100px',
                                      maxWidth: '100px',
                                    }}
                                  >
                                    <div
                                      style={{
                                        display: 'flex',
                                        flexDirection: 'column',
                                        gap: '2px',
                                      }}
                                    >
                                      <button
                                        onClick={() => handleNavigate(page.path)}
                                        style={{
                                          padding: '3px 6px',
                                          fontSize: '11px',
                                          backgroundColor: '#0066cc',
                                          color: 'white',
                                          border: 'none',
                                          borderRadius: '3px',
                                          cursor: 'pointer',
                                          width: '100%',
                                        }}
                                        onMouseOver={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#0052a3')}
                                        onMouseOut={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#0066cc')}
                                      >
                                        {page.screenId === 'DT_010101'
                                          ? '그리드 타입'
                                          : page.screenId === 'MD_010101'
                                            ? '그리드 타입'
                                            : page.screenId === 'MD_030101'
                                              ? '그리드 타입'
                                              : page.screenId === 'DT_020201'
                                                ? '그리드 타입'
                                                : page.screenId === 'DT_020301'
                                                  ? '그리드 타입'
                                                  : page.screenId === 'DT_030201'
                                                    ? '그리드 타입'
                                                    : page.screenId === 'AG_010101'
                                                      ? '그리드 타입'
                                                      : page.screenId === 'AG_020201'
                                                        ? '그리드 타입'
                                                        : page.screenId === 'HM_010101'
                                                          ? '이동'
                                                          : page.screenId === 'MD_030101_P03'
                                                            ? 'Presets'
                                                            : '이동'}
                                      </button>
                                      {page.screenId === 'MD_030101_P03' && (
                                        <button
                                          onClick={() => handleNavigate('model/MD_030101_P03-1')}
                                          style={{
                                            padding: '3px 6px',
                                            fontSize: '11px',
                                            backgroundColor: '#ffc107',
                                            color: 'black',
                                            border: 'none',
                                            borderRadius: '3px',
                                            cursor: 'pointer',
                                            width: '100%',
                                            marginTop: '2px',
                                          }}
                                          onMouseOver={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#e0a800')}
                                          onMouseOut={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#ffc107')}
                                        >
                                          Custom
                                        </button>
                                      )}
                                    </div>
                                  </td>
                                  {group.lv1Korean !== '가이드' && (
                                    <td
                                      style={{
                                        ...cellStyle,
                                        width: '80px',
                                        maxWidth: '80px',
                                        overflow: 'hidden',
                                        textOverflow: 'ellipsis',
                                        whiteSpace: 'nowrap',
                                      }}
                                    >
                                      {(() => {
                                        switch (page.status) {
                                          case '0':
                                            return '미작업';
                                          case '1':
                                            return '검수요청';
                                          case '2':
                                            return '검수완료';
                                          default:
                                            return '확인필요';
                                        }
                                      })()}
                                    </td>
                                  )}
                                </tr>
                              );
                            })}
                          </tbody>
                        </table>
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
        </div>
      </div>

      {groupedPages.length === 0 && (
        <div
          style={{
            padding: '40px',
            textAlign: 'center',
            color: '#666',
          }}
        >
          검색 결과가 없습니다.
        </div>
      )}

      {/* 가이드 그룹 */}
      {groupedPages
        .filter(g => g.lv1Korean === '가이드')
        .map(guideGroup => (
          <div key='guide-section' style={{ marginBottom: '40px', marginTop: '40px' }}>
            <div
              onClick={() => setIsGuideVisible(!isGuideVisible)}
              style={{
                fontSize: '20px',
                fontWeight: 'bold',
                color: '#333',
                padding: '10px 0',
                borderBottom: '2px solid #0066cc',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                cursor: 'pointer',
              }}
            >
              <span>PUB GUIDE</span>
              <div
                style={{
                  width: '24px',
                  height: '24px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  borderRadius: '4px',
                  backgroundColor: 'transparent',
                  transition: 'all 0.2s ease',
                  transform: isGuideVisible ? 'rotate(180deg)' : 'rotate(0deg)',
                  pointerEvents: 'none',
                }}
              >
                <svg width='12' height='8' viewBox='0 0 12 8' fill='none' xmlns='http://www.w3.org/2000/svg'>
                  <path d='M1 1.5L6 6.5L11 1.5' stroke='#666' strokeWidth='2' strokeLinecap='round' strokeLinejoin='round' />
                </svg>
              </div>
            </div>
            {isGuideVisible && (
              <div style={{ backgroundColor: 'white', border: '1px solid #ddd' }}>
                <table
                  style={{
                    width: '100%',
                    borderCollapse: 'collapse',
                    fontSize: '15px',
                  }}
                >
                  <tbody>
                    {guideGroup.pages.map((page: PageData) => {
                      const { lv2Korean } = extractLevelInfo(page.depth, page.screenName);

                      return (
                        <tr
                          key={page.screenId}
                          style={{
                            borderBottom: '1px solid #f0f0f0',
                            backgroundColor: '#ffffff',
                            cursor: 'pointer',
                          }}
                          onClick={() => handleNavigate(page.path)}
                          onMouseEnter={e => {
                            (e.currentTarget as HTMLElement).style.backgroundColor = '#e2e6ea';
                          }}
                          onMouseLeave={e => {
                            (e.currentTarget as HTMLElement).style.backgroundColor = '#ffffff';
                          }}
                        >
                          <td style={{ ...cellStyle, textAlign: 'left' }}>
                            <div>{lv2Korean}</div>
                          </td>
                          <td
                            style={{
                              ...cellStyle,
                              width: '60px',
                              maxWidth: '60px',
                            }}
                          >
                            <button
                              onClick={e => {
                                e.stopPropagation();
                                handleNavigate(page.path);
                              }}
                              style={{
                                padding: '4px 8px',
                                fontSize: '12px',
                                backgroundColor: '#0066cc',
                                color: 'white',
                                border: 'none',
                                borderRadius: '3px',
                                cursor: 'pointer',
                              }}
                              onMouseOver={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#0052a3')}
                              onMouseOut={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#0066cc')}
                            >
                              이동
                            </button>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        ))}
    </div>
  );
};

const headerStyle = {
  padding: '12px 8px',
  textAlign: 'center' as const,
  fontWeight: 'bold',
  border: '1px solid #ddd',
  backgroundColor: '#f8f9fa',
  fontSize: '15px',
};

const cellStyle = {
  padding: '10px 8px',
  textAlign: 'center' as const,
  border: '1px solid #eee',
  fontSize: '15px',
};
