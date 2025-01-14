
package com.tedsaasfaha.blogapplication.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostResponseDTO {

    private Long id;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    private AuthorResponseDTO author;

    @Getter
    @Setter
    public static class AuthorResponseDTO {
        // Nested DTO for author details
        private Long id;
        private String email;
        private String name;
        private String role;
    }
}
//