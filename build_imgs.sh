./mvnw dependency:go-offline -Pproduction
# Собирать npm что-то не помогает
cd ./.npm_libs
npm install
cd ../

# Имена образов
DATE=$(date +"%Y%m%d_%H%M%S")
BUILD_IMAGE="libhome-build:latest"
DB_IMAGE="marolok/lib_home_db:${DATE}"
UI_IMAGE="marolok/lib_home:${DATE}"

echo "Date: ${DATE}"
echo "BUILD_IMAGE: ${BUILD_IMAGE}"
echo "DB_IMAGE: ${DB_IMAGE}"
echo "UI_IMAGE: ${UI_IMAGE}"

# 1. Собираем артефакты

echo "Building base image with all modules..."
docker build -f Dockerfile -t ${BUILD_IMAGE} .
echo "Base image built with artifacts"

# 2. Собираем LibHomeDB

echo "Building DB image (copies artifacts from base)..."
docker build -f LibHomeDB/Dockerfile -t ${DB_IMAGE} .
echo "DB image built"
echo "DB size:"
docker images ${DB_IMAGE} --format "table {{.Repository}}\t{{.Size}}"

# 3. Собираем LibHomeUI

echo "Building UI image (copies artifacts from base)..."
docker build -f LibHomeUI/Dockerfile -t ${UI_IMAGE} .
echo "UI image built"
echo "UI size:"
docker images ${UI_IMAGE} --format "table {{.Repository}}\t{{.Size}}"

# Удаляем артефакты для будущих сборок
docker rmi libhome-build:latest

