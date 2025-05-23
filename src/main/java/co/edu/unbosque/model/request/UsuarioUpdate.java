package co.edu.unbosque.model.request;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UsuarioUpdate {
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String telefono;
}
