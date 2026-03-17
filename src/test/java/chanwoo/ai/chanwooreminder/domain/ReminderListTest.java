package chanwoo.ai.chanwooreminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderListTest {

    @Nested
    @DisplayName("create - 정적 팩토리 메서드")
    class Create {

        @Test
        @DisplayName("이름, 색상, 아이콘을 지정하면 해당 값으로 생성된다")
        void withAllValues() {
            ReminderList list = ReminderList.create("장보기", "#FF3B30", "cart");

            assertThat(list.getName()).isEqualTo("장보기");
            assertThat(list.getColor()).isEqualTo("#FF3B30");
            assertThat(list.getIcon()).isEqualTo("cart");
        }

        @Test
        @DisplayName("색상이 null이면 기본값 #007AFF가 적용된다")
        void nullColorUsesDefault() {
            ReminderList list = ReminderList.create("테스트", null, "star");

            assertThat(list.getColor()).isEqualTo("#007AFF");
        }

        @Test
        @DisplayName("아이콘이 null이면 기본값 list.bullet이 적용된다")
        void nullIconUsesDefault() {
            ReminderList list = ReminderList.create("테스트", "#FF9500", null);

            assertThat(list.getIcon()).isEqualTo("list.bullet");
        }

        @Test
        @DisplayName("createdAt과 updatedAt이 현재 시각으로 설정된다")
        void setsTimestamps() {
            LocalDateTime before = LocalDateTime.now();

            ReminderList list = ReminderList.create("테스트", null, null);

            LocalDateTime after = LocalDateTime.now();
            assertThat(list.getCreatedAt()).isBetween(before, after);
            assertThat(list.getUpdatedAt()).isBetween(before, after);
        }

        @Test
        @DisplayName("createdAt과 updatedAt이 동일한 값이다")
        void createdAtEqualsUpdatedAt() {
            ReminderList list = ReminderList.create("테스트", null, null);

            assertThat(list.getCreatedAt()).isEqualTo(list.getUpdatedAt());
        }

        @Test
        @DisplayName("id는 null이다")
        void idIsNull() {
            ReminderList list = ReminderList.create("테스트", null, null);

            assertThat(list.getId()).isNull();
        }

        @Test
        @DisplayName("reminders는 빈 리스트로 초기화된다")
        void remindersIsEmptyList() {
            ReminderList list = ReminderList.create("테스트", null, null);

            assertThat(list.getReminders()).isNotNull().isEmpty();
        }
    }

    @Nested
    @DisplayName("update - 부분 수정")
    class Update {

        @Test
        @DisplayName("이름, 색상, 아이콘을 모두 변경한다")
        void updatesAllFields() {
            ReminderList list = ReminderList.create("원래 이름", "#007AFF", "list.bullet");

            list.update("새 이름", "#FF3B30", "star");

            assertThat(list.getName()).isEqualTo("새 이름");
            assertThat(list.getColor()).isEqualTo("#FF3B30");
            assertThat(list.getIcon()).isEqualTo("star");
        }

        @Test
        @DisplayName("null인 필드는 기존 값을 유지한다")
        void nullFieldsKeepOriginal() {
            ReminderList list = ReminderList.create("원래 이름", "#007AFF", "list.bullet");

            list.update(null, null, null);

            assertThat(list.getName()).isEqualTo("원래 이름");
            assertThat(list.getColor()).isEqualTo("#007AFF");
            assertThat(list.getIcon()).isEqualTo("list.bullet");
        }

        @Test
        @DisplayName("updatedAt이 갱신되고 createdAt은 변경되지 않는다")
        void updatesTimestamp() {
            ReminderList list = ReminderList.create("테스트", null, null);
            LocalDateTime originalCreatedAt = list.getCreatedAt();
            LocalDateTime originalUpdatedAt = list.getUpdatedAt();
            LocalDateTime before = LocalDateTime.now();

            list.update("변경", null, null);

            LocalDateTime after = LocalDateTime.now();
            assertThat(list.getCreatedAt()).isEqualTo(originalCreatedAt);
            assertThat(list.getUpdatedAt()).isBetween(before, after);
            assertThat(list.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        }
    }
}
