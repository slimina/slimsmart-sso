/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.inspektr.audit.annotation.Auditable;
import org.inspektr.common.ioc.annotation.NotNull;
import org.inspektr.statistics.annotation.Statistic;
import org.inspektr.statistics.annotation.Statistic.Precision;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.MutableAuthentication;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.PersistentIdGenerator;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.ShibbolethCompatiblePersistentIdGenerator;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.services.UnauthorizedProxyingException;
import org.jasig.cas.services.UnauthorizedServiceException;
import org.jasig.cas.services.UnauthorizedSsoServiceException;
import org.jasig.cas.ticket.ExpirationPolicy;
import org.jasig.cas.ticket.InvalidTicketException;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.TicketCreationException;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.jasig.cas.ticket.TicketValidationException;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.util.UniqueTicketIdGenerator;
import org.jasig.cas.validation.Assertion;
import org.jasig.cas.validation.ImmutableAssertionImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of a CentralAuthenticationService, and also the
 * central, organizing component of CAS's internal implementation.
 * <p>
 * This class is threadsafe.
 * <p>
 * This class has the following properties that must be set:
 * <ul>
 * <li> <code>ticketRegistry</code> - The Ticket Registry to maintain the list
 * of available tickets.</li>
 * <li> <code>serviceTicketRegistry</code> - Provides an alternative to configure separate registries for TGTs and ST in order to store them
 * in different locations (i.e. long term memory or short-term)</li>
 * <li> <code>authenticationManager</code> - The service that will handle
 * authentication.</li>
 * <li> <code>ticketGrantingTicketUniqueTicketIdGenerator</code> - Plug in to
 * generate unique secure ids for TicketGrantingTickets.</li>
 * <li> <code>serviceTicketUniqueTicketIdGenerator</code> - Plug in to
 * generate unique secure ids for ServiceTickets.</li>
 * <li> <code>ticketGrantingTicketExpirationPolicy</code> - The expiration
 * policy for TicketGrantingTickets.</li>
 * <li> <code>serviceTicketExpirationPolicy</code> - The expiration policy for
 * ServiceTickets.</li>
 * </ul>
 * 
 * @author William G. Thompson, Jr.
 * @author Scott Battaglia
 * @author Dmitry Kopylenko
 * @version $Revision: 1.16 $ $Date: 2007/04/24 18:11:36 $
 * @since 3.0
 */
