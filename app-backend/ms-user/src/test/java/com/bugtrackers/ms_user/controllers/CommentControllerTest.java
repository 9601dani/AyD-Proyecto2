package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.dto.request.CommentRequest;
import com.bugtrackers.ms_user.dto.response.CommentResponse;
import com.bugtrackers.ms_user.services.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetComments() {
        List<CommentResponse> mockComments = Arrays.asList(
                new CommentResponse(1, "usuario1", "Buen servicio", 5, LocalDateTime.now()),
                new CommentResponse(2, "usuario2", "Malo el servicio", 2, LocalDateTime.now())
        );

        when(commentService.getComments()).thenReturn(mockComments);

        List<CommentResponse> result = commentController.getComments();

        assertEquals(2, result.size());
        assertEquals("usuario1", result.get(0).username());

        verify(commentService, times(1)).getComments();
    }

    @Test
    void testSaveComment() {
        CommentRequest mockRequest = new CommentRequest(1, "Comentario de prueba", 4, LocalDateTime.now());
        CommentResponse mockResponse = new CommentResponse(1, "usuario1", "Comentario de prueba", 4, LocalDateTime.now());

        when(commentService.saveComment(mockRequest)).thenReturn(mockResponse);

        CommentResponse result = commentController.saveComment(mockRequest);

        assertEquals("usuario1", result.username());
        assertEquals("Comentario de prueba", result.comment());
        assertEquals(4, result.value());

        verify(commentService, times(1)).saveComment(mockRequest);
    }
}
