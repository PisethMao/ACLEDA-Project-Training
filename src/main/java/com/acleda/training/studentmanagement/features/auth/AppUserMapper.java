package com.acleda.training.studentmanagement.features.auth;

import com.acleda.training.studentmanagement.features.auth.dto.RegisterRequest;
import com.acleda.training.studentmanagement.features.auth.dto.UserResponse;
import org.mapstruct.*;

import java.util.Locale;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AppUserMapper {
    @Mapping(
            target = "username",
            source = "username",
            qualifiedByName = "normalizeUsername"
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AppUser toEntity(RegisterRequest request);

    UserResponse toResponse(AppUser user);

    @Named("normalizeUsername")
    default String normalizeUsername(
            String username
    ) {
        return username == null
                ? null
                : username
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}