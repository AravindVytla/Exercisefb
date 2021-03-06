package com.exercisefb;

import com.google.api.services.discovery.model.JsonSchema.Variant.Map;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.ColorInfo;
import com.google.cloud.vision.v1.DominantColorsAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DetectProperties {
  public static void detectProperties() throws IOException {
    // TODO(developer): Replace these variables before running the sample.
    //String filePath = "FbExample\\AD6I2485.JPG";
    //detectProperties(filePath);
  }

  // Detects image properties such as color frequency from the specified local image.
  public static void detectProperties(String filePath,LinkedHashMap<String, Float> map) throws IOException {
    List<AnnotateImageRequest> requests = new ArrayList<>();

    ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

    Image img = Image.newBuilder().setContent(imgBytes).build();
    Feature feat = Feature.newBuilder().setType(Feature.Type.IMAGE_PROPERTIES).setMaxResults(256).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
    requests.add(request);

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
      BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
      List<AnnotateImageResponse> responses = response.getResponsesList();

      for (AnnotateImageResponse res : responses) {
        if (res.hasError()) {
          System.out.format("Error: %s%n", res.getError().getMessage());
          return;
        }

        // For full list of available annotations, see http://g.co/cloud/vision/docs
        DominantColorsAnnotation colors = res.getImagePropertiesAnnotation().getDominantColors();
        float red=0,green=0,blue=0;
        for (ColorInfo color : colors.getColorsList()) {
			/*
			 * System.out.format( "fraction: %f%nr: %f, g: %f, b: %f%n",
			 * color.getPixelFraction(), color.getColor().getRed(),
			 * color.getColor().getGreen(), color.getColor().getBlue());
			 */
          red += color.getColor().getRed() * color.getPixelFraction();
          green += color.getColor().getGreen() * color.getPixelFraction();
          blue += color.getColor().getBlue() * color.getPixelFraction();
          //map.put(color.getColor().getRed()+""+color.getColor().getGreen()+""+color.getColor().getBlue(),color.getPixelFraction());
        }
        map.put("red", red);
        map.put("green", green);
        map.put("blue", blue);
      }
    }
  }
}