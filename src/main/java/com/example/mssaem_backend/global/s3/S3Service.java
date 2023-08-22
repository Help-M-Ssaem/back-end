package com.example.mssaem_backend.global.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.example.mssaem_backend.global.s3.dto.S3Result;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import marvin.image.MarvinImage;
import org.imgscalr.Scalr;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
@Service
public class S3Service {

    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

    public List<S3Result> uploadFile(List<MultipartFile> multipartFiles) {
        List<S3Result> fileList = new ArrayList<>();

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileList에 추가
        multipartFiles.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                s3Client.putObject(
                    new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "파일 업로드에 실패했습니다.");
            }
            fileList.add(new S3Result(s3Client.getUrl(bucket, fileName).toString()));
        });
        return fileList;
    }

    public void deleteFile(String fileName) {
        s3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    public String parseFileName(String imgURl) {
        String[] st = imgURl.split("/");
        return st[st.length - 1];
    }

    public static File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }
        return file;
    }

    //파일 하나만 받아 S3에 저장 후 url 반환
    public String uploadImage(MultipartFile multipartFile) throws IOException {
        S3Result fileResult;
        // multipartFile를 File로 변경
        File imageFile  = convert(multipartFile);

        int orientation = 1;
        Metadata metadata;
        ExifIFD0Directory directory;

        // 회전 했는지 체크
        try {
            metadata = ImageMetadataReader.readMetadata(imageFile);
            directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if(directory != null){
                orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }
        }catch (Exception e) {
            orientation=1;
        }

        // 회전 되어 있으면 원상태로 돌린다.
        BufferedImage bfImage = ImageIO.read(imageFile);
        BufferedImage srcImg = bfImage;
        switch (orientation) {
            case 1:
                break;
            case 3:
                srcImg = Scalr.rotate(bfImage, Scalr.Rotation.CW_180, null);
                break;
            case 6:
                srcImg = Scalr.rotate(bfImage, Scalr.Rotation.CW_90, null);
                break;
            case 8:
                srcImg = Scalr.rotate(bfImage, Scalr.Rotation.CW_270, null);
                break;
            default:
                orientation = 1;
                break;
        }

        // 파일을 format 알아내기
        String fileFormatName = multipartFile.getContentType()
            .substring(multipartFile.getContentType().lastIndexOf("/") + 1);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(srcImg, fileFormatName, byteArrayOutputStream);

        // 회전시킨 BufferedImage를 다시 multipartFile로 변경
        CustomMultipartFile customMultipartFile = new CustomMultipartFile(
            byteArrayOutputStream.toByteArray(), multipartFile.getName(),
            multipartFile.getOriginalFilename(),
            multipartFile.getContentType(), multipartFile.getSize());

        // Resizing
        MultipartFile resizeMultipartFile = resizeImage(customMultipartFile.getName(), fileFormatName,
            customMultipartFile, 480);

        String fileName = createFileName(resizeMultipartFile.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(resizeMultipartFile.getSize());
        objectMetadata.setContentType(resizeMultipartFile.getContentType());

        try (InputStream inputStream = resizeMultipartFile.getInputStream()) {
            s3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }
        fileResult = new S3Result(s3Client.getUrl(bucket, fileName).toString());
        return fileResult.getImgUrl();
    }

    // 사진을 resize 시키기
    MultipartFile resizeImage(String fileName, String fileFormatName, MultipartFile originalImage,
        int targetWidth) {
        try {
            // MultipartFile -> BufferedImage Convert
            BufferedImage image = ImageIO.read(originalImage.getInputStream());

            int originWidth = image.getWidth();
            int originHeight = image.getHeight();
            double ratio = (double) originHeight / originWidth;
            int targetHeight = (int) (targetWidth * ratio);

            if (originWidth < targetWidth) {
                return originalImage;
            }

            MarvinImage imageMarvin = new MarvinImage(image);

            Scale scale = new Scale();
            scale.load();
            scale.setAttribute("newWidth", targetWidth);
            scale.setAttribute("newHeight", targetHeight);
            scale.process(imageMarvin.clone(), imageMarvin, null, null, false);

            BufferedImage imageNoAlpha = imageMarvin.getBufferedImageNoAlpha();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imageNoAlpha, fileFormatName, baos);
            baos.flush();
            return new CustomMultipartFile(baos.toByteArray(), fileName,
                originalImage.getOriginalFilename(), fileFormatName, baos.toByteArray().length);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 리사이즈에 실패했습니다.");
        }
    }
}
