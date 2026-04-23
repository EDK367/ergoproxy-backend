package com.ergoproxy.backend.service;

import com.ergoproxy.backend.dto.request.CreateNoteDTO;
import com.ergoproxy.backend.dto.request.ShareNoteDTO;
import com.ergoproxy.backend.dto.request.UpdateNoteDTO;
import com.ergoproxy.backend.dto.response.NoteResponseDTO;
import com.ergoproxy.backend.dto.response.SharedNoteResponseDTO;
import com.ergoproxy.backend.exception.CustomException;
import com.ergoproxy.backend.model.Note;
import com.ergoproxy.backend.model.SharedNote;
import com.ergoproxy.backend.model.User;
import com.ergoproxy.backend.repository.NoteRepository;
import com.ergoproxy.backend.repository.SharedNoteRepository;
import com.ergoproxy.backend.repository.UserRepository;
import com.ergoproxy.backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final SharedNoteRepository sharedNoteRepository;
    private final UserRepository userRepository;

    public List<NoteResponseDTO> getMyNotes() {
        User authenticatedUser = getAuthenticatedUser();
        
        List<Note> ownNotes = noteRepository.findByUserIdEnabled(authenticatedUser.getId());
        List<NoteResponseDTO> ownNoteDTOs = ownNotes.stream()
                .map(note -> this.convertToDTO(note, authenticatedUser, true, false))
                .collect(Collectors.toList());

        List<SharedNote> sharedNotes = sharedNoteRepository.findSharedNotesForUser(authenticatedUser.getId());
        List<NoteResponseDTO> sharedNoteDTOs = sharedNotes.stream()
                .map(sharedNote -> this.convertToDTO(sharedNote.getNote(), authenticatedUser, false, true))
                .collect(Collectors.toList());

        return Stream.concat(ownNoteDTOs.stream(), sharedNoteDTOs.stream())
                .sorted((a, b) -> b.createdAt().compareTo(a.createdAt()))
                .collect(Collectors.toList());
    }

    public NoteResponseDTO getNoteById(UUID noteId) {
        User authenticatedUser = getAuthenticatedUser();
        Note note = getNoteWithAccess(noteId, authenticatedUser);
        
        boolean isOwner = note.getUser().getId().equals(authenticatedUser.getId());
        boolean isShared = false;
        
        if (!isOwner) {
            Optional<SharedNote> sharedNote = sharedNoteRepository.findByNoteIdAndUserSharedId(noteId, authenticatedUser.getId());
            isShared = sharedNote.isPresent() && sharedNote.get().isEnabled();
        }
        
        return this.convertToDTO(note, authenticatedUser, isOwner, isShared);
    }

    public NoteResponseDTO createNote(CreateNoteDTO createNoteDTO) {
        User authenticatedUser = getAuthenticatedUser();

        Note note = Note.builder()
                .title(createNoteDTO.title())
                .content(createNoteDTO.content())
                .important(createNoteDTO.important())
                .user(authenticatedUser)
                .enabled(true)
                .build();

        Note savedNote = noteRepository.save(note);
        return this.convertToDTO(savedNote, authenticatedUser, true, false);
    }

    public NoteResponseDTO updateNote(UUID noteId, UpdateNoteDTO updateNoteDTO) {
        User authenticatedUser = getAuthenticatedUser();

        Note note = noteRepository.findByIdAndUserIdEnabled(noteId, authenticatedUser.getId())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.FORBIDDEN,
                        "You don't have permission to update this note or note does not exist"
                ));

        note.setTitle(updateNoteDTO.title());
        note.setContent(updateNoteDTO.content());
        note.setImportant(updateNoteDTO.important());
        note.setUpdatedAt(LocalDateTime.now());

        Note updatedNote = noteRepository.save(note);
        return this.convertToDTO(updatedNote, authenticatedUser, true, false);
    }

    public void deleteNote(UUID noteId) {
        User authenticatedUser = getAuthenticatedUser();

        Note note = noteRepository.findByIdAndUserIdEnabled(noteId, authenticatedUser.getId())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.FORBIDDEN,
                        "You don't have permission to delete this note or note does not exist"
                ));

        note.setEnabled(false);
        noteRepository.save(note);

        List<SharedNote> sharedNotes = sharedNoteRepository.findSharesByNoteId(noteId);
        sharedNotes.forEach(sn -> {
            sn.setEnabled(false);
            sharedNoteRepository.save(sn);
        });
    }

    public SharedNoteResponseDTO shareNote(UUID noteId, ShareNoteDTO shareNoteDTO) {
        User authenticatedUser = getAuthenticatedUser();

        Note note = noteRepository.findByIdAndUserIdEnabled(noteId, authenticatedUser.getId())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.FORBIDDEN,
                        "You don't have permission to share this note or note does not exist"
                ));

        User userToShare = userRepository.findByIdEnabled(shareNoteDTO.userId())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "User not found or not enabled"
                ));

        if (userToShare.getId().equals(authenticatedUser.getId())) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot share a note with yourself"
            );
        }

        if (sharedNoteRepository.findByNoteIdAndUserSharedId(noteId, userToShare.getId()).isPresent()) {
            throw new CustomException(
                    HttpStatus.CONFLICT,
                    "This note is already shared with this user"
            );
        }

        SharedNote sharedNote = SharedNote.builder()
                .note(note)
                .userShared(userToShare)
                .enabled(true)
                .build();

        SharedNote savedSharedNote = sharedNoteRepository.save(sharedNote);
        return this.convertSharedNoteToDTO(savedSharedNote);
    }

    public void unshareNote(UUID noteId, UUID userId) {
        User authenticatedUser = getAuthenticatedUser();

        Note note = noteRepository.findByIdAndUserIdEnabled(noteId, authenticatedUser.getId())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.FORBIDDEN,
                        "You don't have permission to modify sharing for this note"
                ));

        SharedNote sharedNote = sharedNoteRepository.findByNoteIdAndOwnerIdAndUserSharedId(
                noteId,
                authenticatedUser.getId(),
                userId
        ).orElseThrow(() -> new CustomException(
                HttpStatus.NOT_FOUND,
                "This note is not shared with this user"
        ));

        sharedNote.setEnabled(false);
        sharedNoteRepository.save(sharedNote);
    }

    public List<SharedNoteResponseDTO> getNoteShares(UUID noteId) {
        User authenticatedUser = getAuthenticatedUser();

        noteRepository.findByIdAndUserIdEnabled(noteId, authenticatedUser.getId())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.FORBIDDEN,
                        "You don't have permission to view shares for this note"
                ));

        return sharedNoteRepository.findSharesByNoteId(noteId).stream()
                .map(this::convertSharedNoteToDTO)
                .collect(Collectors.toList());
    }

    private Note getNoteWithAccess(UUID noteId, User user) {
        Optional<Note> ownNote = noteRepository.findByIdAndUserIdEnabled(noteId, user.getId());
        if (ownNote.isPresent()) {
            return ownNote.get();
        }

        Optional<SharedNote> sharedNote = sharedNoteRepository.findByNoteIdAndUserSharedId(noteId, user.getId());
        if (sharedNote.isPresent() && sharedNote.get().isEnabled()) {
            return sharedNote.get().getNote();
        }

        throw new CustomException(
                HttpStatus.FORBIDDEN,
                "You don't have permission to access this note"
        );
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

    private NoteResponseDTO convertToDTO(Note note, User authenticatedUser, boolean isOwner, boolean isShared) {
        return NoteResponseDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .important(note.isImportant())
                .userId(note.getUser().getId())
                .username(note.getUser().getUsername())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .isOwner(isOwner)
                .isShared(isShared)
                .build();
    }

    private SharedNoteResponseDTO convertSharedNoteToDTO(SharedNote sharedNote) {
        return SharedNoteResponseDTO.builder()
                .id(sharedNote.getId())
                .noteId(sharedNote.getNote().getId())
                .noteTitle(sharedNote.getNote().getTitle())
                .noteOwnerId(sharedNote.getNote().getUser().getId())
                .noteOwnerUsername(sharedNote.getNote().getUser().getUsername())
                .userSharedId(sharedNote.getUserShared().getId())
                .userSharedUsername(sharedNote.getUserShared().getUsername())
                .createdAt(sharedNote.getCreatedAt())
                .enabled(sharedNote.isEnabled())
                .build();
    }
}
