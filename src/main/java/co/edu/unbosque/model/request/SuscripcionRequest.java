package co.edu.unbosque.model.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import co.edu.unbosque.model.entity.EstadoSuscripcion;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuscripcionRequest {
    @JsonProperty("customer_details.email")
    private String email;

    @JsonProperty("customer_details.name")
    private String nombre;
    @JsonProperty("amount_total")
    private BigDecimal precio;

    private EstadoSuscripcion estado;
}