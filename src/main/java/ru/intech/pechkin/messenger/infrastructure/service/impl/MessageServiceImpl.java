package ru.intech.pechkin.messenger.infrastructure.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.*;
import ru.intech.pechkin.messenger.infrastructure.persistance.repo.*;
import ru.intech.pechkin.messenger.infrastructure.service.MessageService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.*;
import ru.intech.pechkin.messenger.infrastructure.service.mapper.MessengerServiceMapper;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final UserRoleMutedChatRepository userRoleMutedChatRepository;
    private final MessageRepository messageRepository;
    private final MessageDataRepository messageDataRepository;
    private final ChatMessageDataMessageRepository chatMessageDataMessageRepository;
    private final UserChatCheckedMessageRepository userChatCheckedMessageRepository;
    private final MessengerServiceMapper mapper;

    @Override
    public Page<MessageDto> getPageOfMessages(GetPageOfMessagesDto dto) {
        return getMessageDtoPage(
                dto.getUserId(),
                messageRepository.findAllByChatIdOrderByDateTimeDesc(
                        dto.getChatId(),
                        PageRequest.of(dto.getPageNumber(), dto.getPageSize())
                )
        );
    }

    @Override
    public MessageSendingResponse sendMessage(SendMessageDto dto) {
        List<MessageData> datas = dto
                .getDataDtos()
                .stream()
                .map(dataDto -> mapper.messageDataDtoToEntity(
                        UUID.randomUUID(),
                        dataDto)
                )
                .toList();
        Message message = Message.builder()
                .id(UUID.randomUUID())
                .chatId(dto.getChatId())
                .publisher(dto.getUserId())
                .datas(datas)
                .dateTime(dto.getDateTime())
                .edited(false)
                .build();
        return getMessageSendingResponse(datas, message);
    }

    @Override
    public Page<MessageDto> updateMessageList(UpdateMessageListDto dto) {
        return getMessageDtoPage(
                dto.getUserId(),
                messageRepository.findAllByChatIdAndDateTimeAfterOrderByDateTimeDesc(
                        dto.getChatId(),
                        dto.getDateTime(),
                        PageRequest.of(dto.getPageNumber(), dto.getPageSize())
                )
        );
    }

    @Override
    public void setMessageChecked(SetMessageCheckedDto dto) {
        List<UserChatCheckedMessage> userChatCheckedMessages =
                userChatCheckedMessageRepository.findAllByUserIdInAndChatIdAndMessageIdAndChecked(
                        List.of(dto.getUserId(), dto.getPublisherId()),
                        dto.getChatId(),
                        dto.getMessageId(),
                        false
                );
        userChatCheckedMessages.forEach(userChatCheckedMessage -> userChatCheckedMessage.setChecked(true));
        userChatCheckedMessageRepository.saveAll(userChatCheckedMessages);
    }

    @Override
    public Page<MessageDto> findMessageByValue(FindMessageByValueDto dto) {
        List<ChatMessageDataMessage> chatMessageDataMessages = chatMessageDataMessageRepository.findAllByChatId(
                dto.getChatId(),
                PageRequest.of(dto.getPageNumber(), dto.getPageSize())
        ).orElseThrow(NullPointerException::new).getContent();
        List<UUID> messageDataIds = chatMessageDataMessages.stream()
                .map(ChatMessageDataMessage::getMessageDataId)
                .toList();
        Page<MessageData> messageDataPage = messageDataRepository.findAllByIdInAndValueLikeIgnoreCase(
                messageDataIds,
                dto.getValue(),
                PageRequest.of(dto.getPageNumber(), dto.getPageSize())
        ).orElseThrow(NullPointerException::new);
        List<MessageDto> messageDtos = messageDataPage.stream()
                .map(messageData -> messageRepository.findById(chatMessageDataMessages.stream()
                                .filter(chatMessageDataMessage ->
                                        chatMessageDataMessage.getMessageDataId()
                                                .equals(messageData.getId()))
                                .findFirst()
                                .orElseThrow(NullPointerException::new)
                                .getMessageId())
                        .orElseThrow(NullPointerException::new))
                .map(message -> mapper.messageToMessageDto(
                        message,
                        userChatCheckedMessageRepository.findByUserIdAndChatIdAndMessageId(
                                dto.getUserId(),
                                dto.getChatId(),
                                message.getId()
                        ).orElseThrow(NullPointerException::new).getChecked()
                ))
                .distinct()
                .sorted(Comparator.comparing(MessageDto::getDateTime).reversed())
                .toList();
        return new PageImpl<>(
                messageDtos,
                PageRequest.of(dto.getPageNumber(), dto.getPageSize()),
                messageDtos.size()
        );
    }

    @Override
    public MessageSendingResponse replyToMessage(ReplyToMessageDto dto) {
        List<MessageData> datas = dto
                .getDataDtos()
                .stream()
                .map(dataDto -> mapper.messageDataDtoToEntity(
                        UUID.randomUUID(),
                        dataDto)
                )
                .toList();
        Message message = Message.builder()
                .id(UUID.randomUUID())
                .chatId(dto.getChatId())
                .publisher(dto.getUserId())
                .datas(datas)
                .relatesTo(messageRepository.findById(dto.getMessageToReplyId())
                        .orElseThrow(NullPointerException::new))
                .dateTime(dto.getDateTime())
                .edited(false)
                .build();
        return getMessageSendingResponse(datas, message);
    }

    @Override
    public void editMessage(EditMessageDto dto) {
        List<ChatMessageDataMessage> chatMessageDataMessages =
                chatMessageDataMessageRepository.findAllByChatIdAndMessageId(
                        dto.getChatId(),
                        dto.getMessageId()
                );
        chatMessageDataMessages.forEach(chatMessageDataMessage -> {
            MessageData filteredDto = dto.getDatas().stream()
                    .filter(messageData -> messageData.getId()
                            .equals(chatMessageDataMessage.getMessageDataId()))
                    .findFirst()
                    .orElse(null);
            if (filteredDto == null) {
                messageDataRepository.deleteById(chatMessageDataMessage.getMessageDataId());
                chatMessageDataMessageRepository.delete(chatMessageDataMessage);
            }
        });
        chatMessageDataMessages.removeIf(chatMessageDataMessage -> !dto.getDatas()
                .stream()
                .map(MessageData::getId)
                .toList()
                .contains(chatMessageDataMessage.getMessageDataId())
        );
        dto.getDatas().forEach(messageData -> {
            if (messageData.getId() == null) {
                messageData.setId(UUID.randomUUID());
                chatMessageDataMessages.add(
                        ChatMessageDataMessage.builder()
                                .id(UUID.randomUUID())
                                .chatId(dto.getChatId())
                                .messageId(dto.getMessageId())
                                .messageDataId(messageData.getId())
                                .build()
                );
            }
            messageDataRepository.save(messageData);
        });
        chatMessageDataMessageRepository.saveAll(chatMessageDataMessages);
        Message message = messageRepository.findByIdAndChatId(dto.getMessageId(), dto.getChatId())
                .orElseThrow(NullPointerException::new);
        message.setDatas(dto.getDatas());
        message.setEdited(true);
        messageRepository.save(message);
    }

    @Override
    public void deleteMessage(DeleteMessageDto dto) {
        Message message = messageRepository.findByIdAndChatId(dto.getMessageId(), dto.getChatId())
                .orElseThrow(NullPointerException::new);
        messageDataRepository.deleteAllByIdIn(
                message.getDatas()
                        .stream()
                        .map(MessageData::getId)
                        .toList()
        );
        chatMessageDataMessageRepository.deleteAllByMessageIdAndChatId(dto.getMessageId(), dto.getChatId());
        messageRepository.delete(message);
    }

    private PageImpl<MessageDto> getMessageDtoPage(UUID userId, Page<Message> messagePage) {
        return new PageImpl<>(
                messagePage.stream()
                        .map(message ->
                                mapper.messageToMessageDto(
                                        message,
                                        userChatCheckedMessageRepository.findByUserIdAndChatIdAndMessageId(
                                                        userId,
                                                        message.getChatId(),
                                                        message.getId())
                                                .orElseThrow(NullPointerException::new)
                                                .getChecked()
                                ))
                        .sorted(Comparator.comparing(MessageDto::getDateTime))
                        .toList(),
                messagePage.getPageable(),
                messagePage.getTotalElements()
        );
    }

    private MessageSendingResponse getMessageSendingResponse(List<MessageData> datas, Message message) {
        datas.forEach(messageData -> chatMessageDataMessageRepository.save(
                ChatMessageDataMessage.builder()
                        .id(UUID.randomUUID())
                        .chatId(message.getChatId())
                        .messageId(message.getId())
                        .messageDataId(messageData.getId())
                        .build()
        ));
        messageDataRepository.saveAll(datas);
        messageRepository.save(message);
        List<UserRoleMutedChat> userRoleMutedChats = userRoleMutedChatRepository.findAllByChatId(message.getChatId());
        userRoleMutedChats.forEach(userRoleMutedChat -> userChatCheckedMessageRepository.save(
                UserChatCheckedMessage.builder()
                        .id(UUID.randomUUID())
                        .userId(userRoleMutedChat.getUserId())
                        .chatId(message.getChatId())
                        .messageId(message.getId())
                        .checked(false)
                        .build()
        ));
        return new MessageSendingResponse(message.getId(), message.getDatas());
    }
}
