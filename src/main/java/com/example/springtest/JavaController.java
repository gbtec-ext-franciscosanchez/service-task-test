package com.example.springtest;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@Controller()
public class JavaController {

  private static final String CONTENT_DISPOSITION_LABEL = "Content-Disposition";
  private static final String CONTENT_DISPOSITION_VALUE = "form-data; name=\"%s\"; filename=\"%s\"";

  private final List<ResponseEntity<SomeRandomObject>> responses = new ArrayList<ResponseEntity<SomeRandomObject>>();

  @GetMapping("/api")
  public ResponseEntity<SomeRandomObject> get(
      @RequestHeader Map<String, String> headers,
      @RequestParam(required = false) Map<String, String> params) {
    System.out.println("\nGet : ");
    System.out.println("params : " + params);
    System.out.println("headers : " + headers);
    ResponseEntity<SomeRandomObject> response = new ResponseEntity<>(
        SomeRandomObject.builder()
            .timestamp(LocalDateTime.now().toString()).headers(headers).params(params).endpoint("api").method("get")
            .build(), HttpStatus.OK);
    responses.add(response);
    return response;
  }

  @PostMapping("/api")
  public ResponseEntity<SomeRandomObject> post(
      @RequestHeader Map<String, String> headers,
      @RequestParam(required = false) Map<String, String> params,
      @RequestBody(required = false) Object body) {
    System.out.println("\nPost : ");
    System.out.println("params : " + params);
    System.out.println("headers : " + headers);
    System.out.println("body : " + body);
    ResponseEntity<SomeRandomObject> response = new ResponseEntity<>(
        SomeRandomObject.builder()
            .timestamp(LocalDateTime.now().toString()).headers(headers).params(params).endpoint("api").method("post")
            .body(body).build(),
        HttpStatus.OK);
    responses.add(response);
    return response;
  }

  @PutMapping("/api")
  public ResponseEntity<SomeRandomObject> put(
      @RequestHeader Map<String, String> headers,
      @RequestParam(required = false) Map<String, String> params,
      @RequestBody(required = false) Object body) {
    System.out.println("\nPut : ");
    System.out.println("params : " + params);
    System.out.println("headers : " + headers);
    System.out.println("body : " + body);
    ResponseEntity<SomeRandomObject> response = new ResponseEntity<>(
        SomeRandomObject.builder()
            .timestamp(LocalDateTime.now().toString()).headers(headers).params(params).body(body).endpoint("api")
            .method("put").build(),
        HttpStatus.OK);
    responses.add(response);
    return response;
  }

  @DeleteMapping("/api")
  public ResponseEntity<SomeRandomObject> delete(
      @RequestHeader Map<String, String> headers,
      @RequestParam(required = false) Map<String, String> params,
      @RequestBody(required = false) Object body) {
    System.out.println("\nDelete : ");
    System.out.println("params : " + params);
    System.out.println("headers : " + headers);
    System.out.println("body : " + body);
    ResponseEntity<SomeRandomObject> response = new ResponseEntity<>(
        SomeRandomObject.builder()
            .timestamp(LocalDateTime.now().toString()).headers(headers).params(params).body(body).endpoint("api")
            .method("delete").build(),
        HttpStatus.OK);
    responses.add(response);
    return response;
  }

