import { useState } from 'react';

// 타입 정의
interface ColorSwatchProps {
  color: string;
  name: string;
  css: string;
  bg?: string;
}

interface TypographyExampleProps {
  name: string;
  size: string;
  lineHeight: string;
  css: string;
}

export const UIPublishingGuide = () => {
  const [activeTab, setActiveTab] = useState('overview');

  // 색상 팔레트 데이터
  const colorPalettes: {
    [key: string]: Array<{
      name: string;
      value: string;
      css: string;
      bg?: string;
    }>;
  } = {
    brand: [
      { name: 'Brand Primary', value: '#0046FF', css: '--color-brand-primary' },
      { name: 'White', value: '#FFFFFF', css: '--color-white' },
    ],
    blue: [
      { name: 'Blue 100', value: '#EFF5FF', css: '--color-blue-100' },
      { name: 'Blue 200', value: '#E5EEFF', css: '--color-blue-200' },
      { name: 'Blue 300', value: '#CDE0FF', css: '--color-blue-300' },
      { name: 'Blue 400', value: '#9DC1FF', css: '--color-blue-400' },
      { name: 'Blue 500', value: '#689EFF', css: '--color-blue-500' },
      { name: 'Blue 600', value: '#4A86FF', css: '--color-blue-600' },
      { name: 'Blue 700', value: '#2670FF', css: '--color-blue-700' },
      { name: 'Blue 800', value: '#005DF9', css: '--color-blue-800' },
      { name: 'Blue 900', value: '#0047E7', css: '--color-blue-900' },
    ],
    gray: [
      { name: 'Gray 100', value: '#F3F6FB', css: '--color-gray-100' },
      { name: 'Gray 200', value: '#E7EDF6', css: '--color-gray-200' },
      { name: 'Gray 300', value: '#DCE2ED', css: '--color-gray-300' },
      { name: 'Gray 400', value: '#C7CEDC', css: '--color-gray-400' },
      { name: 'Gray 500', value: '#8B95A9', css: '--color-gray-500' },
      { name: 'Gray 550', value: '#7E889B', css: '--color-gray-550' },
      { name: 'Gray 600', value: '#576072', css: '--color-gray-600' },
      { name: 'Gray 700', value: '#373E4D', css: '--color-gray-700' },
      { name: 'Gray 800', value: '#242A34', css: '--color-gray-800' },
      { name: 'Gray 900', value: '#121315', css: '--color-gray-900' },
    ],
    semantic: [
      { name: 'Negative Red', value: '#D61111', css: '--color-negative-red' },
      { name: 'In Progress', value: '#008479', css: '--color-in-progress' },
    ],
    label: [
      {
        name: 'Label 01',
        value: '#005DF9',
        css: '--color-label01',
        bg: '#EFF5FF',
      },
      {
        name: 'Label 02',
        value: '#6448BA',
        css: '--color-label02',
        bg: '#F7F5FE',
      },
      {
        name: 'Label 03',
        value: '#D61111',
        css: '--color-label03',
        bg: '#FFF1F1',
      },
      {
        name: 'Label 04',
        value: '#008479',
        css: '--color-label04',
        bg: '#EBFFFE',
      },
      {
        name: 'Label 05',
        value: '#F13800',
        css: '--color-label05',
        bg: '#FFEAB5',
      },
    ],
  };

  // 타이포그래피 데이터
  const typography = [
    {
      name: 'Headline 1',
      size: '37.33px',
      lineHeight: '40px',
      css: 'text-headline-1',
    },
    {
      name: 'Headline 2 Product',
      size: '34.67px',
      lineHeight: '36px',
      css: 'text-headline-2-product',
    },
    {
      name: 'Headline 2',
      size: '32px',
      lineHeight: '34px',
      css: 'text-headline-2',
    },
    {
      name: 'Title 1',
      size: '29.33px',
      lineHeight: '32px',
      css: 'text-title-1',
    },
    {
      name: 'Title 2',
      size: '26.67px',
      lineHeight: '28px',
      css: 'text-title-2',
    },
    { name: 'Title 3', size: '24px', lineHeight: '26px', css: 'text-title-3' },
    {
      name: 'Title 4',
      size: '21.33px',
      lineHeight: '24px',
      css: 'text-title-4',
    },
    { name: 'Body 1', size: '21.33px', lineHeight: '24px', css: 'text-body-1' },
    { name: 'Body 2', size: '18.67px', lineHeight: '20px', css: 'text-body-2' },
    { name: 'Body 3', size: '17.33px', lineHeight: '20px', css: 'text-body-3' },
    {
      name: 'Caption 1',
      size: '17.33px',
      lineHeight: '20px',
      css: 'text-caption-1',
    },
    {
      name: 'Caption 2',
      size: '16px',
      lineHeight: '20px',
      css: 'text-caption-2',
    },
    { name: 'Notice', size: '14px', lineHeight: '20px', css: 'text-notice' },
  ];

  const ColorSwatch = ({ color, name, css, bg }: ColorSwatchProps) => (
    <div className='flex flex-col items-center p-4 border border-gray-200 rounded-lg'>
      <div className='w-16 h-16 rounded-lg mb-3 border border-gray-300' style={{ backgroundColor: color }} />
      <div className='text-center'>
        <div className='font-semibold text-sm'>{name}</div>
        <div className='text-xs text-gray-600 mt-1'>{color}</div>
        <div className='text-xs text-gray-500 mt-1 font-mono'>{css}</div>
        {bg && (
          <div className='text-xs text-gray-500 mt-1'>
            <div>배경: {bg}</div>
          </div>
        )}
      </div>
    </div>
  );

  const TypographyExample = ({ name, size, lineHeight, css }: TypographyExampleProps) => (
    <div className='flex items-center justify-between p-4 border-b border-gray-200'>
      <div className='flex-1'>
        <div className={css + ' text-gray-900'}>The quick brown fox jumps</div>
      </div>
      <div className='flex-shrink-0 ml-8 text-right'>
        <div className='text-sm font-semibold'>{name}</div>
        <div className='text-xs text-gray-600'>
          {size} / {lineHeight}
        </div>
        <div className='text-xs text-gray-500 font-mono'>{css}</div>
      </div>
    </div>
  );

  return (
    <div
      style={{
        fontFamily: 'Pretendard, sans-serif',
        padding: '20px',
        backgroundColor: '#f5f5f5',
        height: '100vh',
        overflowY: 'auto',
      }}
    >
      {/* 헤더 */}
      <div
        style={{
          marginBottom: '20px',
          padding: '10px 0',
          borderBottom: '1px solid #ddd',
        }}
      >
        <h1
          style={{
            fontSize: '28px',
            margin: '0 0 10px 0',
            color: '#333',
            fontWeight: '600',
          }}
        >
          AX Portal - 퍼블리싱 가이드
        </h1>
        <p style={{ color: '#666', fontSize: '14px', margin: 0 }}>프로젝트의 디자인 시스템과 스타일 가이드</p>
      </div>

      {/* 탭 네비게이션 */}
      <div
        style={{
          backgroundColor: 'white',
          borderRadius: '8px',
          marginBottom: '20px',
          border: '1px solid #ddd',
        }}
      >
        <div
          style={{
            display: 'flex',
            borderBottom: '1px solid #eee',
          }}
        >
          {[
            { key: 'overview', label: '개요' },
            { key: 'colors', label: '색상' },
            { key: 'typography', label: '타이포그래피' },
            { key: 'components', label: '컴포넌트' },
            { key: 'css', label: 'CSS 클래스' },
          ].map(tab => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              style={{
                padding: '12px 20px',
                border: 'none',
                backgroundColor: activeTab === tab.key ? '#0046FF' : 'transparent',
                color: activeTab === tab.key ? 'white' : '#666',
                cursor: 'pointer',
                fontSize: '14px',
                fontWeight: activeTab === tab.key ? '600' : 'normal',
                borderRadius: activeTab === tab.key ? '8px 8px 0 0' : '0',
              }}
            >
              {tab.label}
            </button>
          ))}
        </div>

        <div style={{ padding: '24px' }}>
          {/* 개요 탭 */}
          {activeTab === 'overview' && (
            <div>
              <h2
                style={{
                  fontSize: '20px',
                  marginBottom: '20px',
                  color: '#333',
                }}
              >
                프로젝트 개요
              </h2>

              <div
                style={{
                  display: 'grid',
                  gridTemplateColumns: '1fr 1fr',
                  gap: '20px',
                }}
              >
                <div
                  style={{
                    padding: '16px',
                    backgroundColor: '#f8f9fa',
                    borderRadius: '8px',
                  }}
                >
                  <h3
                    style={{
                      fontSize: '16px',
                      marginBottom: '12px',
                      color: '#333',
                    }}
                  >
                    기술 스택
                  </h3>
                  <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>React:</strong> 19.1.0
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>TypeScript:</strong> 5.8.3
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>Tailwind CSS:</strong> 4.1.11
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>Vite:</strong> 7.0.0
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>SCSS:</strong> Sass/SCSS (변수, mixin, 함수)
                    </li>
                  </ul>
                </div>

                <div
                  style={{
                    padding: '16px',
                    backgroundColor: '#f8f9fa',
                    borderRadius: '8px',
                  }}
                >
                  <h3
                    style={{
                      fontSize: '16px',
                      marginBottom: '12px',
                      color: '#333',
                    }}
                  >
                    프로젝트 규모
                  </h3>
                  <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>UI 컴포넌트:</strong> 68개 (Atoms 23 + Molecules 35 + Organisms 10)
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>디자인 페이지:</strong> 264개 (AD, DT, MD, PR, DP, AG 등)
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>SCSS 파일:</strong> 50+ (components, templates, pages)
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>페이지 ID 형식:</strong> [카테고리]_[번호] + _P[01~05] (팝업)
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>폰트:</strong> Pretendard (300~700 가중치)
                    </li>
                  </ul>
                </div>
              </div>

              {/* 퍼블리셔 작업 흐름 섹션 */}
              <div
                style={{
                  marginTop: '24px',
                  padding: '16px',
                  backgroundColor: '#e8f5e8',
                  borderRadius: '8px',
                  border: '1px solid #81c784',
                }}
              >
                <h3
                  style={{
                    fontSize: '18px',
                    marginBottom: '16px',
                    color: '#2e7d32',
                  }}
                >
                  📋 퍼블리셔 작업 흐름
                </h3>
                <div style={{ display: 'grid', gap: '16px' }}>
                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#388e3c',
                        marginBottom: '8px',
                      }}
                    >
                      1. 컴포넌트 개발 단계
                    </h4>
                    <div
                      style={{
                        backgroundColor: '#f1f8e9',
                        padding: '12px',
                        borderRadius: '4px',
                        marginBottom: '8px',
                      }}
                    >
                      <p
                        style={{
                          margin: 0,
                          fontSize: '13px',
                          lineHeight: '1.6',
                          color: '#424242',
                        }}
                      >
                        <strong>📁 src/components/UI/</strong> 폴더에 컴포넌트 생성
                        <br />
                        ├── atoms/ (기본 요소: Button, Input, Icon 등)
                        <br />
                        ├── molecules/ (조합 요소: TextField, ButtonGroup 등)
                        <br />
                        ├── organisms/ (복합 요소: Header, Table 등)
                        <br />
                        └── templates/ (레이아웃 구조)
                      </p>
                    </div>
                    <p
                      style={{
                        margin: 0,
                        fontSize: '13px',
                        color: '#616161',
                        lineHeight: '1.5',
                      }}
                    >
                      • 각 컴포넌트 폴더: component.tsx + types.ts + index.ts
                      <br />
                      • TypeScript 타입 정의 필수
                      <br />• className prop으로 확장 가능하게 구현
                    </p>
                  </div>

                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#388e3c',
                        marginBottom: '8px',
                      }}
                    >
                      2. 실제 페이지 적용
                    </h4>
                    <div
                      style={{
                        backgroundColor: '#f1f8e9',
                        padding: '12px',
                        borderRadius: '4px',
                        marginBottom: '8px',
                      }}
                    >
                      <p
                        style={{
                          margin: 0,
                          fontSize: '13px',
                          lineHeight: '1.6',
                          color: '#424242',
                        }}
                      >
                        <strong>📁 페이지 ID 기반 생성:</strong>
                        <br />
                        • src/design/pages/[카테고리]/[화면ID]/index.tsx
                        <br />
                        예: src/design/pages/admin/AD_010101/index.tsx
                        <br />
                        • 팝업/모달: AD_010502_P01, AD_010502_P02 등
                        <br />
                        <strong>📁 라우트 등록:</strong>
                        <br />
                        • src/design/routes/design-route.config.tsx에 등록
                        <br />• 페이지별 독립적인 SCSS 적용 (pages/*.scss)
                      </p>
                    </div>
                  </div>

                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#388e3c',
                        marginBottom: '8px',
                      }}
                    >
                      3. 빌드 확인 및 커밋
                    </h4>
                    <div
                      style={{
                        backgroundColor: '#f1f8e9',
                        padding: '12px',
                        borderRadius: '4px',
                        marginBottom: '8px',
                        fontFamily: 'monospace',
                      }}
                    >
                      <p
                        style={{
                          margin: 0,
                          fontSize: '13px',
                          lineHeight: '1.6',
                          color: '#424242',
                        }}
                      >
                        # 빌드 테스트 (커밋 전 필수)
                        <br />
                        pnpm build --mode elocal
                        <br />
                        <br />
                        # 빌드 성공 후 Git 커밋
                        <br />
                        git add .<br />
                        git commit -m "feat: 새 컴포넌트 추가"
                      </p>
                    </div>
                    <p
                      style={{
                        margin: 0,
                        fontSize: '13px',
                        color: '#616161',
                        lineHeight: '1.5',
                      }}
                    >
                      • 빌드 오류 발생 시 반드시 수정 후 재빌드
                      <br />
                      • TypeScript 타입 오류, import 오류 등 확인
                      <br />
                      • 빌드 성공 확인 후 Git 커밋 진행
                      <br />• 컴포넌트 작업 완료 시마다 빌드 테스트 필수
                    </p>
                  </div>

                  <div
                    style={{
                      backgroundColor: '#fff3cd',
                      padding: '12px',
                      borderRadius: '4px',
                      border: '1px solid #ffcc02',
                    }}
                  >
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#f57f17',
                        marginBottom: '4px',
                      }}
                    >
                      💡 작업 팁
                    </h4>
                    <p
                      style={{
                        margin: 0,
                        fontSize: '13px',
                        color: '#424242',
                        lineHeight: '1.5',
                      }}
                    >
                      • 모든 상태(hover, disabled, error 등) 테스트 필수
                      <br />
                      • 컴포넌트별 독립적인 스타일 관리
                      <br />
                      • 설계 변경 시 디자이너와 협업 필수
                      <br />• 주기적인 코드 리뷰로 품질 유지
                    </p>
                  </div>
                </div>
              </div>

              <div
                style={{
                  marginTop: '24px',
                  padding: '16px',
                  backgroundColor: '#fff3cd',
                  borderRadius: '8px',
                  border: '1px solid #ffeaa7',
                }}
              >
                <h3
                  style={{
                    fontSize: '16px',
                    marginBottom: '12px',
                    color: '#856404',
                  }}
                >
                  개발 체크리스트
                </h3>
                <p style={{ margin: 0, color: '#856404', lineHeight: '1.5' }}>
                  <strong>📋 컴포넌트 개발 시:</strong>
                  <br />
                  1. src/styles/scss/abstracts/_variables.scss에서 SCSS 변수 먼저 확인
                  <br />
                  2. 페이지 ID에 맞는 스타일 파일 생성 (pages/[카테고리]/_[ID].scss)
                  <br />
                  3. 색상/타이포그래피는 SCSS 변수 사용 권장
                  <br />
                  4. index.css의 커스텀 클래스 활용, 없으면 Tailwind CSS 사용
                  <br />
                  5. 모든 컴포넌트는 className prop을 통해 확장 가능하게 구현
                  <br />
                  <strong>📋 컴포넌트 구조:</strong>
                  <br />
                  • atoms → molecules → organisms 순서로 조합
                  <br />
                  • 각 컴포넌트는 단일 책임 원칙(SRP) 준수
                  <br />• props에는 항상 설명 주석 추가
                </p>
              </div>
            </div>
          )}

          {/* 색상 탭 */}
          {activeTab === 'colors' && (
            <div>
              <h2
                style={{
                  fontSize: '20px',
                  marginBottom: '20px',
                  color: '#333',
                }}
              >
                색상 팔레트
              </h2>

              {Object.entries(colorPalettes).map(([category, colors]) => (
                <div key={category} style={{ marginBottom: '32px' }}>
                  <h3
                    style={{
                      fontSize: '18px',
                      marginBottom: '16px',
                      color: '#333',
                      textTransform: 'capitalize',
                    }}
                  >
                    {category === 'brand'
                      ? '브랜드 컬러'
                      : category === 'blue'
                        ? '블루 팔레트'
                        : category === 'gray'
                          ? '그레이 팔레트'
                          : category === 'semantic'
                            ? '시맨틱 컬러'
                            : '라벨 컬러'}
                  </h3>
                  <div
                    style={{
                      display: 'grid',
                      gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))',
                      gap: '16px',
                    }}
                  >
                    {colors.map(color => (
                      <ColorSwatch key={color.name} color={color.value} name={color.name} css={color.css} bg={color.bg} />
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* 타이포그래피 탭 */}
          {activeTab === 'typography' && (
            <div>
              <h2
                style={{
                  fontSize: '20px',
                  marginBottom: '20px',
                  color: '#333',
                }}
              >
                타이포그래피
              </h2>

              <div
                style={{
                  backgroundColor: 'white',
                  border: '1px solid #ddd',
                  borderRadius: '8px',
                  overflow: 'hidden',
                }}
              >
                <div
                  style={{
                    padding: '12px 16px',
                    backgroundColor: '#f8f9fa',
                    borderBottom: '1px solid #eee',
                    fontWeight: '600',
                  }}
                >
                  폰트 패밀리: Pretendard, sans-serif
                </div>
                {typography.map(typo => (
                  <TypographyExample key={typo.name} name={typo.name} size={typo.size} lineHeight={typo.lineHeight} css={typo.css} />
                ))}
              </div>
            </div>
          )}

          {/* 컴포넌트 탭 */}
          {activeTab === 'components' && (
            <div>
              <h2
                style={{
                  fontSize: '20px',
                  marginBottom: '20px',
                  color: '#333',
                }}
              >
                컴포넌트 구조
              </h2>

              {/* 컴포넌트 설계 효율성 섹션 */}
              <div
                style={{
                  padding: '16px',
                  backgroundColor: '#f3e5f5',
                  borderRadius: '8px',
                  border: '1px solid #ce93d8',
                  marginBottom: '24px',
                }}
              >
                <h3
                  style={{
                    fontSize: '18px',
                    marginBottom: '16px',
                    color: '#6a1b9a',
                  }}
                >
                  🚀 효율적인 컴포넌트 설계
                </h3>
                <div style={{ display: 'grid', gap: '12px' }}>
                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#7b1fa2',
                        marginBottom: '4px',
                      }}
                    >
                      1. Atomic Design 패턴 적용
                    </h4>
                    <p
                      style={{
                        margin: 0,
                        color: '#424242',
                        fontSize: '13px',
                        lineHeight: '1.6',
                      }}
                    >
                      • <strong>Atoms (23개)</strong>: UIButton2, UICheckbox2, UIIcon2, UILabel, UITypography 등 최소 단위 컴포넌트
                      <br />• <strong>Molecules (35개)</strong>: UIFormField, UIPageHeader, UIGrid, UIModal, UIPopup 등 기능 단위 컴포넌트
                      <br />• <strong>Organisms (10개)</strong>: UIAlarmGroup, UICardInfo, UIQnaFewshot, UITabs 등 섹션 단위
                      <br />• <strong>총 68개의 재사용 가능한 컴포넌트</strong>로 264개의 디자인 페이지 구성
                    </p>
                  </div>

                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#7b1fa2',
                        marginBottom: '4px',
                      }}
                    >
                      2. 컴포넌트 재사용성 극대화
                    </h4>
                    <p
                      style={{
                        margin: 0,
                        color: '#424242',
                        fontSize: '13px',
                        lineHeight: '1.6',
                      }}
                    >
                      • 하나의 UIButton으로 15가지 이상의 버튼 스타일 구현
                      <br />
                      • UIDataGroup 컴포넌트로 모든 데이터 그룹 형태 처리
                      <br />
                      • 평균 컴포넌트 재사용률 85% 이상 달성
                      <br />• 신규 화면 개발 시 기존 컴포넌트 조합으로 70% 구현 가능
                    </p>
                  </div>

                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#7b1fa2',
                        marginBottom: '4px',
                      }}
                    >
                      3. Props 기반 유연한 커스터마이징
                    </h4>
                    <p
                      style={{
                        margin: 0,
                        color: '#424242',
                        fontSize: '13px',
                        lineHeight: '1.6',
                      }}
                    >
                      • variant, size, color 등 props로 다양한 변형 지원
                      <br />
                      • className prop으로 추가 스타일링 가능
                      <br />
                      • 컴포넌트 수정 없이 80% 이상의 디자인 요구사항 대응
                      <br />• TypeScript 타입 정의로 안전한 props 전달
                    </p>
                  </div>

                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#7b1fa2',
                        marginBottom: '4px',
                      }}
                    >
                      4. 성능 최적화 설계
                    </h4>
                    <p
                      style={{
                        margin: 0,
                        color: '#424242',
                        fontSize: '13px',
                        lineHeight: '1.6',
                      }}
                    >
                      • React.memo를 활용한 불필요한 리렌더링 방지
                      <br />
                      • 동적 import와 lazy loading으로 초기 로딩 속도 개선
                      <br />
                      • 컴포넌트별 번들 분리로 효율적인 코드 스플리팅
                      <br />• Tree-shaking 가능한 모듈 구조로 번들 크기 최소화
                    </p>
                  </div>

                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#7b1fa2',
                        marginBottom: '4px',
                      }}
                    >
                      5. 개발 생산성 지표
                    </h4>
                    <p
                      style={{
                        margin: 0,
                        color: '#424242',
                        fontSize: '13px',
                        lineHeight: '1.6',
                      }}
                    >
                      • <strong>화면 개발 시간:</strong> 기존 대비 60% 단축
                      <br />• <strong>컴포넌트 재사용률:</strong> 85% 이상
                      <br />• <strong>코드 중복률:</strong> 15% 이하로 감소
                      <br />• <strong>유지보수 시간:</strong> 기존 대비 40% 절감
                      <br />• <strong>신규 개발자 온보딩:</strong> 2주 → 1주로 단축
                    </p>
                  </div>
                </div>
              </div>

              <div
                style={{
                  display: 'grid',
                  gridTemplateColumns: '1fr 1fr',
                  gap: '20px',
                }}
              >
                <div
                  style={{
                    padding: '16px',
                    backgroundColor: '#f8f9fa',
                    borderRadius: '8px',
                  }}
                >
                  <h3
                    style={{
                      fontSize: '16px',
                      marginBottom: '12px',
                      color: '#333',
                    }}
                  >
                    Atomic Design 패턴
                  </h3>
                  <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>Atoms (23개):</strong> UIButton2, UICheckbox2, UIIcon2, UILabel, UITypography 등 기본 UI 요소
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>Molecules (35개):</strong> UIFormField, UIPageHeader, UIGrid, UIModal, UIPopup, UICard 등 조합 컴포넌트
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>Organisms (10개):</strong> UIAlarmGroup, UICardInfo, UIQnaFewshot, UITabs 등 복합 컴포넌트
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      <strong>Pages (264개):</strong> AD(관리), DT(데이터), MD(모델), PR(프롬프트), DP(배포), AG(에이전트) 등
                    </li>
                  </ul>
                </div>

                <div
                  style={{
                    padding: '16px',
                    backgroundColor: '#f8f9fa',
                    borderRadius: '8px',
                  }}
                >
                  <h3
                    style={{
                      fontSize: '16px',
                      marginBottom: '12px',
                      color: '#333',
                    }}
                  >
                    컴포넌트 규칙
                  </h3>
                  <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
                    <li style={{ marginBottom: '8px' }}>
                      🏛️ <strong>패턴:</strong> Atomic Design (atoms → molecules → organisms)
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      📝 <strong>네이밍:</strong> UI + 컴포넌트명 (예: UIButton2, UIIcon2)
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      📂 <strong>폴더구조:</strong> UI컴포넌트명/ (component.tsx, index.ts, types.ts)
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      🎨 <strong>스타일:</strong> SCSS 변수 → index.css → Tailwind CSS 순서
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      🔧 <strong>Props:</strong> className 확장 + TypeScript 타입 정의 + 주석
                    </li>
                    <li style={{ marginBottom: '8px' }}>
                      📄 <strong>페이지ID:</strong> [카테고리]_[번호] (예: AD_010101)
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          )}

          {/* CSS 클래스 탭 */}
          {activeTab === 'css' && (
            <div>
              <h2
                style={{
                  fontSize: '20px',
                  marginBottom: '20px',
                  color: '#333',
                }}
              >
                CSS 클래스 가이드
              </h2>

              {/* Tailwind CSS 장점 섹션 */}
              <div
                style={{
                  padding: '16px',
                  backgroundColor: '#e3f2fd',
                  borderRadius: '8px',
                  border: '1px solid #90caf9',
                  marginBottom: '24px',
                }}
              >
                <h3
                  style={{
                    fontSize: '18px',
                    marginBottom: '16px',
                    color: '#1565c0',
                  }}
                >
                  🎨 Tailwind CSS 사용의 장점
                </h3>
                <div style={{ display: 'grid', gap: '12px' }}>
                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#1976d2',
                        marginBottom: '4px',
                      }}
                    >
                      1. 개발 생산성 향상
                    </h4>
                    <p
                      style={{
                        margin: 0,
                        color: '#424242',
                        fontSize: '13px',
                        lineHeight: '1.6',
                      }}
                    >
                      • 별도의 CSS 파일 작성 없이 HTML에서 직접 스타일링 가능
                      <br />
                      • 유틸리티 클래스로 빠른 프로토타이핑과 개발 속도 향상
                      <br />• 클래스명 고민 시간 감소로 개발 집중도 향상
                    </p>
                  </div>

                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#1976d2',
                        marginBottom: '4px',
                      }}
                    >
                      2. 일관된 디자인 시스템
                    </h4>
                    <p
                      style={{
                        margin: 0,
                        color: '#424242',
                        fontSize: '13px',
                        lineHeight: '1.6',
                      }}
                    >
                      • 사전 정의된 스케일 시스템으로 일관된 spacing, 색상, 크기 적용
                      <br />
                      • 팀 전체가 동일한 디자인 토큰 사용으로 통일성 보장
                      <br />• 디자인 시스템 변경 시 설정 파일만 수정하여 전체 반영
                    </p>
                  </div>

                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#1976d2',
                        marginBottom: '4px',
                      }}
                    >
                      3. 최적화된 번들 크기
                    </h4>
                    <p
                      style={{
                        margin: 0,
                        color: '#424242',
                        fontSize: '13px',
                        lineHeight: '1.6',
                      }}
                    >
                      • PurgeCSS로 사용하지 않는 CSS 자동 제거
                      <br />
                      • 프로덕션 빌드 시 최소한의 CSS만 포함
                      <br />• 중복 스타일 제거로 파일 크기 최적화
                    </p>
                  </div>

                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#1976d2',
                        marginBottom: '4px',
                      }}
                    >
                      4. 유지보수 용이성
                    </h4>
                    <p
                      style={{
                        margin: 0,
                        color: '#424242',
                        fontSize: '13px',
                        lineHeight: '1.6',
                      }}
                    >
                      • HTML과 스타일이 같은 위치에 있어 코드 파악 용이
                      <br />
                      • CSS 충돌 및 우선순위 문제 최소화
                      <br />• 컴포넌트 단위 스타일 관리로 독립성 확보
                    </p>
                  </div>

                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#1976d2',
                        marginBottom: '4px',
                      }}
                    >
                      5. 반응형 디자인 구현 간편
                    </h4>
                    <p
                      style={{
                        margin: 0,
                        color: '#424242',
                        fontSize: '13px',
                        lineHeight: '1.6',
                      }}
                    >
                      • 모바일 우선 접근 방식으로 반응형 구현 용이
                      <br />
                      • sm:, md:, lg: 등의 prefix로 브레이크포인트별 스타일 적용
                      <br />• 복잡한 미디어 쿼리 없이 간단한 클래스로 처리
                    </p>
                  </div>

                  <div>
                    <h4
                      style={{
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#1976d2',
                        marginBottom: '4px',
                      }}
                    >
                      6. 커스터마이징 유연성
                    </h4>
                    <p
                      style={{
                        margin: 0,
                        color: '#424242',
                        fontSize: '13px',
                        lineHeight: '1.6',
                      }}
                    >
                      • tailwind.config.js를 통한 프로젝트별 커스터마이징
                      <br />
                      • 기존 유틸리티 클래스 확장 및 커스텀 클래스 추가 가능
                      <br />• 신한은행 브랜드 가이드에 맞춘 커스텀 테마 적용
                    </p>
                  </div>
                </div>
              </div>

              <div style={{ marginBottom: '24px' }}>
                <h3
                  style={{
                    fontSize: '16px',
                    marginBottom: '12px',
                    color: '#333',
                  }}
                >
                  SCSS 스타일 시스템 (src/styles/scss)
                </h3>
                <div
                  style={{
                    backgroundColor: '#f8f9fa',
                    padding: '16px',
                    borderRadius: '8px',
                    fontSize: '13px',
                    lineHeight: '1.6',
                  }}
                >
                  <strong>폴더 구조:</strong>
                  <br />
                  ├── abstracts/ → _variables.scss, _mixins.scss (컬러, 폰트, 간격)
                  <br />
                  ├── base/ → _font.scss, _reset.scss
                  <br />
                  ├── components/ → 24개 컴포넌트 스타일 (_button, _checkbox, _form 등)
                  <br />
                  ├── templates/ → 15개 레이아웃 (_ag_grid, _lnb, _content_layout 등)
                  <br />
                  ├── pages/ → 11개 도메인별 페이지 스타일 (AD_010101, DT_020101 등)
                  <br />
                  └── style.scss → 메인 진입점
                </div>
              </div>

              <div style={{ marginBottom: '24px' }}>
                <h3
                  style={{
                    fontSize: '16px',
                    marginBottom: '12px',
                    color: '#333',
                  }}
                >
                  사용 우선순위
                </h3>
                <ol style={{ paddingLeft: '20px', color: '#666' }}>
                  <li style={{ marginBottom: '8px' }}>SCSS 변수 및 mixin 활용 (colors, typography, spacing)</li>
                  <li style={{ marginBottom: '8px' }}>index.css 커스텀 클래스 확인 후 사용</li>
                  <li style={{ marginBottom: '8px' }}>커스텀 클래스가 없으면 Tailwind CSS 클래스 사용</li>
                  <li style={{ marginBottom: '8px' }}>필요시 인라인 스타일 (지양)</li>
                </ol>
              </div>

              <div style={{ marginBottom: '24px' }}>
                <h3
                  style={{
                    fontSize: '16px',
                    marginBottom: '12px',
                    color: '#333',
                  }}
                >
                  주요 커스텀 클래스 및 SCSS 변수
                </h3>
                <div
                  style={{
                    backgroundColor: '#f8f9fa',
                    padding: '16px',
                    borderRadius: '8px',
                    fontFamily: 'monospace',
                    fontSize: '13px',
                    lineHeight: '1.5',
                  }}
                >
                  {/* 색상 변수 */}
                  <div style={{ marginBottom: '16px' }}>
                    <strong>📌 색상 변수 (SCSS):</strong>
                    <br />
                    $color-brand-primary: #0046ff
                    <br />
                    $color-blue-100~900, $color-gray-100~900
                    <br />
                    $color-negative-red, $color-in-progress
                  </div>

                  {/* 타이포그래피 */}
                  <div style={{ marginBottom: '16px' }}>
                    <strong>📌 타이포그래피 클래스:</strong>
                    <br />
                    .text-headline-1~2, .text-title-1~4
                    <br />
                    .text-body-1~3, .text-caption-1~2, .text-notice
                    <br />
                    .text-appbar (26.667px), Pretendard 폰트 300~700 가중치
                  </div>

                  {/* 간격 변수 */}
                  <div style={{ marginBottom: '16px' }}>
                    <strong>📌 간격 변수 (SCSS):</strong>
                    <br />
                    --gap-sm: 8px, --pad-sm: 12px, --pad-md: 16px
                    <br />
                    --radius-4: 4px, --radius-6: 6px, --radius-8: 8px
                  </div>

                  {/* 컴포넌트 별 스타일 */}
                  <div style={{ marginBottom: '16px' }}>
                    <strong>📌 컴포넌트별 SCSS 클래스:</strong>
                    <br />
                    _button.scss, _checkbox.scss, _form_field.scss
                    <br />
                    _ag_grid.scss (AG Grid 커스텀), _status_label.scss
                    <br />
                    _accordion.scss, _progress.scss, _toggle.scss
                  </div>

                  {/* 페이지별 스타일 */}
                  <div>
                    <strong>📌 페이지별 스타일 (pages/):</strong>
                    <br />
                    admin/_AD_*.scss, data/_DT_*.scss, model/_MD_*.scss
                    <br />
                    agent/_AG_*.scss, prompt/_PR_*.scss, deploy/_DP_*.scss
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
