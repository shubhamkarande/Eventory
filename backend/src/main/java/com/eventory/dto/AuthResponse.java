package com.eventory.dto;

import com.eventory.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private UserResponse user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserResponse {
        private String id;
        private String email;
        private String name;
        private String role;
        private String interests;

        public static UserResponse fromUser(User user) {
            return UserResponse.builder()
                    .id(user.getId().toString())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole().name())
                    .interests(user.getInterests())
                    .build();
        }
    }
}
