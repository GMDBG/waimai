package com.example.rgwaimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rgwaimai.entity.Category;

/**
 * @authro zl
 * @create 2022-11-06-17:02
 */
public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}