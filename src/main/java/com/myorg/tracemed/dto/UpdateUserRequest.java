package com.myorg.tracemed.dto;

import com.myorg.tracemed.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String nomComplet;
    private String email;
    private Role role;
    private Long organisationId;
    private String password; // Optional: only if updating password
}
