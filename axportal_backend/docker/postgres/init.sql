-- PostgreSQL 초기화 스크립트
-- 외부개발 환경용 데이터베이스 설정

-- UTF-8 인코딩 설정
SET client_encoding = 'UTF8';

-- 기본 스키마 생성
CREATE SCHEMA IF NOT EXISTS axportal;

-- 사용자 권한 설정
GRANT ALL PRIVILEGES ON SCHEMA axportal TO axportal_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA axportal TO axportal_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA axportal TO axportal_user;

-- 기본 데이터베이스 설정
COMMENT ON DATABASE axportal_edev IS 'AX Portal 외부개발 환경 데이터베이스';

-- 초기화 완료 로그
SELECT 'PostgreSQL 초기화 완료' AS status;
