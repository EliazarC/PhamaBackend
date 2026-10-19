package pe.edu.upeu.PharmaBackend.security;
import io.jsonwebtoken.ExpiredJwtException; import io.jsonwebtoken.JwtException; import jakarta.servlet.*; import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.security.core.userdetails.UserDetails; import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@Component public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final JwtService jwt; private final UsuarioDetailsServiceImpl users;
 public JwtAuthenticationFilter(JwtService jwt,UsuarioDetailsServiceImpl users){this.jwt=jwt;this.users=users;}
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException{
 String header=request.getHeader("Authorization"); if(header==null||!header.startsWith("Bearer ")){chain.doFilter(request,response);return;}
 String token=header.substring(7);try{String username=jwt.extraerUsername(token);if(SecurityContextHolder.getContext().getAuthentication()==null){UserDetails user=users.loadUserByUsername(username);UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));SecurityContextHolder.getContext().setAuthentication(auth);}}
 catch(ExpiredJwtException e){request.setAttribute("security.error","El token ha expirado. Inicie sesión nuevamente");}
 catch(JwtException|IllegalArgumentException e){request.setAttribute("security.error","El token no es válido");}
 catch(org.springframework.security.core.userdetails.UsernameNotFoundException e){request.setAttribute("security.error","El token no es válido");}
 if(request.getAttribute("security.error")!=null){response.setStatus(401);response.setContentType("application/json");response.setCharacterEncoding("UTF-8");response.getWriter().write("{\"timestamp\":\""+java.time.LocalDateTime.now()+"\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\""+request.getAttribute("security.error")+"\",\"path\":\""+request.getRequestURI()+"\"}");return;}chain.doFilter(request,response);
 }
}
