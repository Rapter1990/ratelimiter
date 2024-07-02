package com.springboot.ratelimiter.common.model.dto.response;

import com.springboot.ratelimiter.common.model.page.CustomPage;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Class named {@link CustomPagingResponse} representing a custom paging response.
 *
 * @param <T> the type of content in the response
 */
@Getter
@Builder
public class CustomPagingResponse<T> {

    private List<T> content;

    private Integer pageNumber;

    private Integer pageSize;

    private Long totalElementCount;

    private Integer totalPageCount;

    /**
     * Builder class for CustomPagingResponse.
     *
     * @param <T> the type of content in the response
     */
    public static class CustomPagingResponseBuilder<T> {

        /**
         * Sets the fields of the CustomPagingResponseBuilder based on the provided CustomPage.
         *
         * @param customPage the CustomPage object
         * @param <C> the type of content in the CustomPage
         * @return the CustomPagingResponseBuilder
         */
        public <C> CustomPagingResponseBuilder<T> of(final CustomPage<C> customPage) {
            return CustomPagingResponse.<T>builder()
                    .pageNumber(customPage.getPageNumber())
                    .pageSize(customPage.getPageSize())
                    .totalElementCount(customPage.getTotalElementCount())
                    .totalPageCount(customPage.getTotalPageCount());
        }

    }

}
