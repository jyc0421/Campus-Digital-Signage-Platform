package com.dddd.contentservice.controller;

import com.dddd.contentservice.entity.FileRecord;
import com.dddd.contentservice.repository.FileRecordRepository;
import com.dddd.contentservice.util.AliyunOssUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileRecordRepository fileRecordRepository;

    @MockBean
    private AliyunOssUtil aliyunOssUtil;

    @BeforeEach
    void setUp() throws IOException {
        fileRecordRepository.deleteAll();

        // ✅ mock OSS 返回固定地址
        Mockito.when(aliyunOssUtil.uploadFile(any(), any()))
                .thenReturn("https://mock.oss.aliyun.com/uploads/test-image.jpg");
    }

    @Test
    void testFileUpload_success() throws Exception {
        // 准备上传文件
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-content".getBytes()
        );

        // 执行上传请求，模拟 userId 为 Long 类型
        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .requestAttr("userId", 123L)) // 避免 ClassCastException
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.url").value(containsString("mock.oss.aliyun.com")))
                .andExpect(jsonPath("$.data.fileId").exists());

        // 验证数据库是否保存记录
        List<FileRecord> saved = fileRecordRepository.findAll();
        assertEquals(1, saved.size());

        FileRecord record = saved.get(0);
        assertEquals("test-image.jpg", record.getOriginalName());
        assertEquals("123", record.getUserId()); // ⚠️ 注意：userId 在实体中为 String 类型
    }
}