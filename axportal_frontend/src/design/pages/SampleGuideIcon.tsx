import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';

import { DesignLayout } from '../components/DesignLayout';

export const SampleGuideIcon = () => {
  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page pub-guide'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='아이콘 가이드'
          description='신한은행 디자인 시스템 아이콘 가이드입니다.'
          // actions={
          //   <>
          //     <UIButton2 className='btn-text-14-semibold-point'>취소</UIButton2>
          //     <UIButton2 className='btn-primary-blue'>확인</UIButton2>
          //   </>
          // }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* system */}
          <UIArticle>
            <dl className='guide'>
              <dt className='guide-title'>system</dt>
              <dd className='guide-content'>
                <div className='icon-item'>
                  <span className='icon-label'>필수항목 표시</span>
                  <UIIcon2 className='ic-system-required' />
                </div>
                <div className='icon-item'>
                  <span className='icon-label'>알림표시</span>
                  <UIIcon2 className='ic-system-alarm' />
                </div>
                <div className='icon-item'>
                  <UIIcon2 className='ic-system-node-connector' />
                </div>
              </dd>
            </dl>
          </UIArticle>

          {/* 10px */}
          <UIArticle>
            <dl className='guide'>
              <dt className='guide-title'>10px</dt>
              <dd className='guide-content'>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-system-10-dropdown' />
                </div>
              </dd>
            </dl>
          </UIArticle>

          {/* 12px */}
          <UIArticle>
            <dl className='guide'>
              <dt className='guide-title'>12px</dt>
              <dd className='guide-content'>
                <p className='icon-category'>시스템</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-system-12-area' />
                  <UIIcon2 className='ic-system-12-arrow-down' />
                  <UIIcon2 className='ic-system-12-arrow-expansion' />
                  <UIIcon2 className='ic-system-12-arrow-left' />
                  <UIIcon2 className='ic-system-12-arrow-reduction' />
                  <UIIcon2 className='ic-system-12-arrow-right-black' />
                  <UIIcon2 className='ic-system-12-arrow-right-gray' />
                  <UIIcon2 className='ic-system-12-arrow-right-blue' />
                  <UIIcon2 className='ic-system-12-arrow-up' />
                  <UIIcon2 className='ic-system-12-calendar' />
                  <UIIcon2 className='ic-system-12-close' />
                  <UIIcon2 className='ic-system-12-download' />
                  <UIIcon2 className='ic-system-12-minus' />
                  <UIIcon2 className='ic-system-12-plus-blue' />
                  <UIIcon2 className='ic-system-12-plus' />
                  <UIIcon2 className='ic-system-12-search' />
                  <UIIcon2 className='ic-system-12-share' />
                </div>
              </dd>
            </dl>
          </UIArticle>

          {/* 14px */}
          <UIArticle>
            <dl className='guide'>
              <dt className='guide-title'>14px</dt>
              <dd className='guide-content'>
                <p className='icon-category'>시스템</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-system-14-badge-complete' />
                  <UIIcon2 className='ic-system-14-badge-ongoing' />
                  <UIIcon2 className='ic-system-14-badge-stop' />
                </div>
              </dd>
            </dl>
          </UIArticle>

          {/* 16px */}
          <UIArticle>
            <dl className='guide'>
              <dt className='guide-title'>16px</dt>
              <dd className='guide-content'>
                <p className='icon-category'>시스템</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-system-16-align' />
                  <UIIcon2 className='ic-system-16-area' />
                  <UIIcon2 className='ic-system-16-arrow-down-blue' />
                  <UIIcon2 className='ic-system-16-arrow-down-gray' />
                  <UIIcon2 className='ic-system-16-arrow-left' />
                  <UIIcon2 className='ic-system-16-arrow-right' />
                  <UIIcon2 className='ic-system-16-arrow-up-blue' />
                  <UIIcon2 className='ic-system-16-arrow-up-gray' />
                  <UIIcon2 className='ic-system-16-btn-minus' />
                  <UIIcon2 className='ic-system-16-btn' />
                  <UIIcon2 className='ic-system-16-close-black' />
                  <UIIcon2 className='ic-system-16-close' />
                  <UIIcon2 className='ic-system-16-complete' />
                  <UIIcon2 className='ic-system-16-dot-1' />
                  <UIIcon2 className='ic-system-16-dot-2' />
                  <UIIcon2 className='ic-system-16-dot-3' />
                  <UIIcon2 className='ic-system-16-dot-4' />
                  <UIIcon2 className='ic-system-16-dot-5' />
                  <UIIcon2 className='ic-system-16-down-black' />
                  <UIIcon2 className='ic-system-16-down-gray' />
                  <UIIcon2 className='ic-system-16-download' />
                  <UIIcon2 className='ic-system-16-error' />
                  <UIIcon2 className='ic-system-16-export' />
                  <UIIcon2 className='ic-system-16-file-Delete' />
                  <UIIcon2 className='ic-system-16-file-save' />
                  <UIIcon2 className='ic-system-16-grid-sort' />
                  <UIIcon2 className='ic-system-16-info-gray' />
                  <UIIcon2 className='ic-system-16-info-red' />
                  <UIIcon2 className='ic-system-16-info' />
                  <UIIcon2 className='ic-system-16-plus-gray' />
                  <UIIcon2 className='ic-system-16-plus' />
                  <UIIcon2 className='ic-system-16-select' />
                  <UIIcon2 className='ic-system-16-sort' />
                  <UIIcon2 className='ic-system-16-tooltip' />
                  <UIIcon2 className='ic-system-16-up-black' />
                  <UIIcon2 className='ic-system-16-up-gray' />
                  <UIIcon2 className='ic-system-16-user' />
                  <UIIcon2 className='ic-system-16-public' />
                </div>
              </dd>
            </dl>
          </UIArticle>

          {/* 20px */}
          <UIArticle>
            <dl className='guide'>
              <dt className='guide-title'>20px</dt>
              <dd className='guide-content'>
                <p className='icon-category'>시스템</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-system-20-area' />
                  <UIIcon2 className='ic-system-20-builder' />
                  <UIIcon2 className='ic-system-20-close' />
                  <UIIcon2 className='ic-system-20-complete' />
                  <UIIcon2 className='ic-system-20-copy-black' />
                  <UIIcon2 className='ic-system-20-copy-gray' />
                  <UIIcon2 className='ic-system-20-download-black' />
                  <UIIcon2 className='ic-system-20-download-blue' />
                  <UIIcon2 className='ic-system-20-download-gray' />
                  <UIIcon2 className='ic-system-20-error' />
                  <UIIcon2 className='ic-system-20-gnb-arrow-left-black' />
                  <UIIcon2 className='ic-system-20-gnb-arrow-left-gray' />
                  <UIIcon2 className='ic-system-20-gnb-arrow-right-black' />
                  <UIIcon2 className='ic-system-20-gnb-arrow-right-gray' />
                  <UIIcon2 className='ic-system-20-graph' />
                  <UIIcon2 className='ic-system-20-headset' />
                  <UIIcon2 className='ic-system-20-info' />
                  <UIIcon2 className='ic-system-20-list' />
                  <UIIcon2 className='ic-system-20-map' />
                  <UIIcon2 className='ic-system-20-play' />
                  <UIIcon2 className='ic-system-20-refresh' />
                  <UIIcon2 className='ic-system-20-reload' />
                  <UIIcon2 className='ic-system-20-stop' />
                  <UIIcon2 className='ic-system-20-user' />
                </div>

                <hr className='icon-divider' />

                <p className='icon-category'>LNB-menu</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-lnb-menu-20-agent-apikey' />
                  <UIIcon2 className='ic-lnb-menu-20-agent-apikey-on' />
                  <UIIcon2 className='ic-lnb-menu-20-agent-builder' />
                  <UIIcon2 className='ic-lnb-menu-20-agent-builder-on' />
                  <UIIcon2 className='ic-lnb-menu-20-agent-deployment' />
                  <UIIcon2 className='ic-lnb-menu-20-agent-deployment-on' />
                  <UIIcon2 className='ic-lnb-menu-20-agent-list' />
                  <UIIcon2 className='ic-lnb-menu-20-agent-list-on' />
                  <UIIcon2 className='ic-lnb-menu-20-data-catalog' />
                  <UIIcon2 className='ic-lnb-menu-20-data-catalog-on' />
                  <UIIcon2 className='ic-lnb-menu-20-data-storage' />
                  <UIIcon2 className='ic-lnb-menu-20-data-storage-on' />
                  <UIIcon2 className='ic-lnb-menu-20-finetuning' />
                  <UIIcon2 className='ic-lnb-menu-20-finetuning-on' />
                  <UIIcon2 className='ic-lnb-menu-20-home-dashboard' />
                  <UIIcon2 className='ic-lnb-menu-20-home-dashboard-on' />
                  <UIIcon2 className='ic-lnb-menu-20-home-modelgarden' />
                  <UIIcon2 className='ic-lnb-menu-20-home-modelgarden-on' />
                  <UIIcon2 className='ic-lnb-menu-20-log' />
                  <UIIcon2 className='ic-lnb-menu-20-log-on' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-access' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-access-on' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-authority' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-authority-on' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-filter' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-filter-on' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-group' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-group-on' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-history' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-history-on' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-project' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-project-on' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-resource' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-resource-on' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-right' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-right-on' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-safety' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-safety-on' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-user' />
                  <UIIcon2 className='ic-lnb-menu-20-manage-user-on' />
                  <UIIcon2 className='ic-lnb-menu-20-model-catalog' />
                  <UIIcon2 className='ic-lnb-menu-20-model-catalog-on' />
                  <UIIcon2 className='ic-lnb-menu-20-model-deployment' />
                  <UIIcon2 className='ic-lnb-menu-20-model-deployment-on' />
                  <UIIcon2 className='ic-lnb-menu-20-model-evaluation' />
                  <UIIcon2 className='ic-lnb-menu-20-model-evaluation-on' />
                  <UIIcon2 className='ic-lnb-menu-20-model-playground' />
                  <UIIcon2 className='ic-lnb-menu-20-model-playground-on' />
                  <UIIcon2 className='ic-lnb-menu-20-mornitoring' />
                  <UIIcon2 className='ic-lnb-menu-20-mornitoring-on' />
                  <UIIcon2 className='ic-lnb-menu-20-notice' />
                  <UIIcon2 className='ic-lnb-menu-20-notice-on' />
                  <UIIcon2 className='ic-lnb-menu-20-playground' />
                  <UIIcon2 className='ic-lnb-menu-20-playground-on' />
                  <UIIcon2 className='ic-lnb-menu-20-prompt-fewshot' />
                  <UIIcon2 className='ic-lnb-menu-20-prompt-fewshot-on' />
                  <UIIcon2 className='ic-lnb-menu-20-prompt-inference' />
                  <UIIcon2 className='ic-lnb-menu-20-prompt-inference-on' />
                  <UIIcon2 className='ic-lnb-menu-20-tool' />
                  <UIIcon2 className='ic-lnb-menu-20-tool-on' />
                  <UIIcon2 className='ic-lnb-menu-20-ide' />
                  <UIIcon2 className='ic-lnb-menu-20-ide-on' />
                </div>

                <hr className='icon-divider' />

                <p className='icon-category'>빌더-노드</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-builder-node-20-categorizer' />
                  <UIIcon2 className='ic-builder-node-20-categorizer-disabled' />
                  <UIIcon2 className='ic-builder-node-20-code' />
                  <UIIcon2 className='ic-builder-node-20-code-disabled' />
                  <UIIcon2 className='ic-builder-node-20-compress' />
                  <UIIcon2 className='ic-builder-node-20-compress-disabled' />
                  <UIIcon2 className='ic-builder-node-20-filter' />
                  <UIIcon2 className='ic-builder-node-20-filter-disabled' />
                  <UIIcon2 className='ic-builder-node-20-generator' />
                  <UIIcon2 className='ic-builder-node-20-generator-disabled' />
                  <UIIcon2 className='ic-builder-node-20-input' />
                  <UIIcon2 className='ic-builder-node-20-input-disabled' />
                  <UIIcon2 className='ic-builder-node-20-note' />
                  <UIIcon2 className='ic-builder-node-20-note-disabled' />
                  <UIIcon2 className='ic-builder-node-20-output-chat' />
                  <UIIcon2 className='ic-builder-node-20-output-chat-disabled' />
                  <UIIcon2 className='ic-builder-node-20-output-keys' />
                  <UIIcon2 className='ic-builder-node-20-output-keys-disabled' />
                  <UIIcon2 className='ic-builder-node-20-rerank' />
                  <UIIcon2 className='ic-builder-node-20-rerank-disabled' />
                  <UIIcon2 className='ic-builder-node-20-rereiever' />
                  <UIIcon2 className='ic-builder-node-20-rereiever-disabled' />
                  <UIIcon2 className='ic-builder-node-20-rewriter' />
                  <UIIcon2 className='ic-builder-node-20-rewriter-disabled' />
                  <UIIcon2 className='ic-builder-node-20-tool' />
                  <UIIcon2 className='ic-builder-node-20-tool-disabled' />
                </div>
              </dd>
            </dl>
          </UIArticle>

          {/* 24px */}
          <UIArticle>
            <dl className='guide'>
              <dt className='guide-title'>24px</dt>
              <dd className='guide-content'>
                <p className='icon-category'>LNB-menu</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-lnb-menu-24-close' />
                  <UIIcon2 className='ic-lnb-menu-24-open' />
                </div>

                <hr className='icon-divider' />

                <p className='icon-category'>시스템</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-system-24-page' />
                  <UIIcon2 className='ic-system-24-prompt' />
                  <UIIcon2 className='ic-system-24-add' />
                  <UIIcon2 className='ic-system-24-download' />
                  <UIIcon2 className='ic-system-24-download-black' />
                  <UIIcon2 className='ic-system-24-alarm' />
                  <UIIcon2 className='ic-system-24-area' />
                  <UIIcon2 className='ic-system-24-arrow-right-double' />
                  <UIIcon2 className='ic-system-24-arrow-left' />
                  <UIIcon2 className='ic-system-24-arrow-right-1' />
                  <UIIcon2 className='ic-system-24-box-disabled-selected' />
                  <UIIcon2 className='ic-system-24-box-disabled' />
                  <UIIcon2 className='ic-system-24-box-selected' />
                  <UIIcon2 className='ic-system-24-box-unselected' />
                  <UIIcon2 className='ic-system-24-calender' />
                  <UIIcon2 className='ic-system-24-call' />
                  <UIIcon2 className='ic-system-24-camera' />
                  <UIIcon2 className='ic-system-24-check-disabled-selected' />
                  <UIIcon2 className='ic-system-24-check-disabled' />
                  <UIIcon2 className='ic-system-24-check-selected' />
                  <UIIcon2 className='ic-system-24-check-unselected' />
                  <UIIcon2 className='ic-system-24-circle-disabled-selected' />
                  <UIIcon2 className='ic-system-24-circle-disabled' />
                  <UIIcon2 className='ic-system-24-circle-unchecked' />
                  <UIIcon2 className='ic-system-24-clear' />
                  <UIIcon2 className='ic-system-24-complete' />
                  <UIIcon2 className='ic-system-24-data' />
                  <UIIcon2 className='ic-system-24-delete' />
                  <UIIcon2 className='ic-system-24-down' />
                  <UIIcon2 className='ic-system-24-error' />
                  <UIIcon2 className='ic-system-24-hide' />
                  <UIIcon2 className='ic-system-24-loading' />
                  <UIIcon2 className='ic-system-24-menu' />
                  <UIIcon2 className='ic-system-24-more-2' />
                  <UIIcon2 className='ic-system-24-more' />
                  <UIIcon2 className='ic-system-24-outline-add' />
                  <UIIcon2 className='ic-system-24-outline-black-alarm' />
                  <UIIcon2 className='ic-system-24-outline-blue-add' />
                  <UIIcon2 className='ic-system-24-outline-blue-alarm' />
                  <UIIcon2 className='ic-system-24-outline-blue-check' />
                  <UIIcon2 className='ic-system-24-outline-blue-complete' />
                  <UIIcon2 className='ic-system-24-outline-blue-edit' />
                  <UIIcon2 className='ic-system-24-outline-blue-export' />
                  <UIIcon2 className='ic-system-24-outline-blue-minus' />
                  <UIIcon2 className='ic-system-24-outline-blue-setting' />
                  <UIIcon2 className='ic-system-24-outline-check' />
                  <UIIcon2 className='ic-system-24-outline-download' />
                  <UIIcon2 className='ic-system-24-outline-duplicate' />
                  <UIIcon2 className='ic-system-24-outline-edit' />
                  <UIIcon2 className='ic-system-24-outline-export' />
                  <UIIcon2 className='ic-system-24-outline-folder' />
                  <UIIcon2 className='ic-system-24-outline-gray-add' />
                  <UIIcon2 className='ic-system-24-outline-gray-alarm' />
                  <UIIcon2 className='ic-system-24-outline-gray-calender' />
                  <UIIcon2 className='ic-system-24-outline-gray-duplicate' />
                  <UIIcon2 className='ic-system-24-outline-gray-left-box-double' />
                  <UIIcon2 className='ic-system-24-outline-gray-left-box' />
                  <UIIcon2 className='ic-system-24-outline-gray-left-double' />
                  <UIIcon2 className='ic-system-24-outline-gray-loading' />
                  <UIIcon2 className='ic-system-24-outline-gray-log' />
                  <UIIcon2 className='ic-system-24-outline-gray-minus' />
                  <UIIcon2 className='ic-system-24-outline-gray-right-box-double' />
                  <UIIcon2 className='ic-system-24-outline-gray-right-box' />
                  <UIIcon2 className='ic-system-24-outline-gray-right-double' />
                  <UIIcon2 className='ic-system-24-outline-gray-search' />
                  <UIIcon2 className='ic-system-24-outline-gray-small-down' />
                  <UIIcon2 className='ic-system-24-outline-gray-small-right' />
                  <UIIcon2 className='ic-system-24-outline-gray-trash' />
                  <UIIcon2 className='ic-system-24-outline-gray-view-off' />
                  <UIIcon2 className='ic-system-24-outline-gray-view' />
                  <UIIcon2 className='ic-system-24-outline-headset' />
                  <UIIcon2 className='ic-system-24-outline-heart' />
                  <UIIcon2 className='ic-system-24-outline-hourglass' />
                  <UIIcon2 className='ic-system-24-outline-large-add' />
                  <UIIcon2 className='ic-system-24-outline-large-close' />
                  <UIIcon2 className='ic-system-24-outline-large-down' />
                  <UIIcon2 className='ic-system-24-outline-large-left' />
                  <UIIcon2 className='ic-system-24-outline-large-right' />
                  <UIIcon2 className='ic-system-24-outline-large-share' />
                  <UIIcon2 className='ic-system-24-outline-large-up' />
                  <UIIcon2 className='ic-system-24-outline-left-box-double' />
                  <UIIcon2 className='ic-system-24-outline-left-box' />
                  <UIIcon2 className='ic-system-24-outline-left-double' />
                  <UIIcon2 className='ic-system-24-outline-list' />
                  <UIIcon2 className='ic-system-24-outline-log' />
                  <UIIcon2 className='ic-system-24-outline-login-pw' />
                  <UIIcon2 className='ic-system-24-outline-login' />
                  <UIIcon2 className='ic-system-24-outline-map' />
                  <UIIcon2 className='ic-system-24-outline-medium-close' />
                  <UIIcon2 className='ic-system-24-outline-medium-down' />
                  <UIIcon2 className='ic-system-24-outline-medium-refresh' />
                  <UIIcon2 className='ic-system-24-outline-medium-up' />
                  <UIIcon2 className='ic-system-24-outline-merge' />
                  <UIIcon2 className='ic-system-24-outline-minus' />
                  <UIIcon2 className='ic-system-24-outline-notice' />
                  <UIIcon2 className='ic-system-24-outline-overflow' />
                  <UIIcon2 className='ic-system-24-outline-pin' />
                  <UIIcon2 className='ic-system-24-outline-right-box-double' />
                  <UIIcon2 className='ic-system-24-outline-right-box' />
                  <UIIcon2 className='ic-system-24-outline-right-double' />
                  <UIIcon2 className='ic-system-24-outline-scroll-bottom' />
                  <UIIcon2 className='ic-system-24-outline-scroll-top' />
                  <UIIcon2 className='ic-system-24-outline-search' />
                  <UIIcon2 className='ic-system-24-outline-seperate' />
                  <UIIcon2 className='ic-system-24-outline-setting' />
                  <UIIcon2 className='ic-system-24-outline-shortcut' />
                  <UIIcon2 className='ic-system-24-outline-small-close' />
                  <UIIcon2 className='ic-system-24-outline-small-down' />
                  <UIIcon2 className='ic-system-24-outline-small-left' />
                  <UIIcon2 className='ic-system-24-outline-small-refresh' />
                  <UIIcon2 className='ic-system-24-outline-small-right' />
                  <UIIcon2 className='ic-system-24-outline-small-share' />
                  <UIIcon2 className='ic-system-24-outline-small-up' />
                  <UIIcon2 className='ic-system-24-outline-sort' />
                  <UIIcon2 className='ic-system-24-outline-star' />
                  <UIIcon2 className='ic-system-24-outline-time' />
                  <UIIcon2 className='ic-system-24-outline-transfer' />
                  <UIIcon2 className='ic-system-24-outline-trash' />
                  <UIIcon2 className='ic-system-24-outline-view-off' />
                  <UIIcon2 className='ic-system-24-outline-view' />
                  <UIIcon2 className='ic-system-24-outline-white-close' />
                  <UIIcon2 className='ic-system-24-outline-write' />
                  <UIIcon2 className='ic-system-24-radio-disabled-selected' />
                  <UIIcon2 className='ic-system-24-radio-disabled' />
                  <UIIcon2 className='ic-system-24-radio-selected' />
                  <UIIcon2 className='ic-system-24-radio-unselected' />
                  <UIIcon2 className='ic-system-24-see' />
                  <UIIcon2 className='ic-system-24-toggle-area-card-on' />
                  <UIIcon2 className='ic-system-24-toggle-area-card' />
                  <UIIcon2 className='ic-system-24-toggle-area-list-on' />
                  <UIIcon2 className='ic-system-24-toggle-area-list' />
                  <UIIcon2 className='ic-system-24-up' />
                  <UIIcon2 className='ic-system-24-AppBar-close' />
                  <UIIcon2 className='ic-system-24-link' />
                </div>

                <hr className='icon-divider' />

                <p className='icon-category'>모델(공급사)</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-model-24-hugging-face' />
                  <UIIcon2 className='ic-model-24-ax' />
                  <UIIcon2 className='ic-model-24-cohere' />
                  <UIIcon2 className='ic-model-24-mistral' />
                  <UIIcon2 className='ic-model-24-lama' />
                  <UIIcon2 className='ic-model-24-meta' />
                  <UIIcon2 className='ic-model-24-openai' />
                  <UIIcon2 className='ic-model-24-google' />
                  <UIIcon2 className='ic-model-24-microsoft' />
                  <UIIcon2 className='ic-model-24-anthropic' />
                  <UIIcon2 className='ic-model-24-etc' />
                </div>

                <hr className='icon-divider' />

                <p className='icon-category'>MCP서버 (LNB 아이콘추가)</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-lnb-menu-20-agent-list' />
                  <UIIcon2 className='ic-lnb-menu-20-agent-list-on' />
                </div>

                <hr className='icon-divider' />

                <p className='icon-category'>명장 AI (아이콘추가&새창링크)</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-lnb-menu-20-master' />
                  <UIIcon2 className='ic-lnb-menu-20-master-on' />
                  <UIButton2 className='cursor-pointer'>
                    <UIIcon2 className='ic-system-16-link' />
                  </UIButton2>
                </div>
              </dd>
            </dl>
          </UIArticle>

          {/* 32px */}
          <UIArticle>
            <dl className='guide'>
              <dt className='guide-title'>32px</dt>
              <dd className='guide-content'>
                <p className='icon-category'>시스템</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-system-32-send' />
                  <UIIcon2 className='ic-system-32-trash' />
                  <UIIcon2 className='ic-system-32-outline-blue-alarm' />
                  <UIIcon2 className='ic-system-32-outline-gray-alarm' />
                  <UIIcon2 className='ic-system-32-outline-gray-log' />
                  <UIIcon2 className='ic-system-32-arrow-down-gray' />
                  <UIIcon2 className='ic-system-32-arrow-down-gray-fill' />
                  <UIIcon2 className='ic-system-32-arrow-up-gray' />
                  <UIIcon2 className='ic-system-32-arrow-up-gray-fill' />
                </div>

                <hr className='icon-divider' />

                <p className='icon-category'>GNB</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-gnb-32-alarm' />
                  <UIIcon2 className='ic-gnb-32-alarm-on' />
                  <UIIcon2 className='ic-gnb-32-setting' />
                </div>

                <hr className='icon-divider' />

                <p className='icon-category'>LNB-menu</p>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-lnb-menu-32-accent-agent-1' />
                  <UIIcon2 className='ic-lnb-menu-32-accent-agent-2' />
                  <UIIcon2 className='ic-lnb-menu-32-accent-data-2' />
                  <UIIcon2 className='ic-lnb-menu-32-accent-develop' />
                  <UIIcon2 className='ic-lnb-menu-32-accent-finetuning' />
                  <UIIcon2 className='ic-lnb-menu-32-accent-home' />
                  <UIIcon2 className='ic-lnb-menu-32-accent-log' />
                  <UIIcon2 className='ic-lnb-menu-32-accent-management' />
                  <UIIcon2 className='ic-lnb-menu-32-accent-model' />
                  <UIIcon2 className='ic-lnb-menu-32-accent-mornitoring' />
                  <UIIcon2 className='ic-lnb-menu-32-accent-notice' />
                  <UIIcon2 className='ic-lnb-menu-32-accent-playground' />
                  <UIIcon2 className='ic-lnb-menu-32-accent-prompt' />
                  <UIIcon2 className='ic-lnb-menu-32-default-agent-1' />
                  <UIIcon2 className='ic-lnb-menu-32-default-agent-2' />
                  <UIIcon2 className='ic-lnb-menu-32-default-data-2' />
                  <UIIcon2 className='ic-lnb-menu-32-default-develop' />
                  <UIIcon2 className='ic-lnb-menu-32-default-finetuning' />
                  <UIIcon2 className='ic-lnb-menu-32-default-home' />
                  <UIIcon2 className='ic-lnb-menu-32-default-log' />
                  <UIIcon2 className='ic-lnb-menu-32-default-management' />
                  <UIIcon2 className='ic-lnb-menu-32-default-model' />
                  <UIIcon2 className='ic-lnb-menu-32-default-mornitoring' />
                  <UIIcon2 className='ic-lnb-menu-32-default-notice' />
                  <UIIcon2 className='ic-lnb-menu-32-default-playground' />
                  <UIIcon2 className='ic-lnb-menu-32-default-prompt' />
                </div>
              </dd>
            </dl>
          </UIArticle>

          {/* 40px */}
          <UIArticle>
            <dl className='guide'>
              <dt className='guide-title'>40px</dt>
              <dd className='guide-content'>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-lnb-menu-40-delete' />
                </div>
              </dd>
            </dl>
          </UIArticle>

          {/* 48px */}
          <UIArticle>
            <dl className='guide'>
              <dt className='guide-title'>48px</dt>
              <dd className='guide-content'>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-system-48-delete' />
                  <UIIcon2 className='ic-system-48-swingchat' />
                  <UIIcon2 className='ic-system-48-molimate' />
                  <UIIcon2 className='ic-system-48-warning' />
                  <UIIcon2 className='ic-system-48-chat' />
                  <UIIcon2 className='ic-system-48-ai' />
                  <UIIcon2 className='ic-system-48-msg' />
                  <UIIcon2 className='ic-system-48-builder' />
                  <UIIcon2 className='ic-system-48-key' />
                </div>
              </dd>
            </dl>
          </UIArticle>

          {/* 72px */}
          <UIArticle>
            <dl className='guide'>
              <dt className='guide-title'>72px</dt>
              <dd className='guide-content'>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-system-72-feedback' />
                  <UIIcon2 className='ic-system-72-feedback-check' />
                  <UIIcon2 className='ic-system-72-feedback-check-blue' />
                  <UIIcon2 className='ic-system-72-alarm' />
                </div>
              </dd>
            </dl>
          </UIArticle>

          {/* 80px */}
          <UIArticle>
            <dl className='guide'>
              <dt className='guide-title'>80px</dt>
              <dd className='guide-content'>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-system-80-default-nodata' />
                </div>
              </dd>
            </dl>
          </UIArticle>

          {/* 180px */}
          <UIArticle>
            <dl className='guide'>
              <dt className='guide-title'>180px</dt>
              <dd className='guide-content'>
                <div className='guide-grid'>
                  <UIIcon2 className='ic-system-180-error' />
                  <UIIcon2 className='ic-system-180-open' />
                </div>
              </dd>
            </dl>
          </UIArticle>
        </UIPageBody>

        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <div className='btn-group direction-row align-center'>
              <UIButton2 className='btn-primary-blue'>확인</UIButton2>
              <UIButton2 className='btn-primary-gray'>취소</UIButton2>
            </div>
          </UIArticle>
        </UIPageFooter>
      </section>
    </DesignLayout>
  );
};
