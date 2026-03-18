package chanwoo.ai.chanwooreminder.dto;

import chanwoo.ai.chanwooreminder.domain.Tag;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TagResponse {
    private Long id;
    private String name;
    private String color;
    private int reminderCount;

    public static TagResponse from(Tag tag) {
        int count = 0;
        try {
            count = tag.getReminders().size();
        } catch (Exception ignored) {}
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .reminderCount(count)
                .build();
    }
}
