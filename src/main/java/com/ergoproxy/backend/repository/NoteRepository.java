package com.ergoproxy.backend.repository;

import com.ergoproxy.backend.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND n.enabled = true ORDER BY n.createdAt DESC")
    List<Note> findByUserIdEnabled(@Param("userId") UUID userId);

    @Query("SELECT n FROM Note n WHERE n.id = :noteId AND n.user.id = :userId AND n.enabled = true")
    Optional<Note> findByIdAndUserIdEnabled(@Param("noteId") UUID noteId, @Param("userId") UUID userId);

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND n.important = true AND n.enabled = true ORDER BY n.createdAt DESC")
    List<Note> findImportantByUserId(@Param("userId") UUID userId);

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND LOWER(n.title) LIKE LOWER(CONCAT('%', :title, '%')) AND n.enabled = true ORDER BY n.createdAt DESC")
    List<Note> findByUserIdAndTitleContaining(@Param("userId") UUID userId, @Param("title") String title);
}
