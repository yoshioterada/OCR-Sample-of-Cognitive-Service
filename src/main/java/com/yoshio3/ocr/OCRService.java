/*
* Copyright 2016 Yoshio Terada
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
 */
package com.yoshio3.ocr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Yoshio Terada
 */
public class OCRService {

    private final static String BASE_URI = "https://api.projectoxford.ai/vision/v1.0/ocr?language=unk&detectOrientation=true";
    private final String subscriptionID;
    private final Invocation.Builder invokeBuilder;

    /* Constructor */
    public OCRService(String subscriptionID) {
        this.subscriptionID = subscriptionID;
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(BASE_URI);
        invokeBuilder = target.request(MediaType.APPLICATION_JSON)
                .header("Ocp-Apim-Subscription-Key", subscriptionID);
    }

    /* 
      Get the result of OCR from specified URI
     */
    public Optional<OCRResponseJSONBody> getOCRAnalysisResult(String pictURI) {
        OCRRequestJSONBody entity = new OCRRequestJSONBody();
        entity.setUrl(pictURI);
        Response response = invokeBuilder.post(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE));
        if (checkReqeuestSuccess(response)) {
            OCRResponseJSONBody result = response.readEntity(OCRResponseJSONBody.class);
            return Optional.of(result);
        } else {
            printErrorMessage(response);
            return Optional.empty();
        }
    }

    /* 
      Get the result of OCR from file
     */
    public Optional<OCRResponseJSONBody> getOCRAnalysisResult(Path path) throws IOException {
        byte[] readAllBytes = Files.readAllBytes(path);
        return getOCRAnalysisResult(readAllBytes);
    }

    /* 
      Get the result of OCR from binary data
     */
    public Optional<OCRResponseJSONBody> getOCRAnalysisResult(byte[] binaryImage) throws IOException {
        Response response = invokeBuilder.post(Entity.entity(binaryImage, MediaType.APPLICATION_OCTET_STREAM_TYPE));
        if (checkReqeuestSuccess(response)) {
            OCRResponseJSONBody result = response.readEntity(OCRResponseJSONBody.class);
            return Optional.of(result);
        } else {
            printErrorMessage(response);
            return Optional.empty();
        }
    }

    /* 
      Print out the Result of OCR to STDOUT 
     */
    public void printOCRResult(OCRResponseJSONBody body) {
        String language = body.getLanguage();
        String orientation = body.getOrientation();
        String textAngle = body.getTextAngle();

        List<Regions> regions = body.getRegions();
        regions.stream().forEach(region -> {
            List<Lines> lines = region.getLines();
            lines.stream().forEach(line -> {
                List<Words> words = line.getWords();
                words.stream().forEach(word -> {
                    String text = word.getText();
                    System.out.print(text);
                });
                System.out.println("");
            });
        });
    }

    /* 
      Print out the Result of OCR to STDOUT 
     */
    private void printErrorMessage(Response response) {
        OCRResponseError error = response.readEntity(OCRResponseError.class);
        Logger.getLogger(OCRService.class.getName()).log(Level.SEVERE, "{0}:{1}", new Object[]{error.getCode(), error.getMessage()});
    }

    /*
      Evaluate the response 
    true : SUCESS
    false : FAILED
    */
    private boolean checkRequestSuccess(Response response) {
        Response.StatusType statusInfo = response.getStatusInfo();
        Response.Status.Family family = statusInfo.getFamily();
        return family != null && family == Response.Status.Family.SUCCESSFUL;
    }

}
