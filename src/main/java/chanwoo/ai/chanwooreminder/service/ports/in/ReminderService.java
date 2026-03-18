package chanwoo.ai.chanwooreminder.service.ports.in;

import chanwoo.ai.chanwooreminder.dto.ReminderRequest;
import chanwoo.ai.chanwooreminder.dto.ReminderResponse;

import java.util.List;

public interface ReminderService {

    List<ReminderResponse> findByListId(Long listId);

    List<ReminderResponse> findByListIdAndCompleted(Long listId, boolean completed);

    ReminderResponse findById(Long id);

    List<ReminderResponse> findToday();

    List<ReminderResponse> findScheduled();

    List<ReminderResponse> findAll(boolean completed);

    ReminderResponse create(Long listId, ReminderRequest request);

    ReminderResponse update(Long id, ReminderRequest request);

    ReminderResponse toggleComplete(Long id);

    void delete(Long id);
}
