package co.edu.unbosque.service;


import co.edu.unbosque.model.entity.EstadoSuscripcion;
import co.edu.unbosque.model.entity.Usuario;
import co.edu.unbosque.model.entity.UsuarioSuscripcion;
import co.edu.unbosque.model.request.SuscripcionRequest;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import co.edu.unbosque.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;


@Service
public class UsuarioService {

	@Autowired
	public UsuarioRepository userRepo;

	private Map<String, String> codigosPorCorreo;


	@Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey; 
    }

	public UsuarioService() {
		codigosPorCorreo = new HashMap<>();
	}
	
	public void guardarCodigo(String correo, String codigo) {
		codigosPorCorreo.put(correo, codigo);
	}
	
	public String obtenerCodigo(String correo) {
		return codigosPorCorreo.get(correo);
	}
	
	public void eliminarCodigo(String correo) {
		codigosPorCorreo.remove(correo);
	}

	public boolean existeEmail(String email) {
		return userRepo.existsByEmail(email);
	}

	
	
	
	public void eliminarCuenta(String email) {
	    Usuario usuario = userRepo.findByEmail(email);
	    if (usuario != null) {
	        userRepo.delete(usuario); 
	    } else {
	        throw new EntityNotFoundException("El usuario con email " + email + " no existe.");
	    }
	}

	public String createCheckoutSession(String priceId) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
            .setSuccessUrl("http://localhost:3000/Registration")
            .setCancelUrl("http://localhost:3000/cancel")
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setPrice(priceId)
                    .setQuantity(1L)
                    .build()
            )
            .build();

        Session session = Session.create(params);
        return session.getUrl(); 
    }

	 public void agregarActulizarSuscripcion(SuscripcionRequest request) {

        // 1. Buscar si el usuario ya tiene un registro en usuario_suscripcion usando su ID
        Usuario user = userExist(request.getEmail());

        UsuarioSuscripcion nuevaSuscripcion = UsuarioSuscripcion.builder()
                .idUsuario(request.getEmail())
                .nombre(request.getNombre())
                .precio(request.getPrecio())
                .estado(EstadoSuscripcion.ACTIVA)
                .fechaFin(LocalDateTime.now().plusMonths(1))
                .fechaInicio(LocalDateTime.now())
                .build();
        user.setSuscripcion(nuevaSuscripcion);

        userRepo.save(user);
    }

	private Usuario userExist(String email) {
        return userRepo.findById(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
    }
}
