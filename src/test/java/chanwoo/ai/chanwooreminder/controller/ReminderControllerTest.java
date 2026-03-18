package chanwoo.ai.chanwooreminder.controller;

import chanwoo.ai.chanwooreminder.domain.Priority;
import chanwoo.ai.chanwooreminder.domain.Reminder;
import chanwoo.ai.chanwooreminder.domain.ReminderList;
import chanwoo.ai.chanwooreminder.repository.ReminderListRepository;
import chanwoo.ai.chanwooreminder.repository.ReminderRepository;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class ReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReminderListRepository listRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private EntityManager em;

    private ReminderList defaultList;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        listRepository.deleteAll();
        em.flush();
        em.clear();

        defaultList = ReminderList.create("기본 목록", "#007AFF", "list.bullet");
        listRepository.save(defaultList);
        em.flush();
    }

    private Reminder saveTestReminder(ReminderList list, String title, boolean completed) {
        Reminder reminder = Reminder.create(title, null, null, null, null, list);
        if (completed) {
            reminder.markCompleted(true);
        }
        reminderRepository.save(reminder);
        em.flush();
        return reminder;
    }

    private Reminder saveTestReminderWithDueDate(ReminderList list, String title, LocalDate dueDate, boolean completed) {
        Reminder reminder = Reminder.create(title, null, dueDate, null, null, list);
        if (completed) {
            reminder.markCompleted(true);
        }
        reminderRepository.save(reminder);
        em.flush();
        return reminder;
    }

    @Nested
    @DisplayName("GetByList - GET /api/lists/{listId}/reminders")
    class GetByList {

        @Test
        @DisplayName("리스트별 전체 리마인더를 반환한다")
        void returnsAllRemindersForList() throws Exception {
            saveTestReminder(defaultList, "할일1", false);
            saveTestReminder(defaultList, "할일2", true);

            mockMvc.perform(get("/api/lists/{listId}/reminders", defaultList.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("completed 파라미터로 필터링한다")
        void filtersWithCompletedParam() throws Exception {
            saveTestReminder(defaultList, "미완료", false);
            saveTestReminder(defaultList, "완료", true);

            mockMvc.perform(get("/api/lists/{listId}/reminders", defaultList.getId())
                            .param("completed", "false"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].title").value("미완료"));
        }

        @Test
        @DisplayName("존재하지 않는 리스트 id는 빈 배열 또는 정상 처리")
        void handlesNonExistentList() throws Exception {
            mockMvc.perform(get("/api/lists/{listId}/reminders", 999L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("Create - POST /api/lists/{listId}/reminders")
    class Create {

        @Test
        @DisplayName("유효한 요청으로 리마인더를 생성한다")
        void createsReminderWithLocationHeader() throws Exception {
            Map<String, Object> request = Map.of("title", "새 리마인더", "memo", "메모 내용");

            mockMvc.perform(post("/api/lists/{listId}/reminders", defaultList.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.title").value("새 리마인더"))
                    .andExpect(jsonPath("$.memo").value("메모 내용"))
                    .andExpect(jsonPath("$.isCompleted").value(false));
        }

        @Test
        @DisplayName("존재하지 않는 리스트에 생성 시 404 반환")
        void returns404WhenListNotFound() throws Exception {
            Map<String, String> request = Map.of("title", "리마인더");

            mockMvc.perform(post("/api/lists/{listId}/reminders", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GetById - GET /api/reminders/{id}")
    class GetById {

        @Test
        @DisplayName("존재하는 리마인더를 반환한다")
        void returnsExistingReminder() throws Exception {
            Reminder reminder = saveTestReminder(defaultList, "할일", false);

            mockMvc.perform(get("/api/reminders/{id}", reminder.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("할일"))
                    .andExpect(jsonPath("$.isCompleted").value(false))
                    .andExpect(jsonPath("$.listId").value(defaultList.getId()));
        }

        @Test
        @DisplayName("존재하지 않는 id는 404 반환")
        void returns404WhenNotFound() throws Exception {
            mockMvc.perform(get("/api/reminders/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Update - PUT /api/reminders/{id}")
    class Update {

        @Test
        @DisplayName("리마인더 필드를 수정한다")
        void updatesReminderFields() throws Exception {
            Reminder reminder = saveTestReminder(defaultList, "원래 제목", false);
            Map<String, Object> request = Map.of("title", "수정된 제목", "priority", "HIGH");

            mockMvc.perform(put("/api/reminders/{id}", reminder.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("수정된 제목"))
                    .andExpect(jsonPath("$.priority").value("HIGH"));
        }

        @Test
        @DisplayName("존재하지 않는 id 수정 시 404 반환")
        void returns404WhenNotFound() throws Exception {
            Map<String, String> request = Map.of("title", "수정");

            mockMvc.perform(put("/api/reminders/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Toggle - PATCH /api/reminders/{id}/toggle")
    class Toggle {

        @Test
        @DisplayName("미완료 → 완료 토글")
        void togglesToCompleted() throws Exception {
            Reminder reminder = saveTestReminder(defaultList, "미완료 할일", false);

            mockMvc.perform(patch("/api/reminders/{id}/toggle", reminder.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isCompleted").value(true))
                    .andExpect(jsonPath("$.completedAt").isNotEmpty());
        }

        @Test
        @DisplayName("완료 → 미완료 토글")
        void togglesToIncomplete() throws Exception {
            Reminder reminder = saveTestReminder(defaultList, "완료된 할일", true);

            mockMvc.perform(patch("/api/reminders/{id}/toggle", reminder.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isCompleted").value(false))
                    .andExpect(jsonPath("$.completedAt").isEmpty());
        }
    }

    @Nested
    @DisplayName("Delete - DELETE /api/reminders/{id}")
    class Delete {

        @Test
        @DisplayName("존재하는 리마인더를 삭제한다")
        void deletesExistingReminder() throws Exception {
            Reminder reminder = saveTestReminder(defaultList, "삭제 대상", false);

            mockMvc.perform(delete("/api/reminders/{id}", reminder.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 id 삭제 시 404 반환")
        void returns404WhenNotFound() throws Exception {
            mockMvc.perform(delete("/api/reminders/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("SmartFilters - 스마트 필터 API")
    class SmartFilters {

        @Test
        @DisplayName("GET /api/reminders/today — 오늘 마감 리마인더만 반환")
        void returnsTodayReminders() throws Exception {
            saveTestReminderWithDueDate(defaultList, "오늘 할일", LocalDate.now(), false);
            saveTestReminderWithDueDate(defaultList, "내일 할일", LocalDate.now().plusDays(1), false);
            saveTestReminder(defaultList, "마감일 없는 할일", false);

            mockMvc.perform(get("/api/reminders/today"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].title").value("오늘 할일"));
        }

        @Test
        @DisplayName("GET /api/reminders/scheduled — 마감일 있는 미완료 리마인더 반환")
        void returnsScheduledReminders() throws Exception {
            saveTestReminderWithDueDate(defaultList, "예정된 할일", LocalDate.now().plusDays(3), false);
            saveTestReminderWithDueDate(defaultList, "완료된 예정 할일", LocalDate.now().plusDays(1), true);
            saveTestReminder(defaultList, "마감일 없는 할일", false);

            mockMvc.perform(get("/api/reminders/scheduled"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].title").value("예정된 할일"));
        }

        @Test
        @DisplayName("GET /api/reminders/all — 전체 미완료 리마인더 반환")
        void returnsAllIncompleteReminders() throws Exception {
            saveTestReminder(defaultList, "미완료1", false);
            saveTestReminder(defaultList, "미완료2", false);
            saveTestReminder(defaultList, "완료됨", true);

            mockMvc.perform(get("/api/reminders/all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("GET /api/reminders/completed — 완료된 리마인더 반환")
        void returnsCompletedReminders() throws Exception {
            saveTestReminder(defaultList, "미완료", false);
            saveTestReminder(defaultList, "완료1", true);
            saveTestReminder(defaultList, "완료2", true);

            mockMvc.perform(get("/api/reminders/completed"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }
}
