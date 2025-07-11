package com.dddd.contentservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GptContentChecker {

    @Value("${openai.api-key}")
    private String openaiApiKey;

    private static final String GPT_URL = "https://api.openai.com/v1/chat/completions";
    private static final String WHISPER_URL = "https://api.openai.com/v1/audio/transcriptions";
    private static final String MODEL = "gpt-4o";

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String checkText(String text) {
        try {
            Map<String, Object> payload = Map.of(
                    "model", MODEL,
                    "temperature", 0,
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", "请判断以下内容是否包含色情、暴力、恐怖主义、歧视、仇恨言论、政治敏感、不当广告或诈骗信息：\n" + text + "\n请用“合规”或“违规”加原因回答"))
            );

            HttpHeaders headers = createJsonHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(GPT_URL, request, Map.class);

            Map<String, Object> responseBody = getSafeResponseBody(response);
            if (responseBody == null) return "⚠️ 无法获取模型响应";

            return Optional.ofNullable(responseBody.get("choices"))
                    .filter(List.class::isInstance)
                    .map(List.class::cast)
                    .filter(choices -> !choices.isEmpty())
                    .map(choices -> ((Map<?, ?>) choices.get(0)).get("message"))
                    .map(Object::toString)
                    .orElse("⚠️ 模型返回空内容");

        } catch (Exception e) {
            return "文本放行：" + e.getMessage();
        }
    }

    public String checkImage(MultipartFile file) {
        try {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            return checkImageBytes(base64);
        } catch (IOException e) {
            return "⚠️ 图片读取失败：" + e.getMessage();
        }
    }

    public String checkVideo(MultipartFile videoFile) {
        File tmpVideo = null;
        try {
            tmpVideo = File.createTempFile("upload-", ".mp4");
            videoFile.transferTo(tmpVideo);

            File framesDir = new File("frames");
            if (!framesDir.exists() && !framesDir.mkdirs()) {
                return "⚠️ 创建帧目录失败";
            }

            ProcessBuilder frameCmd = new ProcessBuilder("ffmpeg", "-i", tmpVideo.getAbsolutePath(),
                    "-vf", "fps=1/3", "frames/frame_%03d.png");
            frameCmd.redirectErrorStream(true);
            frameCmd.start().waitFor();

            List<File> frames = Optional.ofNullable(framesDir.listFiles())
                    .map(Arrays::stream).orElse(Stream.empty())
                    .sorted().limit(3).collect(Collectors.toList());

            List<String> imageResults = new ArrayList<>();
            for (File frame : frames) {
                byte[] bytes = Files.readAllBytes(frame.toPath());
                imageResults.add(checkImageBytes(Base64.getEncoder().encodeToString(bytes)));
            }

            File audioFile = new File("audio.mp3");
            ProcessBuilder audioCmd = new ProcessBuilder("ffmpeg", "-i", tmpVideo.getAbsolutePath(),
                    "-q:a", "0", "-map", "a", audioFile.getAbsolutePath());
            audioCmd.redirectErrorStream(true);
            audioCmd.start().waitFor();

            String transcript = whisperTranscribe(audioFile);
            String audioCheck = checkText(transcript);

            if (imageResults.stream().anyMatch(r -> r.contains("违规")) || audioCheck.contains("违规")) {
                return "违规内容：图片=" + imageResults + "，音频=" + audioCheck;
            }

            return "合规";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 正确处理 InterruptedException
            return "视频分析中断：" + e.getMessage();
        } catch (Exception e) {
            return "视频放行：" + e.getMessage();
        } finally {
            if (tmpVideo != null && tmpVideo.exists()) {
                boolean deleted = tmpVideo.delete();
                if (!deleted) {
                    System.err.println("⚠️ 临时文件删除失败：" + tmpVideo.getAbsolutePath());
                }
            }
        }
    }

    private String checkImageBytes(String base64Image) {
        try {
            String fullData = "data:image/png;base64," + base64Image;

            Map<String, Object> payload = Map.of(
                    "model", MODEL,
                    "temperature", 0,
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", List.of(
                                    Map.of("type", "text", "text", "请判断该图是否违规，用“合规”或“违规”加原因回答。"),
                                    Map.of("type", "image_url", "image_url", Map.of("url", fullData))
                            )
                    ))
            );

            HttpHeaders headers = createJsonHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(GPT_URL, request, Map.class);

            Map<String, Object> responseBody = getSafeResponseBody(response);
            if (responseBody == null) return "⚠️ 无法获取模型响应";

            return Optional.ofNullable(responseBody.get("choices"))
                    .filter(List.class::isInstance)
                    .map(List.class::cast)
                    .filter(choices -> !choices.isEmpty())
                    .map(choices -> ((Map<?, ?>) choices.get(0)).get("message"))
                    .map(Object::toString)
                    .orElse("⚠️ 模型返回空内容");

        } catch (Exception e) {
            return "图片放行：" + e.getMessage();
        }
    }

    private String whisperTranscribe(File audioFile) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(audioFile));
            body.add("model", "whisper-1");

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(openaiApiKey);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(WHISPER_URL, request, Map.class);

            Map<String, Object> responseBody = getSafeResponseBody(response);
            return responseBody != null ? responseBody.getOrDefault("text", "").toString() : "⚠️ Whisper无响应";
        } catch (Exception e) {
            return "⚠️ Whisper转写失败：" + e.getMessage();
        }
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(openaiApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private Map<String, Object> getSafeResponseBody(ResponseEntity<Map> response) {
        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        return null;
    }
}

