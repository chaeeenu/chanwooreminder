package chanwoo.ai.chanwooreminder.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ReminderListRequest {
    private String name;
    private String color;
    private String icon;
}
