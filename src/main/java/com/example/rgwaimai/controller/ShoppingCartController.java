package com.example.rgwaimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.rgwaimai.common.BaseContext;
import com.example.rgwaimai.common.R;
import com.example.rgwaimai.entity.Dish;
import com.example.rgwaimai.entity.ShoppingCart;
import com.example.rgwaimai.service.DishService;
import com.example.rgwaimai.service.SetmealService;
import com.example.rgwaimai.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @authro zl
 * @create 2022-11-08-10:22
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private DishService dishService;

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    public R<List<ShoppingCart>> listR(){
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper =new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);


        return R.success(list);
    }

    /**
     * 添加购物车
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info(shoppingCart.toString());
        //设置用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查询当前菜品或套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getUserId,userId);


        if(dishId!=null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);

        if(one!=null){
            //如果已经存在，则添加到购物车
            one.setNumber(one.getNumber()+1);
            shoppingCartService.updateById(one);
        }else {
            //如果没有，则添加到购物车，数量默认是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        return R.success(one);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        if(shoppingCart.getDishId()!=null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        String key = null;
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if(one.getNumber()>1){
            //如果已经存在，则添加到购物车
            one.setNumber(one.getNumber()-1);
            shoppingCartService.updateById(one);
        }else {
            if(shoppingCart.getDishId()!=null) {
                Dish dish = dishService.getById(one.getDishId());
                key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
                redisTemplate.delete(key);
            }

            shoppingCartService.removeById(one);
        }
        return R.success(one);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        shoppingCartService.remove(queryWrapper);

        return R.success("删除成功");
    }
}