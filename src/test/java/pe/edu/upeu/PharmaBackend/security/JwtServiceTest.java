package pe.edu.upeu.PharmaBackend.security;
import io.jsonwebtoken.*; import org.junit.jupiter.api.Test; import org.springframework.security.core.userdetails.User; import org.springframework.security.core.userdetails.UserDetails; import pe.edu.upeu.PharmaBackend.entity.Rol; import java.util.List; import static org.assertj.core.api.Assertions.*;
class JwtServiceTest {
 private static final String SECRET="w9Z6dP/dzykNH/uiI3Z/KGjBlP0H4Ji+Tuq4fm2cTNuLD4jWafu1gBL74V2SJEvy";
 private UserDetails vendedor(){return User.withUsername("vendedor").password("x").authorities(PermisosPorRol.authorities(List.of(new Rol(2L,"VENDEDOR",null)))).build();}
 @Test void generaTokenConSubjectYAuthorities(){JwtService s=new JwtService(SECRET,60);String t=s.generarToken(vendedor());Claims c=s.leerClaims(t);assertThat(t.split("\\.")).hasSize(3);assertThat(c.getSubject()).isEqualTo("vendedor");assertThat((List<?>)c.get("authorities")).contains("ROLE_VENDEDOR","VENTAS_REGISTRAR").doesNotContain("REPORTES_VER");assertThat(s.getExpiracionSegundos()).isEqualTo(3600);}
 @Test void rechazaTokenAlterado(){JwtService s=new JwtService(SECRET,60);String t=s.generarToken(vendedor());String bad=t.substring(0,t.length()-2)+(t.endsWith("AA")?"BB":"AA");assertThatThrownBy(()->s.extraerUsername(bad)).isInstanceOf(JwtException.class);}
 @Test void rechazaTokenExpirado(){JwtService s=new JwtService(SECRET,-1);String t=s.generarToken(vendedor());assertThatThrownBy(()->s.extraerUsername(t)).isInstanceOf(ExpiredJwtException.class);}
}
