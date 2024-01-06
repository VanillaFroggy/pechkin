package ru.intech.pechkin.messenger.infrastructure.service.mapper;

import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Chat;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Role;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.User;
import ru.intech.pechkin.messenger.infrastructure.service.dto.ChatDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.messenger.infrastructure.service.dto.UserWithRoleDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatServiceMapper {
    UserWithRoleDto userAndRoleToUserWithRoleDto(User user, Role role);

    ChatDto chatToChatDto(Chat chat);
}
