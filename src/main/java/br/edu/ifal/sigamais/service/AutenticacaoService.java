package br.edu.ifal.sigamais.service; // Ou br.edu.ifal.sigamais.security (dependendo da sua pasta)

import br.edu.ifal.sigamais.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // O Spring Security chama esse método sozinho por debaixo dos panos.
        // NUNCA chame o AuthenticationManager aqui dentro!
        return repository.findByEmail(username);
    }
}