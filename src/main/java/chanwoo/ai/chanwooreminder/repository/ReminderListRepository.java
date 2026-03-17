package chanwoo.ai.chanwooreminder.repository;

import chanwoo.ai.chanwooreminder.entity.ReminderList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderListRepository extends JpaRepository<ReminderList, Long> {
}
