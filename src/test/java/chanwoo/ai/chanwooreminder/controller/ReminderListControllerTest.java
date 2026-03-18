package chanwoo.ai.chanwooreminder.controller;

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
class ReminderListControllerTest {

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

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        listRepository.deleteAll();
        em.flush();
        em.clear();
    }

    private ReminderList saveTestList(String name, String color) {
        ReminderList list = ReminderList.create(name, color, "list.bullet");
        listRepository.save(list);
        em.flush();
        return list;
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

    @Nested
    @DisplayName("GetAll - GET /api/lists")
    class GetAll {

        @Test
        @DisplayName("모든 리스트를 미완료 카운트와 함께 반환한다")
        void returnsAllListsWithIncompleteCount() throws Exception {
            ReminderList list1 = saveTestList("목록1", "#007AFF");
            ReminderList list2 = saveTestList("목록2", "#FF3B30");
            saveTestReminder(list1, "할일1", false);
            saveTestReminder(list1, "할일2", false);
            saveTestReminder(list1, "완료된 일", true);
            saveTestReminder(list2, "할일3", false);

            mockMvc.perform(get("/api/lists"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[?(@.name == '목록1')].incompleteCount").value(2))
                    .andExpect(jsonPath("$[?(@.name == '목록2')].incompleteCount").value(1));
        }

        @Test
        @DisplayName("리스트가 없으면 빈 배열을 반환한다")
        void returnsEmptyArrayWhenNoLists() throws Exception {
            mockMvc.perform(get("/api/lists"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GetById - GET /api/lists/{id}")
    class GetById {

        @Test
        @DisplayName("존재하는 리스트를 반환한다")
        void returnsExistingList() throws Exception {
            ReminderList list = saveTestList("장보기", "#007AFF");
            saveTestReminder(list, "우유", false);
            saveTestReminder(list, "빵", true);

            mockMvc.perform(get("/api/lists/{id}", list.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("장보기"))
                    .andExpect(jsonPath("$.color").value("#007AFF"))
                    .andExpect(jsonPath("$.incompleteCount").value(1));
        }

        @Test
        @DisplayName("존재하지 않는 id는 404를 반환한다")
        void returns404WhenNotFound() throws Exception {
            mockMvc.perform(get("/api/lists/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Create - POST /api/lists")
    class Create {

        @Test
        @DisplayName("유효한 요청으로 리스트를 생성한다")
        void createsListWithLocationHeader() throws Exception {
            Map<String, String> request = Map.of("name", "새 목록", "color", "#FF3B30", "icon", "star");

            mockMvc.perform(post("/api/lists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.name").value("새 목록"))
                    .andExpect(jsonPath("$.color").value("#FF3B30"))
                    .andExpect(jsonPath("$.icon").value("star"))
                    .andExpect(jsonPath("$.incompleteCount").value(0));
        }

        @Test
        @DisplayName("기본값 적용 확인 (color=#007AFF, icon=list.bullet)")
        void appliesDefaultValues() throws Exception {
            Map<String, String> request = Map.of("name", "기본 목록");

            mockMvc.perform(post("/api/lists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.color").value("#007AFF"))
                    .andExpect(jsonPath("$.icon").value("list.bullet"));
        }
    }

    @Nested
    @DisplayName("Update - PUT /api/lists/{id}")
    class Update {

        @Test
        @DisplayName("존재하는 리스트의 필드를 수정한다")
        void updatesExistingList() throws Exception {
            ReminderList list = saveTestList("원래 이름", "#007AFF");
            Map<String, String> request = Map.of("name", "새 이름", "color", "#34C759");

            mockMvc.perform(put("/api/lists/{id}", list.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("새 이름"))
                    .andExpect(jsonPath("$.color").value("#34C759"));
        }

        @Test
        @DisplayName("존재하지 않는 id 수정 시 404 반환")
        void returns404WhenNotFound() throws Exception {
            Map<String, String> request = Map.of("name", "수정");

            mockMvc.perform(put("/api/lists/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Delete - DELETE /api/lists/{id}")
    class Delete {

        @Test
        @DisplayName("존재하는 리스트를 삭제한다")
        void deletesExistingList() throws Exception {
            ReminderList list = saveTestList("삭제 대상", "#007AFF");

            mockMvc.perform(delete("/api/lists/{id}", list.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 id 삭제 시 404 반환")
        void returns404WhenNotFound() throws Exception {
            mockMvc.perform(delete("/api/lists/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }
}
