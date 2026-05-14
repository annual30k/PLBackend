package org.dromara.patrol.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.patrol.domain.PatrolMedia;
import org.dromara.patrol.entity.ApiEnvelope;
import org.dromara.patrol.entity.MediaFileDto;
import org.dromara.patrol.mapper.PatrolMediaMapper;
import org.dromara.system.domain.vo.SysOssVo;
import org.dromara.system.service.ISysOssService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * 巡检文件接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class PatrolFileController {

    private static final long RESPONSE_TIME = 1715832000L;

    private final PatrolMediaMapper mediaMapper;
    private final ISysOssService ossService;

    @GetMapping
    public ApiEnvelope<List<MediaFileDto>> listDeviceFiles() {
        List<MediaFileDto> files = mediaMapper.selectList(new LambdaQueryWrapper<PatrolMedia>()
                .eq(PatrolMedia::getStorageSide, "DEVICE"))
            .stream()
            .map(this::toMediaDto)
            .toList();
        return ok(files);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiEnvelope<MediaFileDto> upload(@RequestPart("file") MultipartFile file) {
        SysOssVo oss = ossService.upload(file);
        String fileId = fileId(oss.getOssId());
        PatrolMedia media = new PatrolMedia();
        media.setTenantId(LoginHelper.getTenantId());
        media.setFileId(fileId);
        media.setFileName(StringUtils.blankToDefault(file.getOriginalFilename(), oss.getOriginalName()));
        media.setMediaType(mediaType(media.getFileName(), file.getContentType()));
        media.setCapturedAt("刚刚");
        media.setSizeText(sizeText(file.getSize()));
        media.setSha256Verified(false);
        media.setStorageSide("DEVICE");
        media.setTransferStatus("IDLE");
        media.setProgress(0F);
        media.setContentUri("/files/" + fileId + "/download");
        media.setOssId(oss.getOssId());
        media.setBucketName("ruoyi");
        media.setObjectKey(oss.getFileName());
        media.setDelFlag("0");
        mediaMapper.insert(media);
        return ok(toMediaDto(media));
    }

    @GetMapping("/{fileId}/download")
    public void download(@PathVariable String fileId, HttpServletResponse response) throws IOException {
        PatrolMedia media = findMedia(fileId);
        if (media.getOssId() != null) {
            ossService.download(media.getOssId(), response);
            return;
        }
        byte[] placeholder = ("PatrolLink evidence placeholder for " + fileId + "\n").getBytes(StandardCharsets.UTF_8);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileId + ".bin\"");
        response.setContentLength(placeholder.length);
        response.getOutputStream().write(placeholder);
    }

    @DeleteMapping("/{fileId}")
    public ApiEnvelope<Boolean> delete(@PathVariable String fileId) {
        List<PatrolMedia> files = mediaMapper.selectList(new LambdaQueryWrapper<PatrolMedia>().eq(PatrolMedia::getFileId, fileId));
        boolean deleted = false;
        for (PatrolMedia file : files) {
            if (file.getOssId() != null) {
                ossService.deleteWithValidByIds(List.of(file.getOssId()), true);
            }
            deleted = mediaMapper.deleteById(file.getMediaId()) > 0 || deleted;
        }
        return ok(deleted);
    }

    private PatrolMedia findMedia(String fileId) {
        PatrolMedia media = mediaMapper.selectOne(new LambdaQueryWrapper<PatrolMedia>()
            .eq(PatrolMedia::getFileId, fileId)
            .eq(PatrolMedia::getStorageSide, "DEVICE")
            .orderByDesc(PatrolMedia::getCreateTime)
            .last("limit 1"));
        if (media == null) {
            media = mediaMapper.selectOne(new LambdaQueryWrapper<PatrolMedia>()
                .eq(PatrolMedia::getFileId, fileId)
                .orderByDesc(PatrolMedia::getCreateTime)
                .last("limit 1"));
        }
        if (media == null) {
            throw new ServiceException("媒体文件不存在");
        }
        return media;
    }

    private MediaFileDto toMediaDto(PatrolMedia media) {
        return new MediaFileDto(
            media.getFileId(),
            media.getFileName(),
            media.getMediaType(),
            media.getCapturedAt(),
            media.getSizeText(),
            media.getDurationText(),
            Boolean.TRUE.equals(media.getSha256Verified()),
            media.getStorageSide(),
            media.getTransferStatus(),
            media.getProgress() == null ? 0F : media.getProgress(),
            media.getContentUri()
        );
    }

    private String fileId(Long ossId) {
        return "FILE-" + (ossId == null ? IdUtil.fastSimpleUUID() : ossId);
    }

    private String mediaType(String fileName, String contentType) {
        String lower = StringUtils.blankToDefault(fileName, "").toLowerCase();
        String type = StringUtils.blankToDefault(contentType, "").toLowerCase();
        if (type.startsWith("image/") || lower.matches(".*\\.(jpg|jpeg|png|webp|bmp)$")) {
            return "PHOTO";
        }
        if (type.startsWith("audio/") || lower.matches(".*\\.(mp3|wav|aac|m4a|amr)$")) {
            return "AUDIO";
        }
        return "VIDEO";
    }

    private String sizeText(long bytes) {
        if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024D);
        }
        return String.format("%.1f MB", bytes / 1024D / 1024D);
    }

    private <T> ApiEnvelope<T> ok(T data) {
        return new ApiEnvelope<>(200, "OK", data, UUID.randomUUID().toString(), RESPONSE_TIME);
    }
}
