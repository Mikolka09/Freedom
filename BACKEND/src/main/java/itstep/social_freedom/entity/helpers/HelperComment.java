package itstep.social_freedom.entity.helpers;


import lombok.Data;

@Data
public class HelperComment {

    private String avatarUrl;

    private String fullName;

    private String createDate;

    private int comments;

    private String body;
}
