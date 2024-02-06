package ru.intech.pechkin.messenger.infrastructure.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    @Mapping(target = "id", source = "message.id")
    @Mapping(target = "publisher", source = "publisherDto")
    MessageDto messageToMessageDto(Message message, MessagePublisherDto publisherDto, Boolean checked);

    MessagePublisherDto userToMessagePublisherDto(User user);

    UserDto userToUserDto(User user);
}
