package com.hotel.dao;
import com.hotel.model.User; import java.util.Optional;
public interface UserDao { Optional<User> findByEmail(String email); }
