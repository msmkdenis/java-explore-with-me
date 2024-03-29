package ru.practicum.ewmmain.specification.admin_comments;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.ewmmain.entity.CommentStatus;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
public class AdminCommentRequestParameters {

    private List<Long> authors;
    private List<CommentStatus> states;
    private List<Long> events;
    private String rangeStart;
    private String rangeEnd;
    private Integer minScore;
    private Integer maxScore;
    private String text;

    @Positive
    private int from;

    @Size(min = 1)
    private int size;

    public AdminCommentRequestParameters() {
        this.size = 10;
    }

    public AdminCommentRequestSpecification toSpecification() {
        return new AdminCommentRequestSpecification(this);
    }

    public Pageable toPageable() {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size, Sort.by("created").descending());
    }
}
