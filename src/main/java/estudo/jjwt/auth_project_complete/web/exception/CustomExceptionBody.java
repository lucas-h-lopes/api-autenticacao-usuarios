package estudo.jjwt.auth_project_complete.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.TreeMap;

@Getter @Setter @JsonPropertyOrder({"path", "method", "status", "statusMessage", "message", "foundErrors"})
public class CustomExceptionBody {

    private String path;
    private String method;
    private Integer status;
    private String statusMessage;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private Map<String,String> foundErrors;

    public CustomExceptionBody(){}
    public CustomExceptionBody(HttpServletRequest request, HttpStatus status, String message){
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.status = status.value();
        this.statusMessage = status.getReasonPhrase();
        this.message = message;
    }

    public CustomExceptionBody(HttpServletRequest request, HttpStatus status, String message, BindingResult result){
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.status = status.value();
        this.statusMessage = status.getReasonPhrase();
        this.message = message;
        addErrors(result);
    }

    private void addErrors(BindingResult result) {
        foundErrors = new TreeMap<>();
        for(FieldError error : result.getFieldErrors()){
            this.foundErrors.put(error.getField(), error.getDefaultMessage());
        }
    }
}
