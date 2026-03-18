package chanwoo.ai.chanwooreminder.service;

import chanwoo.ai.chanwooreminder.domain.Reminder;
import chanwoo.ai.chanwooreminder.domain.ReminderList;
import chanwoo.ai.chanwooreminder.dto.ReminderListRequest;
import chanwoo.ai.chanwooreminder.dto.ReminderListResponse;
import chanwoo.ai.chanwooreminder.exception.ResourceNotFoundException;
import chanwoo.ai.chanwooreminder.service.ports.in.ReminderListService;
import chanwoo.ai.chanwooreminder.repository.ReminderListRepository;
import chanwoo.ai.chanwooreminder.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class ReminderListServiceTest {

    @Autowired
    private ReminderListService service;

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
    @DisplayName("findAll - 전체 리스트 조회")
    class FindAll {

        @Test
        @DisplayName("모든 리스트를 미완료 카운트와 함께 반환한다")
        void returnsAllListsWithCount() {
            ReminderList list1 = saveTestList("목록1", "#007AFF");
            ReminderList list2 = saveTestList("목록2", "#FF3B30");
            saveTestReminder(list1, "할일1", false);
            saveTestReminder(list1, "할일2", false);
            saveTestReminder(list1, "완료된 일", true);
            saveTestReminder(list2, "할일3", false);

            List<ReminderListResponse> result = service.findAll();

            assertThat(result).hasSize(2);
            ReminderListResponse res1 = result.stream()
                    .filter(r -> r.getName().equals("목록1")).findFirst().orElseThrow();
            ReminderListResponse res2 = result.stream()
                    .filter(r -> r.getName().equals("목록2")).findFirst().orElseThrow();
            assertThat(res1.getIncompleteCount()).isEqualTo(2);
            assertThat(res2.getIncompleteCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("리스트가 없으면 빈 목록을 반환한다")
        void returnsEmptyWhenNoLists() {
            List<ReminderListResponse> result = service.findAll();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById - 단일 리스트 조회")
    class FindById {

        @Test
        @DisplayName("존재하는 리스트를 미완료 카운트와 함께 반환한다")
        void returnsListWithCount() {
            ReminderList list = saveTestList("장보기", "#007AFF");
            saveTestReminder(list, "우유", false);
            saveTestReminder(list, "빵", false);
            saveTestReminder(list, "계란", true);

            ReminderListResponse result = service.findById(list.getId());

            assertThat(result.getName()).isEqualTo("장보기");
            assertThat(result.getIncompleteCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("존재하지 않는 id로 조회하면 ResourceNotFoundException이 발생한다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> service.findById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("create - 리스트 생성")
    class Create {

        @Test
        @DisplayName("요청 값으로 리스트를 생성하고 저장한다")
        void createsAndSaves() {
            ReminderListRequest request = new ReminderListRequest("새 목록", "#FF3B30", "star");

            ReminderListResponse result = service.create(request);

            assertThat(result.getId()).isNotNull();
            assertThat(result.getName()).isEqualTo("새 목록");
            assertThat(result.getColor()).isEqualTo("#FF3B30");
            assertThat(result.getIcon()).isEqualTo("star");
            assertThat(result.getIncompleteCount()).isEqualTo(0);
            assertThat(listRepository.findById(result.getId())).isPresent();
        }

        @Test
        @DisplayName("색상과 아이콘이 null이면 기본값으로 생성된다")
        void usesDefaultsWhenNull() {
            ReminderListRequest request = new ReminderListRequest("기본 목록", null, null);

            ReminderListResponse result = service.create(request);

            assertThat(result.getColor()).isEqualTo("#007AFF");
            assertThat(result.getIcon()).isEqualTo("list.bullet");
        }
    }

    @Nested
    @DisplayName("update - 리스트 수정")
    class Update {

        @Test
        @DisplayName("존재하는 리스트의 이름과 색상을 수정한다")
        void updatesFields() {
            ReminderList list = saveTestList("원래 이름", "#007AFF");
            saveTestReminder(list, "할일", false);

            ReminderListRequest request = new ReminderListRequest("새 이름", "#34C759", null);
            ReminderListResponse result = service.update(list.getId(), request);

            assertThat(result.getName()).isEqualTo("새 이름");
            assertThat(result.getColor()).isEqualTo("#34C759");
            assertThat(result.getIcon()).isEqualTo("list.bullet");
            assertThat(result.getIncompleteCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("null인 필드는 기존 값을 유지한다")
        void nullFieldsKeepOriginal() {
            ReminderList list = saveTestList("원래 이름", "#FF9500");

            ReminderListRequest request = new ReminderListRequest(null, null, null);
            ReminderListResponse result = service.update(list.getId(), request);

            assertThat(result.getName()).isEqualTo("원래 이름");
            assertThat(result.getColor()).isEqualTo("#FF9500");
        }

        @Test
        @DisplayName("존재하지 않는 리스트를 수정하면 ResourceNotFoundException이 발생한다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> service.update(999L, new ReminderListRequest("x", null, null)))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete - 리스트 삭제")
    class Delete {

        @Test
        @DisplayName("존재하는 리스트를 삭제한다")
        void deletesExisting() {
            ReminderList list = saveTestList("삭제 대상", "#007AFF");
            Long id = list.getId();

            service.delete(id);

            assertThat(listRepository.findById(id)).isEmpty();
        }

        @Test
        @DisplayName("리스트 삭제 시 소속 리마인더도 함께 삭제된다")
        void deletesRemindersWithList() {
            ReminderList list = saveTestList("삭제 대상", "#007AFF");
            saveTestReminder(list, "할일1", false);
            saveTestReminder(list, "할일2", false);
            Long listId = list.getId();
            em.clear();

            service.delete(listId);
            em.flush();

            assertThat(listRepository.findById(listId)).isEmpty();
            assertThat(reminderRepository.findByListId(listId)).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 리스트를 삭제하면 ResourceNotFoundException이 발생한다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> service.delete(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }
}
