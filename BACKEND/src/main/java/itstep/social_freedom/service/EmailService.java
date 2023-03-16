package itstep.social_freedom.service;


import itstep.social_freedom.entity.EmailContext;

import javax.mail.MessagingException;

public interface EmailService {
    void sendSimpleEmail(EmailContext context) throws MessagingException;
}
