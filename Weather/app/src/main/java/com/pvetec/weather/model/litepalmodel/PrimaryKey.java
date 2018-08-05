package com.pvetec.weather.model.litepalmodel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *     author : Relish
 *     e-mail : relish.wang@gmail.com
 *     time   : 2017/05/03
 *     desc   : 逐渐注解
 *     version: 1.0
 * </pre>
 */
@Target(ElementType.FIELD) //注解用在字段上
@Retention(RetentionPolicy.RUNTIME)//不加这个不起作用
public @interface PrimaryKey {
}
