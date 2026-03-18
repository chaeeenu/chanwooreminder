package chanwoo.ai.chanwooreminder.repository;

import chanwoo.ai.chanwooreminder.domain.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByListIdAndIsCompleted(Long listId, boolean isCompleted);

    long countByListIdAndIsCompleted(Long listId, boolean isCompleted);

    List<Reminder> findByListId(Long listId);

    List<Reminder> findByDueDateAndIsCompleted(LocalDate date, boolean isCompleted);

    List<Reminder> findByDueDateIsNotNullAndIsCompleted(boolean isCompleted);

    List<Reminder> findByIsCompleted(boolean isCompleted);

    List<Reminder> findByTitleContainingIgnoreCase(String keyword);

    List<Reminder> findByParentId(Long parentId);

    List<Reminder> findByListIdAndParentIsNull(Long listId);

    List<Reminder> findByListIdAndIsCompletedAndParentIsNull(Long listId, boolean isCompleted);

    List<Reminder> findByTagsId(Long tagId);
}
