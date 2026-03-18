package chanwoo.ai.chanwooreminder.service;

import chanwoo.ai.chanwooreminder.domain.Reminder;
import chanwoo.ai.chanwooreminder.domain.Tag;
import chanwoo.ai.chanwooreminder.dto.ReminderResponse;
import chanwoo.ai.chanwooreminder.dto.TagRequest;
import chanwoo.ai.chanwooreminder.dto.TagResponse;
import chanwoo.ai.chanwooreminder.exception.ResourceNotFoundException;
import chanwoo.ai.chanwooreminder.repository.ReminderRepository;
import chanwoo.ai.chanwooreminder.repository.TagRepository;
import chanwoo.ai.chanwooreminder.service.ports.in.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultTagService implements TagService {

    private final TagRepository tagRepository;
    private final ReminderRepository reminderRepository;

    @Override
    public List<TagResponse> findAll() {
        return tagRepository.findAll().stream()
                .map(TagResponse::from)
                .toList();
    }

    @Override
    public TagResponse findById(Long id) {
        return TagResponse.from(getTagOrThrow(id));
    }

    @Override
    @Transactional
    public TagResponse create(TagRequest request) {
        Tag tag = Tag.create(request.getName(), request.getColor());
        tag = tagRepository.save(tag);
        return TagResponse.from(tag);
    }

    @Override
    @Transactional
    public TagResponse update(Long id, TagRequest request) {
        Tag tag = getTagOrThrow(id);
        tag.update(request.getName(), request.getColor());
        tag = tagRepository.save(tag);
        return TagResponse.from(tag);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Tag tag = getTagOrThrow(id);
        for (Reminder r : tag.getReminders()) {
            r.removeTag(tag);
        }
        tagRepository.delete(tag);
    }

    @Override
    @Transactional
    public ReminderResponse addTagToReminder(Long reminderId, Long tagId) {
        Reminder reminder = getReminderOrThrow(reminderId);
        Tag tag = getTagOrThrow(tagId);
        reminder.addTag(tag);
        reminder = reminderRepository.save(reminder);
        return ReminderResponse.from(reminder);
    }

    @Override
    @Transactional
    public ReminderResponse removeTagFromReminder(Long reminderId, Long tagId) {
        Reminder reminder = getReminderOrThrow(reminderId);
        Tag tag = getTagOrThrow(tagId);
        reminder.removeTag(tag);
        reminder = reminderRepository.save(reminder);
        return ReminderResponse.from(reminder);
    }

    @Override
    public List<ReminderResponse> findRemindersByTag(Long tagId) {
        getTagOrThrow(tagId);
        return reminderRepository.findByTagsId(tagId).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    private Tag getTagOrThrow(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
    }

    private Reminder getReminderOrThrow(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", id));
    }
}
