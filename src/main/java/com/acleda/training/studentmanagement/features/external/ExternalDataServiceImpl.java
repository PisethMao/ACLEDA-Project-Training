package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.config.CacheProperties;
import com.acleda.training.studentmanagement.features.external.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalDataServiceImpl
        implements ExternalDataService {
    private final ExternalUserClient externalUserClient;
    private final ExternalPostClient externalPostClient;
    private final ExternalCommentClient externalCommentClient;
    private final ExternalAlbumClient externalAlbumClient;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;

    @Override
    public Page<ExternalUserResponse> getUsers(
            Pageable pageable
    ) {
        return externalUserClient.getUsers(pageable);
    }

    @Override
    public ExternalUserResponse getUser(
            Long userId
    ) {
        ExternalUserResponse cached =
                getFromCache(
                        buildUserKey(userId),
                        ExternalUserResponse.class
                );
        if (cached != null) {
            return cached;
        }
        ExternalUserResponse response =
                externalUserClient.getUser(userId);
        putToCache(
                buildUserKey(userId),
                response,
                cacheProperties
                        .externalUser()
                        .ttl()
        );
        return response;
    }

    @Override
    public Page<ExternalPostResponse> getPosts(
            Pageable pageable
    ) {
        return externalPostClient.getPosts(pageable);
    }

    @Override
    public ExternalPostResponse getPost(
            Long postId
    ) {
        ExternalPostResponse cached =
                getFromCache(
                        buildPostKey(postId),
                        ExternalPostResponse.class
                );
        if (cached != null) {
            return cached;
        }
        ExternalPostResponse response =
                externalPostClient.getPost(postId);
        putToCache(
                buildPostKey(postId),
                response,
                cacheProperties
                        .externalPost()
                        .ttl()
        );
        return response;
    }

    @Override
    public Page<ExternalPostResponse> getPostsByUser(
            Long userId,
            Pageable pageable
    ) {
        return externalPostClient
                .getPostsByUser(
                        userId,
                        pageable
                );
    }

    @Override
    public Page<ExternalCommentResponse> getComments(
            Pageable pageable
    ) {
        return externalCommentClient
                .getComments(pageable);
    }

    @Override
    public ExternalCommentResponse getComment(
            Long commentId
    ) {
        ExternalCommentResponse cached =
                getFromCache(
                        buildCommentKey(commentId),
                        ExternalCommentResponse.class
                );
        if (cached != null) {
            return cached;
        }
        ExternalCommentResponse response =
                externalCommentClient
                        .getComment(commentId);
        putToCache(
                buildCommentKey(commentId),
                response,
                cacheProperties
                        .externalComment()
                        .ttl()
        );
        return response;
    }

    @Override
    public Page<ExternalCommentResponse> getCommentsByPost(
            Long postId,
            Pageable pageable
    ) {
        return externalCommentClient
                .getCommentsByPost(
                        postId,
                        pageable
                );
    }

    @Override
    public Page<ExternalAlbumResponse> getAlbums(
            Pageable pageable
    ) {
        return externalAlbumClient
                .getAlbums(pageable);
    }

    @Override
    public ExternalAlbumResponse getAlbum(
            Long albumId
    ) {
        ExternalAlbumResponse cached =
                getFromCache(
                        buildAlbumKey(albumId),
                        ExternalAlbumResponse.class
                );
        if (cached != null) {
            return cached;
        }
        ExternalAlbumResponse response =
                externalAlbumClient
                        .getAlbum(albumId);
        putToCache(
                buildAlbumKey(albumId),
                response,
                cacheProperties
                        .externalAlbum()
                        .ttl()
        );
        return response;
    }

    @Override
    public Page<ExternalAlbumResponse> getAlbumsByUser(
            Long userId,
            Pageable pageable
    ) {
        return externalAlbumClient
                .getAlbumsByUser(
                        userId,
                        pageable
                );
    }

    private <T> T getFromCache(
            String key,
            Class<T> responseType
    ) {
        try {
            String json =
                    redisTemplate
                            .opsForValue()
                            .get(key);
            if (json == null) {
                log.info(
                        "External cache MISS - key={}",
                        key
                );
                return null;
            }
            T response =
                    objectMapper.readValue(
                            json,
                            responseType
                    );
            log.info(
                    "External cache HIT - key={}",
                    key
            );
            return response;
        } catch (JacksonException e) {
            log.warn(
                    "Invalid external cache value - key={}",
                    key,
                    e
            );
            deleteCache(key);
            return null;
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while reading external cache - key={}",
                    key,
                    e
            );
            return null;
        }
    }

    private void putToCache(
            String key,
            Object response,
            java.time.Duration ttl
    ) {
        try {
            String json =
                    objectMapper
                            .writeValueAsString(
                                    response
                            );
            redisTemplate
                    .opsForValue()
                    .set(
                            key,
                            json,
                            ttl
                    );
            log.info(
                    "External cache SET - key={}, ttl={}",
                    key,
                    ttl
            );
        } catch (JacksonException e) {
            log.warn(
                    "Could not serialize external response - key={}",
                    key,
                    e
            );
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while caching external response - key={}",
                    key,
                    e
            );
        }
    }

    private void deleteCache(
            String key
    ) {
        try {
            redisTemplate.delete(key);
            log.info(
                    "External cache DELETE - key={}",
                    key
            );
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while deleting external cache - key={}",
                    key,
                    e
            );
        }
    }

    private String buildUserKey(
            Long userId
    ) {
        return cacheProperties
                .externalUser()
                .keyPrefix()
                + userId;
    }

    private String buildPostKey(
            Long postId
    ) {
        return cacheProperties
                .externalPost()
                .keyPrefix()
                + postId;
    }

    private String buildCommentKey(
            Long commentId
    ) {
        return cacheProperties
                .externalComment()
                .keyPrefix()
                + commentId;
    }

    private String buildAlbumKey(
            Long albumId
    ) {
        return cacheProperties
                .externalAlbum()
                .keyPrefix()
                + albumId;
    }
}