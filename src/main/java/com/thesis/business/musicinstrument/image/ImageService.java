package com.thesis.business.musicinstrument.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.thesis.business.musicinstrument.product.Product;

@Singleton
public class ImageService {

    String UPLOAD_DIR = "C:\\Users\\PC\\Desktop\\programing\\luanvan\\music-instrument-frontend\\src\\assets\\image";

    @Inject
    ImageRepository imageRepository;

    public void add(String path, Product product) {
        Image image = new Image();
        image.setPath(path);
        image.setProduct(product);
        imageRepository.persist(image);
    }

    public void deleteByProductId(Long productId) {
        imageRepository.delete("product.id", productId);
    }

    public List<Image> getAll() {
        return imageRepository.listAll();
    }

    public List<Image> getByProductId(Long id) {
        return imageRepository.find("product.id", id).list();
    }

    public String uploadFile(MultipartFormDataInput input, Product product) {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<String> fileNames = new ArrayList<>();
        List<InputPart> inputParts = uploadForm.get("file");
        if (!inputParts.isEmpty()) {
            String fileName = null;
            deleteByProductId(product.getId());
            for (InputPart inputPart : inputParts) {
                try {
                    MultivaluedMap<String, String> header = inputPart.getHeaders();
                    fileName = getFileName(header);
                    fileNames.add(fileName);
                    InputStream inputStream = inputPart.getBody(InputStream.class, null);
                    writeFile(inputStream, fileName, product);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "Files Successfully Uploaded";
        }
        return "";
    }

    private void writeFile(InputStream inputStream, String fileName, Product product) throws IOException {

        byte[] bytes = IOUtils.toByteArray(inputStream);
        File customDir = new File(UPLOAD_DIR);
        fileName = customDir.getAbsolutePath() + File.separator
                + (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000) + fileName;
        Files.write(Paths.get(fileName), bytes, StandardOpenOption.CREATE_NEW);
        add(fileName, product);
    }

    private String getFileName(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "";
    }
}
