package co.edu.unbosque.model.Response;

import java.math.BigDecimal;
import co.edu.unbosque.model.entity.EstadoUsuario;
import co.edu.unbosque.model.entity.Moneda;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponse {

    private String email;
    private String PrimerNombre;
    private String SegundoNombre;
    private String PrimerApellido;
    private String SegundoApellido;
    private BigDecimal saldo;
    private String telefono;
    private EstadoUsuario estado;

    private ConfiguracionDTO configuracion;
    

    @Data
    @Builder
    public static class ConfiguracionDTO {
        private Moneda monedaBase;
        private Boolean recibirNotificaciones;
    }

    

   

}