package pe.edu.upeu.PharmaBackend.security;
import org.springframework.security.core.GrantedAuthority; import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pe.edu.upeu.PharmaBackend.entity.Rol; import pe.edu.upeu.PharmaBackend.enums.Permiso;
import java.util.*;
public final class PermisosPorRol {
 private static final Map<String,Set<Permiso>> MAP=Map.of(
 "ADMIN",Collections.unmodifiableSet(EnumSet.allOf(Permiso.class)),
 "VENDEDOR",Collections.unmodifiableSet(EnumSet.of(Permiso.CATALOGO_LEER,Permiso.CLIENTES_LEER,Permiso.CLIENTES_ESCRIBIR,Permiso.VENTAS_LEER,Permiso.VENTAS_REGISTRAR)),
 "ALMACENERO",Collections.unmodifiableSet(EnumSet.of(Permiso.CATALOGO_LEER,Permiso.CATALOGO_ESCRIBIR)));
 private PermisosPorRol(){}
 public static Set<Permiso> de(String nombre){return MAP.getOrDefault(nombre,Set.of());}
 public static List<String> permisos(Collection<Rol> roles){Set<String> out=new TreeSet<>();roles.forEach(r->de(r.getNombre()).forEach(p->out.add(p.name())));return new ArrayList<>(out);}
 public static List<GrantedAuthority> authorities(Collection<Rol> roles){List<GrantedAuthority> out=new ArrayList<>();roles.stream().map(Rol::getNombre).distinct().sorted().forEach(n->out.add(new SimpleGrantedAuthority("ROLE_"+n)));permisos(roles).forEach(p->out.add(new SimpleGrantedAuthority(p)));return out;}
}
