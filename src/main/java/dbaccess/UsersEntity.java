package dbaccess;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by denis on 24.5.15.
 */

@Entity
@Table(name = "users", schema = "", catalog = "chat")
@NamedQueries({
        @NamedQuery(name = "usersByName",
                query = "select usr " +
                        "from UsersEntity usr " +
                        "where usr.name = ?1"),
        @NamedQuery(name = "users",
                query = "select usr " +
                        "from UsersEntity usr ")
})
public class UsersEntity {
    private int id;
    private String name;
    private Collection<MessagesEntity> messagesesById;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = true, insertable = true, updatable = true, length = 65535)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsersEntity that = (UsersEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "usersByUserId")
    public Collection<MessagesEntity> getMessagesesById() {
        return messagesesById;
    }

    public void setMessagesesById(Collection<MessagesEntity> messagesesById) {
        this.messagesesById = messagesesById;
    }
}
