package com.example.mssaem_backend.domain.discussionoption;

import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionRequestDto.DiscussionReq;
import com.example.mssaem_backend.domain.discussionoption.dto.DiscussionOptionRequestDto.GetOptionReq;
import com.example.mssaem_backend.global.s3.S3Service;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class DiscussionOptionService {

    private final S3Service s3Service;
    private final DiscussionOptionRepository discussionOptionRepository;

    //s3에 이미지 저장 후 DiscussionOption 생성
    public void createOption(Discussion discussion, DiscussionReq discussionReq,
        List<String> imgUrls) {
        //option 리스트 가져오기
        List<GetOptionReq> getOptionReqs = discussionReq.getGetOptionReqs();

        //option 리스트를 돌며 DiscussionOption 생성
        for (GetOptionReq getOptionReq : getOptionReqs) {
            String imgUrl;
            if (getOptionReq.isHasImage() && (imgUrls != null)) {
                imgUrl = imgUrls.get(0);
                imgUrls.remove(0);
            } else {
                imgUrl = null;
            }

            DiscussionOption discussionOption = DiscussionOption.builder().imgUrl(imgUrl)
                .content(getOptionReq.getContent()).discussion(discussion).build();
            discussionOptionRepository.save(discussionOption);
        }
    }

    //s3에 이미지 삭제 후 DiscussionOption 삭제
    public void deleteOption(Discussion discussion) {
        List<DiscussionOption> discussionOptions = discussionOptionRepository.findDiscussionOptionByDiscussion(
            discussion);
        //s3 이미지 삭제
        for (DiscussionOption discussionOption : discussionOptions) {
            if (discussionOption.getImgUrl() != null) {
                s3Service.deleteFile(s3Service.parseFileName(discussionOption.getImgUrl()));
            }
        }
        //해당 discussion에 존재하는 option 삭제
        discussionOptionRepository.deleteAllByDiscussion(discussion);
    }

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        return s3Service.uploadImage(multipartFile);
    }
}
