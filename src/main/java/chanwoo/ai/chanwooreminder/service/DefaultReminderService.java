package chanwoo.ai.chanwooreminder.service;

import chanwoo.ai.chanwooreminder.dto.ReminderRequest;
import chanwoo.ai.chanwooreminder.dto.ReminderResponse;
import chanwoo.ai.chanwooreminder.domain.Reminder;
import chanwoo.ai.chanwooreminder.domain.ReminderList;
import chanwoo.ai.chanwooreminder.exception.ResourceNotFoundException;
import chanwoo.ai.chanwooreminder.service.ports.in.ReminderService;
import chanwoo.ai.chanwooreminder.repository.ReminderListRepository;
import chanwoo.ai.chanwooreminder.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderService implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final ReminderListRepository listRepository;

    @Override
    public List<ReminderResponse> findByListId(Long listId) {
        return reminderRepository.findByListId(listId).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    @Override
    public List<ReminderResponse> findByListIdAndCompleted(Long listId, boolean completed) {
        return reminderRepository.findByListIdAndIsCompleted(listId, completed).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    @Override
    public ReminderResponse findById(Long id) {
        Reminder r = getReminderOrThrow(id);
        return ReminderResponse.from(r);
    }

    @Override
    public List<ReminderResponse> findToday() {
        return reminderRepository.findByDueDateAndIsCompleted(LocalDate.now(), false).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    @Override
    public List<ReminderResponse> findScheduled() {
        return reminderRepository.findByDueDateIsNotNullAndIsCompleted(false).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    @Override
    public List<ReminderResponse> findAll(boolean completed) {
        return reminderRepository.findByIsCompleted(completed).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public ReminderResponse create(Long listId, ReminderRequest request) {
        ReminderList list = getListOrThrow(listId);
        Reminder r = Reminder.create(
                request.getTitle(), request.getMemo(),
                request.getDueDate(), request.getDueTime(),
                request.getPriority(), list);
        r = reminderRepository.save(r);
        return ReminderResponse.from(r);
    }

    @Override
    @Transactional
    public ReminderResponse update(Long id, ReminderRequest request) {
        Reminder r = getReminderOrThrow(id);
        r.update(request.getTitle(), request.getMemo(),
                request.getDueDate(), request.getDueTime(), request.getPriority());
        if (request.getIsCompleted() != null) {
            r.markCompleted(request.getIsCompleted());
        }
        if (request.getListId() != null && !request.getListId().equals(r.getList().getId())) {
            ReminderList newList = getListOrThrow(request.getListId());
            r.moveToList(newList);
        }
        r = reminderRepository.save(r);
        return ReminderResponse.from(r);
    }

    @Override
    @Transactional
    public ReminderResponse toggleComplete(Long id) {
        Reminder r = getReminderOrThrow(id);
        r.toggleComplete();
        r = reminderRepository.save(r);
        return ReminderResponse.from(r);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!reminderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reminder", id);
        }
        reminderRepository.deleteById(id);
    }

    private Reminder getReminderOrThrow(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", id));
    }

    private ReminderList getListOrThrow(Long listId) {
        return listRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("List", listId));
    }
}
