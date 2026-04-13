package com.ecom.order.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Set<UserRole> role;
    private AddressDTO address;
}
