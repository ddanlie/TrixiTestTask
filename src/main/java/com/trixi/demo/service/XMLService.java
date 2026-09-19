package com.trixi.demo.service;

import com.trixi.demo.entities.Municipality;
import com.trixi.demo.repository.XMLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

@Service
public class XMLService
{

    private final XMLRepository xmlRepository;
    @Autowired
    public XMLService(XMLRepository xmlRepository) {
        this.xmlRepository = xmlRepository;
    }
    public CompletableFuture<Void> AsyncDownloadAndSaveFromURL(String url) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenCompose(response -> {
                    if (response.statusCode() != 200) {
                        return CompletableFuture.failedFuture(
                                new RuntimeException("Download failed: HTTP " + response.statusCode())
                        );
                    }
                    return CompletableFuture.supplyAsync(() -> saveToTempFile(response.body()));
                })
                .thenAcceptAsync(zipFile -> {
                    try (InputStream fis = Files.newInputStream(zipFile);
                         ZipInputStream zis = new ZipInputStream(fis)) {

                        // first file inside the zip archive
                        ZipEntry entry = zis.getNextEntry();
                        if (entry != null) {
                            xmlRepository.parseAndSave(zis);
                            zis.closeEntry();
                        } else {
                            throw new RuntimeException("ZIP file was empty");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Error processing zip contents", e);
                    } finally {
                        try {
                            Files.deleteIfExists(zipFile);
                        } catch (Exception ignored) {}
                    }
                });
    }
    private Path saveToTempFile(InputStream inputStream) {
        try {
            Path tempFile = Files.createTempFile("xml-data", ".zip");
            try (inputStream) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save temp file", e);
        }
    }

    public Page<Municipality> getMunicipalitiesPage(int page, int size)
    {
        return this.xmlRepository.getMunicipalitiesPage(page, size);
    }
}
