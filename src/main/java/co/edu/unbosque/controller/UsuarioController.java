package co.edu.unbosque.controller;

import co.edu.unbosque.model.Response.UsuarioResponse;
import co.edu.unbosque.model.entity.Usuario;
import co.edu.unbosque.model.request.EmailRequest;
import co.edu.unbosque.model.request.UsuarioRequest;
import org.springframework.http.MediaType;
import co.edu.unbosque.service.EmailService;

import com.stripe.model.checkout.Session;
import co.edu.unbosque.service.SuscripcionService;
import co.edu.unbosque.service.UsuarioService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import com.stripe.net.Webhook;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;

import java.util.logging.Logger;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:3000" })
@Transactional
public class UsuarioController {
	@Autowired
	private UsuarioService userServ;
	@Autowired
	private EmailService emServ;

	@Autowired
	private SuscripcionService susServ;

	@Value("${stripe.webhook-secret}")
	private String webhookSecret;

	private static final Logger LOG = Logger.getLogger(UsuarioController.class.getName());

	public UsuarioController() {
	}

	@PostMapping(path = "/enviarcorreo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> enviarcorreo(@RequestBody EmailRequest user) {

		Usuario usuario = userServ.findByEmail(user.getEmail());

		if (usuario == null) {
			return new ResponseEntity<>("Correo no registrado", HttpStatus.NOT_FOUND);
		}
		if (!userServ.isValidPassword(user.getPassword(), usuario.getContrasena())) {
			return new ResponseEntity<>("Contraseña incorrecta", HttpStatus.UNAUTHORIZED);
		}

		String codigo2 = emServ.generarCodigo();
		System.out.println("este es el codigo" + codigo2);
		userServ.guardarCodigo(user.getEmail(), codigo2);
		emServ.enviarCorreo(codigo2, user.getEmail());

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

	@GetMapping(path = "/listar")
	public ResponseEntity<UsuarioResponse> listarUsuario(
			@RequestParam String email) {
		UsuarioResponse user = userServ.listarUsuario(email);
		return ResponseEntity.ok(user);
	}

	@PostMapping(path = "/crear")
	public ResponseEntity<String> crearUsuario(@RequestBody UsuarioRequest usuarioRequest) {
		try {
			userServ.crearUsuario(usuarioRequest);
			return ResponseEntity.ok("Usuario creado exitosamente");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Error al crear el usuario: " + e.getMessage());
		}
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
	public ResponseEntity<Map<String, String>> crearSuscripcion(@RequestBody Map<String, String> body) {
		try {
			String plan = body.get("plan");
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

	@PostMapping(value = "/webhookStripe", consumes = "application/json")
	public ResponseEntity<String> stripeWebhook(@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String sigHeader) {

		try {
			if (webhookSecret == null || webhookSecret.isBlank()) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error de configuración: webhookSecret no definido");
			}
			Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
			if ("checkout.session.completed".equals(event.getType())) {

				StripeObject stripeObject = event.getData().getObject();

				if (stripeObject instanceof Session) {
					Session session = (Session) stripeObject;

					if (session.getCustomerDetails() == null) {
						LOG.severe("CustomerDetails es null");
					}
					if (session.getAmountTotal() == null) {
						LOG.severe("AmountTotal es null");
					} else {
						LOG.info("Monto total recibido: " + session.getAmountTotal());
					}

					if (session.getCustomerDetails() == null || session.getAmountTotal() == null
							|| session.getAmountTotal() <= 0) {
						return ResponseEntity.badRequest().body("Datos de sesión incompletos o inválidos");
					}
					susServ.procesarEventoStripe(session);

					return ResponseEntity.ok("Webhook procesado correctamente");
				} else {
					return ResponseEntity.badRequest().body("El objeto recibido no es una sesión de checkout");
				}
			} else {
				return ResponseEntity.ok("Evento no relevante, pero recibido");
			}

		} catch (SignatureVerificationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Firma inválida de Stripe");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error interno del servidor");
		}
	}

	@GetMapping(path = "/buscarSuscripcion/{idUsuario}")
	public ResponseEntity<Boolean> verificarSuscripcion(@PathVariable String idUsuario) {
		boolean tieneSuscripcion = userServ.obtenerSuscripcion(idUsuario);
		return ResponseEntity.ok(tieneSuscripcion);
	}

}
