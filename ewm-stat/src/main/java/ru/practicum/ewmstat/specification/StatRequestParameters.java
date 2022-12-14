package ru.practicum.ewmstat.specification;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class StatRequestParameters {

    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> uris;
    private boolean unique;

    public void setStart(String start) {
        this.start = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setEnd(String end) {
        this.end = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public StatRequestSpecification toSpecification() {
        return new StatRequestSpecification(this);
    }
}
