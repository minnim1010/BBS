package spring.bbs.media.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spring.bbs.media.domain.Media;
import spring.bbs.media.dto.response.MediaResponse;
import spring.bbs.media.dto.util.MediaToResponse;
import spring.bbs.media.repository.MediaRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MediaService {

    private final String MEDIA_PATH = "/Users/mjmj/Desktop/bbs/backend/src/main/resources/media/";

    private final MediaRepository mediaRepository;

    @Transactional
    public List<MediaResponse> saveMedia(List<MultipartFile> files, Long postId) throws IOException {
        List<Media> mediaList = new ArrayList<>();

        for (MultipartFile file : files){
            storeMediaToFile(file, postId, mediaList);
        }

        return mediaList.stream().map(MediaToResponse::convertMediaToResponse).toList();
    }

    private void storeMediaToFile(MultipartFile file, Long postId, List<Media> mediaList) throws IOException {
        if(file.isEmpty())
            return;

        String originalName = file.getOriginalFilename();
        String fileExtension = file.getContentType();
        String storedPathName = System.nanoTime() + fileExtension;

        File storedfile = new File(MEDIA_PATH + storedPathName);
        file.transferTo(storedfile);
        mediaList.add(mediaRepository.save(new Media(originalName, storedPathName, postId)));
    }
}
