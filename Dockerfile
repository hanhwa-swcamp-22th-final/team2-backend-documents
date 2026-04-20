FROM gradle:8.12-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre
# PdfGenerationService 가 PI/PO/SO/MO PDF 를 생성. 한글 폰트 없으면 본문이
# 깨져 나옴. activity 서비스와 동일하게 fonts-nanum 을 이미지에 포함.
RUN apt-get update \
 && apt-get install -y --no-install-recommends fonts-nanum fontconfig \
 && rm -rf /var/lib/apt/lists/* \
 && fc-cache -f || true
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
USER appuser
EXPOSE 8014
ENTRYPOINT ["java", "-jar", "app.jar"]
