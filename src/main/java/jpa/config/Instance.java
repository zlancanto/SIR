package jpa.config;

import jpa.dao.abstracts.*;
import jpa.dao.impl.*;
import jpa.security.impl.AccessTokenServiceImpl;
import jpa.security.interfaces.AccessTokenService;
import jpa.services.impl.*;
import jpa.services.interfaces.*;

/**
 * Configuration component Instance.
 */
public final class Instance {
    // DAO
    public static final AdminDao ADMIN_DAO = new AdminDaoImpl();
    public static final OrganizerDao ORGANIZER_DAO = new OrganizerDaoImpl();
    public static final CustomerDao CUSTOMER_DAO = new CustomerDaoImpl();
    public static final PlaceDao PLACE_DAO = new PlaceDaoImpl();
    public static final TicketDao TICKET_DAO = new TicketDaoImpl();
    public static final UserDao USER_DAO = new UserDaoImpl();
    public static final ConcertDao CONCERT_DAO = new ConcertDaoImpl();
    public static final RefreshTokenDao REFRESH_TOKEN_DAO = new RefreshTokenDaoImpl();

    // SECURITY
    public static final AccessTokenService ACCESS_TOKEN_SERVICE = new AccessTokenServiceImpl(
            AuthConfig.resolveJwtSigningKey(),
            AuthConfig.resolveAccessTokenTtlSeconds(),
            AuthConfig.resolveRefreshTokenTtlSeconds()
    );

    // SERVICES
    public static final AdminService ADMIN_SERVICE = new AdminServiceImpl(ADMIN_DAO);
    public static final OrganizerService ORGANIZER_SERVICE = new OrganizerServiceImpl(ORGANIZER_DAO);
    public static final PlaceService PLACE_SERVICE = new PlaceServiceImpl(PLACE_DAO);
    public static final TicketService TICKET_SERVICE = new TicketServiceImpl(
            TICKET_DAO,
            CONCERT_DAO,
            CUSTOMER_DAO
    );
    public static final UserRegistrationService USER_REGISTRATION_SERVICE = new UserRegistrationServiceImpl(USER_DAO);
    public static final ConcertService CONCERT_SERVICE = new ConcertServiceImpl(
            CONCERT_DAO,
            ORGANIZER_DAO,
            PLACE_DAO,
            ADMIN_DAO,
            USER_DAO,
            TicketConfig.resolveMaxTicketBatchSize()
    );
    public static final AuthService AUTH_SERVICE = new AuthServiceImpl(USER_DAO, REFRESH_TOKEN_DAO, ACCESS_TOKEN_SERVICE);

    private Instance() {}
}
