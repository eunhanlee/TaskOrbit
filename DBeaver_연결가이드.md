# DBeaver PostgreSQL 연결 가이드

## 연결 정보

### 기본 연결 설정
- **호스트**: `localhost`
- **포트**: `5432`
- **데이터베이스**: `taskorbit`
- **사용자명**: `postgres`
- **비밀번호**: `postgres`

## DBeaver 연결 설정 단계

1. **새 연결 생성**
   - DBeaver 실행
   - 상단 메뉴: `Database` → `New Database Connection`
   - 또는 `Ctrl+Shift+N`

2. **PostgreSQL 선택**
   - 데이터베이스 목록에서 `PostgreSQL` 선택
   - `Next` 클릭

3. **연결 정보 입력**
   ```
   Host: localhost
   Port: 5432
   Database: taskorbit
   Username: postgres
   Password: postgres
   ```

4. **연결 테스트**
   - `Test Connection` 버튼 클릭
   - 드라이버가 없다면 자동으로 다운로드됨
   - `Connected` 메시지 확인

5. **연결 완료**
   - `Finish` 클릭

## 생성된 테이블 확인

애플리케이션을 실행하면 Flyway가 자동으로 다음 테이블들을 생성합니다:

1. **tasks** - Task 정보
2. **task_logs** - Task 로그
3. **task_completion_records** - 완료 기록
4. **recurring_task_settings** - 반복 작업 설정
5. **global_logs** - Undo/Redo를 위한 전역 로그

## 테이블 확인 방법

DBeaver에서:
1. 연결된 데이터베이스 확장
2. `Schemas` → `public` → `Tables` 확인
3. 또는 SQL 편집기에서:
   ```sql
   SELECT table_name 
   FROM information_schema.tables 
   WHERE table_schema = 'public';
   ```

## 주의사항

- PostgreSQL 컨테이너가 실행 중이어야 합니다
- 애플리케이션을 최소 한 번 실행해야 Flyway가 테이블을 생성합니다
- 테이블이 보이지 않으면 백엔드 애플리케이션을 실행해보세요


