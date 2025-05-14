package co.edu.unbosque.model.request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailRequest {

    public String email;
    public String password;

}
