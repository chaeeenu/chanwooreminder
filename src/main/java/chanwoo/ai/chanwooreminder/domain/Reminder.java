package chanwoo.ai.chanwooreminder.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminder")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String memo;

    private LocalDate dueDate;

    private LocalTime dueTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Priority priority = Priority.NONE;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    private LocalDateTime completedAt;

    @ManyToMany
    @JoinTable(
            name = "reminder_tag",
            joinColumns = @JoinColumn(name = "reminder_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private java.util.Set<Tag> tags = new java.util.HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Reminder parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<Reminder> subtasks = new java.util.ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private ReminderList list;

    private Integer sortOrder;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static Reminder create(String title, String memo, LocalDate dueDate,
                                  LocalTime dueTime, Priority priority, ReminderList list) {
        LocalDateTime now = LocalDateTime.now();
        return Reminder.builder()
                .title(title)
                .memo(memo)
                .dueDate(dueDate)
                .dueTime(dueTime)
                .priority(priority != null ? priority : Priority.NONE)
                .list(list)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void update(String title, String memo, LocalDate dueDate,
                       LocalTime dueTime, Priority priority) {
        if (title != null) this.title = title;
        if (memo != null) this.memo = memo;
        if (dueDate != null) this.dueDate = dueDate;
        if (dueTime != null) this.dueTime = dueTime;
        if (priority != null) this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }

    public void toggleComplete() {
        this.isCompleted = !this.isCompleted;
        this.completedAt = this.isCompleted ? LocalDateTime.now() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public void moveToList(ReminderList newList) {
        this.list = newList;
        this.updatedAt = LocalDateTime.now();
    }

    public void markCompleted(boolean completed) {
        this.isCompleted = completed;
        this.completedAt = completed ? LocalDateTime.now() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
        this.updatedAt = LocalDateTime.now();
    }

    public void setParent(Reminder parent) {
        this.parent = parent;
        this.updatedAt = LocalDateTime.now();
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        this.updatedAt = LocalDateTime.now();
    }
}
