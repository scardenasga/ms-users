package co.edu.unbosque.ms_users.model.request;

import co.edu.unbosque.ms_users.model.entity.Moneda;
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
    private ConfiguracionUpdateRequest configuracion;

    @Data
    @Builder
    public static class ConfiguracionUpdateRequest {
        private Moneda monedaBase;
        private Boolean recibirNotificaciones;
    }
}
