import { UITypography, UIButton2, UIPagination } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIUnitGroup } from '@/components/UI/molecules';

import { DesignLayout } from '../../components/DesignLayout';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';

export const DT_020303 = () => {
  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='지식파일 상세'
          description=''
          // {/* [251104_퍼블수정] : 메타데이터 버튼 삭제 */}
          // actions={
          //   <>
          //     <UIButton2 className='btn-tertiary-outline line-only-blue' onClick={() => {}}>
          //       메타데이터
          //     </UIButton2>
          //   </>
          // }
        />
        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                지식데이터 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251106_퍼블수정] width값 수정 */}
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
                          MD파일 명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          신용대출 상품 설명서.md
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          타이틀
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          신용대출 상품 설명서모음집
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          첨부파일 이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          상품 설명서
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          UUID
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          23ㄴdac10b-58cc-4372-a567
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          지식데이터
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          외환
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
          <UIArticle>
            <UIListContainer>
              <UIListContentBox.Body className='flex-col'>
                {/* [251106_퍼블수정] width값 수정. 글이 짧을 경우 영역 너비 문제 수정 */}
                <UIArticle className='w-full'>
                  <UIUnitGroup gap={8} direction='column' align='start'>
                    {/* [참고] 타이틀영역은 버튼이 아니라고합니다 */}
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                      phrases_#1
                    </UITypography>
                    <div className='bg-gray-100 rounded-xl px-5 py-5'>
                      <UIButton2 className='cursor-pointer text-left'>
                        <UITypography variant='body-2' className='secondary-neutral-600 mt-1'>
                          개인(신용)정보는 (금융)거래 종료일로부터 5년까지 보유·이용됩니다. (금융)거래 종료일 후에는 금융사고 조사, 분쟁 해결, 민원처리, 법령상 의무 이행을 위한
                          목적으로만 보유·이용됩니다. 보유 및 이용기간 위 보유 기간에서의 (금융)거래 종료일이란 "당 행과 거래중인 모든 계약(여·수신, 내·외국환, 카드 및 제3자 담보
                          제공 등) 해지 및 서비스(대여금고, 보호예수,외국환거래지정, 인터넷뱅킹 포함 전자금융거래 등)가 종료된 날"을 말합니다. 귀하는 동의를 거부하실 수 있습니다.
                          다만, 개인(신용)정보 수집·이용에 관한 동의는 "계약의 체결 거부 권리 및 불이익 및 이행"을 위한 필수적 사항이므로, 동의하셔야만 (금융)거래관계의 설정 및
                          유지가 가능합니다.
                        </UITypography>
                      </UIButton2>
                    </div>
                  </UIUnitGroup>
                </UIArticle>
                <UIArticle className='w-full'>
                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                      phrases_#2
                    </UITypography>
                    <div className='bg-gray-100 rounded-xl px-5 py-5'>
                      <UIButton2 className='cursor-pointer text-left'>
                        <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                          (금융)거래관계의 설정·유지·이행·관리 수집·이용 목적 · 금융사고 조사, 분쟁해결, 민원처리
                        </UITypography>
                        <UITypography variant='body-2' className='secondary-neutral-600 mt-1'>
                          개인(신용)정보는 (금융)거래 종료일로부터 5년까지 보유·이용됩니다. (금융)거래 종료일 후에는 금융사고 조사, 분쟁 해결, 민원처리, 법령상 의무 이행을 위한
                          목적으로만 보유·이용됩니다. 보유 및 이용기간 위 보유 기간에서의 (금융)거래 종료일이란 "당 행과 거래중인 모든 계약(여·수신, 내·외국환, 카드 및 제3자 담보
                          제공 등) 해지 및 서비스(대여금고, 보호예수,외국환거래지정, 인터넷뱅킹 포함 전자금융거래 등)가 종료된 날"을 말합니다. 귀하는 동의를 거부하실 수 있습니다.
                          다만, 개인(신용)정보 수집·이용에 관한 동의는 "계약의 체결 거부 권리 및 불이익 및 이행"을 위한 필수적 사항이므로, 동의하셔야만 (금융)거래관계의 설정 및
                          유지가 가능합니다.
                        </UITypography>
                        <UITypography variant='body-2' className='secondary-neutral-600 mt-1'>
                          개인(신용)정보는 (금융)거래 종료일로부터 5년까지 보유·이용됩니다. (금융)거래 종료일 후에는 금융사고 조사, 분쟁 해결, 민원처리, 법령상 의무 이행을 위한
                          목적으로만 보유·이용됩니다. 보유 및 이용기간 위 보유 기간에서의 (금융)거래 종료일이란 "당 행과 거래중인 모든 계약(여·수신, 내·외국환, 카드 및 제3자 담보
                          제공 등) 해지 및 서비스(대여금고, 보호예수,외국환거래지정, 인터넷뱅킹 포함 전자금융거래 등)가 종료된 날"을 말합니다. 귀하는 동의를 거부하실 수 있습니다.
                          다만, 개인(신용)정보 수집·이용에 관한 동의는 "계약의 체결 거부 권리 및 불이익 및 이행"을 위한 필수적 사항이므로, 동의하셔야만 (금융)거래관계의 설정 및
                          유지가 가능합니다.
                        </UITypography>
                        <UITypography variant='body-2' className='secondary-neutral-600 mt-1'>
                          개인(신용)정보는 (금융)거래 종료일로부터 5년까지 보유·이용됩니다. (금융)거래 종료일 후에는 금융사고 조사, 분쟁 해결, 민원처리, 법령상 의무 이행을 위한
                          목적으로만 보유·이용됩니다. 보유 및 이용기간 위 보유 기간에서의 (금융)거래 종료일이란 "당 행과 거래중인 모든 계약(여·수신, 내·외국환, 카드 및 제3자 담보
                          제공 등) 해지 및 서비스(대여금고, 보호예수,외국환거래지정, 인터넷뱅킹 포함 전자금융거래 등)가 종료된 날"을 말합니다. 귀하는 동의를 거부하실 수 있습니다.
                          다만, 개인(신용)정보 수집·이용에 관한 동의는 "계약의 체결 거부 권리 및 불이익 및 이행"을 위한 필수적 사항이므로, 동의하셔야만 (금융)거래관계의 설정 및
                          유지가 가능합니다.
                        </UITypography>
                      </UIButton2>
                    </div>
                  </UIUnitGroup>
                </UIArticle>
                <UIArticle className='w-full'>
                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                      phrases_#3
                    </UITypography>
                    <div className='bg-gray-100 rounded-xl px-5 py-5'>
                      <UIButton2 className='cursor-pointer text-left'>
                        <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                          (금융)거래관계의 설정·유지·이행·관리 수집·이용 목적 · 금융사고 조사, 분쟁해결, 민원처리
                        </UITypography>
                        <UITypography variant='body-2' className='secondary-neutral-600 mt-1'>
                          개인(신용)정보는 (금융)거래 종료일로부터 5년까지 보유·이용됩니다. (금융)거래 종료일 후에는 금융사고 조사, 분쟁 해결, 민원처리, 법령상 의무 이행을 위한
                          목적으로만 보유·이용됩니다. 보유 및 이용기간 위 보유 기간에서의 (금융)거래 종료일이란 "당 행과 거래중인 모든 계약(여·수신, 내·외국환, 카드 및 제3자 담보
                          제공 등) 해지 및 서비스(대여금고, 보호예수,외국환거래지정, 인터넷뱅킹 포함 전자금융거래 등)가 종료된 날"을 말합니다. 귀하는 동의를 거부하실 수 있습니다.
                          다만, 개인(신용)정보 수집·이용에 관한 동의는 "계약의 체결 거부 권리 및 불이익 및 이행"을 위한 필수적 사항이므로, 동의하셔야만 (금융)거래관계의 설정 및
                          유지가 가능합니다.
                        </UITypography>
                        <UITypography variant='body-2' className='secondary-neutral-600 mt-1'>
                          개인(신용)정보는 (금융)거래 종료일로부터 5년까지 보유·이용됩니다. (금융)거래 종료일 후에는 금융사고 조사, 분쟁 해결, 민원처리, 법령상 의무 이행을 위한
                          목적으로만 보유·이용됩니다. 보유 및 이용기간 위 보유 기간에서의 (금융)거래 종료일이란 "당 행과 거래중인 모든 계약(여·수신, 내·외국환, 카드 및 제3자 담보
                          제공 등) 해지 및 서비스(대여금고, 보호예수,외국환거래지정, 인터넷뱅킹 포함 전자금융거래 등)가 종료된 날"을 말합니다. 귀하는 동의를 거부하실 수 있습니다.
                          다만, 개인(신용)정보 수집·이용에 관한 동의는 "계약의 체결 거부 권리 및 불이익 및 이행"을 위한 필수적 사항이므로, 동의하셔야만 (금융)거래관계의 설정 및
                          유지가 가능합니다.
                        </UITypography>
                        <UITypography variant='body-2' className='secondary-neutral-600 mt-1'>
                          개인(신용)정보는 (금융)거래 종료일로부터 5년까지 보유·이용됩니다. (금융)거래 종료일 후에는 금융사고 조사, 분쟁 해결, 민원처리, 법령상 의무 이행을 위한
                          목적으로만 보유·이용됩니다. 보유 및 이용기간 위 보유 기간에서의 (금융)거래 종료일이란 "당 행과 거래중인 모든 계약(여·수신, 내·외국환, 카드 및 제3자 담보
                          제공 등) 해지 및 서비스(대여금고, 보호예수,외국환거래지정, 인터넷뱅킹 포함 전자금융거래 등)가 종료된 날"을 말합니다. 귀하는 동의를 거부하실 수 있습니다.
                          다만, 개인(신용)정보 수집·이용에 관한 동의는 "계약의 체결 거부 권리 및 불이익 및 이행"을 위한 필수적 사항이므로, 동의하셔야만 (금융)거래관계의 설정 및
                          유지가 가능합니다.
                        </UITypography>
                      </UIButton2>
                    </div>
                  </UIUnitGroup>
                </UIArticle>
                <UIArticle>
                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                      phrases_#4
                    </UITypography>
                    <div className='bg-gray-100 rounded-xl px-5 py-5'>
                      <UIButton2 className='cursor-pointer text-left'>
                        <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                          (금융)거래관계의 설정·유지·이행·관리 수집·이용 목적 · 금융사고 조사, 분쟁해결, 민원처리
                        </UITypography>
                        <UITypography variant='body-2' className='secondary-neutral-600 mt-1'>
                          개인(신용)정보는 (금융)거래 종료일로부터 5년까지 보유·이용됩니다. (금융)거래 종료일 후에는 금융사고 조사, 분쟁 해결, 민원처리, 법령상 의무 이행을 위한
                          목적으로만 보유·이용됩니다. 보유 및 이용기간 위 보유 기간에서의 (금융)거래 종료일이란 "당 행과 거래중인 모든 계약(여·수신, 내·외국환, 카드 및 제3자 담보
                          제공 등) 해지 및 서비스(대여금고, 보호예수,외국환거래지정, 인터넷뱅킹 포함 전자금융거래 등)가 종료된 날"을 말합니다. 귀하는 동의를 거부하실 수 있습니다.
                          다만, 개인(신용)정보 수집·이용에 관한 동의는 "계약의 체결 거부 권리 및 불이익 및 이행"을 위한 필수적 사항이므로, 동의하셔야만 (금융)거래관계의 설정 및
                          유지가 가능합니다.
                        </UITypography>
                      </UIButton2>
                    </div>
                  </UIUnitGroup>
                </UIArticle>
                <UIArticle>
                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                      phrases_#5
                    </UITypography>
                    <div className='bg-gray-100 rounded-xl px-5 py-5'>
                      <UIButton2 className='cursor-pointer text-left'>
                        <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                          (금융)거래관계의 설정·유지·이행·관리 수집·이용 목적 · 금융사고 조사, 분쟁해결, 민원처리
                        </UITypography>
                        <UITypography variant='body-2' className='secondary-neutral-600 mt-1'>
                          개인(신용)정보는 (금융)거래 종료일로부터 5년까지 보유·이용됩니다. (금융)거래 종료일 후에는 금융사고 조사, 분쟁 해결, 민원처리, 법령상 의무 이행을 위한
                          목적으로만 보유·이용됩니다. 보유 및 이용기간 위 보유 기간에서의 (금융)거래 종료일이란 "당 행과 거래중인 모든 계약(여·수신, 내·외국환, 카드 및 제3자 담보
                          제공 등) 해지 및 서비스(대여금고, 보호예수,외국환거래지정, 인터넷뱅킹 포함 전자금융거래 등)가 종료된 날"을 말합니다. 귀하는 동의를 거부하실 수 있습니다.
                          다만, 개인(신용)정보 수집·이용에 관한 동의는 "계약의 체결 거부 권리 및 불이익 및 이행"을 위한 필수적 사항이므로, 동의하셔야만 (금융)거래관계의 설정 및
                          유지가 가능합니다.
                        </UITypography>
                      </UIButton2>
                    </div>
                  </UIUnitGroup>
                </UIArticle>
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
        {/* <UIPageFooter></UIPageFooter> */}
      </section>
    </DesignLayout>
  );
};
