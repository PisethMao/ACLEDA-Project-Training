package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.features.external.dto.ExternalAlbumResponse;
import com.acleda.training.studentmanagement.features.external.dto.ExternalCommentResponse;
import com.acleda.training.studentmanagement.features.external.dto.ExternalPostResponse;
import com.acleda.training.studentmanagement.features.external.dto.ExternalUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExternalDataService {
    Page<ExternalUserResponse> getUsers(
            Pageable pageable
    );

    ExternalUserResponse getUser(
            Long userId
    );

    Page<ExternalPostResponse> getPosts(
            Pageable pageable
    );

    ExternalPostResponse getPost(
            Long postId
    );

    Page<ExternalPostResponse> getPostsByUser(
            Long userId,
            Pageable pageable
    );

    Page<ExternalCommentResponse> getComments(
            Pageable pageable
    );

    ExternalCommentResponse getComment(
            Long commentId
    );

    Page<ExternalCommentResponse> getCommentsByPost(
            Long postId,
            Pageable pageable
    );

    Page<ExternalAlbumResponse> getAlbums(
            Pageable pageable
    );

    ExternalAlbumResponse getAlbum(
            Long albumId
    );

    Page<ExternalAlbumResponse> getAlbumsByUser(
            Long userId,
            Pageable pageable
    );
}