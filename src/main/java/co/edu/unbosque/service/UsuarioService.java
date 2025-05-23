package co.edu.unbosque.service;

import co.edu.unbosque.model.Response.UsuarioResponse;
import co.edu.unbosque.model.Response.UsuarioResponse.ConfiguracionDTO;
import co.edu.unbosque.model.entity.Configuracion;
import co.edu.unbosque.model.entity.EstadoSuscripcion;
import co.edu.unbosque.model.entity.Moneda;
import co.edu.unbosque.model.entity.Usuario;
import co.edu.unbosque.model.entity.UsuarioSuscripcion;
import co.edu.unbosque.model.request.SuscripcionRequest;
import co.edu.unbosque.model.request.UsuarioRequest;
import co.edu.unbosque.model.request.UsuarioUpdate;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import co.edu.unbosque.repository.ConfiguracionRepository;
import co.edu.unbosque.repository.UsuarioRepository;
import co.edu.unbosque.repository.UsuarioSuscripcionRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

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

    @Autowired
    public UsuarioSuscripcionRepository userSusRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    public ConfiguracionRepository conRepo;

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

    public Usuario findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

     public Optional<BigDecimal> findBySaldo(String email) {
    Optional<BigDecimal> saldo = userRepo.findSaldoByEmail(email);
    saldo.ifPresent(s -> System.out.println("Saldo encontrado: " + s));
    return saldo;
}


     public Moneda findByMoneda(String email) {
       Configuracion configuracion = conRepo.findByIdUsuario(email);
        if (configuracion != null) {
            return configuracion.getMonedaBase();
        }
        return Moneda.USD; 
    }
    public UsuarioResponse listarUsuario(String email) {
        Usuario response = userRepo.findByEmail(email);
        return toResponse(response);
    }

    public void crearUsuario(UsuarioRequest user) {
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("El email " + user.getEmail() + " ya está registrado.");
        }
        Usuario person = toEntity(user);
        userRepo.save(person);
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
                .setSuccessUrl("http://localhost:3000/Landingpage")
                .setCancelUrl("http://localhost:3000/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(priceId)
                                .setQuantity(1L)
                                .build())
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    public void agregarActulizarSuscripcion(SuscripcionRequest request) {
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

    public void recargarSaldo(String email, BigDecimal monto) {
        Usuario usuario = userRepo.findByEmail(email);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado con email: " + email);
        }

        BigDecimal saldoActual = usuario.getSaldo();
        usuario.setSaldo(saldoActual.add(monto));
        userRepo.save(usuario);
    }

    private Usuario userExist(String email) {
        return userRepo.findById(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null)
            return null;

        return UsuarioResponse.builder()
                .email(usuario.getEmail())
                .PrimerNombre(usuario.getPrimerNombre())
                .SegundoNombre(usuario.getSegundoNombre())
                .PrimerApellido(usuario.getPrimerApellido())
                .SegundoApellido(usuario.getSegundoApellido())
                .saldo(usuario.getSaldo())
                .telefono(usuario.getTelefono())
                .estado(usuario.getEstado())
                .configuracion(ConfiguracionDTO.builder()
                        .monedaBase(usuario.getConfiguracion().getMonedaBase())
                        .recibirNotificaciones(usuario.getConfiguracion().getRecibirNotificaciones())
                        .build())
                .build();
    }

    private Usuario toEntity(UsuarioRequest request) {

        Configuracion config = Configuracion.builder()
                .idUsuario(request.getEmail())
                .monedaBase(request.getMoneda())
                .recibirNotificaciones(request.getNotificaciones())
                .build();

        return Usuario.builder()
                .email(request.getEmail())
                .primerNombre(request.getPrimerNombre())
                .segundoNombre(request.getSegundoNombre())
                .primerApellido(request.getPrimerApellido())
                .segundoApellido(request.getSegundoApellido())
                .saldo(BigDecimal.ZERO)
                .telefono(request.getTelefono())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .fechaRegistro(LocalDateTime.now())
                .configuracion(config)
                .build();
    }

    public boolean isValidPassword(String password, String encriptPassword) {
        return passwordEncoder.matches(password, encriptPassword);
    }

    public boolean obtenerSuscripcion(String idUsuario) {
        return userSusRepo.existsById(idUsuario);
    }

    public Configuracion obteneConfiguracion(String idUsuario){
        return conRepo.findByIdUsuario(idUsuario);
    }
    
      public void actualizarUsuario(String idUsuario, UsuarioUpdate updateRequest) {
        Usuario usuario = userRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idUsuario));

        if (updateRequest.getPrimerNombre() != null) {
            usuario.setPrimerNombre(updateRequest.getPrimerNombre());
        }
        if (updateRequest.getSegundoNombre() != null) {
            usuario.setSegundoNombre(updateRequest.getSegundoNombre());
        }
        if (updateRequest.getPrimerApellido() != null) {
            usuario.setPrimerApellido(updateRequest.getPrimerApellido());
        }
        if (updateRequest.getSegundoApellido() != null) {
            usuario.setSegundoApellido(updateRequest.getSegundoApellido());
        }
        if (updateRequest.getTelefono() != null) {
            usuario.setTelefono(updateRequest.getTelefono());
        }

        userRepo.save(usuario);
    }
}
