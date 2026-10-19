package pe.edu.upeu.PharmaBackend.security;
import io.jsonwebtoken.*; import io.jsonwebtoken.io.Decoders; import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; import org.springframework.security.core.GrantedAuthority; import org.springframework.security.core.userdetails.UserDetails; import org.springframework.stereotype.Service;
import javax.crypto.SecretKey; import java.util.*; import java.util.stream.Collectors;
@Service public class JwtService {
 private final SecretKey clave; private final long expiracionMinutos;
 public JwtService(@Value("${app.jwt.secret}") String secretoBase64,@Value("${app.jwt.expiracion-minutos:60}") long expiracionMinutos){this.clave=Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));this.expiracionMinutos=expiracionMinutos;}
 public String generarToken(UserDetails user){Date ahora=new Date();List<String> authorities=user.getAuthorities().stream().map(GrantedAuthority::getAuthority).sorted().toList();return Jwts.builder().subject(user.getUsername()).claim("authorities",authorities).issuedAt(ahora).expiration(new Date(ahora.getTime()+getExpiracionSegundos()*1000)).signWith(clave).compact();}
 public Claims leerClaims(String token){return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();}
 public String extraerUsername(String token){return leerClaims(token).getSubject();}
 public long getExpiracionSegundos(){return expiracionMinutos*60;}
}
