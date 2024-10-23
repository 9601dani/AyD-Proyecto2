package com.bugtrackers.ms_user.repositories;

import com.bugtrackers.ms_user.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
