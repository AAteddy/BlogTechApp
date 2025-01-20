
package com.tedsaasfaha.blogapplication.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PostStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED;

    @JsonCreator
    public static PostStatus formValue(String value) {
        return PostStatus.valueOf(value.toUpperCase());
    }
}
//