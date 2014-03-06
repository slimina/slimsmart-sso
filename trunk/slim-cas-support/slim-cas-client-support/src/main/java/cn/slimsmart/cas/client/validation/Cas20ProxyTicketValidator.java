package cn.slimsmart.cas.client.validation;

import java.util.List;

import org.jasig.cas.client.util.XmlUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.InvalidProxyChainTicketValidationException;
import org.jasig.cas.client.validation.ProxyList;
import org.jasig.cas.client.validation.TicketValidationException;

public class Cas20ProxyTicketValidator extends Cas20ServiceTicketValidator {

    private boolean acceptAnyProxy;

    /** This should be a list of an array of Strings */
    private ProxyList allowedProxyChains = new ProxyList();

    public Cas20ProxyTicketValidator(final String casServerUrlPrefix) {
        super(casServerUrlPrefix);
    }

    public ProxyList getAllowedProxyChains() {
        return this.allowedProxyChains;
    }

    protected String getUrlSuffix() {
        return "proxyValidate";
    }

    protected void customParseResponse(final String response, final Assertion assertion) throws TicketValidationException {
        final List<String> proxies = XmlUtils.getTextForElements(response, "proxy");
        final String[] proxiedList = proxies.toArray(new String[proxies.size()]);

        // this means there was nothing in the proxy chain, which is okay
        if (proxies.isEmpty() || this.acceptAnyProxy) {
            return;
        }

        if (this.allowedProxyChains.contains(proxiedList)) {
            return;
        }

        throw new InvalidProxyChainTicketValidationException("Invalid proxy chain: " + proxies.toString());
    }

    public void setAcceptAnyProxy(final boolean acceptAnyProxy) {
        this.acceptAnyProxy = acceptAnyProxy;
    }

    public void setAllowedProxyChains(final ProxyList allowedProxyChains) {
        this.allowedProxyChains = allowedProxyChains;
    }
}
