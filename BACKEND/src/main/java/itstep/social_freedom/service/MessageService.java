package itstep.social_freedom.service;

import itstep.social_freedom.entity.Message;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return messageRepository.findById(id).orElse(new Message());
    }

    public List<Message> findAllMessagesUserById(Long userId) {
        return messageRepository.findAll().stream()
                .filter(x -> Objects.equals(x.getUserRecipient().getId(), userId)).collect(Collectors.toList());
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
