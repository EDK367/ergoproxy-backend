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

    @Query("SELECT n FROM Note n WHERE n.user.idFriend = :idFriend AND n.enabled = true ORDER BY n.createdAt DESC")
    List<Note> findByIdFriendEnabled(@Param("idFriend") UUID idFriend);

    @Query("SELECT n FROM Note n WHERE n.id = :noteId AND n.user.idFriend = :idFriend AND n.enabled = true")
    Optional<Note> findByIdAndIdFriendEnabled(@Param("noteId") UUID noteId, @Param("idFriend") UUID idFriend);

    @Query("SELECT n FROM Note n WHERE n.user.idFriend = :idFriend AND n.important = true AND n.enabled = true ORDER BY n.createdAt DESC")
    List<Note> findImportantByIdFriend(@Param("idFriend") UUID idFriend);

    @Query("SELECT n FROM Note n WHERE n.user.idFriend = :idFriend AND LOWER(n.title) LIKE LOWER(CONCAT('%', :title, '%')) AND n.enabled = true ORDER BY n.createdAt DESC")
    List<Note> findByIdFriendAndTitleContaining(@Param("idFriend") UUID idFriend, @Param("title") String title);
}
