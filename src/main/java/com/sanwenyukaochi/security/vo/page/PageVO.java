package com.sanwenyukaochi.security.vo.page;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageVO<T> {
    private List<T> records;
    private long currentPage;
    private long size;
    private long total;
    private long pages;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PageVO<T> from(Page<T> page) {
        PageVO<T> pageVo = new PageVO<>();
        pageVo.setRecords(page.getContent());
        pageVo.setCurrentPage(page.getNumber());
        pageVo.setSize(page.getSize());
        pageVo.setTotal(page.getTotalElements());
        pageVo.setPages(page.getTotalPages());
        pageVo.setHasNext(!page.isLast());
        pageVo.setHasPrevious(!page.isFirst());
        return pageVo;
    }
}