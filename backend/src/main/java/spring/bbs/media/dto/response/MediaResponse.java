package spring.bbs.media.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MediaResponse {
    private long id;
    private String originalName;
    private String storedPathName;

    public MediaResponse(long id, String originalName, String storedPathName) {
        this.id = id;
        this.originalName = originalName;
        this.storedPathName = storedPathName;
    }
}
