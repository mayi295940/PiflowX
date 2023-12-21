package cn.cnic.component.mxGraph.service;

import org.springframework.web.multipart.MultipartFile;

public interface IMxNodeImageService {

  String uploadNodeImage(
      String username, MultipartFile file, String imageType, String nodeEngineType);

  String getMxNodeImageList(String username, String imageType);
}
