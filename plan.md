# Apple Reminders Web Clone - 개발 계획

## 기술 스택

| 영역 | 기술 | 버전 | 비고 |
|------|------|------|------|
| Backend Runtime | Java | 25 | Gradle toolchain |
| Backend Framework | Spring Boot | 4.0.3 | spring-boot-starter-web, spring-boot-starter-data-jpa |
| Database | H2 | embedded | 파일 모드 (`jdbc:h2:file:./data/reminders`), ddl-auto=update |
| ORM | Hibernate | 7.x (Spring Boot 내장) | JPA 엔티티 매핑 |
| 코드 간소화 | Lombok | Spring Boot 관리 | @Getter, @Setter, @Builder 등 |
| Frontend Framework | Next.js | 16.x (latest) | App Router, TypeScript, Turbopack |
| CSS | Tailwind CSS | 4.x | 유틸리티 기반 스타일링 |
| API 통신 | REST (JSON) | - | fetch API, CORS 설정 |
| 빌드 도구 | Gradle (Kotlin DSL) | 9.3.x | backend 빌드 |
| 패키지 매니저 | npm | 10.x | frontend 의존성 관리 |

---

## Phase 1: Backend 기초 — 데이터 모델 + REST API ✅

> 목표: 리마인더 리스트와 리마인더의 CRUD API를 완성하고 curl로 검증한다.

### 1-1. 프로젝트 설정
- [x] `build.gradle.kts`에 `spring-boot-starter-web` 추가
- [x] `application.properties` — H2 파일 DB, JPA, H2 콘솔 설정

### 1-2. 엔티티 (Entity)
- [x] `Priority.java` — NONE, LOW, MEDIUM, HIGH enum
- [x] `ReminderList.java` — id, name, color, icon, @OneToMany reminders, timestamps
- [x] `Reminder.java` — id, title, memo, dueDate, dueTime, priority, isCompleted, completedAt, @ManyToOne list, timestamps

### 1-3. Repository
- [x] `ReminderListRepository` — JpaRepository 기본 CRUD
- [x] `ReminderRepository` — 스마트 필터 전용 쿼리 메서드
  - `findByListIdAndIsCompleted`, `findByDueDateAndIsCompleted` (오늘)
  - `findByDueDateIsNotNullAndIsCompleted` (예정), `findByIsCompleted` (전체/완료)

### 1-4. DTO
- [x] `ReminderListRequest` / `ReminderListResponse` (incompleteCount 포함)
- [x] `ReminderRequest` / `ReminderResponse` (listName, listColor 포함)

### 1-5. Service
- [x] `ReminderListService` — CRUD + 미완료 개수 계산
- [x] `ReminderService` — CRUD + toggle + 스마트 필터 쿼리

### 1-6. Controller
- [x] `ReminderListController` — `GET/POST/PUT/DELETE /api/lists`
- [x] `ReminderController` — `/api/lists/{listId}/reminders`, `/api/reminders/{id}`, `/api/reminders/today|scheduled|all|completed`

### 1-7. CORS
- [x] `WebConfig.java` — `http://localhost:3000` 허용

### 검증
```bash
./gradlew bootRun
curl -X POST http://localhost:8080/api/lists -H "Content-Type: application/json" -d '{"name":"test","color":"#007AFF","icon":"list.bullet"}'
curl http://localhost:8080/api/lists
```

---

## Phase 2: Frontend 기초 — 프로젝트 셋업 + 레이아웃 ✅

> 목표: Next.js 프로젝트를 생성하고, 사이드바 + 메인 영역의 2-패널 레이아웃을 잡는다.

### 2-1. 프로젝트 생성
- [x] `frontend/` 디렉토리에 Next.js 프로젝트 생성 (TypeScript, Tailwind, App Router)

### 2-2. 글로벌 스타일
- [x] `globals.css` — Apple 디자인 변수 (sidebar-bg `#F2F2F7`, separator `#E5E5EA` 등)
- [x] SF Pro 폰트 스택, 스크롤바 스타일링, 체크 애니메이션 keyframes