  @PostMapping(path = "/upload", consumes = MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<SomeRandomObject> uploadAttachment(
      @RequestHeader Map<String, String> headers,
      @RequestParam(required = false) Map<String, String> params,
      @RequestParam Map<String, MultipartFile> files,
      @RequestParam(value = "DocumentUpload", required = false) MultipartFile documentUpload) {
    System.out.println("\nUpload : ");
    System.out.println("params : " + params);
    System.out.println("headers : " + headers);
    System.out.println("DocumentUpload : " + documentUpload);
    System.out.println("files.size() : " + files.size());
    System.out.println("files : " + files);

    files.forEach((key, file) -> {
      System.out.println("\nkey : " + key);
      System.out.println("file.getOriginalFilename() : " + file.getOriginalFilename());
      System.out.println("file.getName() : " + file.getName());
      System.out.println("file.getSize() : " + file.getSize());
      System.out.println("file.getContentType() : " + file.getContentType());
      try {
        var time = LocalTime.now().toString().replace(" ", "-").replace(":", "-").replace(".", "-");
        var output = new File(
            new File("").getAbsolutePath() + "/uploads/" + time + "-" + file.getOriginalFilename());
        file.transferTo(output);
        System.out.println("\nFILE SAVED : " + output.getAbsolutePath());
      } catch (Exception ex) {
        ex.printStackTrace();
      }

    });

    ResponseEntity<SomeRandomObject> response = new ResponseEntity<>(
        SomeRandomObject.builder().endpoint("upload").method("post")
            .timestamp(LocalDateTime.now().toString())
            .headers(headers)
            .params(params)
            .filenames(files.values()
                .stream()
                .map(MultipartFile::getOriginalFilename)
                .toArray(String[]::new))
            .build(), HttpStatus.OK);
    responses.add(response);
    return response;
  }


  @GetMapping(path = "/download", produces = MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Resource> downloadAttachment(
      @RequestHeader Map<String, String> headers,
      @RequestParam(required = false) Map<String, String> params) {
    System.out.println("\nDownload : ");
    System.out.println("params : " + params);
    System.out.println("headers : " + headers);
    Resource resource = null;
    try {
      var output = new File(new File("").getAbsolutePath() + "/fileToDownload.txt");
      var fileInputStream = new FileInputStream(output);

      resource = new ByteArrayResource(fileInputStream.readAllBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
    ResponseEntity<Resource> response = ok()
        .header(CONTENT_DISPOSITION_LABEL, String.format(CONTENT_DISPOSITION_VALUE, "attachment", "fileToDownload.txt"))
        .header("timestamp", LocalDateTime.now().toString())
        .body(resource);
    responses.add(ok().body(
        SomeRandomObject.builder().endpoint("download").method("get").timestamp(LocalDateTime.now().toString())
            .headers(headers).params(params).build()));
    return response;
  }


  @PostMapping(path = "/transform", consumes = MULTIPART_FORM_DATA_VALUE, produces = MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Resource> transformAttachment(
      @RequestHeader Map<String, String> headers,
      @RequestParam(required = false) Map<String, String> params,
      @RequestParam(value = "DocumentUpload") MultipartFile documentUpload) {
    System.out.println("\nUpload : ");
    System.out.println("params : " + params);
    System.out.println("headers : " + headers);
    System.out.println("DocumentUpload : " + documentUpload);

    ByteArrayResource resource = null;
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      outputStream.write(documentUpload.getBytes());
      outputStream.write(String.format("\n%s", LocalDateTime.now()).getBytes());
      outputStream.flush();
      resource = new ByteArrayResource(outputStream.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
    }
    ResponseEntity<Resource> response = ok()
        .header(CONTENT_DISPOSITION_LABEL, String.format(CONTENT_DISPOSITION_VALUE, "response", "fileToDownload.txt"))
        .header("timestamp", LocalDateTime.now().toString())
        .body(resource);
    responses.add(ok().body(
        SomeRandomObject.builder().endpoint("transform").method("post").timestamp(LocalDateTime.now().toString())
            .headers(headers).params(params).build()));
    return response;
  }

  @GetMapping("/delay")
  public ResponseEntity<SomeRandomObject> delay(
      @RequestHeader Map<String, String> headers,
      @RequestParam(required = false, defaultValue = "10000") String delay,
      @RequestParam(required = false) Map<String, String> params) throws InterruptedException {
    System.out.println("\nGet : ");
    System.out.println("params : " + params);
    System.out.println("headers : " + headers);
    ResponseEntity<SomeRandomObject> response = new ResponseEntity<>(
        SomeRandomObject.builder()
            .timestamp(LocalDateTime.now().toString()).headers(headers).params(params).endpoint("delay").method("get")
            .build(), HttpStatus.OK);
    responses.add(response);
    Integer sleep = null;
    try {
      sleep = Integer.parseInt(delay);
    } catch (Exception e) {
      sleep = 10000;
    }
    Thread.sleep(sleep);
    return response;
  }

  @GetMapping("/501")
  public ResponseEntity<SomeRandomObject> five_o_one(
          @RequestHeader Map<String, String> headers,
          @RequestParam(required = false) Map<String, String> params) {
    System.out.println("\nGet : ");
    System.out.println("params : " + params);
    System.out.println("headers : " + headers);
    ResponseEntity<SomeRandomObject> response = new ResponseEntity<>(
            SomeRandomObject.builder()
                    .timestamp(LocalDateTime.now().toString()).headers(headers).params(params).endpoint("delay").method("get")
                    .build(), HttpStatus.NOT_IMPLEMENTED);
    responses.add(response);
    return response;
  }

  @GetMapping("/logs")
  public ResponseEntity<List<ResponseEntity<SomeRandomObject>>> logs() {
    System.out.println("\nLog Requested\n");
    return ok().body(this.responses);
  }
}
