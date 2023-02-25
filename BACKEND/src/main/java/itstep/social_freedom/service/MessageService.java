package itstep.social_freedom.service;

import itstep.social_freedom.entity.Alert;
import itstep.social_freedom.entity.Message;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> allMessages() {
        return messageRepository.findAll();
    }

    public Message findMessageById(Long id) {
        return messageRepository.findMessageById(id);
    }

    public List<Message> findAllMessagesUserById(Long userId) {
        return messageRepository.findAll().stream()
                .filter(x -> Objects.equals(x.getInvite().getUserTo().getId(), userId))
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed()).collect(Collectors.toList());
    }

    public List<Message> findAllMessagesUserFromById(Long idFrom, Long idTo) {
        return messageRepository.findAll().stream()
                .filter(x -> Objects.equals(x.getInvite().getUserFrom().getId(), idFrom))
                .filter(x->Objects.equals(x.getInvite().getUserTo().getId(), idTo))
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
