package chanwoo.ai.chanwooreminder.controller;

import chanwoo.ai.chanwooreminder.dto.ReminderRequest;
import chanwoo.ai.chanwooreminder.dto.ReminderResponse;
import chanwoo.ai.chanwooreminder.service.ports.in.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping("/lists/{listId}/reminders")
    public ResponseEntity<List<ReminderResponse>> getByList(
            @PathVariable Long listId,
            @RequestParam(required = false) Boolean completed) {
        if (completed != null) {
            return ResponseEntity.ok(reminderService.findByListIdAndCompleted(listId, completed));
        }
        return ResponseEntity.ok(reminderService.findByListId(listId));
    }

    @PostMapping("/lists/{listId}/reminders")
    public ResponseEntity<ReminderResponse> create(@PathVariable Long listId,
                                                   @RequestBody ReminderRequest request) {
        ReminderResponse created = reminderService.create(listId, request);
        return ResponseEntity
                .created(URI.create("/api/reminders/" + created.getId()))
                .body(created);
    }

    @GetMapping("/reminders/{id}")
    public ResponseEntity<ReminderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reminderService.findById(id));
    }

    @PutMapping("/reminders/{id}")
    public ResponseEntity<ReminderResponse> update(@PathVariable Long id,
                                                   @RequestBody ReminderRequest request) {
        return ResponseEntity.ok(reminderService.update(id, request));
    }

    @PatchMapping("/reminders/{id}/toggle")
    public ResponseEntity<ReminderResponse> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(reminderService.toggleComplete(id));
    }

    @DeleteMapping("/reminders/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reminderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reminders/today")
    public ResponseEntity<List<ReminderResponse>> getToday() {
        return ResponseEntity.ok(reminderService.findToday());
    }

    @GetMapping("/reminders/scheduled")
    public ResponseEntity<List<ReminderResponse>> getScheduled() {
        return ResponseEntity.ok(reminderService.findScheduled());
    }

    @GetMapping("/reminders/all")
    public ResponseEntity<List<ReminderResponse>> getAll() {
        return ResponseEntity.ok(reminderService.findAll(false));
    }

    @GetMapping("/reminders/completed")
    public ResponseEntity<List<ReminderResponse>> getCompleted() {
        return ResponseEntity.ok(reminderService.findAll(true));
    }
}
