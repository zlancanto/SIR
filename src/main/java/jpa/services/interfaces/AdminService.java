package jpa.services.interfaces;

import jpa.dto.admin.ResponseAdminSummaryDto;

import java.util.List;

/**
 * Service contract for AdminService.
 */
public interface AdminService {
    /**
     * Lists all admins.
     *
     * @return admins summary list
     */
    List<ResponseAdminSummaryDto> getAllAdmins();
}
