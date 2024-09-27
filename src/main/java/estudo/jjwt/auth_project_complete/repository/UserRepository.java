package estudo.jjwt.auth_project_complete.repository;

import estudo.jjwt.auth_project_complete.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Query("select u.role from User u where u.email like :email")
    User.Role findRoleByEmail(String email);
}
