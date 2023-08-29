package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段填充。。。[insert]");
        log.info(this.getClass().getName()+metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentID());
        metaObject.setValue("createUser", BaseContext.getCurrentID());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段填充。。。[update]");
        log.info(this.getClass().getName()+metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentID());
    }
}
