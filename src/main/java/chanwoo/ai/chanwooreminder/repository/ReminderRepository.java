package chanwoo.ai.chanwooreminder.repository;

import chanwoo.ai.chanwooreminder.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByListIdAndIsCompleted(Long listId, boolean isCompleted);

    List<Reminder> findByListId(Long listId);

    List<Reminder> findByDueDateAndIsCompleted(LocalDate date, boolean isCompleted);

    List<Reminder> findByDueDateIsNotNullAndIsCompleted(boolean isCompleted);

    List<Reminder> findByIsCompleted(boolean isCompleted);
}
