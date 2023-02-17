package itstep.social_freedom.entity;


import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Alert extends BaseEntity {

    private String text;

    @ManyToOne
    @JoinColumn(name = "invite_id")
    private Invite invite;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Alert alert = (Alert) o;
        return getId() != null && Objects.equals(getId(), alert.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
