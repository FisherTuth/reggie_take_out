package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetMealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetMealMapper;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
    @Autowired
    private SetMealDishService setMealDishService;

    @Override
    @Transactional
    public void saveWithDishes(SetMealDto dto) {
        this.save(dto);

        List<SetmealDish>list = dto.getSetmealDishes().stream().map(item->{
            item.setSetmealId(dto.getId());
            return item;
        }).collect(Collectors.toList());

        setMealDishService.saveBatch(list);
    }

    @Override
    public SetMealDto getByIdWithDishes(Long id) {
        Setmeal setMeal = this.getById(id);
        LambdaQueryWrapper<SetmealDish>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setMealDishService.list(queryWrapper);
        SetMealDto setMealDto = new SetMealDto();
        BeanUtils.copyProperties(setMeal,setMealDto);
        setMealDto.setSetmealDishes(setmealDishes);
        return setMealDto;
    }

    @Override
    @Transactional
    public void updateWithDishes(SetMealDto dto) {

        this.updateById(dto);

        LambdaQueryWrapper<SetmealDish>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,dto.getId());
        setMealDishService.remove(queryWrapper);

        List<SetmealDish>list = dto.getSetmealDishes().stream().map(item->{
            item.setSetmealId(dto.getId());
            return item;
        }).collect(Collectors.toList());

        setMealDishService.saveBatch(list);

    }
    @Override
    @Transactional
    public void removeWithDish(List<Long>ids){

        LambdaQueryWrapper<Setmeal>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if(count>0)throw new CustomException("售卖中，不能删除。。");

        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish>queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setMealDishService.remove(queryWrapper1);
    }
}
