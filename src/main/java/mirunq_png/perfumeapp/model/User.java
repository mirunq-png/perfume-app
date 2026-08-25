package mirunq_png.perfumeapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name="prfm_utilizatori")
public class User
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="user_id")
    private int id;

    @Column(name="username",nullable=false,unique=true)
    private String username;

    @JsonIgnore // passw field will be excluded from serialization
    @Column(name="password",nullable=false)
    private String password;

    public User(){}
    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
