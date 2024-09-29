package estudo.jjwt.auth_project_complete.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI().info(new Info()
                .title("API Autenticação de Usuários")
                .contact(new Contact() .email("lucas.hen.lps@gmail.com")
                        .name("Lucas Lopes")
                        .url("https://www.linkedin.com/in/lucashlopes/"))
                        .description("Uma API para autenticação e manipulação de usuários utilizando a biblioteca [JJWT](https://github.com/jwtk/jjwt). ")
                .version("v2"));
    }
}