### 2-3. 타입 정의
- [x] `types/index.ts` — ReminderList, Reminder, Priority, SmartFilter, ViewState 타입

### 2-4. API 클라이언트
- [x] `lib/api.ts` — BASE_URL + typed fetch 래퍼 (lists CRUD, reminders CRUD, smart filters)

### 2-5. 레이아웃
- [x] `layout.tsx` — 한국어 lang, 메타 데이터
- [x] `page.tsx` — 상태 관리 허브 (lists, reminders, view, selectedReminder 등)

---

## Phase 3: Frontend 사이드바 — 스마트 필터 + 리스트 목록 ✅

> 목표: 왼쪽 사이드바에 스마트 필터 카드와 사용자 리스트를 표시한다.

### 3-1. SmartFilterCard
- [x] 2x2 그리드 카드 — 아이콘(컬러 원) + 카운트 + 라벨
- [x] 오늘(파랑 `#007AFF`), 예정(빨강 `#FF3B30`), 전체(검정), 완료(회색)

### 3-2. ListItem
- [x] 컬러 원형 아이콘 + 리스트 이름 + 미완료 카운트
- [x] 활성 상태 배경 + hover 효과

### 3-3. Sidebar
- [x] 상단: 스마트 필터 그리드
- [x] 중단: "나의 목록" 섹션 + 리스트 목록 (스크롤 가능)
- [x] 하단: 목록 추가 버튼

---

## Phase 4: Frontend 메인 영역 — 리마인더 CRUD ✅

> 목표: 리마인더 목록 표시, 생성, 완료 토글, 상세 편집을 구현한다.

### 4-1. ReminderItem
- [x] 원형 체크박스 (리스트 색상 테두리) + 체크 애니메이션 + 0.5초 후 fade-out
- [x] 우선순위 표시 (`!`/`!!`/`!!!`), 마감일 표시 (지나면 빨간색)
- [x] 메모 미리보기 (truncate)

### 4-2. ReminderPanel
- [x] 헤더 (리스트/필터 이름, 리스트 색상)
- [x] 미완료 리마인더 목록
- [x] 완료됨 섹션 (접기/펼치기)
- [x] 비어있을 때 "미리 알림 없음" 표시

### 4-3. AddReminderInput
- [x] 하단 고정, `+ 새로운 미리 알림` 클릭 → 인라인 입력
- [x] Enter로 빠른 생성, Escape로 취소

### 4-4. ReminderDetail
- [x] 우측 패널 — 제목, 메모, 날짜, 시간, 우선순위 편집
- [x] onBlur로 자동 저장
- [x] 미리 알림 삭제 버튼

---

## Phase 5: Frontend 리스트 관리 — 생성/수정 모달 ✅

> 목표: 리스트를 생성하고 편집할 수 있는 모달을 구현한다.

### 5-1. ListFormModal
- [x] 상단 미리보기 (컬러 원형 아이콘)
- [x] 이름 입력 필드
- [x] 12색 팔레트 그리드 (Red, Orange, Yellow, Green, Teal, Blue, Purple, Pink, Brown, Gray, Indigo, Cyan)
- [x] 12종 아이콘 선택 그리드
- [x] 취소/완료 버튼

### 5-2. 리스트 삭제
- [x] 우클릭 컨텍스트 → confirm 다이얼로그로 삭제

---

## Phase 6: UI 폴리싱 + 사용성 개선 ⬜

> 목표: Apple Reminders에 가까운 완성도를 만든다.

### 6-1. 마이크로 인터랙션
- [ ] 완료 체크박스 바운스 애니메이션 개선
- [ ] 리스트 전환 시 fade 트랜지션
- [ ] 모달 열기/닫기 애니메이션 (scale + opacity)
- [ ] 리마인더 항목 드래그 앤 드롭 순서 변경

