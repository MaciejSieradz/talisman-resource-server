package com.talismanresourceserver.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public final class ExceptionResponseDTO {

    private final int status;
    private final String message;
}
