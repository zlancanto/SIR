package jpa.config;

import jpa.dao.abstracts.AdminDao;
import jpa.dao.abstracts.ConcertDao;
import jpa.dao.abstracts.OrganizerDao;
import jpa.dao.abstracts.PlaceDao;
import jpa.dao.impl.AdminDaoImpl;
import jpa.dao.impl.ConcertDaoImpl;
import jpa.dao.impl.OrganizerDaoImpl;
import jpa.dao.impl.PlaceDaoImpl;
import jpa.entities.Admin;
import jpa.entities.Concert;
import jpa.entities.Organizer;
import jpa.entities.Place;
import jpa.entities.Ticket;
import jpa.enums.ConcertStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Initializes immutable reference data used by the application.
 */
public final class ReferenceDataInitializer {

    private static final Logger logger = Logger.getLogger(ReferenceDataInitializer.class.getName());
    private static final int SEED_CONCERT_COUNT = 20;
    private static final String SEED_ORGANIZER_EMAIL = "seed.organizer@sir.local";
    private static final String SEED_ADMIN_EMAIL = "seed.admin@sir.local";
    private static final String SEED_DEFAULT_PASSWORD = "12345678";

    private ReferenceDataInitializer() {}

    /**
     * Inserts the default French venues on first initialization only.
     *
     * <p>If at least one place already exists, no insertion is performed.</p>
     */
    public static void seedPlacesIfEmpty() {
        PlaceDao placeDao = new PlaceDaoImpl();
        if (!placeDao.findAll().isEmpty()) {
            logger.info("Reference place seeding skipped: table already contains data.");
            return;
        }

        List<PlaceSeed> seeds = List.of(
                new PlaceSeed("Accor Arena", "8 Boulevard de Bercy", 75012, "Paris", 20300),
                new PlaceSeed("Stade de France", "ZAC du Cornillon Nord", 93216, "Saint-Denis", 81338),
                new PlaceSeed("L Olympia Bruno Coquatrix", "28 Boulevard des Capucines", 75009, "Paris", 1996),
                new PlaceSeed("Zenith Paris La Villette", "211 Avenue Jean Jaures", 75019, "Paris", 6293),
                new PlaceSeed("Paris La Defense Arena", "99 Jardins de l Arche", 92000, "Nanterre", 40000),
                new PlaceSeed("Le Bataclan", "50 Boulevard Voltaire", 75011, "Paris", 1500),
                new PlaceSeed("La Cigale", "120 Boulevard de Rochechouart", 75018, "Paris", 1389),
                new PlaceSeed("Le Trianon", "80 Boulevard de Rochechouart", 75018, "Paris", 1091),
                new PlaceSeed("Philharmonie de Paris", "221 Avenue Jean Jaures", 75019, "Paris", 2400),
                new PlaceSeed("Salle Pleyel", "252 Rue du Faubourg Saint-Honore", 75008, "Paris", 1913),
                new PlaceSeed("Halle Tony Garnier", "20 Place des Docteurs Merieux", 69007, "Lyon", 17000),
                new PlaceSeed("Le Transbordeur", "3 Boulevard de Stalingrad", 69100, "Villeurbanne", 1800),
                new PlaceSeed("Zenith de Lille", "1 Boulevard des Cites Unies", 59777, "Lille", 7000),
                new PlaceSeed("Arkea Arena", "48-50 Avenue Jean Alfonsa", 33270, "Floirac", 11300),
                new PlaceSeed("Le Dome de Marseille", "48 Avenue de Saint-Just", 13004, "Marseille", 8500),
                new PlaceSeed("Le Silo", "35 Quai du Lazaret", 13002, "Marseille", 2350),
                new PlaceSeed("Zenith de Toulouse", "11 Avenue Raymond Badiou", 31300, "Toulouse", 9000),
                new PlaceSeed("Zenith Nantes Metropole", "Boulevard du Zenith", 44800, "Saint-Herblain", 9000),
                new PlaceSeed("Zenith de Strasbourg Europe", "1 Allee du Zenith", 67201, "Eckbolsheim", 12079),
                new PlaceSeed("Zenith d Auvergne", "24 Allee de Cournon", 63800, "Cournon-d-Auvergne", 9000)
        );

        for (PlaceSeed seed : seeds) {
            Place place = new Place();
            place.setName(seed.name());
            place.setAddress(seed.address());
            place.setZipCode(seed.zipCode());
            place.setCity(seed.city());
            place.setCapacity(seed.capacity());
            placeDao.save(place);
        }

        logger.info(() -> "Reference place seeding completed: " + seeds.size() + " places inserted.");
    }

