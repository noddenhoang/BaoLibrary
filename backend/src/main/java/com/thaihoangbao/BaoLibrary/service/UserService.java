package com.thaihoangbao.BaoLibrary.service;

import com.thaihoangbao.BaoLibrary.dto.PagedResponse;
import com.thaihoangbao.BaoLibrary.dto.UserDTO;
import com.thaihoangbao.BaoLibrary.entity.User;

public interface UserService {
    UserDTO getUserById(Integer id);
    
    UserDTO getUserByUsername(String username);
    
    PagedResponse<UserDTO> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir);
    
    UserDTO createUser(UserDTO userDTO);
    
    UserDTO updateUser(Integer id, UserDTO userDTO);
    
    void deleteUser(Integer id);
    
    void setUserStatus(Integer id, String status);
    
    boolean checkIfUserCanBorrow(Integer userId);
    
    User getUserEntityById(Integer id);
}
