package co.com.powerup.ags.authentication.r2dbc.mapper;

import co.com.powerup.ags.authentication.model.user.User;
import co.com.powerup.ags.authentication.model.user.valueobjects.Email;
import co.com.powerup.ags.authentication.model.user.valueobjects.Password;
import co.com.powerup.ags.authentication.model.user.valueobjects.PhoneNumber;
import co.com.powerup.ags.authentication.r2dbc.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    
    UserEntityMapper INSTANCE = Mappers.getMapper(UserEntityMapper.class);
    
    @Mapping(target = "phoneNumber", source = "phoneNumber.value")
    @Mapping(target = "email", source = "email.value")
    @Mapping(target = "password", expression = "java(user.password() != null ? user.password().hashedPassword() : null)")
    @Mapping(target = "isNew", constant = "true")
    UserEntity toEntity(User user);
    
    @Mapping(target = "phoneNumber", expression = "java(new PhoneNumber(entity.getPhoneNumber()))")
    @Mapping(target = "email", expression = "java(new Email(entity.getEmail()))")
    @Mapping(target = "password", expression = "java(entity.getPassword() != null ? Password.fromStoredData(entity.getPassword()) : null)")
    User toDomain(UserEntity entity);
    
    @Mapping(target = "phoneNumber", source = "phoneNumber.value")
    @Mapping(target = "email", source = "email.value")
    @Mapping(target = "password", expression = "java(user.password() != null ? user.password().hashedPassword() : null)")
    @Mapping(target = "isNew", constant = "false")
    UserEntity toExistingEntity(User user);
}