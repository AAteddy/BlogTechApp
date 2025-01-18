package com.tedsaasfaha.blogapplication.config;


import com.tedsaasfaha.blogapplication.entity.PostStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToPostStatusConverter implements Converter<String, PostStatus> {

    @Override
    public PostStatus convert(String source) {
        return PostStatus.valueOf(source.toUpperCase());
    }
}
