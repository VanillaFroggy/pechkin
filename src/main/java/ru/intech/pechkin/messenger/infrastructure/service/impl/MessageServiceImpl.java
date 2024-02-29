package ru.intech.pechkin.messenger.infrastructure.service.impl;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.*;
import ru.intech.pechkin.messenger.infrastructure.persistance.repo.*;
import ru.intech.pechkin.messenger.infrastructure.service.MessageService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.*;
import ru.intech.pechkin.messenger.infrastructure.service.mapper.MessengerServiceMapper;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final UserRoleMutedPinnedChatRepository userRoleMutedPinnedChatRepository;
    private final MessageRepository messageRepository;
    private final MessageDataRepository messageDataRepository;
    private final ChatMessageDataMessageRepository chatMessageDataMessageRepository;
    private final UserChatCheckedMessageRepository userChatCheckedMessageRepository;
    private final UserRepository userRepository;
    private final MessengerServiceMapper mapper;

    @Override
    public Page<MessageDto> getPageOfMessages(@Valid GetPageOfMessagesDto dto) {
        return getMessageDtoPage(
                dto.getUserId(),
                messageRepository.findAllByChatIdOrderByDateTimeDesc(
                        dto.getChatId(),
                        PageRequest.of(dto.getPageNumber(), dto.getPageSize())
                )
        );
    }

    @Override
    public MessageSendingResponse sendMessage(@Valid SendMessageDto dto) {
        return getMessageSendingResponse(
                mapper.sendMessageDtoToSendOrReplyToMessageDto(
                        dto,
                        null
                )
        );
    }

    @Override
    public Page<MessageDto> updateMessageList(@Valid UpdateMessageListDto dto) {
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
    public Page<MessageDto> findMessageByValue(@Valid FindMessageByValueDto dto) {
        List<ChatMessageDataMessage> chatMessageDataMessages = chatMessageDataMessageRepository.findAllByChatId(
                dto.getChatId(),
                PageRequest.of(dto.getPageNumber(), dto.getPageSize())
        ).orElseThrow(NullPointerException::new).getContent();
        Page<MessageData> messageDataPage = messageDataRepository.findAllByIdInAndValueLikeIgnoreCase(
                chatMessageDataMessages.stream()
                        .map(ChatMessageDataMessage::getMessageDataId)
                        .toList(),
                dto.getValue(),
                PageRequest.of(dto.getPageNumber(), dto.getPageSize())
        ).orElseThrow(NullPointerException::new);
        List<MessageDto> messageDtos = getDistinctAndSortedMessageListFoundByValue(
                dto,
                messageDataPage,
                chatMessageDataMessages
        );
        return new PageImpl<>(
                messageDtos,
                PageRequest.of(dto.getPageNumber(), dto.getPageSize()),
                messageDtos.size()
        );
    }

    @NonNull
    private List<MessageDto> getDistinctAndSortedMessageListFoundByValue(
            FindMessageByValueDto dto,
            Page<MessageData> messageDataPage,
            List<ChatMessageDataMessage> chatMessageDataMessages
    ) {
        List<Message> messages = messageRepository.findAllByChatIdAndIdIn(
                dto.getChatId(),
                chatMessageDataMessages.stream()
                        .map(ChatMessageDataMessage::getMessageId)
                        .toList()
        );
        return messageDataPage.stream()
                .map(messageData ->
                        messages.stream()
                                .filter(message -> message.getDatas().contains(messageData))
                                .findFirst()
                                .orElseThrow(NullPointerException::new)
                )
                .map(message -> convertMessageToDto(message, dto.getUserId()))
                .distinct()
                .sorted(Comparator.comparing(MessageDto::getDateTime).reversed())
                .toList();
    }

    @Override
    public MessageSendingResponse replyToMessage(@Valid ReplyToMessageDto dto) {
        return getMessageSendingResponse(
                mapper.sendMessageDtoToSendOrReplyToMessageDto(
                        mapper.replyToMessageDtoToSendMessageDto(dto),
                        messageRepository.findById(dto.getMessageToReplyId())
                                .orElseThrow(NullPointerException::new)
                )
        );
    }

    @Override
    public void editMessage(@Valid EditMessageDto dto) {
        List<ChatMessageDataMessage> chatMessageDataMessages =
                chatMessageDataMessageRepository.findAllByChatIdAndMessageId(
                        dto.getChatId(),
                        dto.getMessageId()
                );
        removeMessageDataFromEditingMessage(dto, chatMessageDataMessages);
        addMessageDataToEditingMessage(dto, chatMessageDataMessages);
        chatMessageDataMessageRepository.saveAll(chatMessageDataMessages);
        Message message = messageRepository.findByIdAndChatId(dto.getMessageId(), dto.getChatId())
                .orElseThrow(NullPointerException::new);
        message.setDatas(dto.getDatas());
        message.setEdited(true);
        messageRepository.save(message);
    }

    private void removeMessageDataFromEditingMessage(EditMessageDto dto, List<ChatMessageDataMessage> chatMessageDataMessages) {
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
    }

    private void addMessageDataToEditingMessage(EditMessageDto dto, List<ChatMessageDataMessage> chatMessageDataMessages) {
        dto.getDatas().forEach(messageData -> {
            if (messageData.getId() == null) {
                messageData.setId(UUID.randomUUID());
                chatMessageDataMessages.add(
                        createChatMessageDataMessage(
                                dto.getChatId(),
                                dto.getMessageId(),
                                messageData.getId()
                        )
                );
            }
            messageDataRepository.save(messageData);
        });
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
        userChatCheckedMessageRepository.deleteAllByMessageIdAndChatId(dto.getMessageId(), dto.getChatId());
        messageRepository.delete(message);
    }

    private PageImpl<MessageDto> getMessageDtoPage(UUID userId, Page<Message> messagePage) {
        return new PageImpl<>(
                messagePage.stream()
                        .map(message -> convertMessageToDto(message, userId))
                        .sorted(Comparator.comparing(MessageDto::getDateTime))
                        .toList(),
                messagePage.getPageable(),
                messagePage.getTotalElements()
        );
    }

    private MessageDto convertMessageToDto(Message message, UUID userId) {
        MessagePublisherDto publisherDto = null;
        if (message.getPublisher() != null) {
            publisherDto = mapper.userToMessagePublisherDto(
                    userRepository.findById(message.getPublisher())
                            .orElseThrow(NullPointerException::new)
            );
        }
        return mapper.messageToMessageDto(
                message,
                publisherDto,
                userChatCheckedMessageRepository.findByUserIdAndChatIdAndMessageId(
                        userId,
                        message.getChatId(),
                        message.getId()
                ).orElseThrow(NullPointerException::new).getChecked()
        );
    }

    private MessageSendingResponse getMessageSendingResponse(SendOrReplyToMessageDto dto) {
        List<MessageData> datas = dto.getDataDtos()
                .stream()
                .map(dataDto -> mapper.messageDataDtoToEntity(
                        UUID.randomUUID(),
                        dataDto)
                )
                .toList();
        Message message = mapper.sendOrReplyToMessageDtoToEntity(dto, UUID.randomUUID(), datas, false);
        datas.forEach(messageData -> chatMessageDataMessageRepository.save(
                createChatMessageDataMessage(
                        message.getChatId(),
                        message.getId(),
                        messageData.getId()
                )
        ));
        messageDataRepository.saveAll(datas);
        messageRepository.save(message);

        createUserChatCheckedMessageForEveryUserInChat(message);

        return new MessageSendingResponse(message.getId(), message.getDatas());
    }

    private ChatMessageDataMessage createChatMessageDataMessage(UUID chatId, UUID messageId, UUID messageDataId) {
        return ChatMessageDataMessage.builder()
                .id(UUID.randomUUID())
                .chatId(chatId)
                .messageId(messageId)
                .messageDataId(messageDataId)
                .build();
    }

    private void createUserChatCheckedMessageForEveryUserInChat(Message message) {
        List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats = userRoleMutedPinnedChatRepository.findAllByChatId(message.getChatId());
        userRoleMutedPinnedChats.forEach(userRoleMutedPinnedChat -> userChatCheckedMessageRepository.save(
                UserChatCheckedMessage.builder()
                        .id(UUID.randomUUID())
                        .userId(userRoleMutedPinnedChat.getUserId())
                        .chatId(message.getChatId())
                        .messageId(message.getId())
                        .checked(false)
                        .build()
        ));
    }
}
