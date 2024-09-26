package estudo.jjwt.auth_project_complete.service;

import estudo.jjwt.auth_project_complete.entity.User;
import estudo.jjwt.auth_project_complete.repository.UserRepository;
import estudo.jjwt.auth_project_complete.service.exception.DbException;
import estudo.jjwt.auth_project_complete.service.exception.DuplicatedEmailException;
import estudo.jjwt.auth_project_complete.service.exception.InvalidPasswordException;
import estudo.jjwt.auth_project_complete.service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository repository;
    private final String USER_NOT_FOUND_ID_MESSAGE = "User with id '%d' was not found";

    @Transactional
    public User insert(User user) {
        try {
            if (repository.findByEmail(user.getEmail()) != null) {
                throw new DuplicatedEmailException(String.format("A user with email '%s' already exists", user.getEmail()));
            }
            return repository.save(user);
        }catch(DataIntegrityViolationException e){
            log.error("Failed to insert user, exception message: ", e.getMessage());
            throw new DbException("Failed to insert user '" + user.getEmail() + "'");
        }
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new UserNotFoundException(String.format(USER_NOT_FOUND_ID_MESSAGE, id))
        );
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_ID_MESSAGE, id));
        }
        repository.deleteById(id);
    }

    @Transactional
    public void updatePasswordById(Long id, String currentPassword, String newPassword, String confirmPassword) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_ID_MESSAGE, id));
        }
        User dbUser = findById(id);

        validatePassword(dbUser, currentPassword, newPassword, confirmPassword);

        dbUser.setPassword(newPassword);
        repository.save(dbUser);
    }

    private void validatePassword(User user, String currentPassword, String newPassword, String confirmPassword) {
        if (!user.getPassword().equals(currentPassword)) {
            throw new InvalidPasswordException("Current password does not match");
        }
        if (user.getPassword().equals(newPassword)) {
            throw new InvalidPasswordException("New password is equal to current password");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new InvalidPasswordException("Passwords does not match");
        }
    }

}
