package chanwoo.ai.chanwooreminder.controller;

import chanwoo.ai.chanwooreminder.domain.Reminder;
import chanwoo.ai.chanwooreminder.domain.ReminderList;
import chanwoo.ai.chanwooreminder.domain.Tag;
import chanwoo.ai.chanwooreminder.repository.ReminderListRepository;
import chanwoo.ai.chanwooreminder.repository.ReminderRepository;
import chanwoo.ai.chanwooreminder.repository.TagRepository;
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
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TagRepository tagRepository;

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
        tagRepository.deleteAll();
        listRepository.deleteAll();
        em.flush();
        em.clear();

        defaultList = ReminderList.create("기본 목록", "#007AFF", "list.bullet");
        listRepository.save(defaultList);
        em.flush();
    }

    @Nested
    @DisplayName("Tag CRUD")
    class TagCrud {

        @Test
        @DisplayName("태그를 생성하고 조회한다")
        void createsAndGetsTags() throws Exception {
            mockMvc.perform(post("/api/tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("name", "업무", "color", "#FF3B30"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("업무"))
                    .andExpect(jsonPath("$.color").value("#FF3B30"));

            mockMvc.perform(get("/api/tags"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("태그를 수정한다")
        void updatesTag() throws Exception {
            Tag tag = Tag.create("업무", "#FF3B30");
            tagRepository.save(tag);
            em.flush();

            mockMvc.perform(put("/api/tags/{id}", tag.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("name", "개인", "color", "#007AFF"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("개인"))
                    .andExpect(jsonPath("$.color").value("#007AFF"));
        }

        @Test
        @DisplayName("태그를 삭제한다")
        void deletesTag() throws Exception {
            Tag tag = Tag.create("삭제 대상", "#FF3B30");
            tagRepository.save(tag);
            em.flush();

            mockMvc.perform(delete("/api/tags/{id}", tag.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/tags"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("Reminder-Tag 연결")
    class ReminderTagAssociation {

        @Test
        @DisplayName("리마인더에 태그를 추가하고 제거한다")
        void addsAndRemovesTagFromReminder() throws Exception {
            Reminder reminder = Reminder.create("할일", null, null, null, null, defaultList);
            reminderRepository.save(reminder);
            Tag tag = Tag.create("업무", "#FF3B30");
            tagRepository.save(tag);
            em.flush();

            // 태그 추가
            mockMvc.perform(post("/api/reminders/{reminderId}/tags/{tagId}", reminder.getId(), tag.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tags.length()").value(1))
                    .andExpect(jsonPath("$.tags[0].name").value("업무"));

            // 태그별 리마인더 조회
            mockMvc.perform(get("/api/tags/{id}/reminders", tag.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));

            // 태그 제거
            mockMvc.perform(delete("/api/reminders/{reminderId}/tags/{tagId}", reminder.getId(), tag.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tags.length()").value(0));
        }
    }
}
