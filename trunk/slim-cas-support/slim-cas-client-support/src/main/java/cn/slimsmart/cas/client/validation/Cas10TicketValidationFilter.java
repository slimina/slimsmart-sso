package cn.slimsmart.cas.client.validation;

import javax.servlet.FilterConfig;

import org.jasig.cas.client.validation.Cas10TicketValidator;
import org.jasig.cas.client.validation.TicketValidator;

public class Cas10TicketValidationFilter extends AbstractTicketValidationFilter {

    protected final TicketValidator getTicketValidator(final FilterConfig filterConfig) {
        final String casServerUrlPrefix = getPropertyFromInitParams(filterConfig, "casServerUrlPrefix", null);
        final Cas10TicketValidator validator = new Cas10TicketValidator(casServerUrlPrefix);
        validator.setRenew(parseBoolean(getPropertyFromInitParams(filterConfig, "renew", "false")));
        validator.setHostnameVerifier(getHostnameVerifier(filterConfig));
        validator.setEncoding(getPropertyFromInitParams(filterConfig, "encoding", null));

        return validator;
    }
}
