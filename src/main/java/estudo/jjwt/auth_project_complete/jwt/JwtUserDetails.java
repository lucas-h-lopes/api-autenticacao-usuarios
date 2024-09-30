package estudo.jjwt.auth_project_complete.jwt;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetails extends User {

    private estudo.jjwt.auth_project_complete.entity.User user;

    public JwtUserDetails(estudo.jjwt.auth_project_complete.entity.User user) {
        super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    public String getRole(){
        return user.getRole().name();
    }

    public Long getId(){
        return user.getId();
    }
}
