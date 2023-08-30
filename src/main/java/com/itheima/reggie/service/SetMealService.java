package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetMealDto;
import com.itheima.reggie.entity.Setmeal;

public interface SetMealService extends IService<Setmeal> {
    public void saveWithDishes(SetMealDto dto);

    public SetMealDto getByIdWithDishes(Long id);

    public void updateWithDishes(SetMealDto dto);
}
