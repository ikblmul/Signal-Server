/*
 * Copyright 2013-2020 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package org.whispersystems.textsecuregcm.controllers;

import com.amazonaws.HttpMethod;
import com.codahale.metrics.annotation.Timed;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.AttachmentDescriptorV1;
import org.whispersystems.textsecuregcm.entities.AttachmentUri;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.s3.UrlSigner;
import org.whispersystems.textsecuregcm.storage.Account;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import io.dropwizard.auth.Auth;
import org.xmlpull.v1.XmlPullParserException;


@Path("/v1/attachments")
public class AttachmentControllerV1 extends AttachmentControllerBase {

  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(AttachmentControllerV1.class);

  private static final String[] UNACCELERATED_REGIONS = {"+20", "+971", "+968", "+974"};

  private final RateLimiters rateLimiters;
  private final UrlSigner    urlSigner;

  public AttachmentControllerV1(RateLimiters rateLimiters, String endpoint, String accessKey, String accessSecret, String bucket) {
    this.rateLimiters = rateLimiters;
    this.urlSigner    = new UrlSigner(endpoint, accessKey, accessSecret, bucket);
  }

  @Timed
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public AttachmentDescriptorV1 allocateAttachment(@Auth Account account)
      throws RateLimitExceededException, InvalidKeyException, NoSuchAlgorithmException, IOException, XmlPullParserException, MinioException
  {
    if (account.isRateLimited()) {
      rateLimiters.getAttachmentLimiter().validate(account.getNumber());
    }

    long attachmentId = generateAttachmentId();
//    URL  url          = urlSigner.getPreSignedUrl(attachmentId, HttpMethod.PUT, Stream.of(UNACCELERATED_REGIONS).anyMatch(region -> account.getNumber().startsWith(region)));
    String  url          = urlSigner.getPreSignedUrl(attachmentId, HttpMethod.PUT);
//    return new AttachmentDescriptorV1(attachmentId, url.toExternalForm());
    return new AttachmentDescriptorV1(attachmentId, url);
  }

  @Timed
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{attachmentId}")
  public AttachmentUri redirectToAttachment(@Auth                      Account account,
      @PathParam("attachmentId") long    attachmentId)
      throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, MinioException
  {
//    return new AttachmentUri(urlSigner.getPreSignedUrl(attachmentId, HttpMethod.GET, Stream.of(UNACCELERATED_REGIONS).anyMatch(region -> account.getNumber().startsWith(region))));
    return new AttachmentUri(new URL(urlSigner.getPreSignedUrl(attachmentId, HttpMethod.GET)));
  }

}
