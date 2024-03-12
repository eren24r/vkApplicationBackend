package org.vk.backend.facade;

import org.springframework.stereotype.Component;
import org.vk.backend.dto.UserDTO;
import org.vk.backend.entity.user.User;

@Component
public class UserFacade {
    public UserDTO userToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setRoles(user.getRoles());
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        return userDTO;
    }

}
