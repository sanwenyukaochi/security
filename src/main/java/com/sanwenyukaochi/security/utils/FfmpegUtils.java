package com.sanwenyukaochi.security.utils;


import com.sanwenyukaochi.security.exception.APIException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class FfmpegUtils {

    private static final String FFMPEG_PATH = "ffmpeg";

    /**
     * 裁剪视频
     */
    public static void clipVideo(String inputVideoPath, String outputFileName, String startTimestamp, String endTimestamp) throws IOException, InterruptedException {
        ProcessBuilder ffmpegClipProcess = new ProcessBuilder(
                FFMPEG_PATH,
                "-ss", startTimestamp,
                "-to", endTimestamp,
                "-i", inputVideoPath,
                "-c:v", "libx264",
                "-c:a", "aac",
                "-preset", "ultrafast", // 可选，加快处理速度
                outputFileName
        );
        executeProcess(ffmpegClipProcess, "视频裁剪");
    }

    /**
     * 获取视频第一帧图片
     */
    public static void getFirstImageFromVideo(String inputVideoPath, String startTime, String outputImagePath) throws IOException, InterruptedException {
        ProcessBuilder ffmpegCoverProcess = new ProcessBuilder(
                FFMPEG_PATH,
                "-ss", startTime,
                "-i", inputVideoPath,
                "-vframes", "1",  // 截取一帧
                "-q:v", "2",      // 保持较高图像质量
                "-update", "1",   // 关键参数，强制输出单张图片
                "-y",
                outputImagePath
        );
        executeProcess(ffmpegCoverProcess, "视频封面图提取");
    }

    /**
     * 视频软字幕合成
     */
    public static void softSubtitleSynthesisVideo(String inputVideoPath, String subtitlePath, String outputPath) throws IOException, InterruptedException {
        ProcessBuilder ffmpegClipProcess = new ProcessBuilder(
                FFMPEG_PATH,
                "-i", inputVideoPath,
                "-i", subtitlePath,
                "-c:v", "copy",  // 复制视频流
                "-c:a", "copy",  // 复制音频流
                "-c:s", "mov_text",  // 使用mov_text编码器嵌入字幕
                "-metadata:s:s:0", "language=chi",  // 设置字幕语言为中文
                "-y",
                outputPath
        );
        executeProcess(ffmpegClipProcess, "软字幕合成");
    }

    /**
     * 视频硬字幕合成
     * 注意：该功能需要安装 Google Noto 字体才能正常使用。
     * 安装步骤：
     * 1. 执行以下命令安装 Noto CJK 字体：
     *    apt-get install -y fonts-noto-cjk
     * 2. 更新字体缓存：
     *    fc-cache -fv
     */
    public static void hardSubtitleSynthesisVideo(String inputVideoPath, String subtitlePath, String outputPath) throws IOException, InterruptedException {
        ProcessBuilder ffmpegHardSubProcess = new ProcessBuilder(
                FFMPEG_PATH,
                "-i", inputVideoPath,
                "-vf", String.format("subtitles=%s:force_style='FontName=Noto Sans CJK'", subtitlePath),
                "-c:a", "copy",  // 音频直接复制
                "-y",
                outputPath
        );
        executeProcess(ffmpegHardSubProcess, "硬字幕合成");
    }

    /**
     * 执行 Process 并打印日志
     */
    private static void executeProcess(ProcessBuilder processBuilder, String taskName) throws IOException, InterruptedException {
        log.info("[{}]执行命令：{}", taskName, String.join(" ", processBuilder.command()));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        // 异步读取输出日志
        Thread logThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("[{}]{}", taskName, line);
                }
            } catch (IOException e) {
                log.error("[{}]读取子进程输出时出错：{}", taskName, e.getMessage(), e);
            }
        });
        logThread.setDaemon(true);
        logThread.start();
        // 等待进程完成
        int exitCode = process.waitFor();
        logThread.join(); // 确保日志线程结束
        if (exitCode != 0) {
            throw new APIException("[" + taskName + "]执行失败，退出码：" + exitCode);
        }
        log.info("[{}]任务完成", taskName);
    }

}
