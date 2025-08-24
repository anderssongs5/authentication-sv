package co.com.powerup.ags.authentication.api.mapper;

import co.com.powerup.ags.authentication.api.dto.CreateUserRequest;
import co.com.powerup.ags.authentication.api.dto.UpdateUserRequest;
import co.com.powerup.ags.authentication.usecase.user.dto.CreateUserCommand;
import co.com.powerup.ags.authentication.usecase.user.dto.UpdateUserCommand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserRequestMapper {
    
    UserRequestMapper INSTANCE = Mappers.getMapper(UserRequestMapper.class);
    
    CreateUserCommand toCommand(CreateUserRequest request);
    
    UpdateUserCommand toCommand(UpdateUserRequest request, String id);
}