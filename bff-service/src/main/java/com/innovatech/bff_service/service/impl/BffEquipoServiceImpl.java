package com.innovatech.bff_service.service.impl;

import com.innovatech.bff_service.client.ApiGatewayClient;
import com.innovatech.bff_service.dto.AsignarMiembroProyectoRequestDTO;
import com.innovatech.bff_service.dto.MiembroDTO;
import com.innovatech.bff_service.dto.MiembroProyectoDTO;
import com.innovatech.bff_service.dto.MiembroRequestDTO;
import com.innovatech.bff_service.service.BffEquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BffEquipoServiceImpl implements BffEquipoService {

    private final ApiGatewayClient apiGatewayClient;

    @Override
    public List<MiembroDTO> listarMiembros() {
        return apiGatewayClient.listarMiembros();
    }

    @Override
    public MiembroDTO registrarMiembro(MiembroRequestDTO request) {
        return apiGatewayClient.registrarMiembro(request);
    }

    @Override
    public MiembroProyectoDTO asignarMiembroAProyecto(AsignarMiembroProyectoRequestDTO request) {
        return apiGatewayClient.asignarMiembroAProyecto(request);
    }

    @Override
    public List<MiembroProyectoDTO> listarMiembrosPorProyecto(Long proyectoId) {
        return apiGatewayClient.obtenerMiembrosPorProyecto(proyectoId);
    }
}
