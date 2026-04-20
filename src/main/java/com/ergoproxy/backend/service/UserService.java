package com.ergoproxy.backend.service;

import com.ergoproxy.backend.dto.LogsInfoDTO;
import com.ergoproxy.backend.dto.UserDTO;
import com.ergoproxy.backend.model.LogsInfo;
import com.ergoproxy.backend.model.User;
import com.ergoproxy.backend.repository.LogsInfoRepository;
import com.ergoproxy.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
}
