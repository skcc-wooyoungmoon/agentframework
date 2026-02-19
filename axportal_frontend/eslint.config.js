import prettierConfig from 'eslint-config-prettier';
import importPlugin from 'eslint-plugin-import';
import prettier from 'eslint-plugin-prettier';

import js from '@eslint/js';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import { globalIgnores } from 'eslint/config';
import globals from 'globals';
import tseslint from 'typescript-eslint';

export default tseslint.config([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [js.configs.recommended, tseslint.configs.recommended, reactHooks.configs['recommended-latest'], reactRefresh.configs.vite, prettierConfig],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
    },
    plugins: {
      prettier: prettier,
      import: importPlugin,
    },
    rules: {
      'prettier/prettier': 'error',
      'no-unused-vars': 'error',
      '@typescript-eslint/no-unused-vars': 'error',
      'no-console': 'warn',

      // Import 순서 정렬 규칙 (Prettier와 호환)
      'import/order': [
        'error',
        {
          groups: [
            'builtin', // Node.js 내장 모듈
            'external', // 외부 패키지
            'internal', // 내부 모듈 (@/로 시작하는 것들)
            'parent', // 상위 디렉토리
            'sibling', // 같은 디렉토리
            'index', // index 파일
            'object', // object import
            'type', // type import
          ],
          pathGroups: [
            {
              pattern: 'react',
              group: 'external',
              position: 'before',
            },
            {
              pattern: '@/**',
              group: 'internal',
              position: 'after',
            },
          ],
          pathGroupsExcludedImportTypes: ['react'],
          'newlines-between': 'always',
          alphabetize: {
            order: 'asc',
            caseInsensitive: true,
          },
        },
      ],
      'import/no-unresolved': 'off', // TypeScript가 처리하므로 비활성화
      'import/no-duplicates': 'error',
      'import/no-unused-modules': 'warn',
    },
  },
]);
