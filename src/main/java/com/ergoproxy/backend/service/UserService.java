package com.ergoproxy.backend.service;

import com.ergoproxy.backend.dto.LogsInfoDTO;
import com.ergoproxy.backend.dto.UserDTO;
import com.ergoproxy.backend.dto.response.UserProfileDTO;
import com.ergoproxy.backend.exception.CustomException;
import com.ergoproxy.backend.model.LogsInfo;
import com.ergoproxy.backend.model.User;
import com.ergoproxy.backend.repository.LogsInfoRepository;
import com.ergoproxy.backend.repository.UserRepository;
import com.ergoproxy.backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LogsInfoRepository logsInfoRepository;

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAllWithLogs();
        return users.stream().map(user -> {

            List<LogsInfoDTO> logs = user.getLogs() != null
                    ? user.getLogs().stream()
                    .map(log -> new LogsInfoDTO(
                            log.getIp(),
                            log.getCreatedAt()
                    ))
                    .toList()
                    : List.of();

            return UserDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .enabled(user.isEnabled())
                    .metadata(user.getMetadata())
                    .role(user.getRole().getName())
                    .logsInfo(logs)
                    .build();

        }).toList();
    }

    public UserProfileDTO getMyProfile() {
        User authenticatedUser = getAuthenticatedUser();

        return UserProfileDTO.builder()
                .id(authenticatedUser.getId())
                .username(authenticatedUser.getUsername())
                .name(authenticatedUser.getName())
                .lastname(authenticatedUser.getLastname())
                .email(authenticatedUser.getEmail())
                .phoneNumber(authenticatedUser.getPhoneNumber())
                .role(authenticatedUser.getRole().getName())
                .build();
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new CustomException(
                    HttpStatus.UNAUTHORIZED,
                    "User not authenticated"
            );
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }
}
