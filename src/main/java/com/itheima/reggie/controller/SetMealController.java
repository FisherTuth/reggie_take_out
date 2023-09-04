package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetMealDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetMealDishService setMealDishService;

    @GetMapping("/{id}")
    public R<SetMealDto> find(@PathVariable Long id){

        SetMealDto setMealDto = setMealService.getByIdWithDishes(id);

        return R.success(setMealDto);
    }
    @PostMapping
    public R<String> save (@RequestBody SetMealDto setMealDto){

        setMealService.saveWithDishes(setMealDto);
        return R.success("插入成功");
    }
    @PutMapping
    public R<String> update(@RequestBody SetMealDto setMealDto){
        setMealService.updateWithDishes(setMealDto);
        return R.success("修改成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo = new Page();
        Page<SetMealDto> pageDto = new Page();

        LambdaQueryWrapper<Setmeal>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        queryWrapper.like(StringUtils.hasText(name), Setmeal::getName,name);
        setMealService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,pageDto,"records");

        List<SetMealDto>list = pageInfo.getRecords().stream().map(item->{
            Long id = item.getCategoryId();
            Category category = categoryService.getById(id);
            SetMealDto setMealDto= new SetMealDto();
            BeanUtils.copyProperties(item,setMealDto);
            if(category!=null)setMealDto.setCategoryName(category.getName());
            return setMealDto;
        }).collect(Collectors.toList());

        pageDto.setRecords(list);
        return R.success(pageDto);
    }
    @DeleteMapping
    public R<String>delete(@RequestParam List<Long>ids){
        log.info("deleteIDs,{}",ids);
        setMealService.removeWithDish(ids);
        return R.success("删除成功");
    }
    
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long>ids){
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        LambdaQueryWrapper<Setmeal>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        setMealService.update(setmeal,queryWrapper);
        return R.success("修改状态成功");
    }
    @GetMapping("/list")
    public R<List<SetMealDto>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        List<Setmeal>list = setMealService.list(queryWrapper);

        List<SetMealDto>dtoList = list.stream().map(item->{
            SetMealDto dto = new SetMealDto();
            BeanUtils.copyProperties(item,dto);
            LambdaQueryWrapper<SetmealDish>queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(SetmealDish::getSetmealId,item.getId());
            List<SetmealDish> list1 = setMealDishService.list(queryWrapper1);
            dto.setSetmealDishes(list1);
            return dto;
        }).collect(Collectors.toList());

        return R.success(dtoList);
    }
}
