package spring.bbs.media.dto.util;

import spring.bbs.media.domain.Media;
import spring.bbs.media.dto.response.MediaResponse;

public class MediaToResponse {
    public static MediaResponse convertMediaToResponse(Media media) {
        return new MediaResponse(media.getId(), media.getOriginalName(), media.getStoredPathName());
    }
}
