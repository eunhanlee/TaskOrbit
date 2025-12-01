# Docker PostgreSQL 설정 가이드

## 1. Docker 설치 확인

PowerShell에서 다음 명령어로 확인:
```powershell
docker --version
```

## 2. PostgreSQL 컨테이너 실행

### 방법 1: docker-compose 사용 (권장)

프로젝트 루트 디렉토리에서:
```powershell
docker-compose up -d
```

### 방법 2: docker run 사용

```powershell
docker run --name taskorbit-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=taskorbit -p 5432:5432 -d postgres:16
```

## 3. 컨테이너 상태 확인

```powershell
docker ps
```

## 4. 로그 확인 (문제 발생 시)

```powershell
docker logs taskorbit-postgres
```

## 5. 데이터베이스 연결 테스트

PostgreSQL 클라이언트가 있다면:
```powershell
docker exec -it taskorbit-postgres psql -U postgres -d taskorbit
```

## 6. 컨테이너 중지

```powershell
docker-compose down
# 또는
docker stop taskorbit-postgres
```

## 7. 컨테이너 삭제 (데이터 유지)

```powershell
docker-compose down
# 데이터까지 삭제하려면
docker-compose down -v
```

## 문제 해결

### 포트 5432가 이미 사용 중인 경우
다른 PostgreSQL이 실행 중일 수 있습니다. 포트를 변경하거나 기존 서비스를 중지하세요.

### 컨테이너가 시작되지 않는 경우
```powershell
docker logs taskorbit-postgres
```
로 에러 메시지를 확인하세요.



