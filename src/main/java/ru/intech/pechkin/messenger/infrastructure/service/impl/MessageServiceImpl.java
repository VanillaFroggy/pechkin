package ru.intech.pechkin.messenger.infrastructure.service.impl;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.*;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.*;
import ru.intech.pechkin.messenger.infrastructure.service.MessageService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.*;
import ru.intech.pechkin.messenger.infrastructure.service.mapper.MessengerServiceMapper;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
                dto.getChatId(),
                messageRepository.findAllByChatIdOrderByDateTimeDesc(
                        dto.getChatId(),
                        PageRequest.of(dto.getPageNumber(), dto.getPageSize())
                ));
    }

    @Override
    public MessageDto sendMessage(@Valid SendMessageDto dto) {
        return getMessageSendingResponse(
                mapper.sendMessageDtoToSendOrReplyToMessageDto(
                        dto,
                        null
                )
        );
    }

    @Override
    public Page<MessageDto> getPageOfMessagesAfterLastCheckedMessage(@Valid GetPageOfMessagesAfterLastCheckedMessageDto dto) {
        return getMessageDtoPage(
                dto.getUserId(),
                dto.getChatId(),
                messageRepository.findAllByChatIdAndDateTimeAfterOrderByDateTime(
                        dto.getChatId(),
                        getLastCheckedMessageDateTime(dto.getUserId(), dto.getChatId()),
                        PageRequest.of(dto.getPageNumber(), dto.getPageSize())
                )
        );
    }

    @Override
    public Page<MessageDto> getPageOfMessagesBeforeDateTime(GetPageOfMessagesBeforeDateTimeDto dto) {
        return getMessageDtoPage(
                dto.getUserId(),
                dto.getChatId(),
                messageRepository.findAllByChatIdAndDateTimeBeforeOrderByDateTime(
                        dto.getChatId(),
                        dto.getDateTime(),
                        PageRequest.of(dto.getPageNumber(), dto.getPageSize())
                )
        );
    }

    private ZonedDateTime getLastCheckedMessageDateTime(UUID userId, UUID chatId) {
        int count = 0;
        Page<UserChatCheckedMessage> page;
        List<Message> messages = new ArrayList<>();
        do {
            page = userChatCheckedMessageRepository.findAllByUserIdAndChatIdAndChecked(
                    userId,
                    chatId,
                    true,
                    PageRequest.of(count, 2_000)
            );
            messages.add(
                    messageRepository.findFirstByChatIdAndIdInOrderByDateTimeDesc(
                            chatId,
                            page.getContent()
                                    .stream()
                                    .map(UserChatCheckedMessage::getMessageId)
                                    .toList()
                    )
            );
            count++;
        } while (!page.isLast());
        return messages.stream()
                .max(Comparator.comparing(Message::getDateTime))
                .map(Message::getDateTime)
                .orElseThrow(NullPointerException::new);
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
    public Page<MessageDto> findMessagesByValue(@Valid FindMessagesByValueDto dto) {
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
            FindMessagesByValueDto dto,
            Page<MessageData> messageDataPage,
            List<ChatMessageDataMessage> chatMessageDataMessages
    ) {
        List<Message> messages = messageRepository.findAllByChatIdAndIdIn(
                dto.getChatId(),
                chatMessageDataMessages.stream()
                        .map(ChatMessageDataMessage::getMessageId)
                        .toList()
        );
        List<UserChatCheckedMessage> userChatCheckedMessages =
                userChatCheckedMessageRepository.findAllByUserIdAndChatIdAndMessageIdIn(
                        dto.getUserId(),
                        dto.getChatId(),
                        messages.stream()
                                .map(Message::getId)
                                .toList()
                ).orElseThrow(NullPointerException::new);
        return getMessageDtoListForSearchingByMessageValue(messageDataPage, messages, userChatCheckedMessages);
    }

    @NonNull
    private List<MessageDto> getMessageDtoListForSearchingByMessageValue(
            Page<MessageData> messageDataPage,
            List<Message> messages,
            List<UserChatCheckedMessage> userChatCheckedMessages
    ) {
        return messageDataPage.stream()
                .map(messageData ->
                        messages.stream()
                                .filter(message -> message.getDatas().contains(messageData))
                                .findFirst()
                                .orElseThrow(NullPointerException::new)
                )
                .map(message -> convertMessageToDto(
                        message,
                        userChatCheckedMessages.stream()
                                .filter(userChatCheckedMessage ->
                                        userChatCheckedMessage.getMessageId().equals(message.getId()))
                                .findFirst()
                                .orElseThrow(NullPointerException::new)
                                .getChecked()
                ))
                .distinct()
                .sorted(Comparator.comparing(MessageDto::getDateTime).reversed())
                .toList();
    }

    @Override
    public MessageDto replyToMessage(@Valid ReplyToMessageDto dto) {
        return getMessageSendingResponse(
                mapper.sendMessageDtoToSendOrReplyToMessageDto(
                        mapper.replyToMessageDtoToSendMessageDto(dto),
                        messageRepository.findById(dto.getMessageToReplyId())
                                .orElseThrow(NullPointerException::new)
                )
        );
    }

    @Override
    public MessageDto editMessage(@Valid EditMessageDto dto) {
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
        return convertMessageToDto(message, null);
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

    private void addMessageDataToEditingMessage(
            EditMessageDto dto,
            List<ChatMessageDataMessage> chatMessageDataMessages
    ) {
        dto.getDatas().forEach(messageData -> {
            if (messageData.getId() == null) {
                messageData.setId(UUID.randomUUID());
                chatMessageDataMessages.add(
                        ChatMessageDataMessage.create(
                                dto.getChatId(),
                                dto.getMessageId(),
                                messageData.getId()
                        )
                );
            }
        });
        messageDataRepository.saveAll(dto.getDatas());
    }

    @Override
    public void deleteMessage(DeleteMessageDto dto) {
        Message message = messageRepository.findByIdAndChatId(dto.getMessageId(), dto.getChatId())
                .orElseThrow(NullPointerException::new);
        messageDataRepository.deleteAllById(
                message.getDatas()
                        .stream()
                        .map(MessageData::getId)
                        .toList()
        );
        chatMessageDataMessageRepository.deleteAllByMessageIdAndChatId(dto.getMessageId(), dto.getChatId());
        userChatCheckedMessageRepository.deleteAllByMessageIdAndChatId(dto.getMessageId(), dto.getChatId());
        messageRepository.delete(message);
    }

    @Override
    public void deleteAllMessagesById(DeleteAllMessagesByIdDto dto) {
        List<Message> messages = messageRepository.findAllByChatIdAndIdIn(dto.getChatId(), dto.getMessageIds());
        messageDataRepository.deleteAllById(
                messages.parallelStream()
                        .flatMap(message -> message.getDatas().parallelStream())
                        .map(MessageData::getId)
                        .toList()
        );
        chatMessageDataMessageRepository.deleteAllByMessageIdInAndChatId(dto.getMessageIds(), dto.getChatId());
        userChatCheckedMessageRepository.deleteAllByMessageIdInAndChatId(dto.getMessageIds(), dto.getChatId());
        messageRepository.deleteAllById(dto.getMessageIds());
    }

    private PageImpl<MessageDto> getMessageDtoPage(UUID userId, UUID chatId, Page<Message> messagePage) {
        List<UserChatCheckedMessage> userChatCheckedMessages =
                userChatCheckedMessageRepository.findAllByUserIdAndChatIdAndMessageIdIn(
                        userId,
                        chatId,
                        messagePage.get()
                                .map(Message::getId)
                                .toList()
                ).orElseThrow(NullPointerException::new);
        return new PageImpl<>(
                messagePage.stream()
                        .map(message -> convertMessageToDto(
                                message,
                                userChatCheckedMessages.stream()
                                        .filter(userChatCheckedMessage ->
                                                userChatCheckedMessage.getMessageId().equals(message.getId()))
                                        .findFirst()
                                        .orElseThrow(NullPointerException::new)
                                        .getChecked()
                        ))
                        .sorted(Comparator.comparing(MessageDto::getDateTime))
                        .toList(),
                messagePage.getPageable(),
                messagePage.getTotalElements()
        );
    }

    private MessageDto convertMessageToDto(Message message, Boolean checked) {
        MessagePublisherDto publisherDto = null;
        if (message.getPublisher() != null) {
            publisherDto = mapper.userToMessagePublisherDto(
                    userRepository.findById(message.getPublisher())
                            .orElseThrow(NullPointerException::new)
            );
        }
        MessageDto relatesTo = null;
        if (message.getRelatesTo() != null) {
            relatesTo = mapper.messageToMessageDto(
                    message.getRelatesTo(),
                    mapper.userToMessagePublisherDto(
                            userRepository.findById(message.getRelatesTo().getPublisher())
                                    .orElseThrow(NullPointerException::new)
                    ),
                    true,
                    null
            );
        }
        return mapper.messageToMessageDto(
                message,
                publisherDto,
                checked,
                relatesTo
        );
    }

    private MessageDto getMessageSendingResponse(SendOrReplyToMessageDto dto) {
        List<MessageData> datas = dto.getDataDtos()
                .stream()
                .map(dataDto -> mapper.messageDataDtoToEntity(
                        UUID.randomUUID(),
                        dataDto)
                )
                .toList();
        Message message = mapper.sendOrReplyToMessageDtoToEntity(
                dto,
                UUID.randomUUID(),
                datas,
                ZonedDateTime.now(ZoneOffset.UTC),
                false
        );
        chatMessageDataMessageRepository.saveAll(
                datas.stream()
                        .map(messageData ->
                                ChatMessageDataMessage.create(
                                        message.getChatId(),
                                        message.getId(),
                                        messageData.getId()
                                )
                        )
                        .toList()
        );
        messageDataRepository.saveAll(datas);
        messageRepository.save(message);

        createUserChatCheckedMessageForEveryUserInChat(message);

        return convertMessageToDto(message, false);
    }

    private void createUserChatCheckedMessageForEveryUserInChat(Message message) {
        List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats =
                userRoleMutedPinnedChatRepository.findAllByChatId(message.getChatId());
        userChatCheckedMessageRepository.saveAll(
                userRoleMutedPinnedChats.stream()
                        .map(userRoleMutedPinnedChat ->
                                UserChatCheckedMessage.create(
                                        userRoleMutedPinnedChat.getUserId(),
                                        message.getChatId(),
                                        message.getId()
                                )
                        )
                        .toList()
        );
    }
}
