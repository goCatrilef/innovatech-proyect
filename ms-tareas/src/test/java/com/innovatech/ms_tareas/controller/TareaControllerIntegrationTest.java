package com.innovatech.ms_tareas.controller;

import com.innovatech.ms_tareas.client.proyecto.ProyectoClientFacade;
import com.innovatech.ms_tareas.entity.Tarea;
import com.innovatech.ms_tareas.entity.enums.EstadoTarea;
import com.innovatech.ms_tareas.repository.TareaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TareaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TareaRepository tareaRepository;

    @MockitoBean
    private ProyectoClientFacade proyectoClientFacade;

    @BeforeEach
    void setUp() {
        tareaRepository.deleteAll();
    }

    @Test
    void crearTarea_debeResponder201YPersistirEnBaseDeDatos() throws Exception {

        when(proyectoClientFacade.existeProyecto(1L)).thenReturn(true);

        String requestBody = """
                {
                    "descripcion": "Implementar pruebas de integración",
                    "proyectoId": 1,
                    "responsableId": 10,
                    "estado": "PENDING"
                }
                """;

        mockMvc.perform(post("/api/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descripcion").value("Implementar pruebas de integración"))
                .andExpect(jsonPath("$.proyectoId").value(1))
                .andExpect(jsonPath("$.responsableId").value(10))
                .andExpect(jsonPath("$.estado").value("PENDING"));
        assertEquals(1, tareaRepository.count());
        verify(proyectoClientFacade).existeProyecto(1L);
    }

    @Test
    void listarTareasPorProyecto_debeResponder200YRetornarTareasPersistidas() throws Exception {

        tareaRepository.save(Tarea.builder()
                .descripcion("Diseñar frontend")
                .proyectoId(1L)
                .responsableId(10L)
                .estado(EstadoTarea.PENDING)
                .build());

        tareaRepository.save(Tarea.builder()
                .descripcion("Conectar BFF con microservicio")
                .proyectoId(1L)
                .responsableId(11L)
                .estado(EstadoTarea.IN_PROGRESS)
                .build());

        mockMvc.perform(get("/api/tareas/proyecto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].proyectoId").value(1))
                .andExpect(jsonPath("$[1].proyectoId").value(1));
    }

    @Test
    void crearTarea_debeResponder400CuandoDescripcionEstaVacia() throws Exception {

        String requestBody = """
                {
                    "descripcion": "",
                    "proyectoId": 1,
                    "responsableId": 10,
                    "estado": "PENDING"
                }
                """;

        mockMvc.perform(post("/api/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(proyectoClientFacade);
    }

    @Test
    void obtenerTareaPorId_debeResponder404CuandoLaTareaNoExiste() throws Exception {
        mockMvc.perform(get("/api/tareas/999999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}