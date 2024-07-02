package com.springboot.ratelimiter.common.model.page;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Class named {@link CustomPage} representing a custom page of data.
 *
 * @param <T> the type of content in the page
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomPage<T> {
    private List<T> content;

    private Integer pageNumber;

    private Integer pageSize;

    private Long totalElementCount;

    private Integer totalPageCount;

    /**
     * Creates a CustomPage from a list of domain models and a Page object.
     *
     * @param domainModels the list of domain models
     * @param page the Page object
     * @param <C> the type of domain models
     * @param <X> the type of the Page content
     * @return the CustomPage object
     */
    public static <C, X> CustomPage<C> of(final List<C> domainModels, final Page<X> page) {
        return CustomPage.<C>builder()
                .content(domainModels)
                .pageNumber(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalPageCount(page.getTotalPages())
                .totalElementCount(page.getTotalElements())
                .build();
    }

}
