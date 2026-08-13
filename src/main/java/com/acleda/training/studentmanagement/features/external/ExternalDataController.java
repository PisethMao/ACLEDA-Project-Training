package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.exception.ApiResponse;
import com.acleda.training.studentmanagement.exception.ResponseUtil;
import com.acleda.training.studentmanagement.features.external.dto.ExternalAlbumResponse;
import com.acleda.training.studentmanagement.features.external.dto.ExternalCommentResponse;
import com.acleda.training.studentmanagement.features.external.dto.ExternalPostResponse;
import com.acleda.training.studentmanagement.features.external.dto.ExternalUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/externals")
@RequiredArgsConstructor
@Tag(
        name = "External Data",
        description = "APIs for retrieving users, posts, comments, and albums from external services"
)
public class ExternalDataController {
    private final ExternalDataService externalDataService;

    @Operation(
            summary = "Get external users",
            description = "Retrieve a paginated list of users from the external API"
    )
    @GetMapping("/users")
    public ResponseEntity<
            ApiResponse<Page<ExternalUserResponse>>
            > getUsers(
            @ParameterObject
            Pageable pageable,
            HttpServletRequest httpServletRequest
    ) {
        Page<ExternalUserResponse> users =
                externalDataService
                        .getUsers(pageable);
        return ResponseUtil.success(
                HttpStatus.OK,
                "External users retrieved successfully",
                users,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get external user by ID",
            description = "Retrieve a specific external user using the user ID"
    )
    @GetMapping("/users/{userId}")
    public ResponseEntity<
            ApiResponse<ExternalUserResponse>
            > getUser(
            @PathVariable Long userId,
            HttpServletRequest httpServletRequest
    ) {
        ExternalUserResponse user =
                externalDataService
                        .getUser(userId);
        return ResponseUtil.success(
                HttpStatus.OK,
                "External user retrieved successfully",
                user,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get posts by external user",
            description = "Retrieve a paginated list of posts belonging to a specific external user"
    )
    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<
            ApiResponse<Page<ExternalPostResponse>>
            > getPostsByUser(
            @PathVariable Long userId,
            @ParameterObject
            Pageable pageable,
            HttpServletRequest httpServletRequest
    ) {
        Page<ExternalPostResponse> posts =
                externalDataService
                        .getPostsByUser(
                                userId,
                                pageable
                        );
        return ResponseUtil.success(
                HttpStatus.OK,
                "External user posts retrieved successfully",
                posts,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get albums by external user",
            description = "Retrieve a paginated list of albums belonging to a specific external user"
    )
    @GetMapping("/users/{userId}/albums")
    public ResponseEntity<
            ApiResponse<Page<ExternalAlbumResponse>>
            > getAlbumsByUser(
            @PathVariable Long userId,
            @ParameterObject
            Pageable pageable,
            HttpServletRequest httpServletRequest
    ) {
        Page<ExternalAlbumResponse> albums =
                externalDataService
                        .getAlbumsByUser(
                                userId,
                                pageable
                        );
        return ResponseUtil.success(
                HttpStatus.OK,
                "External user albums retrieved successfully",
                albums,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get external posts",
            description = "Retrieve a paginated list of posts from the external API"
    )
    @GetMapping("/posts")
    public ResponseEntity<
            ApiResponse<Page<ExternalPostResponse>>
            > getPosts(
            @ParameterObject
            Pageable pageable,
            HttpServletRequest httpServletRequest
    ) {
        Page<ExternalPostResponse> posts =
                externalDataService
                        .getPosts(pageable);
        return ResponseUtil.success(
                HttpStatus.OK,
                "External posts retrieved successfully",
                posts,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get external post by ID",
            description = "Retrieve a specific external post using the post ID"
    )
    @GetMapping("/posts/{postId}")
    public ResponseEntity<
            ApiResponse<ExternalPostResponse>
            > getPost(
            @PathVariable Long postId,
            HttpServletRequest httpServletRequest
    ) {
        ExternalPostResponse post =
                externalDataService
                        .getPost(postId);
        return ResponseUtil.success(
                HttpStatus.OK,
                "External post retrieved successfully",
                post,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get comments by external post",
            description = "Retrieve a paginated list of comments belonging to a specific external post"
    )
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<
            ApiResponse<Page<ExternalCommentResponse>>
            > getCommentsByPost(
            @PathVariable Long postId,
            @ParameterObject
            Pageable pageable,
            HttpServletRequest httpServletRequest
    ) {
        Page<ExternalCommentResponse> comments =
                externalDataService
                        .getCommentsByPost(
                                postId,
                                pageable
                        );
        return ResponseUtil.success(
                HttpStatus.OK,
                "External post comments retrieved successfully",
                comments,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get external comments",
            description = "Retrieve a paginated list of comments from the external API"
    )
    @GetMapping("/comments")
    public ResponseEntity<
            ApiResponse<Page<ExternalCommentResponse>>
            > getComments(
            @ParameterObject
            Pageable pageable,
            HttpServletRequest httpServletRequest
    ) {
        Page<ExternalCommentResponse> comments =
                externalDataService
                        .getComments(pageable);
        return ResponseUtil.success(
                HttpStatus.OK,
                "External comments retrieved successfully",
                comments,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get external comment by ID",
            description = "Retrieve a specific external comment using the comment ID"
    )
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<
            ApiResponse<ExternalCommentResponse>
            > getComment(
            @PathVariable Long commentId,
            HttpServletRequest httpServletRequest
    ) {
        ExternalCommentResponse comment =
                externalDataService
                        .getComment(commentId);
        return ResponseUtil.success(
                HttpStatus.OK,
                "External comment retrieved successfully",
                comment,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get external albums",
            description = "Retrieve a paginated list of albums from the external API"
    )
    @GetMapping("/albums")
    public ResponseEntity<
            ApiResponse<Page<ExternalAlbumResponse>>
            > getAlbums(
            @ParameterObject
            Pageable pageable,
            HttpServletRequest httpServletRequest
    ) {
        Page<ExternalAlbumResponse> albums =
                externalDataService
                        .getAlbums(pageable);
        return ResponseUtil.success(
                HttpStatus.OK,
                "External albums retrieved successfully",
                albums,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get external album by ID",
            description = "Retrieve a specific external album using the album ID"
    )
    @GetMapping("/albums/{albumId}")
    public ResponseEntity<
            ApiResponse<ExternalAlbumResponse>
            > getAlbum(
            @PathVariable Long albumId,
            HttpServletRequest httpServletRequest
    ) {
        ExternalAlbumResponse album =
                externalDataService
                        .getAlbum(albumId);
        return ResponseUtil.success(
                HttpStatus.OK,
                "External album retrieved successfully",
                album,
                httpServletRequest.getRequestURI()
        );
    }
}