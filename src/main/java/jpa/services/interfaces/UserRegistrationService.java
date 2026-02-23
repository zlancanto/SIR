package jpa.services.interfaces;

import jpa.dto.user.CreateAdminRequestDto;
import jpa.dto.user.CreateUserRequestDto;
import jpa.dto.user.ResponseCurrentUserDto;
import jpa.dto.user.ResponseUserDto;

/**
 * Service contract for UserRegistrationService.
 */
public interface UserRegistrationService {
    ResponseUserDto register(CreateUserRequestDto request);

    ResponseUserDto registerAdmin(CreateAdminRequestDto request, String adminRegistrationKey);

    /**
     * Returns profile information of the authenticated user.
     *
     * @param authenticatedEmail email extracted from authenticated security context
     * @return authenticated user profile payload
     */
    ResponseCurrentUserDto getCurrentUser(String authenticatedEmail);
}
