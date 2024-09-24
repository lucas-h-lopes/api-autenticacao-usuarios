package estudo.jjwt.auth_project_complete.service;

import estudo.jjwt.auth_project_complete.entity.User;
import estudo.jjwt.auth_project_complete.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final String USER_NOT_FOUND_ID_MESSAGE = "User with id '%d' was not found";

    @Transactional
    public User insert(User user){
        return repository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long id){
        return repository.findById(id).orElseThrow(
                () -> new RuntimeException(String.format(USER_NOT_FOUND_ID_MESSAGE, id))
        );
    }

    @Transactional(readOnly = true)
    public List<User> findAll(){
        return repository.findAll();
    }

    @Transactional
    public void deleteById(Long id){
        if(!repository.existsById(id)){
            throw new RuntimeException(String.format(USER_NOT_FOUND_ID_MESSAGE, id));
        }
        repository.deleteById(id);
    }

    @Transactional
    public User updatePasswordById(Long id, String currentPassword, String newPassword, String confirmPassword ){
        if(!repository.existsById(id)){
            throw new RuntimeException(String.format(USER_NOT_FOUND_ID_MESSAGE, id));
        }
        User dbUser = findById(id);

        if(!dbUser.getPassword().equals(currentPassword)){
            throw new RuntimeException("Current password does not match");
        }
        if(dbUser.getPassword().equals(newPassword)){
            throw new RuntimeException("New password is equal to current password");
        }
        if(!newPassword.equals(confirmPassword)){
            throw new RuntimeException("Passwords does not match");
        }

        dbUser.setPassword(newPassword);
        return repository.save(dbUser);
    }

}