### 6-2. 디자인 디테일
- [ ] 사이드바 리스트 항목 더블클릭 → 편집 모달 열기
- [ ] 커스텀 우클릭 컨텍스트 메뉴 (편집/삭제)
- [ ] 빈 상태 일러스트레이션
- [ ] 리마인더 생성 시 날짜/우선순위 빠른 설정 버튼

### 6-3. 키보드 단축키
- [ ] `Cmd/Ctrl + N` — 새 리마인더
- [ ] `Delete/Backspace` — 선택된 리마인더 삭제
- [ ] `↑↓` — 리마인더 목록 탐색
- [ ] `Escape` — 상세 패널 닫기

### 6-4. 반응형
- [ ] 모바일 뷰: 사이드바 토글 (햄버거 메뉴)
- [ ] 태블릿 뷰: 사이드바 축소 모드

---

## Phase 7: 고급 기능 ⬜

> 목표: MVP를 넘어 실사용에 가까운 기능을 추가한다.

### 7-1. 검색
- [ ] Backend: `GET /api/reminders/search?q={keyword}` 엔드포인트
- [ ] Frontend: 사이드바 상단 검색 바, 실시간 필터링

### 7-2. 리마인더 정렬
- [ ] 수동 순서 (drag & drop) — `sortOrder` 필드 추가
- [ ] 마감일순 / 우선순위순 / 생성일순 정렬 옵션

### 7-3. 하위 작업 (Subtasks)
- [ ] Backend: `Reminder` 자기 참조 관계 (`parentId`)
- [ ] Frontend: 들여쓰기된 하위 리마인더 표시

### 7-4. 태그
- [ ] Backend: `Tag` 엔티티 + M:N 관계
- [ ] Frontend: 리마인더에 태그 추가/제거, 태그별 필터링

### 7-5. 알림
- [ ] 브라우저 Notification API로 마감 시간 알림
- [ ] Backend: 예정된 알림 스케줄링 (Spring Scheduler)

---

## 실행 방법

```bash
# Backend (터미널 1)
cd chanwooreminder
./gradlew bootRun
# → http://localhost:8080 (API)
# → http://localhost:8080/h2-console (DB 콘솔)

# Frontend (터미널 2)
cd chanwooreminder/frontend
npm run dev
# → http://localhost:3000 (웹 UI)
```

---

## 디렉토리 구조

```
chanwooreminder/
├── build.gradle.kts
├── spec.md                          # 기능 명세서
├── plan.md                          # 이 문서 (개발 계획)
├── src/main/java/chanwoo/ai/chanwooreminder/
│   ├── ChanwooreminderApplication.java
│   ├── config/
│   │   └── WebConfig.java           # CORS 설정
│   ├── entity/
│   │   ├── Priority.java
│   │   ├── Reminder.java
│   │   └── ReminderList.java
│   ├── repository/
│   │   ├── ReminderRepository.java
│   │   └── ReminderListRepository.java
│   ├── dto/
│   │   ├── ReminderRequest.java
│   │   ├── ReminderResponse.java
│   │   ├── ReminderListRequest.java
│   │   └── ReminderListResponse.java
│   ├── service/
│   │   ├── ReminderService.java
│   │   └── ReminderListService.java
│   └── controller/
│       ├── ReminderController.java
│       └── ReminderListController.java
├── src/main/resources/
│   └── application.properties
└── frontend/
    ├── package.json
    └── src/
        ├── app/
        │   ├── layout.tsx
        │   ├── page.tsx              # 상태 관리 허브
        │   └── globals.css
        ├── components/
        │   ├── Sidebar.tsx
        │   ├── SmartFilterCard.tsx
        │   ├── ListItem.tsx
        │   ├── ListFormModal.tsx
        │   ├── ReminderPanel.tsx
        │   ├── ReminderItem.tsx
        │   ├── ReminderDetail.tsx
        │   └── AddReminderInput.tsx
        ├── lib/
        │   └── api.ts
        └── types/
            └── index.ts
```
