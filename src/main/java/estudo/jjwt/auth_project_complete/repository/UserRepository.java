package estudo.jjwt.auth_project_complete.repository;

import estudo.jjwt.auth_project_complete.entity.User;
import estudo.jjwt.auth_project_complete.repository.projection.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Query("select u.role from User u where u.email like :email")
    User.Role findRoleByEmail(String email);

    @Query("select u from User u")
    Page<UserProjection> findAllPageable(Pageable page);
}
