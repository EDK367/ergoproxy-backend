package com.ergoproxy.backend.repository;

import com.ergoproxy.backend.model.SharedNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SharedNoteRepository extends JpaRepository<SharedNote, Long> {

    @Query("SELECT sn FROM SharedNote sn WHERE sn.userShared.id = :userId AND sn.enabled = true ORDER BY sn.createdAt DESC")
    List<SharedNote> findSharedNotesForUser(@Param("userId") UUID userId);

    @Query("SELECT sn FROM SharedNote sn WHERE sn.note.user.id = :userId AND sn.enabled = true ORDER BY sn.createdAt DESC")
    List<SharedNote> findSharedNotesByUser(@Param("userId") UUID userId);

    @Query("SELECT sn FROM SharedNote sn WHERE sn.note.id = :noteId AND sn.userShared.id = :userId AND sn.enabled = true")
    Optional<SharedNote> findByNoteIdAndUserSharedId(@Param("noteId") UUID noteId, @Param("userId") UUID userId);

    @Query("SELECT sn FROM SharedNote sn WHERE sn.note.id = :noteId AND sn.enabled = true ORDER BY sn.createdAt DESC")
    List<SharedNote> findSharesByNoteId(@Param("noteId") UUID noteId);

    @Query("SELECT sn FROM SharedNote sn WHERE sn.note.id = :noteId AND sn.note.user.id = :ownerId AND sn.userShared.id = :userSharedId AND sn.enabled = true")
    Optional<SharedNote> findByNoteIdAndOwnerIdAndUserSharedId(@Param("noteId") UUID noteId, @Param("ownerId") UUID ownerId, @Param("userSharedId") UUID userSharedId);
}
