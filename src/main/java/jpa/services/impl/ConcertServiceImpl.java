package jpa.services.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jpa.config.AdminConfig;
import jpa.dao.abstracts.AdminDao;
import jpa.dao.abstracts.ConcertDao;
import jpa.dao.abstracts.OrganizerDao;
import jpa.dao.abstracts.PlaceDao;
import jpa.dto.concert.CreateConcertRequestDto;
import jpa.dto.concert.ResponseConcertDetailsDto;
import jpa.dto.concert.ValidateConcertRequestDto;
import jpa.entities.Admin;
import jpa.entities.Concert;
import jpa.entities.Organizer;
import jpa.entities.Place;
import jpa.enums.ConcertStatus;
import jpa.services.interfaces.ConcertService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static jpa.utils.StringValidation.normalizeRequired;

/**
 * Service implementation ConcertServiceImpl.
 */
public class ConcertServiceImpl implements ConcertService {

    private final ConcertDao concertDao;
    private final OrganizerDao organizerDao;
    private final PlaceDao placeDao;
    private final AdminDao adminDao;

    /**
     * Creates a new instance of ConcertServiceImpl.
     *
     * @param concertDao method parameter
     * @param organizerDao method parameter
     * @param placeDao method parameter
     * @param adminDao method parameter
     */
    public ConcertServiceImpl(
            ConcertDao concertDao,
            OrganizerDao organizerDao,
            PlaceDao placeDao,
            AdminDao adminDao
    ) {
        this.concertDao = concertDao;
        this.organizerDao = organizerDao;
        this.placeDao = placeDao;
        this.adminDao = adminDao;
    }

    /**
     * Executes createEvent operation.
     *
     * @param request method parameter
     * @return operation result
     */
    @Override
    public ResponseConcertDetailsDto createConcert(CreateConcertRequestDto request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        if (request.organizerId() == null) {
            throw new BadRequestException("organizerId is required");
        }

        if (request.placeId() == null) {
            throw new BadRequestException("placeId is required");
        }

        if (request.date() == null) {
            throw new BadRequestException("date is required");
        }

        if (request.date().isBefore(Instant.now())) {
            throw new BadRequestException("date must be in the future");
        }

        String title = normalizeRequired("title", request.title());
        String artist = request.artist() == null ? null : request.artist().trim();

        Organizer organizer = organizerDao.findOne(request.organizerId());
        if (organizer == null) {
            throw new NotFoundException("Organizer not found");
        }

        Place place = placeDao.findOne(request.placeId());
        if (place == null) {
            throw new NotFoundException("Place not found");
        }

        Concert concert = new Concert();
        concert.setTitle(title);
        concert.setArtist(artist);
        concert.setDate(request.date());
        concert.setOrganizer(organizer);
        concert.setPlace(place);
        concert.setStatus(ConcertStatus.PENDING_VALIDATION);

        concertDao.save(concert);

        return toResponse(concert);
    }

    /**
     * Executes validateEvent operation.
     *
     * @param concertId method parameter
     * @param request method parameter
     * @param adminActionKey method parameter
     * @return operation result
     */
    @Override
    public ResponseConcertDetailsDto validateConcert(
            UUID concertId,
            ValidateConcertRequestDto request,
            String adminActionKey
    ) {
        if (concertId == null) {
            throw new BadRequestException("concertId is required");
        }

        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        if (request.adminId() == null) {
            throw new BadRequestException("adminId is required");
        }

        validateAdminActionKey(adminActionKey);

        Concert concert = concertDao.findOne(concertId);
        if (concert == null) {
            throw new NotFoundException("Concert not found");
        }

        if (concert.getStatus() != ConcertStatus.PENDING_VALIDATION) {
            throw new ClientErrorException(
                    "Concert is not pending validation",
                    Response.Status.CONFLICT
            );
        }

        Admin admin = adminDao.findOne(request.adminId());
        if (admin == null) {
            throw new NotFoundException("Admin not found");
        }

        concert.setAdmin(admin);
        concert.setStatus(ConcertStatus.PUBLISHED);

        Concert updated = concertDao.update(concert);
        return toResponse(updated);
    }

    /**
     * Executes getPublicEvents operation.
     *
     * @return operation result
     */
    @Override
    public List<ResponseConcertDetailsDto> getPublicConcerts() {
        return concertDao.findByStatus(ConcertStatus.PUBLISHED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Executes getPendingEvents operation.
     *
     * @param adminActionKey method parameter
     * @return operation result
     */
    @Override
    public List<ResponseConcertDetailsDto> getPendingConcerts(String adminActionKey) {
        validateAdminActionKey(adminActionKey);
        return concertDao.findByStatus(ConcertStatus.PENDING_VALIDATION)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ResponseConcertDetailsDto toResponse(Concert concert) {
        UUID organizerId = concert.getOrganizer() != null ? concert.getOrganizer().getId() : null;
        UUID adminId = concert.getAdmin() != null ? concert.getAdmin().getId() : null;
        UUID placeId = concert.getPlace() != null ? concert.getPlace().getId() : null;
        String status = concert.getStatus() != null ? concert.getStatus().name() : null;

        return new ResponseConcertDetailsDto(
                concert.getId(),
                concert.getTitle(),
                concert.getArtist(),
                concert.getDate(),
                status,
                organizerId,
                adminId,
                placeId,
                concert.getCreatedAt(),
                concert.getUpdatedAt()
        );
    }

    private void validateAdminActionKey(String providedKey) {
        String expectedKey = resolveAdminActionKey()
                .orElseThrow(() -> new ForbiddenException("Admin action is disabled"));

        if (providedKey == null || providedKey.isBlank()) {
            throw new ForbiddenException("Missing admin action key");
        }

        boolean valid = MessageDigest.isEqual(
                providedKey.trim().getBytes(StandardCharsets.UTF_8),
                expectedKey.getBytes(StandardCharsets.UTF_8)
        );

        if (!valid) {
            throw new ForbiddenException("Invalid admin action key");
        }
    }

    private Optional<String> resolveAdminActionKey() {
        String actionFromProperty = System.getProperty(AdminConfig.ADMIN_ACTION_KEY_PROPERTY);
        if (actionFromProperty != null && !actionFromProperty.isBlank()) {
            return Optional.of(actionFromProperty.trim());
        }

        String actionFromEnv = System.getenv(AdminConfig.ADMIN_ACTION_KEY_ENV);
        if (actionFromEnv != null && !actionFromEnv.isBlank()) {
            return Optional.of(actionFromEnv.trim());
        }

        String registrationFromProperty = System.getProperty(AdminConfig.ADMIN_REGISTRATION_KEY_PROPERTY);
        if (registrationFromProperty != null && !registrationFromProperty.isBlank()) {
            return Optional.of(registrationFromProperty.trim());
        }

        String registrationFromEnv = System.getenv(AdminConfig.ADMIN_REGISTRATION_KEY_ENV);
        if (registrationFromEnv != null && !registrationFromEnv.isBlank()) {
            return Optional.of(registrationFromEnv.trim());
        }

        return Optional.empty();
    }
}
