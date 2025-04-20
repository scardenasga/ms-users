package co.edu.unbosque.service;

import co.edu.unbosque.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    public UsuarioRepository userRepo;

    public boolean existeEmail(String email) {
        return userRepo.existsByEmail(email);
    }
}
