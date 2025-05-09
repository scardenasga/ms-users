package co.edu.unbosque.controller;

import co.edu.unbosque.model.entity.EstadoSuscripcion;

import co.edu.unbosque.model.request.SuscripcionRequest;
import co.edu.unbosque.service.EmailService;
import co.edu.unbosque.service.UsuarioService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import com.stripe.net.Webhook;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:3000" })
@Transactional
public class UsuarioController {
	@Autowired
	private UsuarioService userServ;
	@Autowired
	private EmailService emServ;

	@Value("${stripe.webhook-secret}")
	private String webhookSecret;

	private static final Logger LOG = Logger.getLogger(UsuarioController.class.getName());

	public UsuarioController() {
	}

	@PostMapping(path = "/enviarcorreo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> enviarcorreo(@RequestBody String email) {

		if (!userServ.existeEmail(email)) {
			return new ResponseEntity<>("Correo no registrado", HttpStatus.NOT_FOUND);
		}

		String codigo2 = emServ.generarCodigo();
		System.out.println("este es el codigo" + codigo2);
		userServ.guardarCodigo(email, codigo2);
		emServ.enviarCorreo(codigo2, email);

		return new ResponseEntity<>("Código enviado al correo", HttpStatus.OK);

	}

	@PostMapping(path = "/verificarCodigo")
	public ResponseEntity<String> enviarCodigo(@RequestParam String correo, @RequestParam String codigo) {
		String codigoGuardado = userServ.obtenerCodigo(correo);

		if (codigoGuardado == null || !codigoGuardado.equals(codigo)) {
			return new ResponseEntity<>("Código incorrecto o expirado", HttpStatus.UNAUTHORIZED);
		}

		userServ.eliminarCodigo(correo);

		return new ResponseEntity<>(HttpStatus.OK);

	}

	@DeleteMapping(path = "/eliminarUsuario/{email}")
	public ResponseEntity<String> eliminarUsuario(
			@PathVariable String email) {

		try {
			userServ.eliminarCuenta(email);
			return new ResponseEntity<>("Eliminado exitosamente", HttpStatus.ACCEPTED);
		} catch (EntityNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}

	}

	@PostMapping(path = "/crearSuscripcion")
	public ResponseEntity<Map<String, String>> crearSuscripcion(@RequestParam String plan) {
		try {
			String priceId;
			if (plan.equalsIgnoreCase("mensual")) {
				priceId = "price_1RJztMCBAIXMM3MqZqewby7R";
			} else if (plan.equalsIgnoreCase("anual")) {
				priceId = "price_1RJztNCBAIXMM3MqRB09fUj4";

			} else {
				return ResponseEntity.badRequest().body(Map.of("error", "Plan inválido"));
			}

			String checkoutUrl = userServ.createCheckoutSession(priceId);
			return ResponseEntity.ok(Map.of("url", checkoutUrl));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error creando sesión de Stripe: " + e.getMessage()));
		}
	}

	@PostMapping("/webhookStripe")
	public ResponseEntity<String> stripeWebhook(@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String sigHeader) {

		try {

			if (webhookSecret == null || webhookSecret.isBlank()) {

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error de configuración: webhookSecret no definido");
			}

			Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

			if ("checkout.session.completed".equals(event.getType())) {

				Optional<Session> sessionOptional = Optional.ofNullable((Session) event.getData().getObject());
				if (!sessionOptional.isPresent()) {

					return ResponseEntity.badRequest().body("Datos de sesión inválidos");
				}

				Session session = sessionOptional.get();

				if (session.getCustomerDetails() == null) {

					return ResponseEntity.badRequest().body("CustomerDetails no proporcionado");
				}

				String customerEmail = session.getCustomerDetails().getEmail();

				Long amountTotal = session.getAmountTotal();
				if (amountTotal == null || amountTotal <= 0) {

					return ResponseEntity.badRequest().body("Monto de pago no válido");
				}

				BigDecimal precio = new BigDecimal(amountTotal / 100.0);
				String tipoSuscripcion;
				if (precio.compareTo(BigDecimal.valueOf(12)) == 0) {
					tipoSuscripcion = "mensual";
				} else if (precio.compareTo(BigDecimal.valueOf(1200)) == 0) {
					tipoSuscripcion = "anual";
				} else {
					return ResponseEntity.badRequest().body("Monto no corresponde a una suscripción válida");
				}

				SuscripcionRequest suscripcionRequest = SuscripcionRequest.builder()
						.email(customerEmail)
						.nombre(tipoSuscripcion)
						.precio(new BigDecimal(amountTotal / 100.0))
						.estado(EstadoSuscripcion.ACTIVA)
						.build();

				try {
					userServ.agregarActulizarSuscripcion(suscripcionRequest);

				} catch (Exception serviceEx) {

					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
							"Error al procesar suscripción", serviceEx);
				}
			} else {
				LOG.info("Evento no manejado: " + event.getType());
			}

			return ResponseEntity.ok("Webhook procesado correctamente");

		} catch (StripeException e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Error de validación Stripe: " + e.getMessage());
		} catch (NullPointerException e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Datos incompletos en el payload");
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error interno del servidor");
		}
	}
}
