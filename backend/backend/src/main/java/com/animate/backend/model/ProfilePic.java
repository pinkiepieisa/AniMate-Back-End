package com.animate.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "pictures")
@Getter
@Setter
public class ProfilePic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private String imageUuid;
    @OneToOne 
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    

    public ProfilePic() {}

    public ProfilePic(String imageUrl, User user) {
        this.imageUrl = imageUrl;
        this.user = user;
    }

    public ProfilePic(String imageUrl, String imageUuid, User user) {
        this.imageUrl = imageUrl;
        this.imageUuid = imageUuid;
        this.user = user;
    }
}
    

