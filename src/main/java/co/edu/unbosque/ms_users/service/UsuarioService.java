package co.edu.unbosque.ms_users.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.ms_users.model.entity.Configuracion;
import co.edu.unbosque.ms_users.model.entity.EstadoSuscripcion;
import co.edu.unbosque.ms_users.model.entity.EstadoUsuario;
import co.edu.unbosque.ms_users.model.entity.Rol;
import co.edu.unbosque.ms_users.model.entity.Usuario;
import co.edu.unbosque.ms_users.model.entity.UsuarioSuscripcion;
import co.edu.unbosque.ms_users.model.request.SuscripcionRequest;
import co.edu.unbosque.ms_users.model.request.UsuarioRequest;
import co.edu.unbosque.ms_users.model.request.UsuarioUpdate;
import co.edu.unbosque.ms_users.model.response.UsuarioResponse;
import co.edu.unbosque.ms_users.model.response.UsuarioResponse.ConfiguracionDTO;
import co.edu.unbosque.ms_users.model.response.UsuarioResponse.SuscripcionDTO;
import co.edu.unbosque.ms_users.repository.RolRepository;
import co.edu.unbosque.ms_users.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Rol> roles() {
        return rolRepository.findAll();
    }

    private Rol rolByName(String nombre) {
        try {
            return rolRepository.findByNombre(nombre.toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombre));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol inválido: " + nombre);
        }
    }

    public List<UsuarioResponse> showAllUsers() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UsuarioResponse> buscarUsuarios() {
        List<Usuario> usuariosActivos = usuarioRepository.findByNoEstado(EstadoUsuario.ELIMINADO.name());
    
        return usuariosActivos.stream()
                .map(this::toResponse)
                .toList();
    }

    public void eliminarUsuario(String idUsuario) {
        userExist(idUsuario);
        actualizarEstado(idUsuario, EstadoUsuario.ELIMINADO.name());
    }

    public void bloquearUsuario(String idUsuario) {
        userExist(idUsuario);
        actualizarEstado(idUsuario, EstadoUsuario.SUSPENDIDO.name());
    }

    public void crearUsuario(UsuarioRequest user) {
        if (usuarioRepository.existsById(user.getEmail())) {
            throw new RuntimeException("El email " + user.getEmail() + " ya está registrado.");
        }
        Usuario person = toEntity(user);
        usuarioRepository.save(person);
    }

    public void actualizarUsuario(String idUsuario, UsuarioUpdate updateRequest) {
        //Verifica si el usuario existe
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idUsuario));

        Configuracion configuracion  = usuario.getConfiguracion();

        if(updateRequest.getConfiguracion() != null) {
            if (updateRequest.getConfiguracion().getMonedaBase() != null) {
                configuracion.setMonedaBase(updateRequest.getConfiguracion().getMonedaBase());
            }
            if (updateRequest.getConfiguracion().getRecibirNotificaciones() != null) {
                configuracion.setRecibirNotificaciones(updateRequest.getConfiguracion().getRecibirNotificaciones());
            }
            usuario.setConfiguracion(configuracion);
        }

        // Actualiza los campos del usuario solo si no son nulos
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

        // Guarda los cambios en la base de datos
        usuarioRepository.save(usuario);
    }

    public void actualizarPassword(String idUsuario, String password) {
        Usuario usuario =  userExist(idUsuario);
        usuario.setContrasena(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);
    }

    public void actualizarRol(String idUsuario, String rol) {
        Usuario usuario = userExist(idUsuario);
        Rol nuevoRol = rolByName(rol);
        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
    }
    
    public void actulizarSaldo(String idUsuario, BigDecimal saldo) {
        Usuario usuario = userExist(idUsuario);
        usuario.setSaldo(saldo);
        usuarioRepository.save(usuario);
    }

    public void actualizarEstado(String idUsuario, String estado) {
        Usuario usuario = userExist(idUsuario);
        usuario.setEstado(EstadoUsuario.valueOf(estado.toUpperCase()));
        usuarioRepository.save(usuario);
    }

    public void agregarActulizarSuscripcion(String idUsuario, SuscripcionRequest request) {

        // 1. Buscar si el usuario ya tiene un registro en usuario_suscripcion usando su ID
        Usuario user = userExist(idUsuario);

        UsuarioSuscripcion nuevaSuscripcion = UsuarioSuscripcion.builder()
                .idUsuario(idUsuario)
                .nombre(request.getNombre())
                .precio(request.getPrecio())
                .estado(EstadoSuscripcion.ACTIVA)
                .fechaFin(LocalDateTime.now().plusMonths(1))
                .fechaInicio(LocalDateTime.now())
                .build();
        user.setSuscripcion(nuevaSuscripcion);

        usuarioRepository.save(user);
    }

    public void cancelarSuscripcion(String idUsuario) {
        // 1. Buscar si el usuario ya tiene un registro en usuario_suscripcion usando su ID
        Usuario user = userExist(idUsuario);

        // 2. Cambiar el estado de la suscripción a CANCELADA
        if (user.getSuscripcion() != null) {
            user.getSuscripcion().setEstado(EstadoSuscripcion.EXPIRADA);
            usuarioRepository.save(user);
        } else {
            throw new RuntimeException("El usuario no tiene una suscripción activa.");
        }
    }
    public void renovarSuscripcion(String idUsuario) {
        // 1. Buscar si el usuario ya tiene un registro en usuario_suscripcion usando su ID
        Usuario user = userExist(idUsuario);

        // 2. Cambiar el estado de la suscripción a ACTIVA
        if (user.getSuscripcion() != null) {
            user.getSuscripcion().setEstado(EstadoSuscripcion.ACTIVA);
            user.getSuscripcion().setFechaFin(LocalDateTime.now().plusMonths(1));
            user.getSuscripcion().setFechaInicio(LocalDateTime.now());
            usuarioRepository.save(user);
        } else {
            throw new RuntimeException("El usuario no tiene una suscripción activa.");
        }
    }
    public void suspenderSuscripcion(String idUsuario) {
        // 1. Buscar si el usuario ya tiene un registro en usuario_suscripcion usando su ID
        Usuario user = userExist(idUsuario);

        // 2. Cambiar el estado de la suscripción a SUSPENDIDA
        if (user.getSuscripcion() != null) {
            user.getSuscripcion().setEstado(EstadoSuscripcion.PENDIENTE);
            usuarioRepository.save(user);
        } else {
            throw new RuntimeException("El usuario no tiene una suscripción activa.");
        }
    }


    private Usuario toEntity(UsuarioRequest request) {

        Rol rol = rolByName(request.getIdRol().name());

        Configuracion config = Configuracion.builder()
                .idUsuario(request.getEmail())
                .build();

        return Usuario.builder()
                // ID usuario
                .email(request.getEmail())
                // Nombre completo
                .primerNombre(request.getPrimerNombre())
                .segundoNombre(request.getSegundoNombre())
                .primerApellido(request.getPrimerApellido())
                .segundoApellido(request.getSegundoApellido())
                // Saldo
                .saldo(BigDecimal.ZERO)
                // Telefono
                .telefono(request.getTelefono())
                // Contrasena
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                // EStado
                // Fecha Registro
                .fechaRegistro(LocalDateTime.now())
                // Tipo de rol
                .rol(rol)
                // Configuracion
                .configuracion(config)
                .build();
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null)
            return null;

        SuscripcionDTO suscripcion = null; // Inicializa el DTO de suscripción a null
        if (usuario.getSuscripcion() != null) {
            suscripcion = SuscripcionDTO.builder()
                    .nombre(usuario.getSuscripcion().getNombre())
                    .precio(usuario.getSuscripcion().getPrecio())
                    .fechaInicio(usuario.getSuscripcion().getFechaInicio())
                    .fechaFin(usuario.getSuscripcion().getFechaFin())
                    .estado(usuario.getSuscripcion().getEstado())
                    .build();

        }

        return UsuarioResponse.builder()
                // ID usuario
                .email(usuario.getEmail())
                // Nombre completo
                .nombreCompleto(usuario.getPrimerNombre() + " " + usuario.getSegundoNombre() + " " +
                        usuario.getPrimerApellido() + " " + usuario.getSegundoApellido())
                // Saldo
                .saldo(usuario.getSaldo())
                // Telefono
                .telefono(usuario.getTelefono())
                // EStado
                .estado(usuario.getEstado())
                // Fecha Registro
                .fechaRegistro(usuario.getFechaRegistro())
                // Tipo de rol
                .rol(usuario.getRol().getNombre())
                // Configuracion
                .configuracion(ConfiguracionDTO.builder()
                        .monedaBase(usuario.getConfiguracion().getMonedaBase())
                        .recibirNotificaciones(usuario.getConfiguracion().getRecibirNotificaciones())
                        .build())
                // Suscripcion
                .suscripcion(suscripcion)

                .build();
    }

    private boolean isValidPassword(String password, String encriptPassword) {
        return passwordEncoder.matches(password, encriptPassword);
    }

    private Usuario userExist(String email) {
        return usuarioRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
    }

}
