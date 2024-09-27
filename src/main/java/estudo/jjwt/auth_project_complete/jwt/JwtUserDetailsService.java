package estudo.jjwt.auth_project_complete.jwt;

import estudo.jjwt.auth_project_complete.entity.User;
import estudo.jjwt.auth_project_complete.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService service;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = service.loadUserByEmail(username);
        return new JwtUserDetails(u);
    }

    public JwtToken getToken(String email){
        User.Role role = service.getRoleByEmail(email);
        return JwtUtils.generateToken(email,role.name());
    }
}
