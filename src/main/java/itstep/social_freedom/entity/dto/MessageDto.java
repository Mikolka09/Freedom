package itstep.social_freedom.entity.dto;


import itstep.social_freedom.entity.Message;
import itstep.social_freedom.entity.User;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class MessageDto {

    private Long id;

    private User user;

    private HashMap<Long,List<Message>> outMessages;

    private HashMap<Long,List<Message>> inMessages;

}
