package com.example.rgwaimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rgwaimai.dto.DishDto;
import com.example.rgwaimai.dto.SetmealDto;
import com.example.rgwaimai.entity.Setmeal;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @authro zl
 * @create 2022-11-06-18:36
 */
public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    void remove(Long id);

    SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);
}
