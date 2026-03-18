# Apple Reminders Web Clone - 작업 목록

> plan.md 기반 세부 태스크. 완료 시 `[x]`로 체크.

---

## Phase 1: Backend 기초 — 데이터 모델 + REST API

### 프로젝트 설정
- [x] `build.gradle.kts`에 `spring-boot-starter-web` 의존성 추가
- [x] `application.properties`에 H2 파일 DB 설정 (`jdbc:h2:file:./data/reminders`)
- [x] `application.properties`에 JPA 설정 (`ddl-auto=update`, `show-sql=true`)
- [x] `application.properties`에 H2 콘솔 설정 (`/h2-console`)

### 엔티티
- [x] `Priority.java` enum 생성 (NONE, LOW, MEDIUM, HIGH)
- [x] `ReminderList.java` 엔티티 생성 (id, name, color, icon, timestamps)
- [x] `ReminderList`에 `@OneToMany` reminders 관계 설정 (cascade ALL, orphanRemoval)
- [x] `Reminder.java` 엔티티 생성 (id, title, memo, dueDate, dueTime, priority, isCompleted, completedAt, timestamps)
- [x] `Reminder`에 `@ManyToOne` list 관계 설정 (LAZY fetch)

### Repository
- [x] `ReminderListRepository` 생성 (JpaRepository 상속)
- [x] `ReminderRepository` 생성 (JpaRepository 상속)
- [x] `findByListIdAndIsCompleted` 쿼리 메서드 추가
- [x] `findByListId` 쿼리 메서드 추가
- [x] `findByDueDateAndIsCompleted` 쿼리 메서드 추가 (오늘 필터)
- [x] `findByDueDateIsNotNullAndIsCompleted` 쿼리 메서드 추가 (예정 필터)
- [x] `findByIsCompleted` 쿼리 메서드 추가 (전체/완료 필터)

### DTO
- [x] `ReminderListRequest` 생성 (name, color, icon)
- [x] `ReminderListResponse` 생성 (id, name, color, icon, incompleteCount)
- [x] `ReminderListResponse.from()` 정적 팩토리 메서드 구현
- [x] `ReminderRequest` 생성 (title, memo, dueDate, dueTime, priority, isCompleted, listId)
- [x] `ReminderResponse` 생성 (전체 필드 + listName, listColor)
- [x] `ReminderResponse.from()` 정적 팩토리 메서드 구현

