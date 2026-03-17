# Apple Reminders Web Clone - PRD (MVP)

## 1. 프로젝트 개요

Apple Reminder 앱의 핵심 기능을 웹으로 구현하는 MVP 프로젝트.
단일 사용자 환경으로 인증 없이 바로 사용 가능하다.

### 기술 스택

| 영역 | 기술 |
|------|------|
| Backend | Spring Boot 4.0.3, Spring Data JPA, H2 Database |
| Frontend | Next.js (latest), TypeScript, Tailwind CSS |
| API 통신 | REST API (JSON) |

---

## 2. 핵심 기능

### 2.1 리마인더 리스트 관리
- 리스트 생성 (이름, 색상, 아이콘)
- 리스트 수정 / 삭제
- 리스트 목록 조회 (각 리스트별 미완료 리마인더 개수 표시)

### 2.2 리마인더 관리
- 리마인더 생성 (제목, 메모, 마감일, 우선순위, 소속 리스트)
- 리마인더 수정 / 삭제
- 완료/미완료 토글
- 리스트별 리마인더 조회

### 2.3 스마트 필터 (읽기 전용, 가상 리스트)
- **오늘**: 마감일이 오늘인 리마인더
- **예정**: 마감일이 설정된 모든 미완료 리마인더
- **전체**: 모든 미완료 리마인더
- **완료됨**: 완료 처리된 리마인더

---

## 3. 데이터 모델

### 3.1 ReminderList

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long (PK) | 자동 생성 |
| name | String | 리스트 이름 (필수) |
| color | String | HEX 색상 코드 (기본값: `#007AFF`) |
| icon | String | 아이콘 식별자 (기본값: `list.bullet`) |
| createdAt | LocalDateTime | 생성 시각 |
| updatedAt | LocalDateTime | 수정 시각 |

### 3.2 Reminder

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long (PK) | 자동 생성 |
| title | String | 제목 (필수) |
| memo | String | 메모 (선택) |
| dueDate | LocalDate | 마감일 (선택) |
| dueTime | LocalTime | 마감 시간 (선택) |
| priority | Enum | `NONE`, `LOW`, `MEDIUM`, `HIGH` |
| isCompleted | Boolean | 완료 여부 (기본값: false) |
| completedAt | LocalDateTime | 완료 시각 |
| listId | Long (FK) | 소속 리스트 |
| createdAt | LocalDateTime | 생성 시각 |
| updatedAt | LocalDateTime | 수정 시각 |

---

## 4. REST API 설계

### 4.1 리마인더 리스트 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/lists` | 전체 리스트 조회 (미완료 개수 포함) |
| POST | `/api/lists` | 리스트 생성 |
| PUT | `/api/lists/{id}` | 리스트 수정 |
| DELETE | `/api/lists/{id}` | 리스트 삭제 (소속 리마인더도 삭제) |

### 4.2 리마인더 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/lists/{listId}/reminders` | 리스트별 리마인더 조회 |
| POST | `/api/lists/{listId}/reminders` | 리마인더 생성 |
| PUT | `/api/reminders/{id}` | 리마인더 수정 |
| PATCH | `/api/reminders/{id}/toggle` | 완료/미완료 토글 |
| DELETE | `/api/reminders/{id}` | 리마인더 삭제 |

### 4.3 스마트 필터 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/reminders/today` | 오늘 마감 리마인더 |
| GET | `/api/reminders/scheduled` | 마감일 있는 미완료 리마인더 |
| GET | `/api/reminders/all` | 전체 미완료 리마인더 |
| GET | `/api/reminders/completed` | 완료된 리마인더 |

### 4.4 요청/응답 예시

**POST /api/lists**
```json
{
  "name": "장보기",
  "color": "#FF3B30",
  "icon": "cart"
}
```

**POST /api/lists/{listId}/reminders**
```json
{
  "title": "우유 사기",
  "memo": "저지방으로",
  "dueDate": "2026-03-20",
  "dueTime": "18:00",
  "priority": "MEDIUM"
}
```

**GET /api/lists 응답**
```json
[
  {
    "id": 1,
    "name": "장보기",
    "color": "#FF3B30",
    "icon": "cart",
    "incompleteCount": 3
  }
]
```

---

## 5. 프론트엔드 구조

### 5.1 레이아웃

Apple Reminders 스타일의 2-패널 레이아웃:
- **왼쪽 사이드바**: 스마트 필터 카드들 + 나의 리스트 목록
- **오른쪽 메인 영역**: 선택된 리스트/필터의 리마인더 목록

### 5.2 페이지

| 경로 | 설명 |
|------|------|
| `/` | 메인 페이지 (사이드바 + 리마인더 목록) |

SPA 방식으로 단일 페이지에서 사이드바 선택에 따라 메인 영역이 변경된다.

### 5.3 주요 컴포넌트

```
app/
├── layout.tsx
├── page.tsx                  # 메인 페이지
├── components/
│   ├── Sidebar.tsx           # 왼쪽 사이드바
│   ├── SmartFilterCard.tsx   # 스마트 필터 카드 (오늘, 예정 등)
│   ├── ListItem.tsx          # 리스트 항목
│   ├── ListFormModal.tsx     # 리스트 생성/수정 모달
│   ├── ReminderPanel.tsx     # 오른쪽 리마인더 목록 영역
│   ├── ReminderItem.tsx      # 리마인더 한 줄 항목
│   ├── ReminderDetail.tsx    # 리마인더 상세/편집 패널
│   └── AddReminderInput.tsx  # 리마인더 추가 인풋
```

### 5.4 UI/UX 가이드라인

- Apple Reminders의 깔끔한 디자인 참고
- 리스트 색상이 아이콘, 제목, 체크박스에 반영
- 완료 시 체크 애니메이션 + 잠시 후 목록에서 사라짐
- 우선순위는 `!`, `!!`, `!!!`로 표시 (LOW, MEDIUM, HIGH)
- 마감일이 지난 리마인더는 빨간색으로 표시

---

## 6. 개발 단계

### Phase 1: Backend API
1. Spring Boot 프로젝트 설정 (H2, JPA)
2. 엔티티 및 리포지토리 구현
3. REST API 컨트롤러 구현
4. API 테스트

### Phase 2: Frontend
1. Next.js 프로젝트 설정 (Tailwind CSS)
2. 사이드바 + 리스트 관리 구현
3. 리마인더 목록 + CRUD 구현
4. 스마트 필터 구현
5. UI 폴리싱 (애니메이션, 반응형)
