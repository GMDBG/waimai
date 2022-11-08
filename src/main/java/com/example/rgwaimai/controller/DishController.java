package com.example.rgwaimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rgwaimai.common.R;
import com.example.rgwaimai.dto.DishDto;
import com.example.rgwaimai.entity.Category;
import com.example.rgwaimai.entity.Dish;
import com.example.rgwaimai.entity.DishFlavor;
import com.example.rgwaimai.service.CategoryService;
import com.example.rgwaimai.service.DishFlavorService;
import com.example.rgwaimai.service.DishService;
import com.fasterxml.jackson.databind.deser.impl.NullsAsEmptyProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @authro zl
 * @create 2022-11-06-23:15
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page:{},pageSize:{},name:{}",page,pageSize, name);

        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        queryWrapper.orderByAsc(Dish::getUpdateTime);

        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();//分类ID
            //更具id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category!=null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlaor(dishDto);
        return R.success("添加成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam Long[] ids){
        for (Long id : ids) {
            dishService.remove(id);
        }
        return R.success("删除菜品成功");
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateByIdWithFlavor(dishDto);
        return R.success("修改成功");
    }

    /**
     * 根据id查询菜品信息和对应口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@RequestParam Long[] ids,@PathVariable int status){
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("菜品状态已经更改成功！");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> listR(@RequestParam Long categoryId){
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
//        queryWrapper.eq(Dish::getCategoryId,categoryId);
//        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }


    //改造
    @GetMapping("/list")
    public R<List<DishDto>> listR(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            //更具id查询分类对象
            Category category1 = categoryService.getById(categoryId);

            if(category1!=null) {
                String categoryName = category1.getName();
                dishDto.setCategoryName(categoryName);
            }

            Long dishId = item.getId();//当前菜品id
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper();
            wrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> flavors = dishFlavorService.list(wrapper);

            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

}