package com.ecom.user.service;


import com.ecom.user.dto.AddressDTO;
import com.ecom.user.dto.RegisterRequest;
import com.ecom.user.dto.UserRequest;
import com.ecom.user.dto.UserResponse;
import com.ecom.user.model.Address;
import com.ecom.user.model.User;
import com.ecom.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService  implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    public boolean userExist(String userName) {
        return userRepository.existsByUserName(userName);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getUserFromRequest(RegisterRequest registerReq,String encodedPassword) {
        User user = new User();

        user.setFirstName(registerReq.getFirstName());
        user.setLastName(registerReq.getLastName());
        user.setUserName(registerReq.getUserName());
        user.setEmail(registerReq.getEmail());
        user.setPassword(encodedPassword);
        user.setPhone(registerReq.getPhone());

        //setup address
        if(registerReq.getAddress() != null){
            Address address = new Address();
            address.setStreet(registerReq.getAddress().getStreet());
            address.setCity(registerReq.getAddress().getCity());
            address.setState(registerReq.getAddress().getState());
            address.setCountry(registerReq.getAddress().getCountry());
            address.setZipcode(registerReq.getAddress().getZipcode());
            user.setAddress(address);
        }
        return user;
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public List<UserResponse> fetchAllUsers() {
        return userRepository.findAll().stream()
                .map(this :: mapToUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRoles());

        if(user.getAddress() != null){
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setState(user.getAddress().getState());
            addressDTO.setCountry(user.getAddress().getCountry());
            addressDTO.setZipcode(user.getAddress().getZipcode());
            response.setAddress(addressDTO);
        }
        return response;
    }

    public Optional<UserResponse> fetchUser(Long id) {
        return userRepository.findById(id).map(this::mapToUserResponse);
    }

    public boolean updateUser(Long id, UserRequest updatedUserRequest) {
       return userRepository.findById(id)
                .map(existingUser -> {
                    updateUserFromRequest(existingUser,updatedUserRequest);
                    userRepository.save(existingUser);
                    return true;
                }).orElse(false);
    }

    private void updateUserFromRequest(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        if (userRequest.getAddress() != null) {
            Address address = new Address();
            address.setStreet(userRequest.getAddress().getStreet());
            address.setState(userRequest.getAddress().getState());
            address.setZipcode(userRequest.getAddress().getZipcode());
            address.setCity(userRequest.getAddress().getCity());
            address.setCountry(userRequest.getAddress().getCountry());
            user.setAddress(address);
        }
    }
}
