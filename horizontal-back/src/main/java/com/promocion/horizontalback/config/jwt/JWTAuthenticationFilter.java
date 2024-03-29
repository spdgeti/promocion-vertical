package com.promocion.horizontalback.config.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends OncePerRequestFilter {


    private UserDetailsService userDetailsService;
    private JwtTokenHelper jwtTokenHelper;



    public JWTAuthenticationFilter(UserDetailsService userDetailsService,JwtTokenHelper jwtTokenHelper) {
        this.userDetailsService=userDetailsService;
        this.jwtTokenHelper=jwtTokenHelper;

    }

    @Override @Transactional
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain ) throws ServletException, IOException {
        String authToken=jwtTokenHelper.getToken(request);

        if(null!=authToken) {

            String userName=jwtTokenHelper.getUsernameFromToken(authToken);

            if(null!=userName) {

                UserDetails userDetails=userDetailsService.loadUserByUsername(userName);

                StatusAuthenticationError(response,userDetails);
                if(jwtTokenHelper.validateToken(authToken, userDetails)) {

                    UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }

            }

        }

        filterChain.doFilter(request, response);
    }


    public  void StatusAuthenticationError(  HttpServletResponse response, UserDetails userDetails) throws IOException {
        String message="";
        if (!userDetails.isAccountNonExpired()){
            message="La cuenta del usuario ha caducado";
        }
        else if (!userDetails.isAccountNonLocked()){
            message="Cuenta bloequeada";
        }
        else if (!userDetails.isCredentialsNonExpired()){
            message="La cuenta de usuario ha caducado";
        }
        else if (!userDetails.isEnabled()){
            message="La cuenta esta desabilitada";
        }
        if (!message.equals("")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
        }

    }

}
