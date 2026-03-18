package chanwoo.ai.chanwooreminder.controller;

import chanwoo.ai.chanwooreminder.dto.ReminderResponse;
import chanwoo.ai.chanwooreminder.dto.TagRequest;
import chanwoo.ai.chanwooreminder.dto.TagResponse;
import chanwoo.ai.chanwooreminder.service.ports.in.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    public ResponseEntity<List<TagResponse>> getAll() {
        return ResponseEntity.ok(tagService.findAll());
    }

    @GetMapping("/tags/{id}")
    public ResponseEntity<TagResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.findById(id));
    }

    @PostMapping("/tags")
    public ResponseEntity<TagResponse> create(@RequestBody TagRequest request) {
        TagResponse created = tagService.create(request);
        return ResponseEntity
                .created(URI.create("/api/tags/" + created.getId()))
                .body(created);
    }

    @PutMapping("/tags/{id}")
    public ResponseEntity<TagResponse> update(@PathVariable Long id, @RequestBody TagRequest request) {
        return ResponseEntity.ok(tagService.update(id, request));
    }

    @DeleteMapping("/tags/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tags/{id}/reminders")
    public ResponseEntity<List<ReminderResponse>> getRemindersByTag(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.findRemindersByTag(id));
    }

    @PostMapping("/reminders/{reminderId}/tags/{tagId}")
    public ResponseEntity<ReminderResponse> addTag(@PathVariable Long reminderId, @PathVariable Long tagId) {
        return ResponseEntity.ok(tagService.addTagToReminder(reminderId, tagId));
    }

    @DeleteMapping("/reminders/{reminderId}/tags/{tagId}")
    public ResponseEntity<ReminderResponse> removeTag(@PathVariable Long reminderId, @PathVariable Long tagId) {
        return ResponseEntity.ok(tagService.removeTagFromReminder(reminderId, tagId));
    }
}
