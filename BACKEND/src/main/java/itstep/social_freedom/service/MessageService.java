package itstep.social_freedom.service;

import itstep.social_freedom.entity.Message;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.entity.dto.MessageDto;
import itstep.social_freedom.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageRepository messageRepository;

    public MessageDto allUserMessages(Long idTo) {
        MessageDto messageDto = new MessageDto();
        messageDto.setId(idTo);
        HashMap<Long, List<Message>> inMessages = new HashMap<>();
        HashMap<Long, List<Message>> outMessages = new HashMap<>();
        List<Message> messages = findAllMessagesUserById(idTo);
        giveHashMapMessages(inMessages, messages);
        messages = findAllMessagesOutUserById(idTo);
        giveHashMapMessages(outMessages, messages);
        messageDto.setUser(userService.findUserById(idTo));
        messageDto.setInMessages(inMessages);
        messageDto.setOutMessages(outMessages);
        return messageDto;
    }

    private void giveHashMapMessages(HashMap<Long, List<Message>> outMessages, List<Message> messages) {
        for (Message msgOne : messages) {
            List<Message> messagesOut = new ArrayList<>();
            for (Message msg : messages) {
                if (Objects.equals(msg.getInvite().getUserFrom().getId(), msgOne.getInvite().getUserFrom().getId()))
                    messagesOut.add(msg);
            }
            outMessages.put(msgOne.getInvite().getUserFrom().getId(), messagesOut.stream()
                    .sorted(Comparator.comparing(Message::getCreatedAt).reversed()).collect(Collectors.toList()));
        }
    }

    public List<Message> findAllDeletedMessagesUserById(Long userId) {
        return messageRepository.findAll().stream()
                .filter(x -> Objects.equals(x.getInvite().getUserTo().getId(), userId) && x.getStatus() == Status.DELETED)
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed()).collect(Collectors.toList());
    }

    public Message findMessageById(Long id) {
        return messageRepository.findMessageById(id);
    }

    public List<Message> findAllMessagesUserById(Long userId) {
        return messageRepository.findAll().stream()
                .filter(x -> Objects.equals(x.getInvite().getUserTo().getId(), userId) && x.getStatus() != Status.DELETED)
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed()).collect(Collectors.toList());
    }

    public List<Message> findAllMessagesOutUserById(Long userId) {
        return messageRepository.findAll().stream()
                .filter(x -> Objects.equals(x.getInvite().getUserFrom().getId(), userId) && x.getStatus() != Status.DELETED)
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed()).collect(Collectors.toList());
    }

    public boolean saveMessage(Message message) {
        Message messageBD = messageRepository.findMessageById(message.getId());
        if (messageBD != null) {
            if (!Objects.equals(messageBD.getStatus(), message.getStatus()))
                return false;
        }
        messageRepository.save(message);
        return true;
    }

    public void deleteMessage(Long id) {
        if (messageRepository.findById(id).isPresent()) {
            Message message = messageRepository.findById(id).orElse(new Message());
            message.setStatus(Status.DELETED);
            messageRepository.save(message);
        }
    }
}
