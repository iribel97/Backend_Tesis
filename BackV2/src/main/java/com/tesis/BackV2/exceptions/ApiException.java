package com.tesis.BackV2.exceptions;

import com.tesis.BackV2.config.ApiResponse;

public class ApiException extends RuntimeException{
    private final ApiResponse<?> apiResponse;

    public ApiException(ApiResponse<?> apiResponse) {
        super(apiResponse.getMensaje()); // Mensaje principal de la excepción
        this.apiResponse = apiResponse;
    }

    public ApiResponse<?> getApiResponse() {
        return apiResponse;
    }
}
