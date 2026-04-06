package com.ecom.auth_service.dto;
import com.ecom.auth_service.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
        private Long id;
        //private String keyCloakId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private Set<Role> role;
        private AddressDTO address;
}
