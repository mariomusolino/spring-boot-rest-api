package com.odissey.tour.repository;

import com.odissey.tour.model.dto.response.CommentResponse;
import com.odissey.tour.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT new com.odissey.tour.model.dto.response.CommentResponse(" +
            "c.id, " +
            "(c.customer.user.firstname || ' ' || c.customer.user.lastname) AS displayName, " +
            "c.createdAt, " +
            "CASE WHEN (c.censored = true) THEN '*********' ELSE c.content END, " +
            "c.refererTo.id" +
            ") FROM Comment c " +
            "WHERE c.tour.id = :tourId " +
            "ORDER BY c.createdAt")
    List<CommentResponse> getCommentsByTour(int tourId);
}
