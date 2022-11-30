package ru.practicum.ewmmain.specification.publicEvents;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class PublicEventsRequestParameters {

    private String text;

    private List<Long> categories;

    private Boolean paid;

    private String rangeStart;

    private String rangeEnd;

    private Boolean onlyAvailable;

    private EventSortType sort;

    private int from;

    private int size;

    public PublicEventsRequestParameters() {
        this.size = 10;
        this.onlyAvailable = false;
        this.sort = EventSortType.EVENT_DATE;
    }

    public PublicEventsRequestSpecification toSpecification() {
        return new PublicEventsRequestSpecification(this);
    }

    public Pageable toPageable() {
        int page = from < size ? 0 : from / size;
        String sorting;
        switch (sort) {
            case EVENT_DATE:
                sorting = "eventDate";
                break;
            case VIEWS:
                sorting = "view";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sort);
        }
        return PageRequest.of(page, size, Sort.by(sorting).descending());
    }
}
