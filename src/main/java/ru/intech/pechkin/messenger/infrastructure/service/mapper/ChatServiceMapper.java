package ru.intech.pechkin.messenger.infrastructure.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Chat;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.MessageData;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Role;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.User;
import ru.intech.pechkin.messenger.infrastructure.service.dto.ChatDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.MessageDataDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.UserWithRoleDto;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatServiceMapper {
    UserWithRoleDto userAndRoleToUserWithRoleDto(User user, Role role);

    ChatDto chatToChatDto(Chat chat);

    MessageData MessageDataDtoToEntity(UUID id, MessageDataDto dto);
}
