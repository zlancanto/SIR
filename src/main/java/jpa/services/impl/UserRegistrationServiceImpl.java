package jpa.services.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jpa.config.AdminConfig;
import jpa.dao.abstracts.UserDao;
import jpa.dto.user.CreateAdminRequestDto;
import jpa.dto.user.CreateUserRequestDto;
import jpa.dto.user.ResponseCurrentUserDto;
import jpa.dto.user.ResponseUserDto;
import jpa.entities.Admin;
import jpa.entities.Customer;
import jpa.entities.Organizer;
import jpa.entities.User;
import jpa.enums.Roles;
import jpa.services.interfaces.UserRegistrationService;
import jpa.utils.UserRoleResolver;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

import static jpa.utils.StringValidation.normalizeRequired;

/**
 * Service implementation UserRegistrationServiceImpl.
 */
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UserDao userDao;

    /**
     * Creates a new instance of UserRegistrationServiceImpl.
     *
     * @param userDao method parameter
     */
    public UserRegistrationServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Executes register operation.
     *
     * @param request method parameter
     * @return operation result
     */
    @Override
    public ResponseUserDto register(CreateUserRequestDto request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        // Public registration only allows business roles that can self-sign up.
        Roles role = parseRole(request.role());
        if (!isSelfRegistrationAllowed(role)) {
            throw new ForbiddenException("Self-registration is not allowed for role " + role.name());
        }

        return registerByRole(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                role
        );
    }

    /**
     * Executes registerAdmin operation.
     *
     * @param request              method parameter
     * @param adminRegistrationKey method parameter
     * @return operation result
     */
    @Override
    public ResponseUserDto registerAdmin(CreateAdminRequestDto request, String adminRegistrationKey) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        validateAdminRegistrationKey(adminRegistrationKey);

        return registerByRole(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                Roles.ROLE_ADMIN
        );
    }

    /**
     * Executes getCurrentUser operation.
     *
     * @param authenticatedEmail method parameter
     * @return operation result
     */
    @Override
    public ResponseCurrentUserDto getCurrentUser(String authenticatedEmail) {
        String email = normalizeRequired("authenticatedEmail", authenticatedEmail)
                .toLowerCase(Locale.ROOT);

        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return new ResponseCurrentUserDto(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                UserRoleResolver.resolve(user).name(),
                user.getCreatedAt()
        );
    }

    private ResponseUserDto registerByRole(
            String rawEmail,
            String rawPassword,
            String rawFirstName,
            String rawLastName,
            Roles role
    ) {
        // Normalize once so validation/storage are consistent across all endpoints.
        String email = normalizeRequired("email", rawEmail).toLowerCase(Locale.ROOT);
        String password = normalizeRequired("password", rawPassword);
        String firstName = normalizeRequired("firstName", rawFirstName);
        String lastName = normalizeRequired("lastName", rawLastName);

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("Invalid email format");
        }

        if (password.length() < 8) {
            throw new BadRequestException("Password must contain at least 8 characters");
        }

        if (userDao.findByEmail(email).isPresent()) {
            throw new ClientErrorException("Email already used", Response.Status.CONFLICT);
        }

        User user = newUserByRole(role);
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        userDao.save(user);

        return new ResponseUserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                role.name()
        );
    }

    private Roles parseRole(String rawRole) {
        if (rawRole == null || rawRole.isBlank()) {
            throw new BadRequestException("role is required");
        }

        String normalized = rawRole.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "CUSTOMER" -> Roles.ROLE_CUSTOMER;
            case "ORGANIZER" -> Roles.ROLE_ORGANIZER;
            case "ADMIN" -> Roles.ROLE_ADMIN;
            default -> throw new BadRequestException("Unsupported role: " + rawRole);
        };
    }

    private boolean isSelfRegistrationAllowed(Roles role) {
        return role == Roles.ROLE_CUSTOMER || role == Roles.ROLE_ORGANIZER;
    }

    private User newUserByRole(Roles role) {
        return switch (role) {
            case ROLE_ADMIN -> new Admin();
            case ROLE_CUSTOMER -> new Customer();
            case ROLE_ORGANIZER -> new Organizer();
            default -> throw new ForbiddenException("No self-registration factory for role " + role.name());
        };
    }

    private void validateAdminRegistrationKey(String providedKey) {
        String expectedKey = resolveAdminRegistrationKey()
                .orElseThrow(() -> new ForbiddenException("Admin registration is disabled"));

        if (providedKey == null || providedKey.isBlank()) {
            throw new ForbiddenException("Missing admin registration key");
        }

        // Constant-time comparison avoids leaking key length/prefix matches through timing.
        boolean valid = MessageDigest.isEqual(
                providedKey.getBytes(StandardCharsets.UTF_8),
                expectedKey.getBytes(StandardCharsets.UTF_8)
        );

        if (!valid) {
            throw new ForbiddenException("Invalid admin registration key");
        }
    }

    private Optional<String> resolveAdminRegistrationKey() {
        String fromProperty = System.getProperty(AdminConfig.ADMIN_REGISTRATION_KEY_PROPERTY);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return Optional.of(fromProperty.trim());
        }

        String fromEnv = System.getenv(AdminConfig.ADMIN_REGISTRATION_KEY_ENV);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return Optional.of(fromEnv.trim());
        }

        return Optional.empty();
    }
}
