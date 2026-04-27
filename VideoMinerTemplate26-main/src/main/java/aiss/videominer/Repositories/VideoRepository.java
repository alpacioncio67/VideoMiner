package aiss.videominer.Repositories;

import aiss.videominer.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video,String> {

    Page<Video> findByName(String name, Pageable paging);
}
