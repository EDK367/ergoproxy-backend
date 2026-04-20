package com.ergoproxy.backend.repository;

import com.ergoproxy.backend.dto.LogsInfoDTO;
import com.ergoproxy.backend.model.LogsInfo;
import com.ergoproxy.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LogsInfoRepository extends JpaRepository<LogsInfo, UUID> {

}
