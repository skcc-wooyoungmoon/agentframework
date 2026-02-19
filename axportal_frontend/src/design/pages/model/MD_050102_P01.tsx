import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography, UILabel } from '@/components/UI/atoms';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIUnitGroup } from '@/components/UI/molecules';

export const MD_050102_P01: React.FC = () => {
  return (
    <section className='section-modal relative pb-5'>
      <UIArticle>
        <div className='article-header'>
          <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
            외부망 백신점검 결과 요약
          </UITypography>
        </div>
        <div className='article-body'>
          <div className='border-t border-black'>
            <table className='tbl-v'>
              <colgroup>
                <col style={{ width: '152px' }} />
                <col style={{ width: '350px' }} />
                <col style={{ width: '152px' }} />
                <col style={{ width: '350px' }} />
              </colgroup>
              <tbody>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      백신 검사 결과
                    </UITypography>
                  </th>
                  <td colSpan={3}>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      <UILabel variant='badge' intent='complete'>
                        적합
                      </UILabel>
                    </UITypography>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      검사한 파일 수
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      25
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      감염 파일 수
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      0
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
          <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
            내부망 백신점검 결과 요약
          </UITypography>
        </div>
        <div className='article-body'>
          <div className='border-t border-black'>
            <table className='tbl-v'>
              <colgroup>
                <col style={{ width: '152px' }} />
                <col style={{ width: '853px' }} />
              </colgroup>
              <tbody>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      백신 검사 결과
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      <UILabel variant='badge' intent='complete'>
                        적합
                      </UILabel>
                    </UITypography>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      백신 점검 내용
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      25
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
          <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
            취약점검 결과 요약
          </UITypography>
        </div>
        <div className='article-body'>
          <div className='border-t border-black'>
            <table className='tbl-v'>
              <colgroup>
                <col style={{ width: '152px' }} />
                <col style={{ width: '350px' }} />
                <col style={{ width: '152px' }} />
                <col style={{ width: '350px' }} />
              </colgroup>
              <tbody>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      취약점검 결과
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      <UILabel variant='badge' intent='error'>
                        취약
                      </UILabel>
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      발견된 취약점 수
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      3
                    </UITypography>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      취약점 심각도 별 파일 수
                    </UITypography>
                  </th>
                  <td>
                    <pre className='text-sm font-normal text-secondary-neutral-600 overflow-auto text-[#576072]'>{'{"high": 0, "medium": 0, "low": 0}'}</pre>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      취약점 내용
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      $취약점 내용이 노출됩니다.취약점 내용이 노출됩니다.취약점 내용이 노출됩니다.취약점 내용이 노출됩니다.취약점 내용이 노출됩니다.취약점 내용이 노출됩니다.취약점
                      내용이 노출됩니다.취약점 내용이 노출됩니다.취약점 내용이 노출됩니다.취약점 내용이 노출됩니다.취약점 내용이 노출됩니다.취약점 내용이 노출됩니다.$
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
          <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
            모델 점검 결과 상세
          </UITypography>
        </div>
        <div className='article-body'>
          <div className='border-t border-black'>
            <table className='tbl-v'>
              <colgroup>
                <col style={{ width: '152px' }} />
                <col style={{ width: '870px' }} />
              </colgroup>
              <tbody>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      백신 검사 결과
                    </UITypography>
                  </th>
                  <td>
                    <pre className='text-sm font-normal text-secondary-neutral-600 overflow-auto text-[#576072]'>
                      {`Model artifact download completed, Verification result: {'verifiers': {'sophos': {'success': True, 'infected_count': 0, 'scanned_at': '2025-12-11T07:07:14.811978+0000', 'scan_time': 0.126003, 'scanned_count': 25, 'metadata': {'scan_command': '/usr/local/bin/avscanner --scan-archives /home/lablup/temp-storage/google/gemma-3-27b-it/main'}, 'error': None}, 'v3': {'success': True, 'infected_count': 0, 'scanned_at': '2025-12-11T07:07:14.938581+0000', 'scan_time': 6.225144, 'scanned_count': 25, 'metadata': {'scan_command': 'action scan start directory /home/lablup/temp-storage/google/gemma-3-27b-it/main'}, 'error': None}, 'deepsecurity': {'success': True, 'infected_count': 0, 'scanned_at': '2025-12-11T07:07:14.811978+0000', 'scan_time': 0.126003, 'scanned_count': 25, 'metadata': {'scan_command': '/usr/local/bin/avscanner --scan-archives /home/lablup/temp-storage/google/gemma-3-27b-it/main'}, 'error': None}}}`}
                    </pre>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      취약점검 결과
                    </UITypography>
                  </th>
                  <td>
                    <pre className='text-sm font-normal text-secondary-neutral-600 overflow-auto text-[#576072]'>
                      {'VULNERABILITY_CHECK { "total_vulnerabilities": 0, "vulnerabilities_by_severity": 0, "vulnerabilities": [] }'}
                    </pre>
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
            최종 반입 결재 정보
          </UITypography>
        </div>
        <div className='article-body'>
          <div className='border-t border-black'>
            <table className='tbl-v'>
              <colgroup>
                <col style={{ width: '152px' }} />
                <col style={{ width: '350px' }} />
                <col style={{ width: '152px' }} />
                <col style={{ width: '350px' }} />
              </colgroup>
              <tbody>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      결재 결과
                    </UITypography>
                  </th>
                  <td colSpan={3}>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      <UILabel variant='badge' intent='complete'>
                        승인
                      </UILabel>
                    </UITypography>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      결재 요청자
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      공몰리 ㅣ Data기획Unit
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      결재 요청일시
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      2025.03.24 18:23:43
                    </UITypography>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </UIArticle>

      <div className='modal-buttons'>
        <UIUnitGroup gap={8} direction='row' align='center'>
          <UIButton2 className='btn-secondary-gray w-[164px]'>삭제</UIButton2>
          <UIButton2 className='btn-secondary-blue w-[164px]'>최종 반입 결재요청</UIButton2>
        </UIUnitGroup>
      </div>
    </section>
  );
};
