package com.example.taskmanager.integration;

import com.example.taskmanager.dto.request.TaskRequest;
import com.example.taskmanager.dto.response.TaskResponse;
import com.example.taskmanager.model.enums.TaskStatus;
import com.example.taskmanager.service.interfaces.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test création d'une tâche
     */
    @Test
    @WithMockUser(username = "test@mail.com")
    void shouldCreateTask() throws Exception {

        TaskRequest request = new TaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setStatus(TaskStatus.TODO);

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle("Test Task");
        response.setDescription("Test Description");
        response.setStatus(TaskStatus.TODO);

        Mockito.when(taskService.createTask(Mockito.any(), Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Test Task"))
                .andExpect(jsonPath("$.data.status").value("TODO"));
    }

    /**
     * Test récupération des tâches
     */
    @Test
    @WithMockUser(username = "test@mail.com")
    void shouldReturnTasks() throws Exception {

        TaskResponse task = new TaskResponse();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(TaskStatus.TODO);

        Page<TaskResponse> page =
                new PageImpl<>(List.of(task));

        Mockito.when(taskService.getAllTasks(Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("Test Task"));
    }
}
