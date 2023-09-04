package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page pageInfo = new Page(page, pageSize);
        Page<DishDto> pageDto = new Page(page, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(name), Dish::getName, name);
        queryWrapper.orderByAsc(Dish::getCategoryId).orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, pageDto, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map(item -> {
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(item, dto);
            if(category != null)dto.setCategoryName(category.getName());
            return dto;
        }).collect(Collectors.toList());
        pageDto.setRecords(list);
        return R.success(pageDto);
    }

    @PostMapping
    public R<String> insert(@RequestBody DishDto dto) {
        dishService.saveWithFlavors(dto);
        return R.success("插入新菜品成功");
    }

    @GetMapping("/{dishId}")
    public R<DishDto> find(@PathVariable Long dishId) {
        DishDto dish = dishService.getByIdWithFlavor(dishId);

        return R.success(dish);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dto){
        dishService.updateWithFlavors(dto);
        return R.success("修改成功");
    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        List<Dish>list = dishService.list(queryWrapper);

        List<DishDto>dtoList = list.stream().map(item->{
           DishDto dishDto = new DishDto();
           BeanUtils.copyProperties(item,dishDto);
           LambdaQueryWrapper<DishFlavor>queryWrapper1 = new LambdaQueryWrapper<>();
           queryWrapper1.eq(DishFlavor::getDishId,item.getId());
           List<DishFlavor> list1 = dishFlavorService.list(queryWrapper1);
           dishDto.setFlavors(list1);
           return dishDto;
        }).collect(Collectors.toList());

        return R.success(dtoList);
    }
}
