package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.features.external.dto.*;
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
            cacheNames = ExternalCacheNames.USERS_PAGE,
            key = "#pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalUserResponse> getUsers(
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalUserClient.getUsers(pageable)
        );
    }

    @Cacheable(
            cacheNames = ExternalCacheNames.POSTS_PAGE,
            key = "#pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalPostResponse> getPosts(
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalPostClient.getPosts(pageable)
        );
    }

    @Cacheable(
            cacheNames = ExternalCacheNames.POSTS_BY_USER,
            key = "#userId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalPostResponse> getPostsByUser(
            Long userId,
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalPostClient.getPostsByUser(
                        userId,
                        pageable
                )
        );
    }

    @Cacheable(
            cacheNames = ExternalCacheNames.COMMENTS_PAGE,
            key = "#pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalCommentResponse> getComments(
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalCommentClient.getComments(pageable)
        );
    }

    @Cacheable(
            cacheNames = ExternalCacheNames.COMMENTS_BY_POST,
            key = "#postId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalCommentResponse> getCommentsByPost(
            Long postId,
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalCommentClient.getCommentsByPost(
                        postId,
                        pageable
                )
        );
    }

    @Cacheable(
            cacheNames = ExternalCacheNames.ALBUMS_PAGE,
            key = "#pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalAlbumResponse> getAlbums(
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalAlbumClient.getAlbums(pageable)
        );
    }

    @Cacheable(
            cacheNames = ExternalCacheNames.ALBUMS_BY_USER,
            key = "#userId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort",
            sync = true
    )
    public ExternalCachedPage<ExternalAlbumResponse> getAlbumsByUser(
            Long userId,
            Pageable pageable
    ) {
        return ExternalCachedPage.from(
                externalAlbumClient.getAlbumsByUser(
                        userId,
                        pageable
                )
        );
    }
}