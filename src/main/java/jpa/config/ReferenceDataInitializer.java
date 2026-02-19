package jpa.config;

import jpa.dao.abstracts.PlaceDao;
import jpa.dao.impl.PlaceDaoImpl;
import jpa.entities.Place;

import java.util.List;
import java.util.logging.Logger;

/**
 * Initializes immutable reference data used by the application.
 */
public final class ReferenceDataInitializer {

    private static final Logger logger = Logger.getLogger(ReferenceDataInitializer.class.getName());

    private ReferenceDataInitializer() {
    }

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

    private record PlaceSeed(
            String name,
            String address,
            Integer zipCode,
            String city,
            Integer capacity
    ) {
    }
}
