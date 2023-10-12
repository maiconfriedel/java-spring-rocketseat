package br.com.maiconfriedel.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.maiconfriedel.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var servletPath = request.getServletPath();

    if (servletPath.equals("/users/") || servletPath.startsWith("/h2-console")) {
      filterChain.doFilter(request, response);
    } else {

      var authorization = request.getHeader("Authorization");

      if (authorization == null) {
        response.sendError(401, "Unauthorized");
      } else {
        authorization = authorization.substring("Basic".length()).trim();

        byte[] decoded = Base64.getDecoder().decode(authorization);
        String user = new String(decoded);

        String[] credentials = user.split(":");
        String username = credentials[0];
        String password = credentials[1];

        var existingUser = this.userRepository.findByUsername(username);

        if (existingUser == null) {
          response.sendError(401, "Unauthorized");
        } else {
          var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), existingUser.getPassword());

          if (passwordVerify.verified) {
            request.setAttribute("userId", existingUser.getId());
            filterChain.doFilter(request, response);
          } else {
            response.sendError(401, "Unauthorized");
          }
        }
      }
    }
  }
}
