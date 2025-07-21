package com.sanwenyukaochi.security.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileStorageFactory {
    
    @Value("${storage.type:oss}")  // Default to OBS if not specified
    private String storageType;

    private final ObsFileStorage obsFileStorage;
    
    public FileStorage getFileStorage() {
        return switch (storageType.toLowerCase()) {
            case "obs" -> obsFileStorage;
            default -> throw new IllegalArgumentException("不支持的存储类型: " + storageType);
        };
    }
}
