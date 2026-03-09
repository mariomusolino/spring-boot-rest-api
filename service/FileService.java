package com.odissey.tour.service;

import com.odissey.tour.exception.Exception400;
import com.odissey.tour.exception.Exception404;
import com.odissey.tour.exception.Exception409;
import com.odissey.tour.exception.Exception500;
import com.odissey.tour.model.dto.response.TourDetailResponse;
import com.odissey.tour.model.entity.Tour;
import com.odissey.tour.model.entity.enumerator.TourStatus;
import com.odissey.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final TourRepository tourRepository;

    @Value("${tour.image.size}")
    long size;
    @Value("${tour.image.mimeTypes}")
    String[] mimeTypes;
    @Value("${tour.image.extensions}")
    String[] extensions;
    @Value("${tour.image.width}")
    int width;
    @Value("${tour.image.height}")
    int height;
    @Value("${tour.image.path}")
    String path;


    @Transactional
    public TourDetailResponse uploadImage(int tourId, MultipartFile file){
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(()-> new Exception404("Tour non trovato con id "+tourId));
        if(!tour.getStatus().equals(TourStatus.WORK_IN_PROGRESS))
            throw new Exception409("Il tour è ormai pubblicato, non si può cambiare l'immagine.");

        if(!checkIsNotEmpty(file))
            throw new Exception400("Il file è vuoto.");

        if(!checkAnyExtension(file))
            throw new Exception400("Estensione del file non permessa");

        if(!checkSize(file, size))
            throw new Exception400("Il file supera la dimesione di "+size);

        if(!checkDimension(file, width, height))
            throw new Exception400("Il file non è delle dimensioni di "+width+"px X "+height+"px");

        if(!checkMimeType(file, mimeTypes))
            throw new Exception400("Il file non è del tipo consentito");

        String fileToUpload = uploadFile(file, tour.getImage());
        tour.setImage(fileToUpload);
        tourRepository.save(tour);

        return TourDetailResponse.fromEntityToDto(tour);
    }


    private boolean checkIsNotEmpty(MultipartFile file){
        return !file.isEmpty();
    }

    private boolean checkSize(MultipartFile file, long size){
        return file.getSize() < size;
    }

    private BufferedImage fromMultipartFileToBufferedImage(MultipartFile file){
        try{
            return ImageIO.read(file.getInputStream());
        } catch (IOException e){
            throw new Exception400("File non valido");
        }
    }

    private boolean checkDimension(MultipartFile file, int width, int height){
        BufferedImage bf = fromMultipartFileToBufferedImage(file);
        if(bf != null)
            return bf.getWidth() == width && bf.getHeight() == height;
        return false;
    }


    private boolean checkMimeType(MultipartFile file, String[] mimeTypes){
        String trueMimeType = getTrueMimeType(file);
        for(String s : mimeTypes){
            if(s.equals(trueMimeType))
                return true;
        }
        return false;
    }

    private String getTrueMimeType(MultipartFile file){
        Tika tika = new Tika();
        // uso il try-with-resource in modo da essere sicuro che l'inputStream venga chiuso
        try(InputStream inputStream = file.getInputStream()){
            return tika.detect(inputStream);
        } catch (IOException e){
            return null;
        }
    }

    private String uploadFile(MultipartFile file, String oldFile){
        // 000.jpg -> eiuw-09jfkwemf-vlpelvp.jpg
        String originalFileName = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String newFileName = UUID.randomUUID().toString()+"."+extension;

        try {
            if(oldFile != null){
                Path destinationPathToDelete = Paths.get(path + oldFile);
                Files.delete(destinationPathToDelete);
            }
            Path destinationPath = Paths.get(path + newFileName);
            Files.write(destinationPath, file.getBytes());
        } catch (IOException e){
            log.error(">>> "+e.getMessage());
            throw  new Exception500("Impossibile caricare il file");
        }
        return newFileName;
    }

    private boolean checkAnyExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if(extension == null || extension.isEmpty())
            throw new Exception404("Il file è privo di estensione");
        for(String s : extensions){
            if(s.equals(extension))
                return true;
        }
        return false;
    }

}

