package aiss.videominer.Repositories;

import aiss.videominer.model.Caption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptionRepository extends JpaRepository<Caption,String> {

    Page<Caption> findByName(String name, Pageable paging);
}
