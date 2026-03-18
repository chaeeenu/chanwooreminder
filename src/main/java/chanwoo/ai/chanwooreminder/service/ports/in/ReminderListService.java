package chanwoo.ai.chanwooreminder.service.ports.in;

import chanwoo.ai.chanwooreminder.dto.ReminderListRequest;
import chanwoo.ai.chanwooreminder.dto.ReminderListResponse;

import java.util.List;

public interface ReminderListService {

    List<ReminderListResponse> findAll();

    ReminderListResponse findById(Long id);

    ReminderListResponse create(ReminderListRequest request);

    ReminderListResponse update(Long id, ReminderListRequest request);

    void delete(Long id);
}
