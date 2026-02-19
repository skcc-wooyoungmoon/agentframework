// https://vite.dev/config/
import path from 'node:path';
import { fileURLToPath } from 'node:url';

// import { storybookTest } from '@storybook/addon-vitest/vitest-plugin';
import tailwindcss from '@tailwindcss/vite';
import react from '@vitejs/plugin-react';
import { defineConfig, loadEnv } from 'vite';
const dirname = typeof __dirname !== 'undefined' ? __dirname : path.dirname(fileURLToPath(import.meta.url));

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [react(), tailwindcss()],
    resolve: {
      alias: {
        '@': path.resolve(dirname, 'src'),
      },
    },
    esbuild: {
      drop: env.VITE_RUN_MODE === 'PROD' ? ['console', 'debugger'] : undefined,
    },
    server: {
      proxy: {
        '/v1': {
          target: 'https://aip-stg.sktai.io/api',
          changeOrigin: true,
          secure: false,
          rewrite: path => path.replace(/^\/v1/, '/v1'),
        },
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          secure: false,
        },
      },
    },
    build: {
      // 빌드 시 이전 파일 정리
      emptyOutDir: true,
      // Edge 브라우저 및 저사양 PC 최적화
      target: 'es2020', // 더 넓은 호환성
      minify: 'esbuild', // 빠른 minification
      rollupOptions: {
        output: {
          manualChunks: {
            // Edge/저사양 PC를 위한 더 작은 청크 분할
            'react-core': ['react', 'react-dom'],
            'react-router': ['react-router-dom'],
            'query-lib': ['@tanstack/react-query'],
            'ag-grid': ['ag-grid-community', 'ag-grid-react'],
            charts: ['apexcharts', 'react-apexcharts'],
            flow: ['@xyflow/react'],
            codemirror: ['@uiw/react-codemirror', '@codemirror/lang-json', '@codemirror/lang-python', '@codemirror/lang-yaml'],
            utils: ['axios', 'date-fns', 'jotai', 'numeral', 'validator', 'imask', 'react-intl'],
          },
          // 청크 파일명 포맷 - 더 강력한 해시 생성
          chunkFileNames: 'assets/js/[name]-[hash:8].js',
          entryFileNames: 'assets/js/[name]-[hash:8].js',
          assetFileNames: assetInfo => {
            // 이미지 파일은 별도 폴더로 분리하고 해시 추가
            if (assetInfo.name && /\.(png|jpe?g|svg|gif|webp)$/.test(assetInfo.name)) {
              return 'assets/images/[name]-[hash:8][extname]';
            }
            // 폰트 파일도 해시 추가
            if (assetInfo.name && /\.(woff2?|ttf|eot|otf)$/.test(assetInfo.name)) {
              return 'assets/font/[name]-[hash:8][extname]';
            }
            return 'assets/[ext]/[name]-[hash:8].[ext]';
          },
        },
      },
      // Edge/저사양 PC를 위한 청크 크기 제한
      chunkSizeWarningLimit: 400, // 400KB로 제한
      // 소스맵 생성 (디버깅용, 프로덕션에서는 false로 설정 가능)
      sourcemap: false,
      // publicDir에서 제외할 파일 패턴
      copyPublicDir: true,
      // CSS 코드 분할 활성화
      cssCodeSplit: true,
      // 에셋 인라인 크기 제한 (4KB 미만은 base64로 인라인)
      assetsInlineLimit: 4096,
    },
  };
});
