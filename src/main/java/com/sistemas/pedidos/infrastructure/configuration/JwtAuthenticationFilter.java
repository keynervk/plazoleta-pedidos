package com.sistemas.pedidos.infrastructure.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAdapter jwtAdapter;

    public JwtAuthenticationFilter(JwtAdapter jwtAdapter) {
        this.jwtAdapter = jwtAdapter;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtAdapter.validarYObtenerClaims(token);
                String rol = claims.get("rol", String.class);

                if (claims.get("id") instanceof Number id && rol != null) {
                    var usuario = new UsuarioAutenticado(id.longValue(), claims.getSubject(), rol);
                    var authority = new SimpleGrantedAuthority("ROLE_" + rol);
                    // El token se guarda como credencial para reenviarlo a Usuarios y Plazoleta
                    var authentication = new UsernamePasswordAuthenticationToken(
                            usuario, token, List.of(authority));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}