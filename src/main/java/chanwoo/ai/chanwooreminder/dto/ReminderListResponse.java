package chanwoo.ai.chanwooreminder.dto;

import chanwoo.ai.chanwooreminder.domain.ReminderList;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ReminderListResponse {
    private Long id;
    private String name;
    private String color;
    private String icon;
    private int incompleteCount;

    public static ReminderListResponse from(ReminderList list, int incompleteCount) {
        return ReminderListResponse.builder()
                .id(list.getId())
                .name(list.getName())
                .color(list.getColor())
                .icon(list.getIcon())
                .incompleteCount(incompleteCount)
                .build();
    }
}
