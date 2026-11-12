package com.medicare.service;

import com.medicare.dto.AuthRequestDTO;
import com.medicare.dto.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO register(AuthRequestDTO request);
    AuthResponseDTO login(AuthRequestDTO request);
}
