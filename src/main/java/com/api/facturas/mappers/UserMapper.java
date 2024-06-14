package com.api.facturas.mappers;

import com.api.facturas.dto.UserCredentialDTO;
import com.api.facturas.dto.UserWrapper;
import com.api.facturas.pojo.User;
import org.springframework.beans.BeanUtils;

public class UserMapper {
    public static User credentials_to_Entity(UserCredentialDTO userCredentials) {
        User user = new User();
        BeanUtils.copyProperties(userCredentials,user);
        return user;
    }
    public static UserWrapper entity_to_Wrapper(User user) {
        UserWrapper userWrapper = new UserWrapper();
        BeanUtils.copyProperties(user,userWrapper);
        return userWrapper;
    }
    public static User wrapper_to_Entity(UserWrapper userWrapper) {
        User user = new User();
        BeanUtils.copyProperties(userWrapper,user);
        return user;
    }
}
