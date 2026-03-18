package chanwoo.ai.chanwooreminder.repository;

import chanwoo.ai.chanwooreminder.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
