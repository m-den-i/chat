package dbaccess;

import javax.persistence.*;

/**
 * Created by denis on 24.5.15.
 */

@Entity
@Table(name = "messages", schema = "", catalog = "chat")
@NamedQueries({
        @NamedQuery(name = "messagesByUserId",
                query = "select mes " +
                        "from MessagesEntity mes " +
                        "where mes.userId = ?1"),
        @NamedQuery(name = "messagesById",
                query = "select mes " +
                        "from MessagesEntity mes " +
                        "where mes.id = ?1"),
        @NamedQuery(name = "messages",
                query = "select mes " +
                        "from MessagesEntity mes ")
})
public class MessagesEntity {
    private int id;
    private String textdate;
    private Integer userId;
    private UsersEntity usersByUserId;

    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "textdate", nullable = true, insertable = true, updatable = true, length = 65535)
    public String getTextdate() {
        return textdate;
    }

    public void setTextdate(String textdate) {
        this.textdate = textdate;
    }

    @Basic
    @Column(name = "user_id", nullable = true, insertable = true, updatable = true)
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessagesEntity that = (MessagesEntity) o;

        if (id != that.id) return false;
        if (textdate != null ? !textdate.equals(that.textdate) : that.textdate != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (textdate != null ? textdate.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    public UsersEntity getUsersByUserId() {
        return usersByUserId;
    }

    public void setUsersByUserId(UsersEntity usersByUserId) {
        this.usersByUserId = usersByUserId;
    }
}
