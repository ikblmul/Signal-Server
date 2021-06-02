/*
 * Copyright 2013-2020 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package org.whispersystems.textsecuregcm.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import org.xmlpull.v1.XmlPullParserException;
import io.minio.MinioClient;
import io.minio.errors.MinioException;

import java.net.URL;
import java.util.Date;

public class UrlSigner {

  private static final long   DURATION = 60 * 60 * 1000;

//  private final AWSCredentials credentials;

  private final String endpoint;
  private final String accessKey;
  private final String accessSecret;
  private final String bucket;

  public UrlSigner(String endpoint, String accessKey, String accessSecret, String bucket) {
    this.endpoint     = endpoint;
    this.accessKey    = accessKey;
    this.accessSecret = accessSecret;
    this.bucket       = bucket;
  }

//  public URL getPreSignedUrl(long attachmentId, HttpMethod method, boolean unaccelerated) {
////    AmazonS3                    client  = new AmazonS3Client(credentials);
//    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, String.valueOf(attachmentId), method);
//
//    request.setExpiration(new Date(System.currentTimeMillis() + DURATION));
//    request.setContentType("application/octet-stream");
//
//    if (unaccelerated) {
//      client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
//    } else {
//      client.setS3ClientOptions(S3ClientOptions.builder().setAccelerateModeEnabled(true).build());
//    }
//
//    return client.generatePresignedUrl(request);
//  }

  public String getPreSignedUrl(long attachmentId, HttpMethod method) throws InvalidKeyException, NoSuchAlgorithmException, IOException, XmlPullParserException, MinioException {
    String request = geturl(bucket, String.valueOf(attachmentId), method);
    return request;
  }

  public String geturl(String bucketname, String attachmentId, HttpMethod method) throws NoSuchAlgorithmException, IOException, InvalidKeyException, XmlPullParserException, MinioException {

    String url = null;
    MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey,accessSecret).build();
    try {
      if(method==HttpMethod.PUT){
        url = minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.PUT)
                .bucket(bucketname)
                .object(attachmentId)
                .expiry(60 * 60 * 24)
                .build());
      }
      if(method==HttpMethod.GET){
        url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
            .method(Method.GET)
            .bucket(bucketname)
            .object(attachmentId)
            .build());
      }
      System.out.println(url);
    } catch(MinioException e) {
      System.out.println("Error occurred: " + e);
    } catch (java.security.InvalidKeyException e) {
      e.printStackTrace();
    }
    return url;
  }
}
