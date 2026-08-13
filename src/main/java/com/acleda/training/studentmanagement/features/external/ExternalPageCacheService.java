package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.features.external.dto.ExternalAlbumResponse;
import com.acleda.training.studentmanagement.features.external.dto.ExternalCommentResponse;
import com.acleda.training.studentmanagement.features.external.dto.ExternalPostResponse;
import com.acleda.training.studentmanagement.features.external.dto.ExternalUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalPageCacheService {
    private final ExternalUserClient externalUserClient;
    private final ExternalPostClient externalPostClient;
    private final ExternalCommentClient externalCommentClient;
    private final ExternalAlbumClient externalAlbumClient;

    @Cacheable(
            value = "external-user-pages",
            key = "'page:' + #pageable.pageNumber"
                    + " + ':size:' + #pageable.pageSize"
                    + " + ':sort:' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalUserResponse> getUsers(
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalUserClient
                        .getUsers(pageable)
        );
    }

    @Cacheable(
            value = "external-post-pages",
            key = "'page:' + #pageable.pageNumber"
                    + " + ':size:' + #pageable.pageSize"
                    + " + ':sort:' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalPostResponse> getPosts(
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalPostClient
                        .getPosts(pageable)
        );
    }

    @Cacheable(
            value = "external-user-post-pages",
            key = "'user:' + #userId"
                    + " + ':page:' + #pageable.pageNumber"
                    + " + ':size:' + #pageable.pageSize"
                    + " + ':sort:' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalPostResponse> getPostsByUser(
            Long userId,
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalPostClient
                        .getPostsByUser(
                                userId,
                                pageable
                        )
        );
    }

    @Cacheable(
            value = "external-comment-pages",
            key = "'page:' + #pageable.pageNumber"
                    + " + ':size:' + #pageable.pageSize"
                    + " + ':sort:' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalCommentResponse> getComments(
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalCommentClient
                        .getComments(pageable)
        );
    }

    @Cacheable(
            value = "external-post-comment-pages",
            key = "'post:' + #postId"
                    + " + ':page:' + #pageable.pageNumber"
                    + " + ':size:' + #pageable.pageSize"
                    + " + ':sort:' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalCommentResponse> getCommentsByPost(
            Long postId,
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalCommentClient
                        .getCommentsByPost(
                                postId,
                                pageable
                        )
        );
    }

    @Cacheable(
            value = "external-album-pages",
            key = "'page:' + #pageable.pageNumber"
                    + " + ':size:' + #pageable.pageSize"
                    + " + ':sort:' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalAlbumResponse> getAlbums(
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalAlbumClient
                        .getAlbums(pageable)
        );
    }

    @Cacheable(
            value = "external-user-album-pages",
            key = "'user:' + #userId"
                    + " + ':page:' + #pageable.pageNumber"
                    + " + ':size:' + #pageable.pageSize"
                    + " + ':sort:' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalAlbumResponse> getAlbumsByUser(
            Long userId,
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalAlbumClient
                        .getAlbumsByUser(
                                userId,
                                pageable
                        )
        );
    }
}