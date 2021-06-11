package com.example.springtest;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.MediaType.parseMediaType;
import static org.springframework.http.ResponseEntity.ok;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller()
public class JavaController {

  private static final String CONTENT_DISPOSITION_LABEL = "Content-Disposition";
  private static final String CONTENT_DISPOSITION_VALUE = "form-data; name=\"attachment\"; filename=\"%s\"";

  @GetMapping("/api")
  public ResponseEntity<SomeRandomObject> get(@RequestParam(required = false) Map<String, String> params,
      @RequestHeader Map<String, String> headers) {
    System.out.println(params);
    System.out.println(headers);
    return new ResponseEntity<SomeRandomObject>(
        SomeRandomObject.builder().headers(headers).params(params).build(), HttpStatus.OK);
  }

  @PostMapping("/api")
  public ResponseEntity<SomeRandomObject> post(@RequestParam(required = false) Map<String, String> params,
      @RequestHeader Map<String, String> headers, @RequestBody(required = false) Object body) {
    System.out.println(params);
    System.out.println(headers);
    System.out.println(body);
    return new ResponseEntity<SomeRandomObject>(
        SomeRandomObject.builder().headers(headers).params(params).body(body).build(), HttpStatus.OK);
  }

  @PostMapping(path = "upload", consumes = MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<SomeRandomObject> uploadAttachment(
      @RequestParam(required = false) Map<String, String> params,
      @RequestHeader Map<String, String> headers,
      @RequestParam Map<String, MultipartFile> files,
      @RequestParam(value = "DocumentUpload", required = false) MultipartFile documentUpload) {
    System.out.println("params : " + params);
    System.out.println("headers : " + headers);
    System.out.println("files : " + files);
    System.out.println("files.size() : " + files.size());
    System.out.println("documentUpload : " + documentUpload);

    files.forEach((key, file) -> {
      System.out.println();
      System.out.println("key : " + key);
      System.out.println("file.getOriginalFilename() : " + file.getOriginalFilename());
      System.out.println("file.getName() : " + file.getName());
      System.out.println("file.getSize() : " + file.getSize());
      System.out.println("file.getContentType() : " + file.getContentType());
      try {
        var time = LocalTime.now().toString().replace(" ", "-").replace(":", "-").replace(".", "-");
        var output = new File(
            new File("").getAbsolutePath() + "/uploads/" + time + "-" + file.getOriginalFilename());
        file.transferTo(output);
        System.out.println("File saved: " + output.getAbsolutePath());
      } catch (Exception ex) {
        ex.printStackTrace();
      }

    });

    return new ResponseEntity<SomeRandomObject>(
        SomeRandomObject.builder()
            .headers(headers)
            .params(params)
            .filenames(files.values()
                .stream()
                .map(MultipartFile::getOriginalFilename)
                .toArray(String[]::new))
            .build(), HttpStatus.OK);
  }


  @GetMapping(path = "download", produces = MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Resource> downloadAttachment(@RequestParam(required = false) Map<String, String> params,
      @RequestHeader Map<String, String> headers) {
    System.out.println(params);
    System.out.println(headers);

    Resource resource = null;
    try {
      var output = new File(new File("").getAbsolutePath() + "/fileToDownload.txt");
      var fileInputStream = new FileInputStream(output);

      resource = new ByteArrayResource(fileInputStream.readAllBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ok().header(CONTENT_DISPOSITION_LABEL, String.format(CONTENT_DISPOSITION_VALUE, "fileToDownload.txt"))
        .contentType(parseMediaType("text/plain"))
        .body(resource);
  }
}
