#!/bin/bash

set -e

PROJECT_PATH=/home/ubuntu/project
NGINX_CONF=${PROJECT_PATH}/nginx/service-url.inc

cd ${PROJECT_PATH}

echo "=============================="
echo " Blue Green Deploy Start"
echo "=============================="

#################################################
# 최초 실행이면 Blue를 기본값으로 생성
#################################################

if [ ! -f nginx/service-url.inc ]; then

cat > nginx/service-url.inc <<EOF
upstream spring {
    server spring-blue:8080;
}
EOF

fi

#################################################
# app.jar 존재 확인
#################################################

if [ ! -f app.jar ]; then
    echo "app.jar가 존재하지 않습니다."
    exit 1
fi

#################################################
# 현재 서비스 확인
#################################################

CURRENT=$(grep "server spring-" ${NGINX_CONF} | awk '{print $2}' | cut -d':' -f1)

echo "Current Service : ${CURRENT}"

if [ "${CURRENT}" = "spring-blue" ]; then

    TARGET=spring-green
    TARGET_PORT=8082
    BEFORE=spring-blue

else

    TARGET=spring-blue
    TARGET_PORT=8081
    BEFORE=spring-green

fi

echo "Deploy Target : ${TARGET}"

#################################################
# 새 이미지 Build + 실행
#################################################

echo "Build New Image..."

#################################################
# PostgreSQL 먼저 실행
#################################################

docker compose up -d postgres

#################################################
# Spring 실행
#################################################

docker compose up -d --build ${TARGET}

#################################################
# Health Check
#################################################

echo "Health Check..."

SUCCESS=false

for RETRY in {1..20}
do

    if curl -fs http://localhost:${TARGET_PORT}/health > /dev/null
    then

        SUCCESS=true
        echo "Health Check Success"

        break

    fi

    echo "Retry... (${RETRY}/20)"

    sleep 5

done

if [ "$SUCCESS" = false ]; then

    echo "Health Check Fail"

    docker compose logs ${TARGET}

    exit 1

fi

#################################################
# nginx 전환
#################################################

echo "Switch Nginx"

cat > ${NGINX_CONF} <<EOF
upstream spring {
    server ${TARGET}:8080;
}
EOF

#################################################
# nginx Reload (최초 배포 대응)
#################################################

if docker ps --format '{{.Names}}' | grep -q "^nginx$"; then

    echo "Reload Nginx"

    docker exec nginx nginx -s reload

else

    echo "Start Nginx"

    docker compose up -d nginx

fi

#################################################
# 이전 컨테이너 종료
#################################################

echo "Stop ${BEFORE}"

docker compose stop ${BEFORE}

echo "=============================="
echo " Deploy Success"
echo "=============================="