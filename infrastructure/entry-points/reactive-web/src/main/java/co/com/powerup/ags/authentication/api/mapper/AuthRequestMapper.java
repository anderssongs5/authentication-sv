package co.com.powerup.ags.authentication.api.mapper;

import co.com.powerup.ags.authentication.api.dto.LoginRequest;
import co.com.powerup.ags.authentication.usecase.auth.dto.LoginCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthRequestMapper {
    
    AuthRequestMapper INSTANCE = Mappers.getMapper(AuthRequestMapper.class);

    @Mapping(target = "email", expression = "java(new Email(request.getEmail()))")
    LoginCommand toRequest(LoginRequest request);
}