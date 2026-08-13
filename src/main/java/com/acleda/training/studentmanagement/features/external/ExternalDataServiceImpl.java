package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.features.external.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalDataServiceImpl
        implements ExternalDataService {
    private final ExternalUserClient externalUserClient;
    private final ExternalPostClient externalPostClient;
    private final ExternalCommentClient externalCommentClient;
    private final ExternalAlbumClient externalAlbumClient;

    private final ExternalPageCacheService externalPageCacheService;

    @Override
    public Page<ExternalUserResponse> getUsers(
            Pageable pageable
    ) {
        return externalPageCacheService
                .getUsers(pageable)
                .toPage();
    }

    @Override
    @Cacheable(
            value = "external-users",
            key = "#userId",
            sync = true
    )
    public ExternalUserResponse getUser(
            Long userId
    ) {
        return externalUserClient
                .getUser(userId);
    }

    @Override
    public Page<ExternalPostResponse> getPosts(
            Pageable pageable
    ) {
        return externalPageCacheService
                .getPosts(pageable)
                .toPage();
    }

    @Override
    @Cacheable(
            value = "external-posts",
            key = "#postId",
            sync = true
    )
    public ExternalPostResponse getPost(
            Long postId
    ) {
        return externalPostClient
                .getPost(postId);
    }

    @Override
    public Page<ExternalPostResponse> getPostsByUser(
            Long userId,
            Pageable pageable
    ) {
        return externalPageCacheService
                .getPostsByUser(
                        userId,
                        pageable
                )
                .toPage();
    }

    @Override
    public Page<ExternalCommentResponse> getComments(
            Pageable pageable
    ) {
        return externalPageCacheService
                .getComments(pageable)
                .toPage();
    }

    @Override
    @Cacheable(
            value = "external-comments",
            key = "#commentId",
            sync = true
    )
    public ExternalCommentResponse getComment(
            Long commentId
    ) {
        return externalCommentClient
                .getComment(commentId);
    }

    @Override
    public Page<ExternalCommentResponse> getCommentsByPost(
            Long postId,
            Pageable pageable
    ) {
        return externalPageCacheService
                .getCommentsByPost(
                        postId,
                        pageable
                )
                .toPage();
    }

    @Override
    public Page<ExternalAlbumResponse> getAlbums(
            Pageable pageable
    ) {
        return externalPageCacheService
                .getAlbums(pageable)
                .toPage();
    }

    @Override
    @Cacheable(
            value = "external-albums",
            key = "#albumId",
            sync = true
    )
    public ExternalAlbumResponse getAlbum(
            Long albumId
    ) {
        return externalAlbumClient
                .getAlbum(albumId);
    }

    @Override
    public Page<ExternalAlbumResponse> getAlbumsByUser(
            Long userId,
            Pageable pageable
    ) {
        return externalPageCacheService
                .getAlbumsByUser(
                        userId,
                        pageable
                )
                .toPage();
    }
}