package com.innovatech.ms_tareas.e2e;

import com.innovatech.ms_tareas.client.proyecto.ProyectoClientFacade;
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

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TareaE2ETest {

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
    void flujoE2E_debeCrearTareaYConsultarlaPorId() throws Exception {
        when(proyectoClientFacade.existeProyecto(1L)).thenReturn(true);

        String crearTareaRequest = """
                {
                    "descripcion": "E2E - Crear y consultar tarea",
                    "proyectoId": 1,
                    "responsableId": 10,
                    "estado": "PENDING"
                }
                """;

        String response = mockMvc.perform(post("/api/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(crearTareaRequest))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.estado").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long tareaId = extraerIdDesdeResponse(response);

        mockMvc.perform(get("/api/tareas/" + tareaId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tareaId))
                .andExpect(jsonPath("$.descripcion").value("E2E - Crear y consultar tarea"))
                .andExpect(jsonPath("$.estado").value("PENDING"));
    }

    @Test
    void flujoE2E_debeCrearTareaYCambiarEstadoAEnProgreso() throws Exception {
        when(proyectoClientFacade.existeProyecto(1L)).thenReturn(true);

        String crearTareaRequest = """
                {
                    "descripcion": "E2E - Cambiar estado de tarea",
                    "proyectoId": 1,
                    "responsableId": 10,
                    "estado": "PENDING"
                }
                """;

        String response = mockMvc.perform(post("/api/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(crearTareaRequest))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long tareaId = extraerIdDesdeResponse(response);

        String cambiarEstadoRequest = """
                {
                    "estado": "IN_PROGRESS"
                }
                """;

        mockMvc.perform(patch("/api/tareas/" + tareaId + "/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cambiarEstadoRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tareaId))
                .andExpect(jsonPath("$.estado").value("IN_PROGRESS"));

        mockMvc.perform(get("/api/tareas/" + tareaId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("IN_PROGRESS"));
    }

    @Test
    void flujoE2E_debeRechazarTransicionInvalidaDePendienteADone() throws Exception {
        when(proyectoClientFacade.existeProyecto(1L)).thenReturn(true);

        String crearTareaRequest = """
                {
                    "descripcion": "E2E - Transición inválida",
                    "proyectoId": 1,
                    "responsableId": 10,
                    "estado": "PENDING"
                }
                """;

        String response = mockMvc.perform(post("/api/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(crearTareaRequest))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long tareaId = extraerIdDesdeResponse(response);

        String cambiarEstadoRequest = """
                {
                    "estado": "DONE"
                }
                """;

        mockMvc.perform(patch("/api/tareas/" + tareaId + "/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cambiarEstadoRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Transición de estado no permitida: PENDING -> DONE"));
    }

    private Long extraerIdDesdeResponse(String response) {
        String idComoTexto = response.replaceAll(".*\"id\":(\\d+).*", "$1");
        return Long.parseLong(idComoTexto);
    }
}