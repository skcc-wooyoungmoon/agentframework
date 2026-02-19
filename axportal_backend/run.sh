#!/bin/bash

echo "=========================================="
echo "AxportalBackend 빌드 및 실행 스크립트"
echo "=========================================="

show_menu() {
    echo ""
    echo "1. 로컬 환경으로 실행 (H2)"
    echo "2. 외부 로컬 환경으로 실행 (H2)"
    echo "3. 외부 개발 환경으로 실행 (PostgreSQL)"
    echo "4. 빌드만 수행"
    echo "5. 테스트 실행"
    echo "6. H2 콘솔 접속 정보"
    echo "7. Swagger UI 접속 정보"
    echo "8. 종료"
    echo ""
}

run_local() {
    echo ""
    echo "[로컬 환경 실행]"
    echo "프로필: local"
    echo "데이터베이스: H2 인메모리"
    echo "포트: 8080"
    echo ""
    mvn spring-boot:run -Dspring-boot.run.profiles=local
}

run_elocal() {
    echo ""
    echo "[외부 로컬 환경 실행]"
    echo "프로필: elocal"
    echo "데이터베이스: H2 인메모리"
    echo "포트: 8080"
    echo ""
    mvn spring-boot:run -Dspring-boot.run.profiles=elocal
}

run_edev() {
    echo ""
    echo "[외부 개발 환경 실행]"
    echo "프로필: edev"
    echo "데이터베이스: PostgreSQL"
    echo "포트: 8080"
    echo ""
    echo "PostgreSQL이 localhost:5432에서 실행 중인지 확인하세요."
    echo "데이터베이스: axportal_edev"
    echo "사용자: axportal_user"
    echo ""
    mvn spring-boot:run -Dspring-boot.run.profiles=edev
}

build_project() {
    echo ""
    echo "[프로젝트 빌드]"
    echo ""
    mvn clean package
    echo ""
    echo "빌드 완료! 생성된 JAR: target/aiplatform-1.0.0.jar"
}

run_tests() {
    echo ""
    echo "[테스트 실행]"
    echo ""
    mvn test
}

show_h2_info() {
    echo ""
    echo "[H2 Database 콘솔 접속 정보]"
    echo "=========================================="
    echo "URL: http://localhost:8080/api/v1/h2-console"
    echo "JDBC URL: jdbc:h2:mem:axportal"
    echo "사용자명: sa"
    echo "비밀번호: (공백)"
    echo "=========================================="
    echo ""
    read -p "계속하려면 Enter를 누르세요..."
}

show_swagger_info() {
    echo ""
    echo "[Swagger UI 접속 정보]"
    echo "=========================================="
    echo "Swagger UI: http://localhost:8080/api/v1/swagger-ui.html"
    echo "API Docs: http://localhost:8080/api/v1/api-docs"
    echo "Health Check: http://localhost:8080/api/v1/health"
    echo "System Info: http://localhost:8080/api/v1/info"
    echo "=========================================="
    echo ""
    read -p "계속하려면 Enter를 누르세요..."
}

while true; do
    show_menu
    read -p "원하는 옵션을 선택하세요 (1-8): " choice
    
    case $choice in
        1)
            run_local
            ;;
        2)
            run_elocal
            ;;
        3)
            run_edev
            ;;
        4)
            build_project
            ;;
        5)
            run_tests
            ;;
        6)
            show_h2_info
            ;;
        7)
            show_swagger_info
            ;;
        8)
            echo ""
            echo "빌드 스크립트를 종료합니다."
            exit 0
            ;;
        *)
            echo "잘못된 선택입니다. 1-8 사이의 숫자를 입력하세요."
            ;;
    esac
done
