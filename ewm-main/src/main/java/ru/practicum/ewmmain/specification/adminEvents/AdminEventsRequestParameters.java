package ru.practicum.ewmmain.specification.adminEvents;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewmmain.entity.EventStatus;

import java.util.List;

@Getter
@Setter
@ToString
public class AdminEventsRequestParameters {

    private List<Long> users;

    private List<EventStatus> states;

    private List<Long> categories;

    private String rangeStart;

    private String rangeEnd;

    private int from;

    private int size;

    public AdminEventsRequestParameters() {
        this.size = 10;
    }

    public AdminEventsRequestSpecification toSpecification() {
        return new AdminEventsRequestSpecification(this);
    }

    public Pageable toPageable() {
        return PageRequest.of(from / size, size);
    }
}
