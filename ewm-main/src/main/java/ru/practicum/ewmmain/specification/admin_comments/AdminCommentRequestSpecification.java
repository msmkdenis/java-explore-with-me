package ru.practicum.ewmmain.specification.admin_comments;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewmmain.entity.Comment;
import ru.practicum.ewmmain.util.Constants;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class AdminCommentRequestSpecification implements Specification<Comment> {

    public static final String DATE_TIME_STRING = Constants.datePattern;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_STRING);

    private AdminCommentRequestParameters parameters;

    @Override
    public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (parameters.getAuthors() != null && !parameters.getAuthors().isEmpty()) {
            predicates.add(root.get("author").get("id").in(parameters.getAuthors()));
        }

        if (parameters.getStates() != null && !parameters.getStates().isEmpty()) {
            predicates.add(root.get("status").in(parameters.getStates()));
        }

        if (parameters.getEvents() != null && !parameters.getEvents().isEmpty()) {
            predicates.add(root.get("event").get("id").in(parameters.getEvents()));
        }

        if (parameters.getRangeStart() != null && !parameters.getRangeStart().isEmpty()) {
            predicates.add(cb.greaterThanOrEqualTo(
                    root.get("created"),
                    LocalDateTime.parse(parameters.getRangeStart(), DATE_TIME_FORMATTER))
            );
        }

        if (parameters.getRangeEnd() != null && !parameters.getRangeEnd().isEmpty()) {
            predicates.add(cb.lessThanOrEqualTo(
                    root.get("created"),
                    LocalDateTime.parse(parameters.getRangeEnd(), DATE_TIME_FORMATTER))
            );
        }

        if (parameters.getMinScore() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventScore"), parameters.getMinScore()));
        }

        if (parameters.getMaxScore() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventScore"), parameters.getMaxScore()));
        }

        String text = parameters.getText();
        if (text != null && !text.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("text")), "%" + text.toLowerCase() + "%"));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}