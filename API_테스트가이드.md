# API 테스트 가이드

## 백엔드 서버 실행

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

또는 IntelliJ에서 `TaskOrbitApplication` 실행

서버가 `http://localhost:8080`에서 실행됩니다.

## API 엔드포인트

### Task API

#### 1. Today 탭 조회
```http
GET http://localhost:8080/api/tasks/today
```

#### 2. Later 탭 조회
```http
GET http://localhost:8080/api/tasks/later
```

#### 3. Done 탭 조회
```http
GET http://localhost:8080/api/tasks/done
```

#### 4. Record 탭 조회
```http
GET http://localhost:8080/api/tasks/record
```

#### 5. 작업 생성
```http
POST http://localhost:8080/api/tasks
Content-Type: application/json

{
  "title": "새로운 작업",
  "category": "개발",
  "size": "UNDER_30_MIN",
  "status": "ONGOING"
}
```

#### 6. 작업 조회
```http
GET http://localhost:8080/api/tasks/{id}
```

#### 7. 작업 수정
```http
PUT http://localhost:8080/api/tasks/{id}
Content-Type: application/json

{
  "title": "수정된 작업",
  "status": "WAITING"
}
```

#### 8. 작업 완료
```http
POST http://localhost:8080/api/tasks/{id}/complete
```

#### 9. 작업을 Waiting 상태로 변경
```http
POST http://localhost:8080/api/tasks/{id}/waiting
```

#### 10. 작업 삭제
```http
DELETE http://localhost:8080/api/tasks/{id}
```

### Task Log API

#### 1. 작업의 모든 로그 조회
```http
GET http://localhost:8080/api/tasks/{taskId}/logs
```

#### 2. 최신 로그 조회 (Next Action)
```http
GET http://localhost:8080/api/tasks/{taskId}/logs/latest
```

#### 3. 로그 생성
```http
POST http://localhost:8080/api/tasks/{taskId}/logs
Content-Type: application/json

{
  "content": "진행 상황 기록",
  "nextAction": "다음에 할 일"
}
```

### Recurring Task Setting API

#### 1. 활성화된 반복 작업 설정 조회
```http
GET http://localhost:8080/api/recurring-settings/active
```

#### 2. 반복 작업 설정 생성
```http
POST http://localhost:8080/api/recurring-settings
Content-Type: application/json

{
  "title": "매일 운동",
  "recurrenceType": "DAILY",
  "isActive": true
}
```

## 테스트 방법

### 1. 브라우저에서 직접 테스트
- GET 요청은 브라우저 주소창에 입력하여 확인 가능

### 2. curl 사용
```powershell
# Today 탭 조회
curl http://localhost:8080/api/tasks/today

# 작업 생성
curl -X POST http://localhost:8080/api/tasks -H "Content-Type: application/json" -d "{\"title\":\"테스트 작업\",\"category\":\"테스트\"}"
```

### 3. Postman 또는 Insomnia 사용
- 위의 API 엔드포인트를 Postman에 등록하여 테스트

### 4. 프론트엔드에서 테스트
- 프론트엔드 개발 서버 실행 후 API 호출 테스트

## 예상 응답 형식

### TaskResponse
```json
{
  "id": 1,
  "title": "새로운 작업",
  "category": "개발",
  "size": "UNDER_30_MIN",
  "status": "ONGOING",
  "workDate": "2025-11-29",
  "scheduleDate": "2025-11-29",
  "createdAt": "2025-11-29T10:00:00",
  "updatedAt": "2025-11-29T10:00:00",
  "nextAction": "다음에 할 일"
}
```

## 주의사항

- PostgreSQL 컨테이너가 실행 중이어야 합니다
- 백엔드 서버가 실행 중이어야 합니다
- CORS가 설정되어 있어 프론트엔드에서 호출 가능합니다

