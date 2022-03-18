package guru.sfg.brewery.domain.security.google;

import guru.sfg.brewery.domain.security.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Request;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class Google2faFilter extends GenericFilterBean {

    private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();
    private final Google2faFailureHandler google2faFailureHandler = new Google2faFailureHandler();
    private final RequestMatcher urlIs2fa = new AntPathRequestMatcher("/user/verify2fa");
    private final RequestMatcher urlResource = new AntPathRequestMatcher("/resources/**");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse resp= (HttpServletResponse) response;

        StaticResourceRequest.StaticResourceRequestMatcher staticResourceRequestMatcher = PathRequest.toStaticResources().atCommonLocations();
        if(urlIs2fa.matches(req) || urlResource.matches(req) || staticResourceRequestMatcher.matcher(req).isMatch()){
            chain.doFilter(req,resp);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && !authenticationTrustResolver.isAnonymous(authentication)){
            log.debug("Processing 2fa Filter");
            if(authentication.getPrincipal() != null && authentication.getPrincipal() instanceof User){
                User user = (User) authentication.getPrincipal();
                if(user.getUseGoogle2f() && user.getGoogle2faRequired()){
                    log.debug("2fa required");
                    google2faFailureHandler.onAuthenticationFailure(req, resp, null);
                }
            }
        }
        chain.doFilter(req, resp);
    }
}
