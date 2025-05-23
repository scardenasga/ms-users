package co.edu.unbosque.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

import co.edu.unbosque.model.entity.Configuracion;
import co.edu.unbosque.model.entity.EstadoSuscripcion;
import co.edu.unbosque.model.entity.Moneda;
import co.edu.unbosque.model.request.SuscripcionRequest;
import co.edu.unbosque.repository.ConfiguracionRepository;

import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.web.client.RestTemplate;

@Service
public class PagoService {

    private static final Logger LOG = Logger.getLogger(PagoService.class.getName());
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Value("${exchange.api.key}")
private String exchangeApiKey;

    @Autowired
    private UsuarioService userServ;

    @Autowired
    public ConfiguracionRepository conRepo;

    public void procesarEventoStripe(Session session) {
        Map<String, String> metadata = session.getMetadata();
        if (metadata != null && "recarga".equalsIgnoreCase(metadata.get("tipoOperacion"))) {
            String email = metadata.get("Email");
            BigDecimal monto = BigDecimal.valueOf(session.getAmountTotal() / 100.0);
            Configuracion conf = conRepo.findByIdUsuario(email);
            String moneda = conf.getMonedaBase().toString();

            BigDecimal montoEnUSD = convertirDolar(monto, moneda);
            LOG.info("Procesando recarga de fondos para " + email + " por " + monto + " " + moneda);
            LOG.info("Monto convertido a USD: " + montoEnUSD);

            userServ.recargarSaldo(email, montoEnUSD);
        } else {
            SuscripcionRequest request = construirRequestDesdeSession(session);
            LOG.info("Este es el request: " + request);
            userServ.agregarActulizarSuscripcion(request);
            enviarCorreoConfirmacion(session, request);
        }
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

    public Session recargarFondos(String email, BigDecimal monto) throws StripeException {
        Configuracion conf = conRepo.findByIdUsuario(email);
        Moneda moneda = conf.getMonedaBase();
        String monedaStripe = moneda.toString().toLowerCase();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("tipoOperacion", "recarga");
        metadata.put("usuario_id", email);
        metadata.put("moneda", moneda);
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(monedaStripe)
                                                .setUnitAmount(monto.multiply(BigDecimal.valueOf(100)).longValue())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Recarga de fondos")
                                                                .build())
                                                .build())
                                .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/Landingpage")
                .setCancelUrl("http://localhost:3000/cancel")
                .putMetadata("Email", String.valueOf(email))
                .putMetadata("tipoOperacion", "recarga")
                .build();
        return Session.create(params);

    }

    public BigDecimal convertirDolar(BigDecimal monto, String moneda) {
        if (moneda.equalsIgnoreCase("USD")) {
            return monto;
        }
        String url = String.format("https://v6.exchangerate-api.com/v6/%s/latest/%s", exchangeApiKey, moneda);
        Map response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("conversion_rates")) {
            throw new RuntimeException("No se pudo obtener la tasa de cambio");
        }
        Map<String, Double> tasas = (Map<String, Double>) response.get("conversion_rates");
        Double tasaUSD = tasas.get("USD");

        if (tasaUSD == null) {
            throw new RuntimeException("No hay tasa de conversión a USD");
        }

        return monto.multiply(BigDecimal.valueOf(tasaUSD));

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
