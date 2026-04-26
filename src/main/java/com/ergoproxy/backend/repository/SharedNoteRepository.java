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

    @Query("SELECT sn FROM SharedNote sn WHERE sn.userShared.idFriend = :idFriend AND sn.enabled = true ORDER BY sn.createdAt DESC")
    List<SharedNote> findSharedNotesForUser(@Param("idFriend") UUID idFriend);

    @Query("SELECT sn FROM SharedNote sn WHERE sn.note.user.idFriend = :idFriend AND sn.enabled = true ORDER BY sn.createdAt DESC")
    List<SharedNote> findSharedNotesByUser(@Param("idFriend") UUID idFriend);

    @Query("SELECT sn FROM SharedNote sn WHERE sn.note.id = :noteId AND sn.userShared.idFriend = :idFriend AND sn.enabled = true")
    Optional<SharedNote> findByNoteIdAndUserSharedIdFriend(@Param("noteId") UUID noteId, @Param("idFriend") UUID idFriend);

    @Query("SELECT sn FROM SharedNote sn WHERE sn.note.id = :noteId AND sn.enabled = true ORDER BY sn.createdAt DESC")
    List<SharedNote> findSharesByNoteId(@Param("noteId") UUID noteId);

    @Query("SELECT sn FROM SharedNote sn WHERE sn.note.id = :noteId AND sn.note.user.idFriend = :ownerIdFriend AND sn.userShared.idFriend = :userSharedIdFriend AND sn.enabled = true")
    Optional<SharedNote> findByNoteIdAndOwnerIdFriendAndUserSharedIdFriend(@Param("noteId") UUID noteId, @Param("ownerIdFriend") UUID ownerIdFriend, @Param("userSharedIdFriend") UUID userSharedIdFriend);
}
