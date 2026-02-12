package jpa.config;

import jpa.controllers.*;
import jpa.dao.abstracts.*;
import jpa.dao.impl.*;
import jpa.services.impl.*;
import jpa.services.interfaces.*;

public final class Instance {
    // Admin
    public static final AdminDao ADMIN_DAO = new AdminDaoImpl();
    public static final AdminService ADMIN_SERVICE = new AdminServiceImpl(ADMIN_DAO);
    public static final AdminController ADMIN_CONTROLLER = new AdminController(ADMIN_SERVICE);

    // Concert
    public static final ConcertDao CONCERT_DAO = new ConcertDaoImpl();
    public static final ConcertService CONCERT_SERVICE = new ConcertServiceImpl(CONCERT_DAO);
    public static final ConcertController CONCERT_CONTROLLER = new ConcertController(CONCERT_SERVICE);

    // Customer
    public static final CustomerDao CUSTOMER_DAO = new CustomerDaoImpl();
    public static final CustomerService CUSTOMER_SERVICE = new CustomerServiceImpl(CUSTOMER_DAO);
    public static final CustomerController CUSTOMER_CONTROLLER = new CustomerController(CUSTOMER_SERVICE);

    // Organizer
    public static final OrganizerDao ORGANIZER_DAO = new OrganizerDaoImpl();
    public static final OrganizerService ORGANIZER_SERVICE = new OrganizerServiceImpl(ORGANIZER_DAO);
    public static final OrganizerController ORGANIZER_CONTROLLER = new OrganizerController(ORGANIZER_SERVICE);

    // Place
    public static final PlaceDao PLACE_DAO = new PlaceDaoImpl();
    public static final PlaceService PLACE_SERVICE = new PlaceServiceImpl(PLACE_DAO);
    public static final PlaceController PLACE_CONTROLLER = new PlaceController(PLACE_SERVICE);

    // Ticket
    public static final TicketDao TICKET_DAO = new TicketDaoImpl();
    public static final TicketService TICKET_SERVICE = new TicketServiceImpl(TICKET_DAO);
    public static final TicketController TICKET_CONTROLLER = new TicketController(TICKET_SERVICE);

    private Instance() {}
}
