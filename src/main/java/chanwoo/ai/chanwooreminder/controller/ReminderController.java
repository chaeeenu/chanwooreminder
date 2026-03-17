package chanwoo.ai.chanwooreminder.controller;

import chanwoo.ai.chanwooreminder.dto.ReminderRequest;
import chanwoo.ai.chanwooreminder.dto.ReminderResponse;
import chanwoo.ai.chanwooreminder.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping("/lists/{listId}/reminders")
    public List<ReminderResponse> getByList(
            @PathVariable Long listId,
            @RequestParam(required = false) Boolean completed) {
        if (completed != null) {
            return reminderService.findByListIdAndCompleted(listId, completed);
        }
        return reminderService.findByListId(listId);
    }

    @PostMapping("/lists/{listId}/reminders")
    public ReminderResponse create(@PathVariable Long listId, @RequestBody ReminderRequest request) {
        return reminderService.create(listId, request);
    }

    @GetMapping("/reminders/{id}")
    public ReminderResponse getById(@PathVariable Long id) {
        return reminderService.findById(id);
    }

    @PutMapping("/reminders/{id}")
    public ReminderResponse update(@PathVariable Long id, @RequestBody ReminderRequest request) {
        return reminderService.update(id, request);
    }

    @PatchMapping("/reminders/{id}/toggle")
    public ReminderResponse toggle(@PathVariable Long id) {
        return reminderService.toggleComplete(id);
    }

    @DeleteMapping("/reminders/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reminderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reminders/today")
    public List<ReminderResponse> getToday() {
        return reminderService.findToday();
    }

    @GetMapping("/reminders/scheduled")
    public List<ReminderResponse> getScheduled() {
        return reminderService.findScheduled();
    }

    @GetMapping("/reminders/all")
    public List<ReminderResponse> getAll() {
        return reminderService.findAll(false);
    }

    @GetMapping("/reminders/completed")
    public List<ReminderResponse> getCompleted() {
        return reminderService.findAll(true);
    }
}
