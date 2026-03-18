package chanwoo.ai.chanwooreminder.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("리소스 이름과 id로 메시지가 구성된다")
    void messageFormat() {
        ResourceNotFoundException ex = new ResourceNotFoundException("List", 42L);

        assertThat(ex.getMessage()).isEqualTo("List not found: 42");
    }

    @Test
    @DisplayName("RuntimeException을 상속한다")
    void isRuntimeException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Reminder", 1L);

        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}
