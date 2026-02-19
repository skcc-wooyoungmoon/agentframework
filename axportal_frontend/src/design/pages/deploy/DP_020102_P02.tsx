import { UIButton2, UIIcon2, UITypography } from '@/components/UI/atoms';
import { UITextArea2 } from '@/components/UI/molecules/input';
import { UIFilter } from '@/components/UI/organisms';
import React, { useState } from 'react';
import { DesignLayout } from '../../components/DesignLayout';

export const DP_020102_P02: React.FC = () => {
  const [messageInput, setMessageInput] = useState('');

  const handleSendMessage = () => {
    setMessageInput('');
  };

  const handleRegenerateAnswer = () => {
  };

  const handleChatReset = () => {
  };

  const handleClose = () => {
  };

  const messages = [
    {
      type: 'user',
      text: '2025년도 입출금이 자유로운 예금 상품설명서 내용에 대해 알려줘.',
    },
    {
      type: 'system',
      responses: [
        {
          similarityScore: '0.6235827',
          content:
            '11 신한은행은 고객이 전자금융거래 서비스를 안전하게 이용할 수 있도록 필요한 보안 조치를 이행하며, 이용자는 본인의 접근매체를 타인에게 양도하거나 공유해서는 안 됩니다. 접근매체 관리 소홀로 인한 손해는 이용자에게 귀속됩니다.',
          timestamp: '2025.04.24 04:47:02',
        },
      ],
    },
    {
      type: 'user',
      text: '2025년도 입출금이 자유로운 예금 상품설명서 내용에 대해 알려줘.',
    },
    {
      type: 'system',
      responses: [
        {
          similarityScore: '0.6235827',
          content:
            '22 신한은행은 고객이 전자금융거래 서비스를 안전하게 이용할 수 있도록 필요한 보안 조치를 이행하며, 이용자는 본인의 접근매체를 타인에게 양도하거나 공유해서는 안 됩니다. 접근매체 관리 소홀로 인한 손해는 이용자에게 귀속됩니다.',
          timestamp: '2025.04.24 04:47:02',
        },
      ],
    },
    {
      type: 'system',
      responses: [
        {
          similarityScore: '0.6235827',
          content:
            '22 신한은행은 고객이 전자금융거래 서비스를 안전하게 이용할 수 있도록 필요한 보안 조치를 이행하며, 이용자는 본인의 접근매체를 타인에게 양도하거나 공유해서는 안 됩니다. 접근매체 관리 소홀로 인한 손해는 이용자에게 귀속됩니다.',
          timestamp: '2025.04.24 04:47:02',
        },
      ],
    },
    {
      type: 'system',
      responses: [
        {
          similarityScore: '0.6235827',
          content:
            '22 신한은행은 고객이 전자금융거래 서비스를 안전하게 이용할 수 있도록 필요한 보안 조치를 이행하며, 이용자는 본인의 접근매체를 타인에게 양도하거나 공유해서는 안 됩니다. 접근매체 관리 소홀로 인한 손해는 이용자에게 귀속됩니다.',
          timestamp: '2025.04.24 04:47:02',
        },
      ],
    },
    {
      type: 'system',
      responses: [
        {
          similarityScore: '0.6235827',
          content:
            '22 신한은행은 고객이 전자금융거래 서비스를 안전하게 이용할 수 있도록 필요한 보안 조치를 이행하며, 이용자는 본인의 접근매체를 타인에게 양도하거나 공유해서는 안 됩니다. 접근매체 관리 소홀로 인한 손해는 이용자에게 귀속됩니다.',
          timestamp: '2025.04.24 04:47:02',
        },
      ],
    },
    {
      type: 'system',
      responses: [
        {
          similarityScore: '0.6235827',
          content:
            '22 신한은행은 고객이 전자금융거래 서비스를 안전하게 이용할 수 있도록 필요한 보안 조치를 이행하며, 이용자는 본인의 접근매체를 타인에게 양도하거나 공유해서는 안 됩니다. 접근매체 관리 소홀로 인한 손해는 이용자에게 귀속됩니다.',
          timestamp: '2025.04.24 04:47:02',
        },
      ],
    },
    {
      type: 'system',
      responses: [
        {
          similarityScore: '0.6235827',
          content:
            '22 신한은행은 고객이 전자금융거래 서비스를 안전하게 이용할 수 있도록 필요한 보안 조치를 이행하며, 이용자는 본인의 접근매체를 타인에게 양도하거나 공유해서는 안 됩니다. 접근매체 관리 소홀로 인한 손해는 이용자에게 귀속됩니다.',
          timestamp: '2025.04.24 04:47:02',
        },
      ],
    },
  ];

  return (
    <>
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
              에이전트 빌더
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              에이전트 빌더 - 채팅 테스트 진행 중...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* 오버레이 필터 */}
      <UIFilter onChatReset={handleChatReset} onClose={handleClose} showDropdown={true}>
        {/* [퍼블수정] h-full 클래스 추가 */}
        <div className='flex-1 flex flex-col h-full'>
          <div className='flex-1 overflow-y-auto p-6 space-y-6'>
            {messages.map((message, index) => (
              <div key={index}>
                {message.type === 'user' ? (
                  <div className='flex flex-col items-end space-y-2 question-group'>
                    <div>
                      <div className='max-w-[320px] bg-blue-700 text-white rounded-xl rounded-br-sm px-4 py-3 question-wrap mb-0'>
                        <UITypography variant='body-2' className='secondary-neutral-f text-sb'>
                          {message.text}
                        </UITypography>
                      </div>
                    </div>
                  </div>
                ) : (
                  <div>
                    {message.responses?.map((response, responseIndex) => (
                      <div key={responseIndex} className='max-w-[368px] mt-[24px]'>
                        <div className='text-sm text-gray-900 leading-5 px-4 py-3 bg-gray-100 rounded-xl rounded-bl-sm'>
                          <UITypography variant='body-2' className='secondary-neutral-800'>
                            {response.content}
                          </UITypography>
                        </div>
                        <div className='w-full flex justify-between items-center mt-[8px] question-sub-wrap'>
                          <UITypography variant='caption-2' className='secondary-neutral-600'>
                            {response.timestamp}
                          </UITypography>
                          <UIButton2
                            onClick={handleRegenerateAnswer}
                            className='flex gap-1 text-xs text-[#373E4D] font-semibold leading-5 cursor-pointer'
                            leftIcon={{ className: 'ic-system-20-reload', children: '' }}
                          >
                            답변 재생성
                          </UIButton2>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>

          <div className='p-6'>
            <div className='flex gap-2'>
              <div className='flex-1  min-w-[344px] h-[48px]'>
                <UITextArea2
                  value={messageInput}
                  onChange={e => setMessageInput(e.target.value)}
                  placeholder='메시지를 입력하세요'
                  rows={1}
                  lineType={'single-line'}
                  resizable={false}
                />
              </div>
              <UIButton2 onClick={handleSendMessage} className='cursor-pointer'>
                <UIIcon2 className='ic-system-48-chat' />
              </UIButton2>
            </div>
          </div>
        </div>
      </UIFilter>
    </>
  );
};
