package ru.practicum.ewmstat.specification;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewmstat.entity.EndPointHit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class StatRequestSpecification implements Specification<EndPointHit> {

    private StatRequestParameters parameters;

    @Override
    public Predicate toPredicate(Root<EndPointHit> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        LocalDateTime start = parameters.getStart();
        if (start != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), start));
        }

        LocalDateTime end = parameters.getEnd();
        if (start != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), end));
        }

        List<String> uris = parameters.getUris();
        if (uris != null && !uris.isEmpty()) {
            predicates.add(root.get("uri").in(uris));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
