package org.vk.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.vk.backend.entity.user.User;
import org.vk.backend.service.CustomUserDetailsService;

import java.io.IOException;
import java.util.Collections;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    public static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJWTFromReq(request);
            if(StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)){
                Long userId = jwtProvider.getUserIdFromToken(jwt);
                User userDetails = userDetailsService.loadUserById(userId);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, Collections.emptyList()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }catch (Exception exception){
            LOG.error("Could not set user authentication!");
        }

        filterChain.doFilter(request, response);
    }

    public String getJWTFromReq(HttpServletRequest request){
        String tkn = request.getHeader(SecurityConst.HEADER_STRING);
        if(StringUtils.hasText(tkn) && tkn.startsWith(SecurityConst.TOKEN_PREFIX)){
            return tkn.split(" ")[1];
        }
        return null;
    }
}
