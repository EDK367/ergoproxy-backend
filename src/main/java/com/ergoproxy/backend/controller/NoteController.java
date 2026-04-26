package com.ergoproxy.backend.controller;

import com.ergoproxy.backend.dto.request.CreateNoteDTO;
import com.ergoproxy.backend.dto.request.ShareNoteDTO;
import com.ergoproxy.backend.dto.request.UpdateNoteDTO;
import com.ergoproxy.backend.dto.response.NoteResponseDTO;
import com.ergoproxy.backend.dto.response.Response;
import com.ergoproxy.backend.dto.response.SharedNoteResponseDTO;
import com.ergoproxy.backend.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<Response<List<NoteResponseDTO>>> getMyNotes() {
        return ResponseEntity.ok(Response.<List<NoteResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Notes retrieved successfully")
                .data(noteService.getMyNotes())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<NoteResponseDTO>> getNoteById(@PathVariable UUID id) {
        return ResponseEntity.ok(Response.<NoteResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Note retrieved successfully")
                .data(noteService.getNoteById(id))
                .build());
    }

    @PostMapping
    public ResponseEntity<Response<NoteResponseDTO>> createNote(
            @Valid @RequestBody CreateNoteDTO createNoteDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.<NoteResponseDTO>builder()
                .status(HttpStatus.CREATED.value())
                .message("Note created successfully")
                .data(noteService.createNote(createNoteDTO))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<NoteResponseDTO>> updateNote(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateNoteDTO updateNoteDTO
    ) {
        return ResponseEntity.ok(Response.<NoteResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Note updated successfully")
                .data(noteService.updateNote(id, updateNoteDTO))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteNote(@PathVariable UUID id) {
        noteService.deleteNote(id);
        return ResponseEntity.ok(Response.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Note deleted successfully")
                .build());
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<Response<SharedNoteResponseDTO>> shareNote(
            @PathVariable UUID id,
            @Valid @RequestBody ShareNoteDTO shareNoteDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.<SharedNoteResponseDTO>builder()
                .status(HttpStatus.CREATED.value())
                .message("Note shared successfully")
                .data(noteService.shareNote(id, shareNoteDTO))
                .build());
    }

    @DeleteMapping("/{id}/share/{idFriend}")
    public ResponseEntity<Response<Void>> unshareNote(
            @PathVariable UUID id,
            @PathVariable UUID idFriend
    ) {
        noteService.unshareNote(id, idFriend);
        return ResponseEntity.ok(Response.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Note unshared successfully")
                .build());
    }

    @GetMapping("/{id}/shares")
    public ResponseEntity<Response<List<SharedNoteResponseDTO>>> getNoteShares(@PathVariable UUID id) {
        return ResponseEntity.ok(Response.<List<SharedNoteResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Note shares retrieved successfully")
                .data(noteService.getNoteShares(id))
                .build());
    }
}
