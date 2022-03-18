package guru.sfg.brewery.config.security;

import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class RestUrlAuthFilter extends AbstractRestAuthFilter{
    public RestUrlAuthFilter(RequestMatcher requiresAuthRequestMatcher) {
        super(requiresAuthRequestMatcher);
    }

    @Override
    protected String getPassword(HttpServletRequest request) {
        return request.getHeader("apiSecret");
    }

    @Override
    protected String getUsername(HttpServletRequest request) {
        return request.getParameter("apiKey");
    }
}
