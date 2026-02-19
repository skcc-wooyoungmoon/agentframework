import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UICode } from '@/components/UI/atoms/UICode';
import { UIGroup } from '@/components/UI/molecules';
import { UITostRenderer } from '@/components/UI/molecules/toast/UITostRenderer/components';
import { UIAlarm, UIAlarmGroup } from '@/components/UI/organisms';
import { useToast } from '@/hooks/common/toast/useToast';
import { UITypography } from '../../../components/UI/atoms/UITypography';
import { DesignLayout } from '../../components/DesignLayout';

export const DP_020102_P03: React.FC = () => {
  const handleClose = () => {};

  const { toast } = useToast();

  const handleCopy = (message: string) => {
    toast(message);
    toast.success(message);
    toast.error(message);
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'model', label: '모델' }}
        initialSubMenu={{
          id: 'model-catalog',
          label: '에이전트 배포 조회',
          icon: 'ico-lnb-menu-20-model-catalog',
        }}
      >
        {/* Toast */}
        <UITostRenderer position='bottom-center' />

        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              에이전트 배포 조회
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              컨텐츠..
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* cURL 코드 확인하기 */}
      <UIAlarm onClose={handleClose} title='cURL 코드 확인하기'>
        <UIAlarmGroup>
          <div className='side-code'>
            {/* list */}
            <div className='side-code-item'>
              <div className='code-head'>
                <UIGroup gap={8} direction='column'>
                  <div className='code-head-top'>
                    <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                      Invoke
                    </UITypography>
                    <UIButton2 className='btn-text-14-underline-point' onClick={() => handleCopy('토스트 팝업입니다. 최대 글자수는 20글자')}>
                      복사
                    </UIButton2>
                  </div>
                  <UITypography variant='body-1' className='secondary-neutral-700'>
                    요청에 대한 응답을 한 번에 받아올 때 사용합니다.
                  </UITypography>
                </UIGroup>
              </div>
              <div className='code-edit mt-4'>
                {/* 실제 에디트 코드 영역 */}
                {/* 
                  [참고]
                  minHeight, height, maxHeight = 모두 동일한 높이 사이즈 넣기
                */}
                <UICode
                  value={` 

###### [스크롤 : 가로 / 세로 ] ######

여기는 에디터 화면입니다. 테스트 test
여기는 에디터 화면입니다. 테스트 test
  여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
    여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
        여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
          여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
            여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
              여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
                  여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
                여기는 에디터 화면입니다. 테스트 test
                    여기는 에디터 화면입니다. 테스트 test
여기는 에디터 화면입니다. 테스트 test
    여기는 에디터 화면입니다. 테스트 test
    여기는 에디터 화면입니다. 테스트 test
        여기는 에디터 화면입니다. 테스트 test
        여기는 에디터 화면입니다. 테스트 test
              여기는 에디터 화면입니다. 테스트 test
              여기는 에디터 화면입니다. 테스트 test
여기는 에디터 화면입니다. 테스트 test
111111
2222222
33333
4444
5555555`}
                  language='python'
                  theme='dark'
                  width='385px'
                  minHeight='350px'
                  height='350px'
                  maxHeight='350px'
                  readOnly={false}
                />
              </div>
            </div>
            {/* list */}
            <div className='side-code-item'>
              <div className='code-head'>
                <UIGroup gap={8} direction='column'>
                  <div className='code-head-top'>
                    <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                      Stream
                    </UITypography>
                    <UIButton2 className='btn-text-14-underline-point' onClick={() => handleCopy('복사가 완료되었습니다.')}>
                      복사
                    </UIButton2>
                  </div>
                  <UITypography variant='body-1' className='secondary-neutral-700'>
                    응답을 실시간 스트리밍 형식으로 받아올 때 사용합니다.
                  </UITypography>
                </UIGroup>
              </div>
              <div className='code-edit mt-4'>
                {/* 실제 에디트 코드 영역 */}
                <UICode
                  value={`
 ###### [스크롤 : 가로 ] ######

여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
  여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
    여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
        여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
            여기는 에디터 화면입니다. 테스트 test
                여기는 에디터 화면입니다. 테스트 test
                    여기는 에디터 화면입니다. 테스트 test`}
                  language='python'
                  theme='dark'
                  width='385px'
                  minHeight='350px'
                  height='350px'
                  maxHeight='350px'
                  readOnly={false}
                />
              </div>
            </div>
          </div>
        </UIAlarmGroup>
      </UIAlarm>
    </>
  );
};
