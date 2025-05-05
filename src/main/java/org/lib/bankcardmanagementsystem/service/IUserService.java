package org.lib.bankcardmanagementsystem.service;

import org.lib.bankcardmanagementsystem.entity.User;

public interface IUserService {
    User getUserById(Long userId);
    User getUserByEmail(String email);
}
