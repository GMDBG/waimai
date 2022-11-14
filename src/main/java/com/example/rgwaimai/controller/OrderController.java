package com.example.rgwaimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rgwaimai.common.BaseContext;
import com.example.rgwaimai.common.R;
import com.example.rgwaimai.entity.Orders;
import com.example.rgwaimai.service.OrderDetailService;
import com.example.rgwaimai.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info(orders.toString());
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> pageR(int page,int pageSize){
        Page<Orders> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(Orders::getStatus);

        orderService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, Long number, LocalDateTime beginTime,LocalDateTime endTime){
        Page<Orders> pageInfo = new Page(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(number!=null,Orders::getNumber,number);
        queryWrapper.between(beginTime!=null && endTime!=null,Orders::getOrderTime,beginTime,endTime);
        queryWrapper.orderByDesc(Orders::getCheckoutTime);

        orderService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<Orders> update(@RequestBody Orders orders){
        Long id = orders.getId();
        Integer status = orders.getStatus();
        orders = orderService.getById(id);
        orders.setStatus(status);
        orderService.updateById(orders);
        return R.success(orders);
    }

    @PostMapping("/again")
    public R<Orders> again(@RequestBody Orders orders){
        return R.success(orders);
    }

}