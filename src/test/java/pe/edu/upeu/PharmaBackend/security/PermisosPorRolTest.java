package pe.edu.upeu.PharmaBackend.security;
import org.junit.jupiter.api.Test; import org.springframework.security.core.GrantedAuthority; import pe.edu.upeu.PharmaBackend.entity.Rol; import pe.edu.upeu.PharmaBackend.enums.Permiso; import java.util.*; import static org.assertj.core.api.Assertions.*;
class PermisosPorRolTest {private List<String> a(Rol...r){return PermisosPorRol.authorities(List.of(r)).stream().map(GrantedAuthority::getAuthority).toList();}
 @Test void adminTieneTodo(){assertThat(PermisosPorRol.de("ADMIN")).containsExactlyInAnyOrder(Permiso.values());assertThat(a(new Rol(1L,"ADMIN",null))).contains("ROLE_ADMIN","REPORTES_VER","USUARIOS_ADMINISTRAR");}
 @Test void vendedorNoEditaCatalogoNiVeReportes(){assertThat(a(new Rol(2L,"VENDEDOR",null))).contains("VENTAS_REGISTRAR","CATALOGO_LEER").doesNotContain("REPORTES_VER","CATALOGO_ESCRIBIR");}
 @Test void combinaRolesSinDuplicados(){assertThat(a(new Rol(2L,"VENDEDOR",null),new Rol(3L,"ALMACENERO",null))).contains("VENTAS_REGISTRAR","CATALOGO_ESCRIBIR").doesNotContain("REPORTES_VER").doesNotHaveDuplicates();}
 @Test void rolDesconocidoSoloRecibeRole(){assertThat(a(new Rol(9L,"AUDITOR",null))).containsExactly("ROLE_AUDITOR");}
}