### Service
- [x] `ReminderListService` — 전체 리스트 조회 (미완료 카운트 포함)
- [x] `ReminderListService` — 단일 리스트 조회
- [x] `ReminderListService` — 리스트 생성 (기본값: color=#007AFF, icon=list.bullet)
- [x] `ReminderListService` — 리스트 수정 (부분 업데이트)
- [x] `ReminderListService` — 리스트 삭제
- [x] `ReminderService` — 리스트별 리마인더 조회
- [x] `ReminderService` — 리스트별 완료/미완료 필터 조회
- [x] `ReminderService` — 단일 리마인더 조회
- [x] `ReminderService` — 오늘 마감 리마인더 조회
- [x] `ReminderService` — 예정된 리마인더 조회
- [x] `ReminderService` — 전체/완료 리마인더 조회
- [x] `ReminderService` — 리마인더 생성
- [x] `ReminderService` — 리마인더 수정 (부분 업데이트, 리스트 이동 포함)
- [x] `ReminderService` — 완료 토글 (isCompleted 반전 + completedAt 설정)
- [x] `ReminderService` — 리마인더 삭제

### Controller
- [x] `ReminderListController` — `GET /api/lists` (전체 조회)
- [x] `ReminderListController` — `GET /api/lists/{id}` (단일 조회)
- [x] `ReminderListController` — `POST /api/lists` (생성)
- [x] `ReminderListController` — `PUT /api/lists/{id}` (수정)
- [x] `ReminderListController` — `DELETE /api/lists/{id}` (삭제)
- [x] `ReminderController` — `GET /api/lists/{listId}/reminders` (리스트별 조회, ?completed 파라미터)
- [x] `ReminderController` — `POST /api/lists/{listId}/reminders` (생성)
- [x] `ReminderController` — `GET /api/reminders/{id}` (단일 조회)
- [x] `ReminderController` — `PUT /api/reminders/{id}` (수정)
- [x] `ReminderController` — `PATCH /api/reminders/{id}/toggle` (완료 토글)
- [x] `ReminderController` — `DELETE /api/reminders/{id}` (삭제)
- [x] `ReminderController` — `GET /api/reminders/today` (오늘)
- [x] `ReminderController` — `GET /api/reminders/scheduled` (예정)
- [x] `ReminderController` — `GET /api/reminders/all` (전체)
- [x] `ReminderController` — `GET /api/reminders/completed` (완료됨)

### CORS + 검증
- [x] `WebConfig.java` — `http://localhost:3000` CORS 허용
- [x] `./gradlew build` 빌드 성공 확인
- [x] `./gradlew bootRun` 후 curl로 API 정상 동작 확인

---

## Phase 2: Frontend 기초 — 프로젝트 셋업 + 레이아웃

### 프로젝트 생성
- [x] Next.js 프로젝트 생성 (`frontend/`, TypeScript, Tailwind, App Router)
- [x] `npm run build` 빌드 성공 확인

### 글로벌 스타일
- [x] CSS 변수 정의 (sidebar-bg `#F2F2F7`, main-bg, hover-bg, separator 등)
- [x] SF Pro 폰트 스택 설정
- [x] 스크롤바 커스텀 스타일링
- [x] 체크마크 애니메이션 `@keyframes checkmark` 정의
- [x] fade-out 애니메이션 `@keyframes fadeOut` 정의

### 타입 정의
- [x] `Priority` enum 정의
- [x] `ReminderList` 인터페이스 정의
- [x] `Reminder` 인터페이스 정의
- [x] `SmartFilter` 타입 정의
- [x] `ViewState` 유니온 타입 정의 (smart | list)

### API 클라이언트
- [x] `fetchJson` 공통 래퍼 함수 (BASE_URL, Content-Type, 에러 처리)
- [x] 리스트 API 함수 (getLists, createList, updateList, deleteList)
- [x] 리마인더 API 함수 (getReminders, createReminder, updateReminder, toggleReminder, deleteReminder)
- [x] 스마트 필터 API 함수 (getTodayReminders, getScheduledReminders, getAllReminders, getCompletedReminders)

### 메인 레이아웃
- [x] `layout.tsx` — 한국어 lang, 메타 데이터 설정
- [x] `page.tsx` — 전체 상태 관리 (lists, reminders, view, selectedReminder, smartCounts)
- [x] `page.tsx` — 데이터 로딩 (loadLists, loadSmartCounts, loadReminders)
- [x] `page.tsx` — 이벤트 핸들러 (toggle, add, update, delete, saveList, deleteList)

---

## Phase 3: Frontend 사이드바 — 스마트 필터 + 리스트 목록

### SmartFilterCard 컴포넌트
- [x] 컬러 원형 아이콘 (오늘=파랑, 예정=빨강, 전체=검정, 완료=회색)
- [x] SVG 아이콘 렌더링 (캘린더, 클립보드 등)
- [x] 우상단 카운트 숫자 표시
- [x] 좌하단 라벨 표시 (오늘, 예정, 전체, 완료됨)
- [x] 활성 상태 배경색 변경

### ListItem 컴포넌트
- [x] 컬러 원형 아이콘 (리스트 색상 배경 + 흰색 목록 SVG)
- [x] 리스트 이름 표시 (truncate)
- [x] 미완료 카운트 표시
- [x] 활성 상태 배경 + hover 효과
- [x] 우클릭 이벤트 핸들러

### Sidebar 컴포넌트
- [x] 상단: 2x2 SmartFilterCard 그리드
- [x] 중단: "나의 목록" 섹션 헤더
- [x] 중단: ListItem 목록 (스크롤 가능)
- [x] 하단: 목록 추가 버튼 (파란색 + 아이콘)

---

## Phase 4: Frontend 메인 영역 — 리마인더 CRUD

### ReminderItem 컴포넌트
- [x] 원형 체크박스 (리스트 색상 테두리)
- [x] 체크 시 컬러 채움 + 체크마크 SVG 애니메이션
- [x] 완료 후 0.5초 대기 → fade-out 애니메이션
- [x] 우선순위 표시 (`!` / `!!` / `!!!`, 빨간색)
- [x] 제목 표시 (완료 시 취소선 + 회색)
- [x] 메모 미리보기 (1줄 truncate)
- [x] 마감일 표시 (오늘/내일/M/D 포맷, 지나면 빨간색)
- [x] 선택 상태 배경 + hover 효과

### ReminderPanel 컴포넌트
- [x] 헤더: 리스트/필터 이름 (리스트 색상 적용)
- [x] 미완료 리마인더 목록 렌더링
- [x] 빈 상태: "미리 알림 없음" 메시지
- [x] 완료됨 섹션: 접기/펼치기 토글 (화살표 회전)
- [x] 완료됨 섹션: 완료 카운트 표시
- [x] 리스트 뷰일 때만 하단 AddReminderInput 표시

### AddReminderInput 컴포넌트
- [x] 비활성 상태: `+ 새로운 미리 알림` 텍스트 버튼
- [x] 클릭 시 인라인 입력 필드 활성화 (autoFocus)
- [x] 빈 원형 체크박스 (리스트 색상) 표시
- [x] Enter 키로 빠른 생성
- [x] Escape 키로 취소
- [x] onBlur 시 자동 제출 + 비활성화

### ReminderDetail 컴포넌트
- [x] 우측 패널 레이아웃 (w-72, 보더 왼쪽)
- [x] 헤더: "세부사항" 제목 + 닫기 버튼
- [x] 제목 입력 필드 (onBlur 저장)
- [x] 메모 텍스트영역 (onBlur 저장)
- [x] 날짜 입력 (date picker, onBlur 저장)
- [x] 시간 입력 (time picker, onBlur 저장)
- [x] 우선순위 선택 (없음/낮음/중간/높음, onChange 즉시 저장)
- [x] reminder prop 변경 시 폼 상태 동기화 (useEffect)
- [x] 하단: 미리 알림 삭제 버튼 (confirm 다이얼로그)

---

## Phase 5: Frontend 리스트 관리 — 생성/수정 모달

### ListFormModal 컴포넌트
- [x] 오버레이 배경 (클릭 시 닫기)
- [x] 모달 헤더: 취소/완료 버튼 + 제목 ("새로운 목록" / "목록 편집")
- [x] 상단 미리보기: 컬러 원형 아이콘 (선택한 색상 + 아이콘)
- [x] 이름 입력 필드 (가운데 정렬, autoFocus)
- [x] 12색 팔레트 그리드 (선택 시 scale + ring 효과)
- [x] 12종 아이콘 선택 그리드 (선택 시 컬러 배경 전환)
- [x] 편집 모드: 기존 값으로 초기화 (useEffect)
- [x] 완료 버튼: 이름 비어있으면 비활성화

### 리스트 삭제
- [x] 사이드바 리스트 우클릭 → confirm 다이얼로그 → 삭제
- [x] 삭제 후 스마트 필터(오늘)로 뷰 전환

---

## Phase 6: UI 폴리싱 + 사용성 개선

### 6-1. 마이크로 인터랙션
- [x] 완료 체크박스 바운스 애니메이션 개선 (spring 효과)
- [x] 리스트/필터 전환 시 fade 트랜지션
- [x] 모달 열기 애니메이션 (scale 0.95→1 + opacity 0→1)
- [x] 모달 닫기 애니메이션 (scale 1→0.95 + opacity 1→0)
- [ ] 리마인더 항목 드래그 앤 드롭 순서 변경 (Backend: `sortOrder` 필드 추가) → Phase 7-2로 이관

### 6-2. 디자인 디테일
- [x] 사이드바 리스트 항목 더블클릭 → 편집 모달 열기
- [x] 커스텀 우클릭 컨텍스트 메뉴 컴포넌트 (편집/삭제 옵션)
- [x] 빈 상태 일러스트레이션 (리스트 없음, 리마인더 없음)
- [x] AddReminderInput에 날짜/우선순위 빠른 설정 아이콘 버튼 추가

### 6-3. 키보드 단축키
- [x] `Cmd/Ctrl + N` — 새 리마인더 입력 활성화
- [x] `Delete` / `Backspace` — 선택된 리마인더 삭제 (confirm)
- [x] `↑` / `↓` — 리마인더 목록 탐색 (선택 이동)
- [x] `Escape` — 상세 패널 닫기 / 모달 닫기

### 6-4. 반응형 레이아웃
- [x] 모바일 (<768px): 사이드바 숨김 + 햄버거 토글 버튼
- [x] 모바일: 상세 패널을 하단 시트로 전환
- [x] 태블릿 (768~1024px): 사이드바 축소 모드 (아이콘만 표시)

---

## Phase 7: 고급 기능

### 7-1. 검색
- [ ] Backend: `ReminderRepository`에 `findByTitleContainingIgnoreCase` 추가
- [ ] Backend: `GET /api/reminders/search?q={keyword}` 엔드포인트 구현
- [ ] Frontend: 사이드바 상단에 검색 입력 바 추가
- [ ] Frontend: 검색 결과 실시간 필터링 (debounce 300ms)
- [ ] Frontend: 검색 결과 하이라이트 표시

### 7-2. 리마인더 정렬
- [ ] Backend: `Reminder` 엔티티에 `sortOrder` (Integer) 필드 추가
- [ ] Backend: `PUT /api/reminders/reorder` 엔드포인트 (순서 일괄 업데이트)
- [ ] Frontend: 드래그 앤 드롭 라이브러리 도입 (dnd-kit 또는 react-beautiful-dnd)
- [ ] Frontend: 정렬 옵션 드롭다운 (수동 / 마감일순 / 우선순위순 / 생성일순)

### 7-3. 하위 작업 (Subtasks)
- [ ] Backend: `Reminder` 엔티티에 `parentId` (자기 참조) 추가
- [ ] Backend: `ReminderRepository`에 `findByParentId` 추가
- [ ] Backend: 하위 작업 CRUD API 구현
- [ ] Frontend: ReminderItem에 들여쓰기 + 접기/펼치기 UI
- [ ] Frontend: 하위 작업 추가 버튼

### 7-4. 태그
- [ ] Backend: `Tag` 엔티티 생성 (id, name, color)
- [ ] Backend: `Reminder` ↔ `Tag` M:N 관계 설정 (조인 테이블)
- [ ] Backend: 태그 CRUD API (`/api/tags`)
- [ ] Backend: 태그별 리마인더 필터 API
- [ ] Frontend: 리마인더 상세에 태그 추가/제거 UI
- [ ] Frontend: 사이드바에 태그 필터 섹션

### 7-5. 알림
- [ ] Frontend: 브라우저 Notification API 권한 요청
- [ ] Frontend: 마감 시간 도달 시 브라우저 알림 발송
- [ ] Backend: Spring `@Scheduled`로 예정 알림 조회
- [ ] Backend: WebSocket 또는 SSE로 실시간 알림 푸시
