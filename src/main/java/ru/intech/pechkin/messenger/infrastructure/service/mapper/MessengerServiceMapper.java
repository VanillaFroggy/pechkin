package ru.intech.pechkin.messenger.infrastructure.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.*;
import ru.intech.pechkin.messenger.infrastructure.service.dto.*;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessengerServiceMapper {
    UserWithRoleDto userAndRoleToUserWithRoleDto(User user, Role role);

    ChatDto chatToChatDto(Chat chat);

    MessageData messageDataDtoToEntity(UUID id, MessageDataDto dto);

    MessageDto messageToMessageDto(Message message, Boolean checked);

    UserDto userToUserDto(User user);
}
