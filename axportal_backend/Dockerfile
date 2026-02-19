
# Run stage
FROM public.ecr.aws/docker/library/eclipse-temurin:17-jre-alpine
COPY libs libs
COPY target/*.jar app.jar

RUN mkdir -p /gapdat/migration/aiplatform
RUN chmod 777 /gapdat/migration/aiplatform

EXPOSE 8080

# JVM 최적화 설정 (검증된 옵션만 사용)
ENTRYPOINT ["java", \
    # 메모리 설정: 컨테이너 메모리 비율 기준
    "-XX:InitialRAMPercentage=50.0", \
    "-XX:MaxRAMPercentage=60.0", \
    "-XX:MaxMetaspaceSize=512m", \
    # G1GC 기본 설정: 안정적이고 검증된 옵션들
    "-XX:+UseG1GC", \
    "-jar", "app.jar"]