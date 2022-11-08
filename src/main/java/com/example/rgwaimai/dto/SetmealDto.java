package com.example.rgwaimai.dto;


import com.example.rgwaimai.entity.Setmeal;
import com.example.rgwaimai.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
