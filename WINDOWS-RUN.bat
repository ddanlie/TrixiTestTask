@echo off
set IMAGE_NAME=trixi_test_task
set CONTAINER_NAME=trixi_test_task_app

echo ===> Cleaning up old containers...
docker rm -f %CONTAINER_NAME% 2>nul

echo ===> Building Docker image: %IMAGE_NAME%...
docker build -t %IMAGE_NAME% .
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker build failed!
    exit /b %ERRORLEVEL%
)

echo ===> Running Docker container...
docker run --name %CONTAINER_NAME% --rm -p 8080:8080 %IMAGE_NAME%