package pe.edu.upeu.PharmaBackend.security;
import org.springframework.security.core.userdetails.*; import org.springframework.stereotype.Service; import pe.edu.upeu.PharmaBackend.entity.Usuario; import pe.edu.upeu.PharmaBackend.repository.UsuarioRepository;
@Service public class UsuarioDetailsServiceImpl implements UserDetailsService {
 private final UsuarioRepository repo; public UsuarioDetailsServiceImpl(UsuarioRepository repo){this.repo=repo;}
 @Override public UserDetails loadUserByUsername(String username){Usuario u=repo.findByUsernameIgnoreCase(username).orElseThrow(()->new UsernameNotFoundException("Usuario no encontrado"));return User.withUsername(u.getUsername()).password(u.getPassword()).disabled(!Boolean.TRUE.equals(u.getEstado())).authorities(PermisosPorRol.authorities(u.getRoles())).build();}
}
