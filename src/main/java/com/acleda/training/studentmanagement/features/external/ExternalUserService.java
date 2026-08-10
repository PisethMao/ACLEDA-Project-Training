package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.features.external.dto.ExternalUserResponse;

public interface ExternalUserService {
    ExternalUserResponse getExternalUser(Long userId);
}