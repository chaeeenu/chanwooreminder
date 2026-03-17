package chanwoo.ai.chanwooreminder.controller;

import chanwoo.ai.chanwooreminder.dto.ReminderListRequest;
import chanwoo.ai.chanwooreminder.dto.ReminderListResponse;
import chanwoo.ai.chanwooreminder.service.ReminderListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ReminderListController {

    private final ReminderListService listService;

    @GetMapping
    public List<ReminderListResponse> getAll() {
        return listService.findAll();
    }

    @GetMapping("/{id}")
    public ReminderListResponse getById(@PathVariable Long id) {
        return listService.findById(id);
    }

    @PostMapping
    public ReminderListResponse create(@RequestBody ReminderListRequest request) {
        return listService.create(request);
    }

    @PutMapping("/{id}")
    public ReminderListResponse update(@PathVariable Long id, @RequestBody ReminderListRequest request) {
        return listService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        listService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
