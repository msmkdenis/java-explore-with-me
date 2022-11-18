package ru.practicum.ewmmain.specification.adminEvents;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewmmain.entity.Event;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class AdminEventsRequestSpecification implements Specification<Event> {

    public static final String DATE_TIME_STRING = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_STRING);

    private AdminEventsRequestParameters parameters;

    @Override
    public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (parameters.getUsers() != null && !parameters.getUsers().isEmpty()) {
            predicates.add(root.get("initiator").get("id").in(parameters.getUsers()));
        }

        if (parameters.getStates() != null && !parameters.getStates().isEmpty()) {
            predicates.add(root.get("eventStatus").in(parameters.getStates()));
        }

        if (parameters.getCategories() != null && !parameters.getCategories().isEmpty()) {
            predicates.add(root.get("category").get("id").in(parameters.getCategories()));
        }

        if (parameters.getRangeStart() != null && !parameters.getRangeStart().isEmpty()) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("eventDate"),
                    LocalDateTime.parse(parameters.getRangeStart(), DATE_TIME_FORMATTER))
            );
        }

        if (parameters.getRangeEnd() != null && !parameters.getRangeEnd().isEmpty()) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("eventDate"),
                    LocalDateTime.parse(parameters.getRangeEnd(), DATE_TIME_FORMATTER))
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
