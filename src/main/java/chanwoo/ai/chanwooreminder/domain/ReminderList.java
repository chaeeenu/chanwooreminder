package chanwoo.ai.chanwooreminder.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reminder_list")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ReminderList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String icon;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reminder> reminders = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static ReminderList create(String name, String color, String icon) {
        LocalDateTime now = LocalDateTime.now();
        return ReminderList.builder()
                .name(name)
                .color(color != null ? color : "#007AFF")
                .icon(icon != null ? icon : "list.bullet")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void update(String name, String color, String icon) {
        if (name != null) this.name = name;
        if (color != null) this.color = color;
        if (icon != null) this.icon = icon;
        this.updatedAt = LocalDateTime.now();
    }
}
