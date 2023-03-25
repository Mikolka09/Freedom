package itstep.social_freedom.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "invites")
public class Invite extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_from_id")
    private User userFrom;

    @ManyToOne
    @JoinColumn(name = "user_to_id")
    private User userTo;

    @OneToMany(mappedBy = "invite")
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private Set<Alert> alerts = new HashSet<>();

    @OneToMany(mappedBy = "invite")
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private Set<Message> messages = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

}
