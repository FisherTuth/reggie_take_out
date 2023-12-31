package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Transactional
    @Override
    public void saveWithFlavors(DishDto dto){
        this.save(dto);

        Long dishId = dto.getId();

        List<DishFlavor> flavors = dto.getFlavors();

        flavors = flavors.stream().map(item->{
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    //根据id查询菜品信息和对应口味信息
    @Override
    public DishDto getByIdWithFlavor(Long id){
        //查询 菜品基本信息 ，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;

    }
    @Override
    @Transactional
    public void updateWithFlavors(DishDto dto){

        this.updateById(dto);

        LambdaQueryWrapper<DishFlavor>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dto.getId());
        dishFlavorService.remove(queryWrapper);

        List<DishFlavor>list = dto.getFlavors().stream().map(item->{
            item.setDishId(dto.getId());return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(list);
    }
}
