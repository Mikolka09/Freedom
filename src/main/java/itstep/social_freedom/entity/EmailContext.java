package itstep.social_freedom.entity;

import lombok.Data;

import java.util.HashMap;

@Data
public class EmailContext {
    private String from;
    private String to;
    private String subject;
    private String templateLocation;
    private HashMap<String, Object> context;
}
