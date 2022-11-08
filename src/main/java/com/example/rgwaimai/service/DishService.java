package com.example.rgwaimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rgwaimai.dto.DishDto;
import com.example.rgwaimai.entity.Dish;

/**
 * @authro zl
 * @create 2022-11-06-18:35
 */
public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味，操作两张表：dish,dish_flavor
    public void saveWithFlaor(DishDto dishDto);

    void remove(Long ids);

    //根据id查询菜品信息和对应口味
    public DishDto getByIdWithFlavor(Long id);

    void updateByIdWithFlavor(DishDto dishDto);
}
