package estudo.jjwt.auth_project_complete.web.exception;

import estudo.jjwt.auth_project_complete.service.exception.DbException;
import estudo.jjwt.auth_project_complete.service.exception.DuplicatedEmailException;
import estudo.jjwt.auth_project_complete.service.exception.InvalidPasswordException;
import estudo.jjwt.auth_project_complete.service.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomExceptionBody> userNotFoundException(UserNotFoundException e, HttpServletRequest request){
        CustomExceptionBody ex = new CustomExceptionBody(request, HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity.status(404).body(ex);
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<CustomExceptionBody> duplicatedEmailException(DuplicatedEmailException e, HttpServletRequest request){
        CustomExceptionBody ex = new CustomExceptionBody(request, HttpStatus.CONFLICT, e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<CustomExceptionBody> duplicatedEmailException(InvalidPasswordException e, HttpServletRequest request){
        CustomExceptionBody ex = new CustomExceptionBody(request, HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex);
    }

    @ExceptionHandler(DbException.class)
    public ResponseEntity<CustomExceptionBody> dbException(DbException e, HttpServletRequest request){
        CustomExceptionBody ex = new CustomExceptionBody(request, HttpStatus.CONFLICT, "Database error");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomExceptionBody> methodArgumentNotValidException(HttpServletRequest request, BindingResult result){
        CustomExceptionBody ex = new CustomExceptionBody(request, HttpStatus.BAD_REQUEST, "Validation error(s)", result);
        return ResponseEntity.status(400).body(ex);
    }


}
