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
import java.util.*;
import java.util.stream.Collectors;

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
    public Page<MessageDto> getPageOfMessagesBeforeDateTime(@Valid GetPageOfMessagesBeforeDateTimeDto dto) {
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
    public void setMessageChecked(@Valid SetMessageCheckedDto dto) {
        List<UserChatCheckedMessage> userChatCheckedMessages =
                userChatCheckedMessageRepository.findAllByUserIdInAndChatIdAndMessageIdAndChecked(
                        getUsersIdSetToSetMessagesChecked(
                                dto.getUserId(),
                                dto.getPublisherId() == null ? null : List.of(dto.getPublisherId())
                        ),
                        dto.getChatId(),
                        dto.getMessageId(),
                        false
                );
        checkCheckedMessagesListEmptiness(userChatCheckedMessages);
        userChatCheckedMessages.forEach(userChatCheckedMessage -> userChatCheckedMessage.setChecked(true));
        userChatCheckedMessageRepository.saveAll(userChatCheckedMessages);
    }

    @Override
    public void setMessageListChecked(@Valid SetMessageListCheckedDto dto) {
        List<UserChatCheckedMessage> userChatCheckedMessages =
                userChatCheckedMessageRepository.findAllByUserIdInAndChatIdAndMessageIdInAndChecked(
                        getUsersIdSetToSetMessagesChecked(dto.getUserId(), dto.getMessagesWithPublishers().values()),
                        dto.getChatId(),
                        dto.getMessagesWithPublishers().keySet(),
                        false
                );
        checkCheckedMessagesListEmptiness(userChatCheckedMessages);
        userChatCheckedMessages.forEach(userChatCheckedMessage -> userChatCheckedMessage.setChecked(true));
        userChatCheckedMessageRepository.saveAll(userChatCheckedMessages);
    }

    private static Set<UUID> getUsersIdSetToSetMessagesChecked(UUID userId, Collection<UUID> publishers) {
        if (publishers == null || publishers.stream().noneMatch(Objects::nonNull)) {
            return Set.of(userId);
        }
        Set<UUID> userIds = new HashSet<>(publishers);
        userIds.add(userId);
        return userIds;
    }

    private static void checkCheckedMessagesListEmptiness(List<UserChatCheckedMessage> userChatCheckedMessages) {
        if (userChatCheckedMessages.isEmpty()) {
            throw new IllegalArgumentException("Looks like you have no message to read");
        }
    }

    @Override
    public Page<MessageDto> findMessagesByValue(@Valid FindMessagesByValueDto dto) {
        Map<UUID, UUID> chatMessageDataMessagesMap = chatMessageDataMessageRepository.findAllByChatId(
                        dto.getChatId(),
                        PageRequest.of(dto.getPageNumber(), dto.getPageSize())
                ).orElseThrow(NullPointerException::new)
                .get()
                .collect(Collectors.toMap(ChatMessageDataMessage::getMessageDataId, ChatMessageDataMessage::getMessageId));
        Page<MessageData> messageDataPage = messageDataRepository.findAllByIdInAndValueLikeIgnoreCase(
                chatMessageDataMessagesMap.keySet(),
                dto.getValue(),
                PageRequest.of(dto.getPageNumber(), dto.getPageSize())
        ).orElseThrow(NullPointerException::new);
        List<MessageDto> messageDtos = getDistinctAndSortedMessageListFoundByValue(
                dto,
                messageDataPage,
                chatMessageDataMessagesMap
        );
        return new PageImpl<>(
                messageDtos,
                PageRequest.of(dto.getPageNumber(), dto.getPageSize()),
                chatMessageDataMessagesMap.size()
        );
    }

    @NonNull
    private List<MessageDto> getDistinctAndSortedMessageListFoundByValue(
            FindMessagesByValueDto dto,
            Page<MessageData> messageDataPage,
            Map<UUID, UUID> chatMessageDataMessagesMap
    ) {
        Map<UUID, Message> messages = messageRepository.findAllByChatIdAndIdIn(
                        dto.getChatId(),
                        chatMessageDataMessagesMap.values()
                ).stream()
                .collect(Collectors.toMap(Message::getId, message -> message));
        List<UserChatCheckedMessage> userChatCheckedMessages =
                userChatCheckedMessageRepository.findAllByUserIdAndChatIdAndMessageIdIn(
                        dto.getUserId(),
                        dto.getChatId(),
                        messages.keySet()
                ).orElseThrow(NullPointerException::new);
        return getMessageDtoListForSearchingByMessageValue(
                messageDataPage,
                messages,
                userChatCheckedMessages,
                chatMessageDataMessagesMap
        );
    }

    @NonNull
    private List<MessageDto> getMessageDtoListForSearchingByMessageValue(
            Page<MessageData> messageDataPage,
            Map<UUID, Message> messages,
            List<UserChatCheckedMessage> userChatCheckedMessages,
            Map<UUID, UUID> chatMessageDataMessagesMap
    ) {
        Map<UUID, User> users = userRepository.findAllById(getSetOfPublishersForMessages(messages.values()))
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        Map<UUID, UserChatCheckedMessage> userChatCheckedMessageMap = userChatCheckedMessages.stream()
                .collect(Collectors.toMap(
                        UserChatCheckedMessage::getMessageId,
                        userChatCheckedMessage -> userChatCheckedMessage
                ));
        return messageDataPage.stream()
                .map(messageData -> messages.get(chatMessageDataMessagesMap.get(messageData.getId())))
                .map(message -> mapper.wrapMessageToMessageDto(
                        message,
                        users,
                        userChatCheckedMessageMap.get(message.getId()).getChecked()
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
        User user = userRepository.findById(message.getPublisher())
                .orElseThrow(NullPointerException::new);
        return mapper.wrapMessageToMessageDto(
                message,
                Map.of(user.getId(), user),
                null
        );
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
    public void deleteMessage(@Valid DeleteMessageDto dto) {
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
    public void deleteAllMessagesById(@Valid DeleteAllMessagesByIdDto dto) {
        List<Message> messages = messageRepository.findAllByChatIdAndIdIn(dto.getChatId(), dto.getMessageIds());
        messageDataRepository.deleteAllById(
                messages.stream()
                        .flatMap(message -> message.getDatas().stream())
                        .map(MessageData::getId)
                        .collect(Collectors.toSet())
        );
        chatMessageDataMessageRepository.deleteAllByMessageIdInAndChatId(dto.getMessageIds(), dto.getChatId());
        userChatCheckedMessageRepository.deleteAllByMessageIdInAndChatId(dto.getMessageIds(), dto.getChatId());
        messageRepository.deleteAllById(dto.getMessageIds());
    }

    private PageImpl<MessageDto> getMessageDtoPage(UUID userId, UUID chatId, Page<Message> messagePage) {
        Map<UUID, UserChatCheckedMessage> userChatCheckedMessages =
                userChatCheckedMessageRepository.findAllByUserIdAndChatIdAndMessageIdIn(
                                userId,
                                chatId,
                                messagePage.get()
                                        .map(Message::getId)
                                        .toList()
                        ).orElseThrow(NullPointerException::new)
                        .stream()
                        .collect(Collectors.toMap(
                                UserChatCheckedMessage::getMessageId,
                                userChatCheckedMessage -> userChatCheckedMessage
                        ));
        Map<UUID, User> users = userRepository.findAllById(getSetOfPublishersForMessages(messagePage.getContent()))
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        return new PageImpl<>(
                messagePage.stream()
                        .map(message ->
                                mapper.wrapMessageToMessageDto(
                                        message,
                                        users,
                                        userChatCheckedMessages.get(message.getId()).getChecked()
                                )
                        )
                        .sorted(Comparator.comparing(MessageDto::getDateTime))
                        .toList(),
                messagePage.getPageable(),
                messagePage.getTotalElements()
        );
    }

    private static Set<UUID> getSetOfPublishersForMessages(Collection<Message> messages) {
        Set<UUID> userIds = messages.stream()
                .map(Message::getPublisher)
                .collect(Collectors.toSet());
        Set<UUID> relatesToUserIds = messages.stream()
                .filter(message -> message.getRelatesTo() != null)
                .map(message -> message.getRelatesTo().getPublisher())
                .collect(Collectors.toSet());
        if (!relatesToUserIds.isEmpty()) {
            userIds.addAll(relatesToUserIds);
        }
        return userIds;
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

        Map<UUID, User> users = null;
        if (message.getPublisher() != null) {
            User user = userRepository.findById(message.getPublisher())
                    .orElseThrow(NullPointerException::new);
            users = Map.of(user.getId(), user);
        }

        return mapper.wrapMessageToMessageDto(
                message,
                users,
                false
        );
    }

    private void createUserChatCheckedMessageForEveryUserInChat(Message message) {
        userChatCheckedMessageRepository.saveAll(
                userRoleMutedPinnedChatRepository.findAllByChatId(message.getChatId())
                        .stream()
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
