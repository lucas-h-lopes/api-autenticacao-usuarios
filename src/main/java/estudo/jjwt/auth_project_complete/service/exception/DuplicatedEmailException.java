package estudo.jjwt.auth_project_complete.service.exception;

public class DuplicatedEmailException extends RuntimeException{
    public DuplicatedEmailException(String msg){
        super(msg);
    }
}
