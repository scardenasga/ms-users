package co.edu.unbosque.model.request;
import co.edu.unbosque.model.entity.Moneda;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioRequest {

    private String email;

    private String primerNombre;

    private String segundoNombre;

    private String primerApellido;

    private String segundoApellido;

    private String telefono;

    private String contrasena;

    private Moneda moneda; 
    private Boolean notificaciones; 

}
