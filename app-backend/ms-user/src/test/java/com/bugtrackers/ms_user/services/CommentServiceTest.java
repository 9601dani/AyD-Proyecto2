package com.bugtrackers.ms_user.services;

import com.bugtrackers.ms_user.dto.request.CommentRequest;
import com.bugtrackers.ms_user.dto.response.CommentResponse;
import com.bugtrackers.ms_user.models.Comment;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.repositories.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetComments() {
        User mockUser1 = new User();
        mockUser1.setId(1);
        mockUser1.setUsername("usuario1");

        User mockUser2 = new User();
        mockUser2.setId(2);
        mockUser2.setUsername("usuario2");

        Comment comment1 = new Comment();
        comment1.setId(1);
        comment1.setUser(mockUser1);
        comment1.setComment("Buen servicio");
        comment1.setValue(5);
        comment1.setCreatedAt(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setId(2);
        comment2.setUser(mockUser2);
        comment2.setComment("Malo el servicio");
        comment2.setValue(2);
        comment2.setCreatedAt(LocalDateTime.now());

        List<Comment> mockComments = new ArrayList<>();
        mockComments.add(comment1);
        mockComments.add(comment2);

        when(commentRepository.findAll()).thenReturn(mockComments);

        List<CommentResponse> result = commentService.getComments();

        assertEquals(2, result.size());
        assertEquals("usuario1", result.get(0).username());
        assertEquals("Buen servicio", result.get(0).comment());
        assertEquals(5, result.get(0).value());

        verify(commentRepository, times(1)).findAll();
    }

    @Test
    void testSaveComment() {
        CommentRequest mockRequest = new CommentRequest(1, "Comentario de prueba", 4, LocalDateTime.now());

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("usuario1");

        Comment mockComment = new Comment();
        mockComment.setId(1);
        mockComment.setUser(mockUser);
        mockComment.setComment("Comentario de prueba");
        mockComment.setValue(4);
        mockComment.setCreatedAt(LocalDateTime.now());

        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        CommentResponse result = commentService.saveComment(mockRequest);

        assertEquals("usuario1", result.username());
        assertEquals("Comentario de prueba", result.comment());
        assertEquals(4, result.value());

        verify(commentRepository, times(1)).save(any(Comment.class));
    }
}
