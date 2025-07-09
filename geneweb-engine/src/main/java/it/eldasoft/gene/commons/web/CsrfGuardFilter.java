package it.eldasoft.gene.commons.web;


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.owasp.csrfguard.CsrfGuard;

/**
 * Override della classe ufficiale al solo fine di skippare, per le sole richieste multipart, la gestione dei controlli eseguiti dal filtro.
 */
public class CsrfGuardFilter extends org.owasp.csrfguard.CsrfGuardFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        CsrfGuard csrfGuard = CsrfGuard.getInstance();
        if (csrfGuard.isEnabled()) {
            String contentType = request.getContentType();
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                // per le sole richieste multipart si sposta la validazione del token a valle del processo, nel GeneRequestProcessor
                filterChain.doFilter(request, response);
            } else {
                // le richieste non multipart vengono controllate, come previsto dalla classe estesa, nel filtro stesso (quindi richiamo il padre)
                super.doFilter(request, response, filterChain);
            }
        } else {
            filterChain.doFilter(request, response);
        }

    }
}