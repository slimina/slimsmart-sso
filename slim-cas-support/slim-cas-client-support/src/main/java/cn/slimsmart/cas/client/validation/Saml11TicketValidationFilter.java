package cn.slimsmart.cas.client.validation;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.jasig.cas.client.validation.Saml11TicketValidator;
import org.jasig.cas.client.validation.TicketValidator;

public final class Saml11TicketValidationFilter extends AbstractTicketValidationFilter {

    public Saml11TicketValidationFilter() {
        setArtifactParameterName("SAMLart");
        setServiceParameterName("TARGET");
    }

    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        super.initInternal(filterConfig);

        log.warn("SAML1.1 compliance requires the [artifactParameterName] and [serviceParameterName] to be set to specified values.");
        log.warn("This filter will overwrite any user-provided values (if any are provided)");

        setArtifactParameterName("SAMLart");
        setServiceParameterName("TARGET");
    }

    protected final TicketValidator getTicketValidator(final FilterConfig filterConfig) {
    	final Saml11TicketValidator validator = new Saml11TicketValidator(getPropertyFromInitParams(filterConfig, "casServerUrlPrefix", null));
        final String tolerance = getPropertyFromInitParams(filterConfig, "tolerance", "1000");
        validator.setTolerance(Long.parseLong(tolerance));
        validator.setRenew(parseBoolean(getPropertyFromInitParams(filterConfig, "renew", "false")));
        validator.setHostnameVerifier(getHostnameVerifier(filterConfig));
        validator.setEncoding(getPropertyFromInitParams(filterConfig, "encoding", null));
        validator.setDisableXmlSchemaValidation(parseBoolean(getPropertyFromInitParams(filterConfig, "disableXmlSchemaValidation", "false")));
        return validator;
    }
}
