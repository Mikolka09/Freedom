package itstep.social_freedom.entity;

import lombok.Data;

@Data
public class EmailContext {
    private String from;
    private String to;
    private String subject;
    private String templateLocation;
    private String context;
}
