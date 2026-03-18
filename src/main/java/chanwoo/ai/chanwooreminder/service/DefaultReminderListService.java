package chanwoo.ai.chanwooreminder.service;

import chanwoo.ai.chanwooreminder.dto.ReminderListRequest;
import chanwoo.ai.chanwooreminder.dto.ReminderListResponse;
import chanwoo.ai.chanwooreminder.domain.ReminderList;
import chanwoo.ai.chanwooreminder.exception.ResourceNotFoundException;
import chanwoo.ai.chanwooreminder.service.ports.in.ReminderListService;
import chanwoo.ai.chanwooreminder.repository.ReminderListRepository;
import chanwoo.ai.chanwooreminder.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderListService implements ReminderListService {

    private final ReminderListRepository listRepository;
    private final ReminderRepository reminderRepository;

    @Override
    public List<ReminderListResponse> findAll() {
        return listRepository.findAll().stream()
                .map(list -> {
                    int count = (int) reminderRepository.countByListIdAndIsCompleted(list.getId(), false);
                    return ReminderListResponse.from(list, count);
                })
                .toList();
    }

    @Override
    public ReminderListResponse findById(Long id) {
        ReminderList list = getListOrThrow(id);
        int count = (int) reminderRepository.countByListIdAndIsCompleted(id, false);
        return ReminderListResponse.from(list, count);
    }

    @Override
    @Transactional
    public ReminderListResponse create(ReminderListRequest request) {
        ReminderList list = ReminderList.create(request.getName(), request.getColor(), request.getIcon());
        list = listRepository.save(list);
        return ReminderListResponse.from(list, 0);
    }

    @Override
    @Transactional
    public ReminderListResponse update(Long id, ReminderListRequest request) {
        ReminderList list = getListOrThrow(id);
        list.update(request.getName(), request.getColor(), request.getIcon());
        int count = (int) reminderRepository.countByListIdAndIsCompleted(id, false);
        return ReminderListResponse.from(list, count);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!listRepository.existsById(id)) {
            throw new ResourceNotFoundException("List", id);
        }
        listRepository.deleteById(id);
    }

    private ReminderList getListOrThrow(Long id) {
        return listRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("List", id));
    }
}
