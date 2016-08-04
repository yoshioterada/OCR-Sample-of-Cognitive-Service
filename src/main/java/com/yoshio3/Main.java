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
package com.yoshio3;

import com.yoshio3.ocr.OCRResponseJSONBody;
import com.yoshio3.ocr.OCRService;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the sample of Microsoft Cognitive Service
 * Computer Vision API - v1.0 (OCR)
 *
 * https://dev.projectoxford.ai/docs/services/56f91f2d778daf23d8ec6739/operations/56f91f2e778daf14a499e1fc
 * 
 * @author Yoshio Terada
 */
public class Main {
    // Please get the Subscription from
    // https://www.microsoft.com/cognitive-services/en-us/computer-vision-api
    // Or Microsoft Azure Portal
    // https://ms.portal.azure.com
    private final static String SUBSCRIPTION_ID = "*******************************************";

    public static void main(String argv[]) {
        OCRService service = new OCRService(SUBSCRIPTION_ID);

        // GET the result of OCR from specified URI
        String pictURI = "http://businessnetwork.jp/Portals/0/SP2016/PSTN/img/1604_microsoft_top.jpg";
        Optional<OCRResponseJSONBody> result = service.getOCRAnalysisResult(pictURI);
        result.ifPresent(resultBody -> service.printOCRResult(resultBody));
        
        //GET the result of OCR from the local file
        try {
            Optional<OCRResponseJSONBody> result2 = service.getOCRAnalysisResult(Paths.get("/Users/tyoshio2002/Downloads/aaa.jpeg"));
            result2.ifPresent(resultBody -> service.printOCRResult(resultBody));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
