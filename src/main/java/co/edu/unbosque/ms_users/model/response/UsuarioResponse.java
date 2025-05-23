package co.edu.unbosque.ms_users.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.edu.unbosque.ms_users.model.entity.EstadoSuscripcion;
import co.edu.unbosque.ms_users.model.entity.EstadoUsuario;
import co.edu.unbosque.ms_users.model.entity.Moneda;
import co.edu.unbosque.ms_users.model.entity.TipoRol;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponse {

    private String email;
    private String nombreCompleto;
    private BigDecimal saldo;
    private String telefono;
    private EstadoUsuario estado;
    private LocalDateTime fechaRegistro;
    private TipoRol rol;

    private ConfiguracionDTO configuracion;
    private SuscripcionDTO suscripcion;

    @Data
    @Builder
    public static class ConfiguracionDTO {
        private Moneda monedaBase;
        private Boolean recibirNotificaciones;
    }

    @Data
    @Builder
    public static class SuscripcionDTO {
        private String nombre; // Nombre del plan
        private BigDecimal precio;
        private LocalDateTime fechaInicio;
        private LocalDateTime fechaFin;
        private EstadoSuscripcion estado;
    }

}
