package com.example.springtest;

import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;


@Data
@Builder
public class SomeRandomObject {

  private Map<String, String> params;
  private Map<String, String> headers;
  private Object body;
  private String[] filenames;
  private Resource resource;
}
