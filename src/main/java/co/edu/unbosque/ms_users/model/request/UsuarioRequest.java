package co.edu.unbosque.ms_users.model.request;

import co.edu.unbosque.ms_users.model.entity.TipoRol;
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

    @Builder.Default
    private TipoRol idRol = TipoRol.INVERSIONISTA; // ID del rol asociado
}
