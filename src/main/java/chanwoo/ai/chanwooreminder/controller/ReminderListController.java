package chanwoo.ai.chanwooreminder.controller;

import chanwoo.ai.chanwooreminder.dto.ReminderListRequest;
import chanwoo.ai.chanwooreminder.dto.ReminderListResponse;
import chanwoo.ai.chanwooreminder.service.ports.in.ReminderListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ReminderListController {

    private final ReminderListService listService;

    @GetMapping
    public ResponseEntity<List<ReminderListResponse>> getAll() {
        return ResponseEntity.ok(listService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderListResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(listService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ReminderListResponse> create(@RequestBody ReminderListRequest request) {
        ReminderListResponse created = listService.create(request);
        return ResponseEntity
                .created(URI.create("/api/lists/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReminderListResponse> update(@PathVariable Long id,
                                                       @RequestBody ReminderListRequest request) {
        return ResponseEntity.ok(listService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        listService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
