package itstep.social_freedom.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "invites")
public class Invite extends BaseEntity {

    @Column(name = "userFrom")
    private User userFrom;

    @Column(name = "userTo")
    private User userTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

}
