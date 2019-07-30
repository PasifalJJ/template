package com.jsc.controller;

import com.jsc.exception.MyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping
public class FileController {
    @RequestMapping("upload")
    public String uploadFile(MultipartFile leiMu, HttpServletRequest request) throws Exception{
        String path = request.getServletContext().getRealPath("images");
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println(file);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String name = uuid + leiMu.getOriginalFilename();
        try {
            leiMu.transferTo(new File(file,name));
        } catch (IOException e) {
            e.printStackTrace();
            throw new MyException("上传错误");
        }
        request.setAttribute("msg","上传成功");
        return "success";

    }
}
