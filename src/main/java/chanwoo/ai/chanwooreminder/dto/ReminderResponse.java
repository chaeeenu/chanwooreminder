package chanwoo.ai.chanwooreminder.dto;

import chanwoo.ai.chanwooreminder.domain.Priority;
import chanwoo.ai.chanwooreminder.domain.Reminder;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ReminderResponse {
    private Long id;
    private String title;
    private String memo;
    private LocalDate dueDate;
    private LocalTime dueTime;
    private Priority priority;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private Long listId;
    private String listName;
    private String listColor;
    private Long parentId;
    private List<TagResponse> tags;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReminderResponse from(Reminder r) {
        return ReminderResponse.builder()
                .id(r.getId())
                .title(r.getTitle())
                .memo(r.getMemo())
                .dueDate(r.getDueDate())
                .dueTime(r.getDueTime())
                .priority(r.getPriority())
                .isCompleted(r.getIsCompleted())
                .completedAt(r.getCompletedAt())
                .listId(r.getList().getId())
                .listName(r.getList().getName())
                .listColor(r.getList().getColor())
                .parentId(r.getParent() != null ? r.getParent().getId() : null)
                .tags(r.getTags().stream().map(TagResponse::from).toList())
                .sortOrder(r.getSortOrder())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
