package pe.edu.upeu.PharmaBackend.repository;
import org.springframework.data.jpa.repository.JpaRepository; import pe.edu.upeu.PharmaBackend.entity.Rol;
public interface RolRepository extends JpaRepository<Rol,Long>{ java.util.Optional<Rol> findByNombre(String nombre); }
