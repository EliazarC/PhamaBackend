
package pe.edu.upeu.PharmaBackend.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import pe.edu.upeu.PharmaBackend.dto.auth.*;
import pe.edu.upeu.PharmaBackend.dto.usuario.*;
import pe.edu.upeu.PharmaBackend.entity.*;
import pe.edu.upeu.PharmaBackend.repository.*;
import pe.edu.upeu.PharmaBackend.security.JwtService;
import pe.edu.upeu.PharmaBackend.security.PermisosPorRol;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SeguridadService {

 private final AuthenticationManager authManager;
 private final UsuarioRepository usuarios;
 private final EmpleadoRepository empleados;
 private final RolRepository roles;
 private final PasswordEncoder encoder;
 private final JwtService jwt;

 public SeguridadService(
         AuthenticationManager am,
         UsuarioRepository u,
         EmpleadoRepository e,
         RolRepository r,
         PasswordEncoder p,
         JwtService j) {

  this.authManager = am;
  this.usuarios = u;
  this.empleados = e;
  this.roles = r;
  this.encoder = p;
  this.jwt = j;
 }

 // Iniciar sesión y generar el token JWT
 @Transactional(readOnly = true)
 public LoginResponseDTO login(LoginRequestDTO req) {

  String username = req.getUsername().trim();

  try {
   Authentication autenticacion = authManager.authenticate(
           new UsernamePasswordAuthenticationToken(
                   username,
                   req.getPassword()
           )
   );

   UserDetails details =
           (UserDetails) autenticacion.getPrincipal();

   Usuario u = usuarios.findByUsernameIgnoreCase(username)
           .orElseThrow(() ->
                   new BadCredentialsException(
                           "Usuario o contraseña incorrectos"
                   )
           );

   return new LoginResponseDTO(
           jwt.generarToken(details),
           "Bearer",
           jwt.getExpiracionSegundos(),
           u.getUsername(),
           u.getEmpleado().getNombreCompleto(),
           u.getRoles().stream()
                   .map(Rol::getNombre)
                   .sorted()
                   .toList(),
           PermisosPorRol.permisos(u.getRoles())
   );

  } catch (AuthenticationException ex) {
   throw new BadCredentialsException(
           "Usuario o contraseña incorrectos"
   );
  }
 }

 // Consultar los datos del usuario autenticado
 @Transactional(readOnly = true)
 public UsuarioResponseDTO me(String username) {

  Usuario u = usuarios.findByUsernameIgnoreCase(username)
          .orElseThrow(() ->
                  new BadCredentialsException(
                          "Usuario o contraseña incorrectos"
                  )
          );

  return UsuarioResponseDTO.from(u);
 }

 // Cambiar la contraseña del usuario autenticado
 @Transactional
 public void cambiarPassword(
         String username,
         CambioPasswordDTO dto) {

  Usuario u = usuarios.findByUsernameIgnoreCase(username)
          .orElseThrow(() ->
                  new BadCredentialsException(
                          "Usuario o contraseña incorrectos"
                  )
          );

  if (!encoder.matches(
          dto.passwordActual(),
          u.getPassword())) {

   throw new BadCredentialsException(
           "La contraseña actual es incorrecta"
   );
  }

  u.setPassword(
          encoder.encode(dto.passwordNueva())
  );
 }

 // Crear un usuario y asignarle roles
 @Transactional
 public UsuarioResponseDTO crear(CrearUsuarioDTO dto) {

  if (usuarios.existsByUsernameIgnoreCase(dto.username())) {
   throw new ResponseStatusException(
           HttpStatus.CONFLICT,
           "El username ya está registrado"
   );
  }

  Empleado empleado = empleados.findById(dto.empleadoId())
          .orElseThrow(() ->
                  new ResponseStatusException(
                          HttpStatus.NOT_FOUND,
                          "Empleado no encontrado: "
                                  + dto.empleadoId()
                  )
          );

  if (usuarios.existsByEmpleadoId(dto.empleadoId())) {
   throw new ResponseStatusException(
           HttpStatus.CONFLICT,
           "El empleado " + dto.empleadoId()
                   + " ya tiene un usuario"
   );
  }

  List<Rol> encontrados =
          roles.findAllById(dto.rolIds());

  Set<Long> idsEncontrados = new HashSet<>();

  encontrados.forEach(rol ->
          idsEncontrados.add(rol.getId())
  );

  List<Long> faltantes = dto.rolIds().stream()
          .filter(id -> !idsEncontrados.contains(id))
          .distinct()
          .toList();

  if (!faltantes.isEmpty()) {
   throw new ResponseStatusException(
           HttpStatus.NOT_FOUND,
           "Roles no encontrados: " + faltantes
   );
  }

  Usuario u = new Usuario();
  u.setUsername(dto.username().trim());
  u.setPassword(encoder.encode(dto.password()));
  u.setEmpleado(empleado);
  u.setEstado(true);
  u.setRoles(new HashSet<>(encontrados));

  Usuario guardado = usuarios.save(u);

  return UsuarioResponseDTO.from(guardado);
 }

 // Listar usuarios
 @Transactional(readOnly = true)
 public List<UsuarioResponseDTO> listarUsuarios() {

  return usuarios.findAll().stream()
          .map(UsuarioResponseDTO::from)
          .toList();
 }

 // Listar empleados e indicar si ya tienen una cuenta
 @Transactional(readOnly = true)
 public List<EmpleadoResponseDTO> listarEmpleados() {

  return empleados.findAll().stream()
          .map(e -> EmpleadoResponseDTO.from(
                  e,
                  usuarios.existsByEmpleadoId(e.getId())
          ))
          .toList();
 }

 @Transactional(readOnly = true)
 public List<RolResponseDTO> listarRoles() {

  return roles.findAll().stream()
          .sorted(Comparator.comparing(Rol::getNombre))
          .map(RolResponseDTO::from)
          .toList();
 }
}
