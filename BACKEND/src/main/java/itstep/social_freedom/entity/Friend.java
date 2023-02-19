package itstep.social_freedom.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Friend extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "user_req_id")
    User friendRequester;

    @ManyToOne
    @JoinColumn(name = "user_rec_id")
    User friendReceiver;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Friend friend = (Friend) o;
        return getId() != null && Objects.equals(getId(), friend.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
