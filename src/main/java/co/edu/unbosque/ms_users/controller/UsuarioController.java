package co.edu.unbosque.ms_users.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.ms_users.model.entity.Rol;
import co.edu.unbosque.ms_users.model.request.SuscripcionRequest;
import co.edu.unbosque.ms_users.model.request.UsuarioRequest;
import co.edu.unbosque.ms_users.model.request.UsuarioUpdate;
import co.edu.unbosque.ms_users.model.response.UsuarioResponse;
import co.edu.unbosque.ms_users.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> roles() {
        return ResponseEntity.ok(usuarioService.roles());
    }

    @PostMapping("/crear") // El método para crear usuario será accesible a través de POST /usuarios/crear
    public ResponseEntity<String> crearUsuario(@RequestBody UsuarioRequest usuarioRequest) {
        try {
            // Llamamos al servicio para crear el usuario
            usuarioService.crearUsuario(usuarioRequest);
            return ResponseEntity.ok("Usuario creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear el usuario: " + e.getMessage());
        }
    }

    @PatchMapping("/{email}/eliminar")
    public ResponseEntity<String> eliminarUsuario(@PathVariable String email) {
        try {
            usuarioService.eliminarUsuario(email);
            return ResponseEntity.ok("Usuario eliminado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar el usuario: " + e.getMessage());
        }
    }

    @PatchMapping("/{email}/bloquear")
    public ResponseEntity<String> bloquearUsuario(@PathVariable String email) {
        try {
            usuarioService.bloquearUsuario(email);
            return ResponseEntity.ok("Usuario bloqueado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al bloquear el usuario: " + e.getMessage());
        }
    }

    @GetMapping("/") // El método para listar usuarios será accesible a través de GET
                     // /usuarios/listar
    public ResponseEntity<List<UsuarioResponse>> mostrarUsuarios() {
        return ResponseEntity.ok(usuarioService.buscarUsuarios());
    }

    @GetMapping("/all") // El método para listar usuarios será accesible a través de GET
                     // /usuarios/listar
    public ResponseEntity<List<UsuarioResponse>> allUsers() {
        return ResponseEntity.ok(usuarioService.showAllUsers());
    }

    // {
    // "primerNombre": "NuevoNombre",
    // "segundoNombre": "OtroSegundoNombre",
    // "primerApellido": "NuevoApellido",
    // "segundoApellido": "OtroSegundoApellido",
    // "telefono": "1234567890",
    // "configuracion": {
    // "monedaBase": "EUR",
    // "recibirNotificaciones": true
    // }
    // }
    @PutMapping("/actualizar/{email}") // El método para actualizar usuario será accesible a través de PUT
                                       // /usuarios/actualizar
    public ResponseEntity<String> actualizarUsuario(@PathVariable String email,
            @RequestBody UsuarioUpdate usuarioRequest) {
        try {
            usuarioService.actualizarUsuario(email, usuarioRequest);
            return ResponseEntity.ok("Usuario actualizado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar informacion del usuario: " + e.getMessage());
        }
    }

    @PatchMapping("/{email}/password")
    public ResponseEntity<String> actualizarPassword(@PathVariable String email, @RequestBody String password) {
        try {
            usuarioService.actualizarPassword(email, password);
            return ResponseEntity.ok("Contraseña actualizada exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar la contraseña: " + e.getMessage());
        }
    }

    @PatchMapping("/{email}/rol")
    public ResponseEntity<String> actualizarRol(@PathVariable String email, @RequestBody String rol) {
        try {
            usuarioService.actualizarRol(email, rol);
            return ResponseEntity.ok("Rol actualizado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el rol: " + e.getMessage());
        }
    }

    @PatchMapping("/{email}/saldo")
    public ResponseEntity<String> actualizarSaldo(@PathVariable String email, @RequestBody BigDecimal saldo) {
        try {
            usuarioService.actulizarSaldo(email, saldo);
            return ResponseEntity.ok("Saldo actualizado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el saldo: " + e.getMessage());
        }
    }

    @PatchMapping("/{email}/estado")
    public ResponseEntity<String> actualizarEstado(@PathVariable String email, @RequestBody String estado) {
        try {
            usuarioService.actualizarEstado(email, estado);
            return ResponseEntity.ok("Estado actualizado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el estado: " + e.getMessage());
        }
    }

    @PostMapping("/{email}/suscripcion")
    public ResponseEntity<String> agregarActualizarSuscripcion(@PathVariable String email, @RequestBody SuscripcionRequest request) {
        try {
            usuarioService.agregarActulizarSuscripcion(email, request);
            return ResponseEntity.ok("Suscripción agregada o actualizada exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al agregar o actualizar la suscripción: " + e.getMessage());
        }
    }

    @PatchMapping("/{idUsuario}/suscripcion/cancelar")
    public ResponseEntity<String> cancelarSuscripcion(@PathVariable String idUsuario) {
        try {
            usuarioService.cancelarSuscripcion(idUsuario);
            return ResponseEntity.ok("Suscripción cancelada exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al cancelar la suscripción: " + e.getMessage());
        }
    }

    @PatchMapping("/{idUsuario}/suscripcion/renovar")
    public ResponseEntity<String> renovarSuscripcion(@PathVariable String idUsuario) {
        try {
            usuarioService.renovarSuscripcion(idUsuario);
            return ResponseEntity.ok("Suscripción renovada exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al renovar la suscripción: " + e.getMessage());
        }
    }

    @PatchMapping("/{idUsuario}/suscripcion/suspender")
    public ResponseEntity<String> suspenderSuscripcion(@PathVariable String idUsuario) {
        try {
            usuarioService.suspenderSuscripcion(idUsuario);
            return ResponseEntity.ok("Suscripción suspendida exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al suspender la suscripción: " + e.getMessage());
        }
    }

}
