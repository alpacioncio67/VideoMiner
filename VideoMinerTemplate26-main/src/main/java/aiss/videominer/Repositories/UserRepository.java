package aiss.videominer.Repositories;

import aiss.videominer.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    //JPA Contiene un metodo para buscar por atributos de nuestro modelo

    Page<User> findByName(String name, Pageable paging);

    //Page<User> findByNameContaining(String name,Pageable paging);
}
