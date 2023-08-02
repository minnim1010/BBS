package spring.bbs.media.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String originalName;
    private String storedPathName;
    @NotNull
    private long postId;

    public Media(String originalName, String storedPathName, long postId) {
        this.originalName = originalName;
        this.storedPathName = storedPathName;
        this.postId = postId;
    }
}