    /**
     * Inserts the default concerts on first initialization only.
     *
     * <p>If at least one concert already exists, no insertion is performed.</p>
     */
    public static void seedConcertsIfEmpty() {
        ConcertDao concertDao = new ConcertDaoImpl();
        if (!concertDao.findAll().isEmpty()) {
            logger.info("Reference concert seeding skipped: table already contains data.");
            return;
        }

        PlaceDao placeDao = new PlaceDaoImpl();
        List<Place> places = placeDao.findAll();
        if (places.isEmpty()) {
            logger.warning("Reference concert seeding skipped: no places available.");
            return;
        }

        Organizer organizer = resolveSeedOrganizer();
        Admin admin = resolveSeedAdmin();
        List<ConcertSeed> seeds = defaultConcertSeeds();
        Instant firstConcertDate = Instant.now()
                .plus(14, ChronoUnit.DAYS)
                .truncatedTo(ChronoUnit.MINUTES);

        for (int i = 0; i < SEED_CONCERT_COUNT; i++) {
            ConcertSeed seed = seeds.get(i);
            Place place = places.get(i % places.size());

            Concert concert = new Concert();
            concert.setTitle(seed.title());
            concert.setArtist(seed.artist());
            concert.setDate(firstConcertDate.plus(i, ChronoUnit.DAYS));
            concert.setStatus(ConcertStatus.PUBLISHED);
            concert.setOrganizer(organizer);
            concert.setAdmin(admin);
            concert.setPlace(place);
            concert.setTickets(createSeedTickets(
                    concert,
                    resolveTicketQuantity(place.getCapacity()),
                    seed.ticketUnitPrice()
            ));

            concertDao.save(concert);
        }

        logger.info(() -> "Reference concert seeding completed: " + SEED_CONCERT_COUNT + " concerts inserted.");
    }

    private static Organizer resolveSeedOrganizer() {
        OrganizerDao organizerDao = new OrganizerDaoImpl();
        List<Organizer> organizers = organizerDao.findAll();
        if (!organizers.isEmpty()) {
            return organizers.get(0);
        }

        Organizer organizer = new Organizer();
        organizer.setEmail(SEED_ORGANIZER_EMAIL);
        organizer.setPassword(SEED_DEFAULT_PASSWORD);
        organizer.setFirstName("Seed");
        organizer.setLastName("Organizer");
        organizerDao.save(organizer);
        return organizer;
    }

    private static Admin resolveSeedAdmin() {
        AdminDao adminDao = new AdminDaoImpl();
        List<Admin> admins = adminDao.findAll();
        if (!admins.isEmpty()) {
            return admins.get(0);
        }

        Admin admin = new Admin();
        admin.setEmail(SEED_ADMIN_EMAIL);
        admin.setPassword(SEED_DEFAULT_PASSWORD);
        admin.setFirstName("Seed");
        admin.setLastName("Admin");
        adminDao.save(admin);
        return admin;
    }

    private static int resolveTicketQuantity(Integer placeCapacity) {
        if (placeCapacity == null || placeCapacity <= 0) {
            return 200;
        }
        int suggested = placeCapacity / 8;
        return Math.max(100, Math.min(500, suggested));
    }

    private static List<Ticket> createSeedTickets(Concert concert, int quantity, BigDecimal unitPrice) {
        List<Ticket> tickets = new ArrayList<>(quantity);
        for (int i = 0; i < quantity; i++) {
            Ticket ticket = new Ticket();
            ticket.setConcert(concert);
            ticket.setPrice(unitPrice);
            ticket.setBarcode(generateBarcode());
            tickets.add(ticket);
        }
        return tickets;
    }

    private static String generateBarcode() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT);
    }

    private static List<ConcertSeed> defaultConcertSeeds() {
        return List.of(
                new ConcertSeed("Nuit Electro Paris", "Pulse Vector", new BigDecimal("39.90")),
                new ConcertSeed("Rock Arena", "Northern Echo", new BigDecimal("45.00")),
                new ConcertSeed("Jazz sur Seine", "Blue Latitude", new BigDecimal("29.90")),
                new ConcertSeed("Pop City Lights", "Nova Bloom", new BigDecimal("49.90")),
                new ConcertSeed("Metal Storm", "Iron Horizon", new BigDecimal("55.00")),
                new ConcertSeed("Acoustic Session", "Lumen Folk", new BigDecimal("24.90")),
                new ConcertSeed("Indie Vibes", "Paper Planes", new BigDecimal("34.90")),
                new ConcertSeed("Rap Factory", "Kilo Verse", new BigDecimal("42.00")),
                new ConcertSeed("Classic Night", "Orchestre Aurora", new BigDecimal("59.90")),
                new ConcertSeed("Electro Sunset", "Neon Grid", new BigDecimal("37.50")),
                new ConcertSeed("Soul Evening", "Velvet Lines", new BigDecimal("31.00")),
                new ConcertSeed("Urban Beats", "District One", new BigDecimal("40.00")),
                new ConcertSeed("Festival Opening", "Sunset Avenue", new BigDecimal("47.90")),
                new ConcertSeed("Funk Revival", "Groove Station", new BigDecimal("33.50")),
                new ConcertSeed("Live Sessions", "The Parcels", new BigDecimal("28.00")),
                new ConcertSeed("Electro Clash", "Digital Riot", new BigDecimal("44.90")),
                new ConcertSeed("Summer Arena", "Atlas Sound", new BigDecimal("52.00")),
                new ConcertSeed("Night Orchestra", "Symphonic Flux", new BigDecimal("61.00")),
                new ConcertSeed("Hip Hop Live", "Street Poets", new BigDecimal("38.90")),
                new ConcertSeed("Closing Show", "Golden Hour", new BigDecimal("46.00"))
        );
    }

    private record PlaceSeed(
            String name,
            String address,
            Integer zipCode,
            String city,
            Integer capacity
    ) {
    }

    private record ConcertSeed(
            String title,
            String artist,
            BigDecimal ticketUnitPrice
    ) {
    }
}
