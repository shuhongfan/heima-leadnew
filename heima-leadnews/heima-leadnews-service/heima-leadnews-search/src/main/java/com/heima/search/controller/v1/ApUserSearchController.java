package com.heima.search.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.HistorySearchDto;
import com.heima.search.service.ApUserSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * APP用户搜索信息表 前端控制器
 * </p>
 *
 * @author itheima
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/history")
public class ApUserSearchController{

    @Autowired
    private ApUserSearchService apUserSearchService;

    /**
     * 查询搜索历史
     * @return
     */
    @PostMapping("/load")
    public ResponseResult findUserSearch() {
        return apUserSearchService.findUserSearch();
    }

    /**
     * 删除搜索记录
     * @param historySearchDto
     * @return
     */
    @PostMapping("/del")
    public ResponseResult delUserSearch(@RequestBody HistorySearchDto historySearchDto) {
        return apUserSearchService.delUserSearch(historySearchDto);
    }

}
