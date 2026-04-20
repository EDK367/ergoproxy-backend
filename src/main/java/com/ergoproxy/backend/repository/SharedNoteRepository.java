package com.ergoproxy.backend.repository;

import com.ergoproxy.backend.model.SharedNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedNoteRepository extends JpaRepository<SharedNote, Long> {
}
