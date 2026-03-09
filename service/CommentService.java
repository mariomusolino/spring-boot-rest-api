package com.odissey.tour.service;

import com.odissey.tour.exception.Exception400;
import com.odissey.tour.exception.Exception401;
import com.odissey.tour.exception.Exception404;
import com.odissey.tour.model.dto.request.CommentRequest;
import com.odissey.tour.model.dto.response.CommentResponse;
import com.odissey.tour.model.entity.Comment;
import com.odissey.tour.model.entity.Customer;
import com.odissey.tour.model.entity.Tour;
import com.odissey.tour.model.entity.User;
import com.odissey.tour.model.entity.enumerator.TourStatus;
import com.odissey.tour.repository.CommentRepository;
import com.odissey.tour.repository.CustomerRepository;
import com.odissey.tour.repository.TourRepository;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public CommentResponse create(CommentRequest req, UserDetails userDetails) {
        Tour tour = tourRepository.findById(req.getTourId())
                .orElseThrow(()-> new Exception404("Tour non trovato con id: " + req.getTourId()));
        if(!tour.getStatus().equals(TourStatus.EXPIRED))
            throw new Exception400("Non si può commentare un tour che non si è ancora concluso");

        User user = (User) userDetails;
        Customer customer = customerRepository.findById(user.getCustomer().getId())
                .orElseThrow(()-> new Exception404("Customer non trovato con id: " + user.getCustomer().getId()));

        if(!tour.getCustomers().contains(customer))
            throw new Exception401("Solo i clienti che hanno partecipato al tour possono commentarlo");

        Comment refererTo = null;
        if(req.getRefererTo() != null)
            refererTo = commentRepository.findById(req.getRefererTo())
                    .orElseThrow(()-> new Exception404("Commento di riferimento non trovato"));

        Comment comment = new Comment(customer, tour, req.getContent(), refererTo);
        commentRepository.save(comment);

        return new CommentResponse(
                comment.getId(),
                user.getFirstname().concat(" ").concat(user.getLastname()),
                comment.getCreatedAt(),
                comment.getContent(),
                refererTo == null ? null : refererTo.getId()
        );

    }

    public List<CommentResponse> getCommentsByTour(int tourId) {
        if(tourRepository.existsById(tourId))
            return commentRepository.getCommentsByTour(tourId);

        return new ArrayList<CommentResponse>();
    }

    public String censorComment(int commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new Exception404("Commento non trovato con id: " + commentId));
        comment.setCensored(true);
        commentRepository.save(comment);
        return "Commento '" + comment.getContent() + "' censurato con successo";
    }
}
