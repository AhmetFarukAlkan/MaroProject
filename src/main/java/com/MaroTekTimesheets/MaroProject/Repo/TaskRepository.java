package com.MaroTekTimesheets.MaroProject.Repo;

import com.MaroTekTimesheets.MaroProject.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
