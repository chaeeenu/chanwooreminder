package chanwoo.ai.chanwooreminder.service;

import chanwoo.ai.chanwooreminder.dto.ReminderListRequest;
import chanwoo.ai.chanwooreminder.dto.ReminderListResponse;
import chanwoo.ai.chanwooreminder.domain.ReminderList;
import chanwoo.ai.chanwooreminder.repository.ReminderListRepository;
import chanwoo.ai.chanwooreminder.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReminderListService {

    private final ReminderListRepository listRepository;
    private final ReminderRepository reminderRepository;

    public List<ReminderListResponse> findAll() {
        return listRepository.findAll().stream()
                .map(list -> {
                    int count = reminderRepository.findByListIdAndIsCompleted(list.getId(), false).size();
                    return ReminderListResponse.from(list, count);
                })
                .toList();
    }

    public ReminderListResponse findById(Long id) {
        ReminderList list = listRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("List not found: " + id));
        int count = reminderRepository.findByListIdAndIsCompleted(id, false).size();
        return ReminderListResponse.from(list, count);
    }

    @Transactional
    public ReminderListResponse create(ReminderListRequest request) {
        ReminderList list = ReminderList.create(request.getName(), request.getColor(), request.getIcon());
        list = listRepository.save(list);
        return ReminderListResponse.from(list, 0);
    }

    @Transactional
    public ReminderListResponse update(Long id, ReminderListRequest request) {
        ReminderList list = listRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("List not found: " + id));
        list.update(request.getName(), request.getColor(), request.getIcon());
        list = listRepository.save(list);
        int count = reminderRepository.findByListIdAndIsCompleted(id, false).size();
        return ReminderListResponse.from(list, count);
    }

    @Transactional
    public void delete(Long id) {
        listRepository.deleteById(id);
    }
}
