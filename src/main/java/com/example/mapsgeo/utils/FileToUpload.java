package com.example.mapsgeo.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileToUpload {
    public ResponseEntity<String> upload (String nameDirector, String nameFile, InputStream inputFile) {
        String fileLocation = nameDirector + "/" + nameFile;
        try {
            Files.copy(inputFile, Path.of(fileLocation), StandardCopyOption.REPLACE_EXISTING);
            return new ResponseEntity<String>("Arquivo carregado com sucesso!", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Erro ao carregar arquivo!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
