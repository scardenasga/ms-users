package co.edu.unbosque.ms_users.model.request;

import java.math.BigDecimal;

import co.edu.unbosque.ms_users.model.entity.EstadoSuscripcion;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuscripcionRequest {

    private String nombre;

    private BigDecimal precio;

    private EstadoSuscripcion estado;
}
