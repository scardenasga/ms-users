package co.edu.unbosque.service;


import co.edu.unbosque.model.Usuario;


import co.edu.unbosque.model.UsuarioDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.EntityNotFoundException;
import co.edu.unbosque.repository.UsuarioRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

	@Autowired
	public UsuarioRepository userRepo;

	private Map<String, String> codigosPorCorreo;

	@Value("${jwt.secret}")
	private String SECRET_KEY;

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
	
	public UsuarioDTO buscarPorCorreo(String correo) {
	    Usuario usuario = userRepo.findByEmail(correo);
	    if (usuario == null) return null;

	    UsuarioDTO dto = new UsuarioDTO();
	    dto.setEmail(usuario.getEmail());
	    dto.setContrasena(usuario.getContrasena());

	    return dto;
	}

	public String generarToken(UsuarioDTO usuario) {
		return Jwts.builder().setSubject(usuario.getEmail()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 20 * 60 * 1000))
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();
	}
	
	
	public void eliminarCuenta(String email) {
	    Usuario usuario = userRepo.findByEmail(email);
	    if (usuario != null) {
	        userRepo.delete(usuario); 
	    } else {
	        throw new EntityNotFoundException("El usuario con email " + email + " no existe.");
	    }
	}
}
