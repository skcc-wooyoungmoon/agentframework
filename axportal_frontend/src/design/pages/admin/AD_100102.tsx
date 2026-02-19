import { DesignLayout } from '../../components/DesignLayout';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UICode, UITypography } from '@/components/UI/atoms';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIFormField } from '@/components/UI/molecules';
import { UILabel } from '@/components/UI/atoms/UILabel';

export const AD_100102 = () => {
  return (
    <DesignLayout
      initialMenu={{ id: 'admin', label: '관리' }}
      initialSubMenu={{
        id: 'admin-users',
        label: '사용자 조회',
        icon: 'ico-lnb-menu-20-admin-user',
      }}
    >
      {/* 탭 내용 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='사용 이력 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* [251105_퍼블수정] 마크업 수정 */}
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                사용자 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251105_퍼블수정] width값 수정 */}
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
                          이름
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          김신한
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          프로젝트명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          슈퍼SOL 챗봇 개발
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          역할명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          개발자
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                접속 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251105_퍼블수정] width값 수정 */}
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
                          요청 일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.08.20 18:23:43
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          응답 결과
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UILabel variant='badge' intent='complete'>
                            성공
                          </UILabel>
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          접속 환경
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          Chrome / MacOS
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          접속 IP
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          123.45.02.8
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                요청 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251105_퍼블수정] width값 수정 */}
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
                          메뉴 경로
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          데이터 &gt; 지식/학습 데이터 관리
                          {/* [251111_퍼블수정] 타이틀명칭 변경 : 데이터 카탈로그 > 지식/학습 데이터 관리 */}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          요청 경로
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          ADXP
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          요청 메서드
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          POST
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          API URL
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          /api/admin/knowledge/tools
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                Request
              </UITypography>
              {/* 소스코드 영역 */}
              {/* 
                [251120_퍼블수정] : UICode 강제 자동줄바꿈 사용시 wordWrap={true} 세팅 필요 !
              */}
              <UICode
                value={
                  '{"content":[{"notiId":72,"title":"취약점처리확인123!","msg":"123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#\n1680","type":"보안 안내","useYn":"Y","expFrom":"2025-11-18 14:07:00","expTo":"9999-12-31 23:59:00","files":[{"fileId":82,"originalFilename":"image (4) (1) (1) (2).png","storedFilename":"a84d3a5e-3222-408e-afa7-a98ef1afaa0a.png","fileSize":"139333","contentType":"image/png","uploadDate":"2025-11-18T14:07:55.585964","useYn":"Y"}],"createAt":"2025-11-18 14:07:55","updateAt":"2025-11-18 14:08:40","createBy":"SGO1033491","updateBy":"SGO1033491","createdByName":null,"createdByDepts":null,"createdByPos":null,"updatedByName":null,"updatedByDepts":null,"updatedByPos":null},{"notiId":70,"title":"업로드취약점테스트","msg":"ㅇㅇ","type":"시스템 점검","useYn":"Y","expFrom":"2025-11-18 09:09:00","expTo":"9999-12-31 23:59:00","files":[{"fileId":80,"originalFilename":"image (4) (1) (1) (1).png","storedFilename":"235f6d43-35f2-4e3e-9764-9b01de1d4da3.png","fileSize":"139333","contentType":"image/png","uploadDate":"2025-11-18T09:14:07.924785","useYn":"Y"}],"createAt":"2025-11-18 09:14:07","updateAt":"2025-11-18 09:14:07","createBy":"SGO1033491","updateBy":"SGO1033491","createdByName":null,"createdByDepts":null,"createdByPos":null,"updatedByName":null,"updatedByDepts":null,"updatedByPos":null},{"notiId":68,"title":"백엔드 수정 테스트","msg":"감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다, {줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트, 줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트 , 줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트, 줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트 ,줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트,줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트,줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트,줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트,줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트,줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트,줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트,줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트,줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트,줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트,줄바꿈이 되는지 테스트 입니다. 신한은행 UIcode 줄바꿈 테스트 테스트}'
                }
                language='python'
                theme='dark'
                width='100%'
                minHeight='300px'
                maxHeight='500px'
                readOnly={false}
                wordWrap={true} //  << [251120_퍼블수정] 자동 줄바꿈사용시 : 이 부분 추가
              />
            </UIFormField>
          </UIArticle>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                Response
              </UITypography>
              {/* 소스코드 영역 */}
              {/* 
                [251120_퍼블수정] : UICode 줄바꿈없이 디폴트 상태경우 
              */}
              <UICode
                value={
                  '{"content":[{"notiId":72,"title":"취약점처리확인123!","msg":"123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#123123@#\n1680","type":"보안 안내","useYn":"Y","expFrom":"2025-11-18 14:07:00","expTo":"9999-12-31 23:59:00","files":[{"fileId":82,"originalFilename":"image (4) (1) (1) (2).png","storedFilename":"a84d3a5e-3222-408e-afa7-a98ef1afaa0a.png","fileSize":"139333","contentType":"image/png","uploadDate":"2025-11-18T14:07:55.585964","useYn":"Y"}],"createAt":"2025-11-18 14:07:55","updateAt":"2025-11-18 14:08:40","createBy":"SGO1033491","updateBy":"SGO1033491","createdByName":null,"createdByDepts":null,"createdByPos":null,"updatedByName":null,"updatedByDepts":null,"updatedByPos":null},{"notiId":70,"title":"업로드취약점테스트","msg":"ㅇㅇ","type":"시스템 점검","useYn":"Y","expFrom":"2025-11-18 09:09:00","expTo":"9999-12-31 23:59:00","files":[{"fileId":80,"originalFilename":"image (4) (1) (1) (1).png","storedFilename":"235f6d43-35f2-4e3e-9764-9b01de1d4da3.png","fileSize":"139333","contentType":"image/png","uploadDate":"2025-11-18T09:14:07.924785","useYn":"Y"}],"createAt":"2025-11-18 09:14:07","updateAt":"2025-11-18 09:14:07","createBy":"SGO1033491","updateBy":"SGO1033491","createdByName":null,"createdByDepts":null,"createdByPos":null,"updatedByName":null,"updatedByDepts":null,"updatedByPos":null},{"notiId":68,"title":"백엔드 수정 테스트","msg":"감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다. 감사합니다'
                }
                language='python'
                theme='dark'
                width='100%'
                minHeight='300px'
                maxHeight='500px'
                readOnly={false}
              />
            </UIFormField>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
