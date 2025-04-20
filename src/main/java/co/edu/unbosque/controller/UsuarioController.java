package co.edu.unbosque.controller;

import co.edu.unbosque.model.Usuario;
import co.edu.unbosque.model.UsuarioDTO;
import co.edu.unbosque.service.EmailService;
import co.edu.unbosque.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "*" })
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
		emServ.enviarCorreo(codigo2, usuario.getEmail(), usuario.getContrasena());

		return new ResponseEntity<>("Código enviado al correo", HttpStatus.OK);

	}


}
