package com.example.Cohort_platform.repository;

import com.example.Cohort_platform.entity.Notification;
import com.example.Cohort_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiver(User user);

    List<Notification> findByReceiverAndReadFalse(User user);
}
