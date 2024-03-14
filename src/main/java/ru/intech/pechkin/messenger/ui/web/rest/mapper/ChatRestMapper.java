package ru.intech.pechkin.messenger.ui.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.messenger.infrastructure.service.dto.chat.*;
import ru.intech.pechkin.messenger.ui.web.rest.dto.chat.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface ChatRestMapper {
  GetPageOfChatsDto getPageOfChatsRequestToDto(GetPageOfChatsRequest request);

  GetChatByIdAndUserIdDto getChatByIdAndUserIdRequestToDto(GetChatByIdAndUserIdRequest request);

  GetP2PChatByUsersDto getP2PChatByUsersRequestToDto(GetP2PChatByUsersRequest request);

  CreateP2PChatDto createP2PChatRequestToDto(CreateP2PChatRequest request);

  CreateGroupChatDto createGroupChatRequestToDto(CreateGroupChatRequest request);

  UpdateGroupChatDto updateGroupChatRequestToDto(UpdateGroupChatRequest request);

  UpdateChatMutedOrPinnedStatusDto updateChatMutedOrPinnedStatusRequestToDto(UpdateChatMutedOrPinnedStatusRequest request);
}
