package ru.practicum.ewmstat.entity;

import lombok.*;

@Getter
@Setter
@Builder
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
