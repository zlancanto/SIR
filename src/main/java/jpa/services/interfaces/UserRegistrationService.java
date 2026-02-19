package jpa.services.interfaces;

import jpa.dto.user.CreateAdminRequestDto;
import jpa.dto.user.CreateUserRequestDto;
import jpa.dto.user.ResponseUserDto;

public interface UserRegistrationService {
    ResponseUserDto register(CreateUserRequestDto request);
    ResponseUserDto registerAdmin(CreateAdminRequestDto request, String adminRegistrationKey);
}
