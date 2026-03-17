package chanwoo.ai.chanwooreminder.service;

import chanwoo.ai.chanwooreminder.dto.ReminderRequest;
import chanwoo.ai.chanwooreminder.dto.ReminderResponse;
import chanwoo.ai.chanwooreminder.entity.Priority;
import chanwoo.ai.chanwooreminder.entity.Reminder;
import chanwoo.ai.chanwooreminder.entity.ReminderList;
import chanwoo.ai.chanwooreminder.repository.ReminderListRepository;
import chanwoo.ai.chanwooreminder.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final ReminderListRepository listRepository;

    public List<ReminderResponse> findByListId(Long listId) {
        return reminderRepository.findByListId(listId).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    public List<ReminderResponse> findByListIdAndCompleted(Long listId, boolean completed) {
        return reminderRepository.findByListIdAndIsCompleted(listId, completed).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    public ReminderResponse findById(Long id) {
        Reminder r = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found: " + id));
        return ReminderResponse.from(r);
    }

    public List<ReminderResponse> findToday() {
        return reminderRepository.findByDueDateAndIsCompleted(LocalDate.now(), false).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    public List<ReminderResponse> findScheduled() {
        return reminderRepository.findByDueDateIsNotNullAndIsCompleted(false).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    public List<ReminderResponse> findAll(boolean completed) {
        return reminderRepository.findByIsCompleted(completed).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    @Transactional
    public ReminderResponse create(Long listId, ReminderRequest request) {
        ReminderList list = listRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found: " + listId));
        Reminder r = Reminder.builder()
                .title(request.getTitle())
                .memo(request.getMemo())
                .dueDate(request.getDueDate())
                .dueTime(request.getDueTime())
                .priority(request.getPriority() != null ? request.getPriority() : Priority.NONE)
                .list(list)
                .build();
        r = reminderRepository.save(r);
        return ReminderResponse.from(r);
    }

    @Transactional
    public ReminderResponse update(Long id, ReminderRequest request) {
        Reminder r = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found: " + id));
        if (request.getTitle() != null) r.setTitle(request.getTitle());
        if (request.getMemo() != null) r.setMemo(request.getMemo());
        if (request.getDueDate() != null) r.setDueDate(request.getDueDate());
        if (request.getDueTime() != null) r.setDueTime(request.getDueTime());
        if (request.getPriority() != null) r.setPriority(request.getPriority());
        if (request.getIsCompleted() != null) {
            r.setIsCompleted(request.getIsCompleted());
            r.setCompletedAt(request.getIsCompleted() ? LocalDateTime.now() : null);
        }
        if (request.getListId() != null && !request.getListId().equals(r.getList().getId())) {
            ReminderList newList = listRepository.findById(request.getListId())
                    .orElseThrow(() -> new RuntimeException("List not found: " + request.getListId()));
            r.setList(newList);
        }
        r = reminderRepository.save(r);
        return ReminderResponse.from(r);
    }

    @Transactional
    public ReminderResponse toggleComplete(Long id) {
        Reminder r = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found: " + id));
        r.setIsCompleted(!r.getIsCompleted());
        r.setCompletedAt(r.getIsCompleted() ? LocalDateTime.now() : null);
        r = reminderRepository.save(r);
        return ReminderResponse.from(r);
    }

    @Transactional
    public void delete(Long id) {
        reminderRepository.deleteById(id);
    }
}
