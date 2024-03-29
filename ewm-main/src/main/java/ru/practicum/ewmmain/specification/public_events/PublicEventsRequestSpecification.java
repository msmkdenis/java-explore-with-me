package ru.practicum.ewmmain.specification.public_events;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewmmain.entity.Event;
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
public class PublicEventsRequestSpecification implements Specification<Event> {

    private PublicEventsRequestParameters parameters;

    public static final String DATE_TIME_STRING = Constants.datePattern;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_STRING);

    @Override
    public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        String text = parameters.getText();
        if (text != null && !text.isBlank()) {
            predicates.add(
                    cb.or(
                            cb.like(cb.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    )
            );
        }

        List<Long> categories = parameters.getCategories();
        if (categories != null && !categories.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categories));
        }

        Boolean paid = parameters.getPaid();
        if (paid != null) {
            predicates.add(cb.equal(root.get("paid"), paid));
        }

        String rangeStart = parameters.getRangeStart();
        if (rangeStart != null) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER);
            predicates.add(
                    cb.greaterThanOrEqualTo(root.get("eventDate"), start)
            );
        } else {
            predicates.add(
                    cb.greaterThanOrEqualTo(root.get("eventDate"), LocalDateTime.now())
            );
        }

        String rangeEnd = parameters.getRangeEnd();
        if (rangeEnd != null) {
            LocalDateTime end = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER);
            predicates.add(
                    cb.lessThanOrEqualTo(root.get("eventDate"), end)
            );
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
