package com.ecom.user.dto;
import com.ecom.user.model.Role;
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
