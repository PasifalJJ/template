package com.jsc.convert;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 将字符串转换为日期
 */
public class MyConvert implements Converter<String, Date> {
    public Date convert(String s) {
        //2012-12-12
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(s);
            return date;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

