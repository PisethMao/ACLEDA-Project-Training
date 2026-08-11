package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.exception.ApiResponse;
import com.acleda.training.studentmanagement.exception.ResponseUtil;
import com.acleda.training.studentmanagement.features.external.dto.ExternalUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/external-users")
@RequiredArgsConstructor
@Tag(
        name = "External User",
        description = "APIs for retrieving user data from third-party services"
)
public class ExternalUserController {
    private final ExternalUserService externalUserService;

    @Operation(
            summary = "Get external user by ID",
            description = "Retrieves an external user from a third-party API by user ID"
    )
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ExternalUserResponse>>
    getExternalUser(
            @PathVariable
            @Min(
                    value = 1,
                    message = "User ID must be greater than 0"
            )
            Long userId,
            HttpServletRequest httpServletRequest
    ) {
        ExternalUserResponse response =
                externalUserService.getExternalUser(
                        userId
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "External user retrieved successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }
}