package guru.sfg.brewery.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public abstract class AbstractRestAuthFilter extends AbstractAuthenticationProcessingFilter {

    public AbstractRestAuthFilter(RequestMatcher requiresAuthRequestMatcher){
        super(requiresAuthRequestMatcher);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse resp= (HttpServletResponse) response;

        if(log.isDebugEnabled()){
            log.debug("Request is to process authentication...");
        }
        try {
            Authentication authenticationResult = this.attemptAuthentication(req, resp);
            if (authenticationResult != null) {
                successfulAuthentication(req, resp, chain, authenticationResult);
            } else {
                chain.doFilter(req, resp);
            }
        }catch (AuthenticationException ex){
            log.error("Authentication Failed...", ex);
            unsuccessfulAuthentication(req, resp, ex);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        if(log.isDebugEnabled()) {
            this.log.trace("Failed to process authentication request", failed);
            this.log.trace("Cleared SecurityContextHolder");
            this.log.trace("Handling authentication failure");
        }
        response.sendError(HttpStatus.UNAUTHORIZED.value(),HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        if (this.logger.isDebugEnabled()) {
            this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authResult));
        }
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException{
        String username = getUsername(request);
        String password = getPassword(request);

        if(username == null)username = "";
        if(password == null)password = "";

        log.debug("Authenticating user: "+username);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,password);
        if(StringUtils.hasText(username)){
            return this.getAuthenticationManager().authenticate(token);
        }else{
            return null;
        }
    }

    protected abstract String getPassword(HttpServletRequest request);
    protected abstract String getUsername(HttpServletRequest request);

}
