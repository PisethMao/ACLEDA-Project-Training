package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.features.external.dto.ExternalUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalUserServiceImpl
        implements ExternalUserService {
    private final ExternalUserClient externalUserClient;

    @Override
    public ExternalUserResponse getExternalUser(
            Long userId
    ) {
        return externalUserClient.getUser(userId);
    }
}