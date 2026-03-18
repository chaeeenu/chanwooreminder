package chanwoo.ai.chanwooreminder.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String color;

    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    private Set<Reminder> reminders = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static Tag create(String name, String color) {
        LocalDateTime now = LocalDateTime.now();
        return Tag.builder()
                .name(name)
                .color(color != null ? color : "#007AFF")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void update(String name, String color) {
        if (name != null) this.name = name;
        if (color != null) this.color = color;
        this.updatedAt = LocalDateTime.now();
    }
}
