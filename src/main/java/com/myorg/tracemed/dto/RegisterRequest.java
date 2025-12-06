package com.myorg.tracemed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String nomComplet;
    private String email;
    private com.myorg.tracemed.entity.Role role;
    private Long organisationId;
}