public final class CentralAuthenticationServiceImpl implements
    CentralAuthenticationService {

    /** Log instance for logging events, info, warnings, errors, etc. */
    private final Log log = LogFactory.getLog(this.getClass());

    /** TicketRegistry for storing and retrieving tickets as needed. */
    @NotNull
    private TicketRegistry ticketRegistry;
    
    /** New Ticket Registry for storing and retrieving services tickets. Can point to the same one as the ticketRegistry variable. */
    @NotNull
    private TicketRegistry serviceTicketRegistry;

    /**
     * AuthenticationManager for authenticating credentials for purposes of
     * obtaining tickets.
     */
    @NotNull
    private AuthenticationManager authenticationManager;

    /**
     * UniqueTicketIdGenerator to generate ids for TicketGrantingTickets
     * created.
     */
    @NotNull
    private UniqueTicketIdGenerator ticketGrantingTicketUniqueTicketIdGenerator;

    /** Map to contain the mappings of service->UniqueTicketIdGenerators */
    @NotNull
    private Map<String, UniqueTicketIdGenerator> uniqueTicketIdGeneratorsForService;

    /** Expiration policy for ticket granting tickets. */
    @NotNull
    private ExpirationPolicy ticketGrantingTicketExpirationPolicy;

    /** ExpirationPolicy for Service Tickets. */
    @NotNull
    private ExpirationPolicy serviceTicketExpirationPolicy;

    /** Implementation of Service Manager */
    @NotNull
    private ServicesManager servicesManager;

    /** Encoder to generate PseudoIds. */
    @NotNull
    private PersistentIdGenerator persistentIdGenerator = new ShibbolethCompatiblePersistentIdGenerator();

    /**
     * Implementation of destoryTicketGrantingTicket expires the ticket provided
     * and removes it from the TicketRegistry.
     * 
     * @throws IllegalArgumentException if the TicketGrantingTicket ID is null.
     */
    @Auditable(action="TICKET_GRANTING_TICKET_DESTROYED",actionResolverClass=org.inspektr.audit.spi.support.DefaultAuditableActionResolver.class,resourceResolverClass=org.jasig.cas.audit.spi.TicketAsFirstParameterResourceResolver.class)
    @Statistic(name="DESTROY_TICKET_GRANTING_TICKET",requiredPrecision={Precision.DAY,Precision.MINUTE,Precision.HOUR})
    @Transactional(readOnly = false)
    public void destroyTicketGrantingTicket(final String ticketGrantingTicketId) {
        Assert.notNull(ticketGrantingTicketId);

        if (log.isDebugEnabled()) {
            log.debug("Removing ticket [" + ticketGrantingTicketId
                + "] from registry.");
        }
        final TicketGrantingTicket ticket = (TicketGrantingTicket) this.ticketRegistry
            .getTicket(ticketGrantingTicketId, TicketGrantingTicket.class);

        if (ticket == null) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Ticket found.  Expiring and then deleting.");
        }
        ticket.expire();
        this.ticketRegistry.deleteTicket(ticketGrantingTicketId);
    }

    /**
     * @throws IllegalArgumentException if TicketGrantingTicket ID, Credentials
     * or Service are null.
     */
    @Auditable(action="SERVICE_TICKET",successSuffix="_CREATED",failureSuffix="_NOT_CREATED",actionResolverClass=org.inspektr.audit.spi.support.ObjectCreationAuditableActionResolver.class,resourceResolverClass=org.jasig.cas.audit.spi.ServiceResourceResolver.class)
    @Statistic(name="GRANT_SERVICE_TICKET",requiredPrecision={Precision.DAY,Precision.MINUTE,Precision.HOUR})
    @Transactional(readOnly = false)
    public String grantServiceTicket(final String ticketGrantingTicketId,
        final Service service, final Credentials credentials)
        throws TicketException {

        Assert.notNull(ticketGrantingTicketId,
            "ticketGrantingticketId cannot be null");
        Assert.notNull(service, "service cannot be null");

        final TicketGrantingTicket ticketGrantingTicket;
        ticketGrantingTicket = (TicketGrantingTicket) this.ticketRegistry
            .getTicket(ticketGrantingTicketId, TicketGrantingTicket.class);

        if (ticketGrantingTicket == null) {
            throw new InvalidTicketException();
        }

        synchronized (ticketGrantingTicket) {
            if (ticketGrantingTicket.isExpired()) {
                this.ticketRegistry.deleteTicket(ticketGrantingTicketId);
                throw new InvalidTicketException();
            }
        }

        final RegisteredService registeredService = this.servicesManager
            .findServiceBy(service);

        if (registeredService == null || !registeredService.isEnabled()) {
            if (log.isDebugEnabled()) {
                log.debug("Service [" + service.getId()
                    + "] not found in ServiceRegistry.");
            }
            throw new UnauthorizedServiceException();
        }

        if (!registeredService.isSsoEnabled() && credentials == null
            && ticketGrantingTicket.getCountOfUses() > 0) {
            throw new UnauthorizedSsoServiceException();
        }

        if (credentials != null) {
            try {
                final Authentication authentication = this.authenticationManager
                    .authenticate(credentials);
                final Authentication originalAuthentication = ticketGrantingTicket.getAuthentication();

                if (!(authentication.getPrincipal().equals(originalAuthentication.getPrincipal()) && authentication.getAttributes().equals(originalAuthentication.getAttributes()))) {
                    throw new TicketCreationException();
                }
            } catch (final AuthenticationException e) {
                throw new TicketCreationException(e);
            }
        }

        // XXX fix this
        final UniqueTicketIdGenerator serviceTicketUniqueTicketIdGenerator = this.uniqueTicketIdGeneratorsForService
            .get(service.getClass().getName());

        final ServiceTicket serviceTicket = ticketGrantingTicket
            .grantServiceTicket(serviceTicketUniqueTicketIdGenerator
                .getNewTicketId(ServiceTicket.PREFIX), service,
                this.serviceTicketExpirationPolicy, credentials != null);

        this.serviceTicketRegistry.addTicket(serviceTicket);

        if (log.isInfoEnabled()) {
            log.info("Granted service ticket ["
                + serviceTicket.getId()
                + "] for service ["
                + service.getId()
                + "] for user ["
                + serviceTicket.getGrantingTicket().getAuthentication()
                    .getPrincipal().getId() + "]");
        }
        return serviceTicket.getId();
    }

    @Auditable(action="SERVICE_TICKET",successSuffix="_CREATED",failureSuffix="_NOT_CREATED",actionResolverClass=org.inspektr.audit.spi.support.ObjectCreationAuditableActionResolver.class,resourceResolverClass=org.jasig.cas.audit.spi.ServiceResourceResolver.class)
    @Statistic(name="GRANT_SERVICE_TICKET",requiredPrecision={Precision.DAY,Precision.MINUTE,Precision.HOUR})
    @Transactional(readOnly = false)
    public String grantServiceTicket(final String ticketGrantingTicketId,
        final Service service) throws TicketException {
        return this.grantServiceTicket(ticketGrantingTicketId, service, null);
    }

    /**
     * @throws IllegalArgumentException if the ServiceTicketId or the
     * Credentials are null.
     */
    @Auditable(action="PROXY_GRANTING_TICKET",successSuffix="_CREATED",failureSuffix="_NOT_CREATED",actionResolverClass=org.inspektr.audit.spi.support.ObjectCreationAuditableActionResolver.class,resourceResolverClass=org.inspektr.audit.spi.support.ReturnValueAsStringResourceResolver.class)
    @Statistic(name="GRANT_PROXY_TICKET",requiredPrecision={Precision.DAY,Precision.MINUTE,Precision.HOUR})
    @Transactional(readOnly = false)
    public String delegateTicketGrantingTicket(final String serviceTicketId,
        final Credentials credentials) throws TicketException {

        Assert.notNull(serviceTicketId, "serviceTicketId cannot be null");
        Assert.notNull(credentials, "credentials cannot be null");

        try {
            final Authentication authentication = this.authenticationManager
                .authenticate(credentials);

            final ServiceTicket serviceTicket;
            serviceTicket = (ServiceTicket) this.serviceTicketRegistry.getTicket(
                serviceTicketId, ServiceTicket.class);

            if (serviceTicket == null || serviceTicket.isExpired()) {
                throw new InvalidTicketException();
            }

            final RegisteredService registeredService = this.servicesManager
                .findServiceBy(serviceTicket.getService());

            if (registeredService == null || !registeredService.isEnabled()
                || !registeredService.isAllowedToProxy()) {
                throw new UnauthorizedProxyingException();
            }

            final TicketGrantingTicket ticketGrantingTicket = serviceTicket
                .grantTicketGrantingTicket(
                    this.ticketGrantingTicketUniqueTicketIdGenerator
                        .getNewTicketId(TicketGrantingTicket.PREFIX),
                    authentication, this.ticketGrantingTicketExpirationPolicy);

            this.ticketRegistry.addTicket(ticketGrantingTicket);

            return ticketGrantingTicket.getId();
        } catch (final AuthenticationException e) {
            throw new TicketCreationException(e);
        }
    }

    /**
     * @throws IllegalArgumentException if the ServiceTicketId or the Service
     * are null.
     */
    @Auditable(action="SERVICE_TICKET_VALIDATE",successSuffix="D",failureSuffix="_FAILED",actionResolverClass=org.inspektr.audit.spi.support.ObjectCreationAuditableActionResolver.class,resourceResolverClass=org.jasig.cas.audit.spi.TicketAsFirstParameterResourceResolver.class)
    @Statistic(name="SERVICE_TICKET_VALIDATE",requiredPrecision={Precision.DAY,Precision.MINUTE,Precision.HOUR})
    @Transactional(readOnly = false)
    public Assertion validateServiceTicket(final String serviceTicketId,
        final Service service) throws TicketException {
        Assert.notNull(serviceTicketId, "serviceTicketId cannot be null");
        Assert.notNull(service, "service cannot be null");

        final ServiceTicket serviceTicket = (ServiceTicket) this.serviceTicketRegistry
            .getTicket(serviceTicketId, ServiceTicket.class);

        final RegisteredService registeredService = this.servicesManager
            .findServiceBy(service);

        if (registeredService == null || !registeredService.isEnabled()) {
            throw new UnauthorizedServiceException(
                "Service not allowed to validate tickets.");
        }

        if (serviceTicket == null) {
            if (log.isDebugEnabled()) {
                log.debug("ServiceTicket [" + serviceTicketId
                    + "] does not exist.");
            }
            throw new InvalidTicketException();
        }

        try {
            synchronized (serviceTicket) {
                if (serviceTicket.isExpired()) {
                    if (log.isDebugEnabled()) {
                        log.debug("ServiceTicket [" + serviceTicketId
                            + "] has expired.");
                    }
                    throw new InvalidTicketException();
                }

                if (!serviceTicket.isValidFor(service)) {
                    if (log.isErrorEnabled()) {
                        log.error("ServiceTicket [" + serviceTicketId
                            + "] with service [" + serviceTicket.getService().getId() + " does not match supplied service [" + service + "]");
                    }
                    throw new TicketValidationException(serviceTicket.getService());
                }
            }

            final int authenticationChainSize = serviceTicket
                .getGrantingTicket().getChainedAuthentications().size();
            final Authentication authentication = serviceTicket
                .getGrantingTicket().getChainedAuthentications().get(
                    authenticationChainSize - 1);
            final Principal principal = authentication.getPrincipal();
            final String principalId = registeredService.isAnonymousAccess()
                ? this.persistentIdGenerator.generate(principal, serviceTicket
                    .getService()) : principal.getId();
                
            final Authentication authToUse;
            
            if (!registeredService.isIgnoreAttributes()) {
                final Map<String, Object> attributes = new HashMap<String, Object>();
    
                for (final String attribute : registeredService
                    .getAllowedAttributes()) {
                    final Object value = principal.getAttributes().get(
                        attribute);
    
                    if (value != null) {
                        attributes.put(attribute, value);
                    }
                }

                final Principal modifiedPrincipal = new SimplePrincipal(
                    principalId, attributes);
                final MutableAuthentication mutableAuthentication = new MutableAuthentication(
                    modifiedPrincipal, authentication.getAuthenticatedDate());
                mutableAuthentication.getAttributes().putAll(
                    authentication.getAttributes());
                mutableAuthentication.getAuthenticatedDate().setTime(
                    authentication.getAuthenticatedDate().getTime());
                authToUse = mutableAuthentication;
            } else {
                authToUse = authentication;
            }
            

            final List<Authentication> authentications = new ArrayList<Authentication>();

            for (int i = 0; i < authenticationChainSize - 1; i++) {
                authentications.add(serviceTicket.getGrantingTicket()
                    .getChainedAuthentications().get(i));
            }
            authentications.add(authToUse);

            return new ImmutableAssertionImpl(authentications, serviceTicket
                .getService(), serviceTicket.isFromNewLogin());
        } finally {
            if (serviceTicket.isExpired()) {
                this.serviceTicketRegistry.deleteTicket(serviceTicketId);
            }
        }
    }

    /**
     * @throws IllegalArgumentException if the credentials are null.
     */
    @Auditable(action="TICKET_GRANTING_TICKET",successSuffix="_CREATED",failureSuffix="_NOT_CREATED",actionResolverClass=org.inspektr.audit.spi.support.ObjectCreationAuditableActionResolver.class,resourceResolverClass=org.inspektr.audit.spi.support.ReturnValueAsStringResourceResolver.class)
    @Statistic(name="CREATE_TICKET_GRANTING_TICKET",requiredPrecision={Precision.DAY,Precision.MINUTE,Precision.HOUR})
    @Transactional(readOnly = false)
    public String createTicketGrantingTicket(final Credentials credentials)
        throws TicketCreationException {
        Assert.notNull(credentials, "credentials cannot be null");

        if (log.isDebugEnabled()) {
            log.debug("Attempting to create TicketGrantingTicket for "
                + credentials);
        }

        try {
            final Authentication authentication = this.authenticationManager
                .authenticate(credentials);

            final TicketGrantingTicket ticketGrantingTicket = new TicketGrantingTicketImpl(
                this.ticketGrantingTicketUniqueTicketIdGenerator
                    .getNewTicketId(TicketGrantingTicket.PREFIX),
                authentication, this.ticketGrantingTicketExpirationPolicy);

            this.ticketRegistry.addTicket(ticketGrantingTicket);
            return ticketGrantingTicket.getId();
        } catch (final AuthenticationException e) {
            throw new TicketCreationException(e);
        }
    }

    /**
     * Method to set the TicketRegistry.
     * 
     * @param ticketRegistry the TicketRegistry to set.
     */
    public void setTicketRegistry(final TicketRegistry ticketRegistry) {
        this.ticketRegistry = ticketRegistry;
        
        if (this.serviceTicketRegistry == null) {
            this.serviceTicketRegistry = ticketRegistry;
        }
    }
    
    public void setServiceTicketRegistry(final TicketRegistry serviceTicketRegistry) {
        this.serviceTicketRegistry = serviceTicketRegistry;
    }

    /**
     * Method to inject the AuthenticationManager into the class.
     * 
     * @param authenticationManager The authenticationManager to set.
     */
    public void setAuthenticationManager(
        final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Method to inject the TicketGrantingTicket Expiration Policy.
     * 
     * @param ticketGrantingTicketExpirationPolicy The
     * ticketGrantingTicketExpirationPolicy to set.
     */
    public void setTicketGrantingTicketExpirationPolicy(
        final ExpirationPolicy ticketGrantingTicketExpirationPolicy) {
        this.ticketGrantingTicketExpirationPolicy = ticketGrantingTicketExpirationPolicy;
    }

    /**
     * Method to inject the Unique Ticket Id Generator into the class.
     * 
     * @param uniqueTicketIdGenerator The uniqueTicketIdGenerator to use
     */
    public void setTicketGrantingTicketUniqueTicketIdGenerator(
        final UniqueTicketIdGenerator uniqueTicketIdGenerator) {
        this.ticketGrantingTicketUniqueTicketIdGenerator = uniqueTicketIdGenerator;
    }

    /**
     * Method to inject the TicketGrantingTicket Expiration Policy.
     * 
     * @param serviceTicketExpirationPolicy The serviceTicketExpirationPolicy to
     * set.
     */
    public void setServiceTicketExpirationPolicy(
        final ExpirationPolicy serviceTicketExpirationPolicy) {
        this.serviceTicketExpirationPolicy = serviceTicketExpirationPolicy;
    }

    public void setUniqueTicketIdGeneratorsForService(
        final Map<String, UniqueTicketIdGenerator> uniqueTicketIdGeneratorsForService) {
        this.uniqueTicketIdGeneratorsForService = uniqueTicketIdGeneratorsForService;
    }

    public void setServicesManager(final ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    public void setPersistentIdGenerator(
        final PersistentIdGenerator persistentIdGenerator) {
        this.persistentIdGenerator = persistentIdGenerator;
    }
}
