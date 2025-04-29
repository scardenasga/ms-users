package co.edu.unbosque.controller;


import co.edu.unbosque.model.UsuarioDTO;



import co.edu.unbosque.service.EmailService;
import co.edu.unbosque.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081","http://localhost:3000"})
@Transactional
public class UsuarioController {
	@Autowired
	private UsuarioService userServ;
	@Autowired
	private EmailService emServ;

	public UsuarioController() {
	}

	@PostMapping(path = "/enviarcorreo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> enviarcorreo(@RequestBody UsuarioDTO usuario) {

		if (!userServ.existeEmail(usuario.getEmail())) {
			return new ResponseEntity<>("Correo no registrado", HttpStatus.NOT_FOUND);
		}

		String codigo2 = emServ.generarCodigo();
		System.out.println("este es el codigo" + codigo2);
		userServ.guardarCodigo(usuario.getEmail(), codigo2);
		emServ.enviarCorreo(codigo2, usuario.getEmail(), usuario.getContrasena());

		return new ResponseEntity<>("Código enviado al correo", HttpStatus.OK);

	}
	
	@PostMapping(path = "/verificarCodigo")
	public ResponseEntity<String> enviarCodigo(@RequestParam String correo, @RequestParam String codigo)  {
		String codigoGuardado = userServ.obtenerCodigo(correo);

	    if (codigoGuardado == null || !codigoGuardado.equals(codigo)) {
	        return new ResponseEntity<>("Código incorrecto o expirado", HttpStatus.UNAUTHORIZED);
	    }

	    userServ.eliminarCodigo(correo);

	    UsuarioDTO usuario = userServ.buscarPorCorreo(correo);
	    String token = userServ.generarToken(usuario);

	    return new ResponseEntity<>(token, HttpStatus.OK);
		

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


}
