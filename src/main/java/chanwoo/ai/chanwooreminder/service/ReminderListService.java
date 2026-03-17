package chanwoo.ai.chanwooreminder.service;

import chanwoo.ai.chanwooreminder.dto.ReminderListRequest;
import chanwoo.ai.chanwooreminder.dto.ReminderListResponse;
import chanwoo.ai.chanwooreminder.entity.ReminderList;
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
        ReminderList list = ReminderList.builder()
                .name(request.getName())
                .color(request.getColor() != null ? request.getColor() : "#007AFF")
                .icon(request.getIcon() != null ? request.getIcon() : "list.bullet")
                .build();
        list = listRepository.save(list);
        return ReminderListResponse.from(list, 0);
    }

    @Transactional
    public ReminderListResponse update(Long id, ReminderListRequest request) {
        ReminderList list = listRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("List not found: " + id));
        if (request.getName() != null) list.setName(request.getName());
        if (request.getColor() != null) list.setColor(request.getColor());
        if (request.getIcon() != null) list.setIcon(request.getIcon());
        list = listRepository.save(list);
        int count = reminderRepository.findByListIdAndIsCompleted(id, false).size();
        return ReminderListResponse.from(list, count);
    }

    @Transactional
    public void delete(Long id) {
        listRepository.deleteById(id);
    }
}
