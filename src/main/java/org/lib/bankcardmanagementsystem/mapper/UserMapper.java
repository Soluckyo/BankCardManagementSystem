package org.lib.bankcardmanagementsystem.mapper;

import org.lib.bankcardmanagementsystem.dto.UserDto;
import org.lib.bankcardmanagementsystem.entity.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserid(user.getIdUser());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().toString());
        return dto;
    }
}
