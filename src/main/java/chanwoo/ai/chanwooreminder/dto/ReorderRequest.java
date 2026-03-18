package chanwoo.ai.chanwooreminder.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ReorderRequest {
    private List<ReorderItem> items;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class ReorderItem {
        private Long id;
        private Integer sortOrder;
    }
}
