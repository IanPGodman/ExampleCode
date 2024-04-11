package coding.example.database.dto.user;

import coding.example.database.entity.user.User;
import org.mapstruct.Mapper;

@Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDtoWithRoles toDto(User user);
}