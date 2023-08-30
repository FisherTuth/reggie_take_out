package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetMealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
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
}
