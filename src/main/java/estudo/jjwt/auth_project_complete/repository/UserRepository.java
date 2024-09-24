package estudo.jjwt.auth_project_complete.repository;

import estudo.jjwt.auth_project_complete.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
