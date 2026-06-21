package br.edu.ifal.sigamais.security;

import br.edu.ifal.sigamais.model.Usuario;
import br.edu.ifal.sigamais.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verifica se a tabela de usuários está vazia
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador do Sistema");
            admin.setCpf("00000000000"); // Nosso novo campo obrigatório!
            admin.setEmail("admin@ifal.edu.br");
            admin.setSenha(passwordEncoder.encode("123456")); // A senha que o David vai usar
            admin.setPerfil("ADMIN");

            usuarioRepository.save(admin);
            System.out.println("✅ Usuário ADMIN padrão criado com sucesso no banco de dados!");
        }
    }
}