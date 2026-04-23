package com.ergoproxy.backend.service;

import com.ergoproxy.backend.model.LogsInfo;
import com.ergoproxy.backend.model.User;
import com.ergoproxy.backend.repository.LogsInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LogsInfoService {

    private final LogsInfoRepository logsInfoRepository;

    public void logSuccessfulLogin(User user, String ip) {
        LogsInfo logsInfo = LogsInfo.builder()
                .user(user)
                .ip(ip)
                .build();
        
        logsInfoRepository.save(logsInfo);
    }

    public void logFailedLoginAttempt(String ip) {
        LogsInfo logsInfo = LogsInfo.builder()
                .ip(ip)
                .user(null)
                .build();
        
        logsInfoRepository.save(logsInfo);
    }
}
