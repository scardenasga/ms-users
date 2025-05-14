package co.edu.unbosque.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.stripe.model.checkout.Session;
import co.edu.unbosque.model.entity.EstadoSuscripcion;
import co.edu.unbosque.model.request.SuscripcionRequest;

@Service
public class SuscripcionService {

    private static final Logger LOG = Logger.getLogger(SuscripcionService.class.getName());

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Autowired
    private UsuarioService userServ;

    public void procesarEventoStripe(Session session) {
         SuscripcionRequest request = construirRequestDesdeSession(session);
         LOG.info("Este es el request: " + request);
    userServ.agregarActulizarSuscripcion(request);
    enviarCorreoConfirmacion(session, request);
    }


    private SuscripcionRequest construirRequestDesdeSession(Session session) {
        Long amountTotal = session.getAmountTotal();
        BigDecimal precio = BigDecimal.valueOf(amountTotal / 100.0);

        LOG.info("Monto total recibido desde Stripe (centavos): " + amountTotal);
LOG.info("Precio calculado en dólares: " + precio);

        String tipoSuscripcion;
        if (precio.compareTo(BigDecimal.valueOf(12)) == 0) {
            tipoSuscripcion = "mensual";
            LOG.info("tipo de suscripcion: " + tipoSuscripcion);
        } else if (precio.compareTo(BigDecimal.valueOf(120)) == 0) {
            tipoSuscripcion = "anual";
            LOG.info("tipo de suscripcion: " + tipoSuscripcion);
        } else {
            throw new IllegalArgumentException("Monto no corresponde a una suscripción válida");
        }

        return SuscripcionRequest.builder()
                .email(session.getCustomerDetails().getEmail())
                .nombre(tipoSuscripcion)
                .precio(precio)
                .estado(EstadoSuscripcion.ACTIVA)
                .build();
    }

    private void enviarCorreoConfirmacion(Session session, SuscripcionRequest request) {
        String nombre = session.getCustomerDetails().getName();
        String fechaActual = LocalDate.now().toString();

        StrategyEmail correo = new CorreoSuscripcion(
                request.getEmail(),
                nombre,
                request.getNombre().toUpperCase(),
                request.getPrecio(),
                fechaActual);

        correo.enviarCorreo();
    }

}
