package chanwoo.ai.chanwooreminder.dto;

import chanwoo.ai.chanwooreminder.domain.Priority;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ReminderRequest {
    private String title;
    private String memo;
    private LocalDate dueDate;
    private LocalTime dueTime;
    private Priority priority;
    private Boolean isCompleted;
    private Long listId;
    private Long parentId;
}
