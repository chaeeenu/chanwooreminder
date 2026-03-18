package chanwoo.ai.chanwooreminder.service.ports.in;

import chanwoo.ai.chanwooreminder.dto.TagRequest;
import chanwoo.ai.chanwooreminder.dto.TagResponse;
import chanwoo.ai.chanwooreminder.dto.ReminderResponse;

import java.util.List;

public interface TagService {

    List<TagResponse> findAll();

    TagResponse findById(Long id);

    TagResponse create(TagRequest request);

    TagResponse update(Long id, TagRequest request);

    void delete(Long id);

    ReminderResponse addTagToReminder(Long reminderId, Long tagId);

    ReminderResponse removeTagFromReminder(Long reminderId, Long tagId);

    List<ReminderResponse> findRemindersByTag(Long tagId);
}
