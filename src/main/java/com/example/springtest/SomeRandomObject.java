package com.example.springtest;

import java.util.Map;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SomeRandomObject {

  private String method;
  private String endpoint;
  private Map<String, String> params;
  private Map<String, String> headers;
  private Object body;
  private String timestamp;
  private String[] filenames;
}
