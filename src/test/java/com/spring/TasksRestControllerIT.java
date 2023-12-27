package com.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql("/tasks_rest_controller/test_data.sql")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class TasksRestControllerIT {

    @Autowired
    MockMvc mockMvc;

//    @Autowired
//    InMemTaskRepository taskRepository;

//    @AfterEach
//    void tearDown() {
//        this.taskRepository.getTasks().clear();
//    }

    @Test
//    @WithMockUser
    void handleGetAllTasks_ReturnsValidResponseEntity() throws Exception {
        // given
        RequestBuilder requestBuilder = get("/api/tasks")
                .with(httpBasic("user1", "password1"))
                .contentType(MediaType.APPLICATION_JSON);
//        this.taskRepository.getTasks().addAll(List.of(new Task(UUID.fromString("71117396-8694-11ed-9ef6-77042ee83937"), "Первая задача", false),
//                new Task(UUID.fromString("71117396-8694-11ed-9ef6-77042ee83938"), "Вторая задача", true)));

        // when
        this.mockMvc.perform(requestBuilder)
        // then
                .andExpectAll(status().isOk(),
//                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                        [
                        {
                            "id": "71117396-8694-11ed-9ef6-77042ee83937",
                            "details": "РџРµСЂРІР°СЏ Р·Р°РґР°С‡Р°",
                            "completed": false
                        },
                        {
                            "id": "71117396-8694-11ed-9ef6-77042ee83938",
                            "details": "Р’С‚РѕСЂР°СЏ Р·Р°РґР°С‡Р°",
                            "completed": true
                        }
                        ]
                        """)
                );
    }

    @Test
    void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        // given
        RequestBuilder requestBuilder = post("/api/tasks")
                .with(httpBasic("user2", "password2"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "details": "Третья задача"
                }
                """);
        //when
        this.mockMvc.perform(requestBuilder)
        //then

                .andExpectAll(status().isCreated(),
                header().exists(HttpHeaders.LOCATION),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                {
                    "details": "Третья задача",
                    "completed": false
                }
                """),
                        jsonPath("$.id").exists());

//        assertEquals(1, this.taskRepository.getTasks().size());
//        assertNotNull(this.taskRepository.getTasks().get(0).id());
//        assertEquals("Третья задача", this.taskRepository.getTasks().get(0).details());
//        assertFalse(this.taskRepository.getTasks().get(0).completed());
    }

    @Test
    void handleCreateNewTask_PayloadIsValid_ReturnsInvalidResponseEntity() throws Exception {
        // given
        RequestBuilder requestBuilder = post("/api/tasks")
                .with(httpBasic("user1", "password1"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en")
                .content("""
                {
                    "details": null
                }
                """);
        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                {
                    "errors": ["Task details must be set"]
                }
                """, true));

//        assertTrue(this.taskRepository.getTasks().isEmpty());
    }


}