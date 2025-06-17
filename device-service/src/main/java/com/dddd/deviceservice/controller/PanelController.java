package com.dddd.deviceservice.controller;

import com.dddd.deviceservice.dto.*;
import com.dddd.deviceservice.service.PanelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panels")
@RequiredArgsConstructor
public class PanelController {

    private final PanelService panelService;

    @PostMapping
    public ApiResponse<PanelResponseDTO> createPanel(@RequestBody PanelRequestDTO request) {
        return ApiResponse.success(panelService.createPanel(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<PanelResponseDTO> getPanel(@PathVariable Long id) {
        return ApiResponse.success(panelService.getPanelById(id));
    }

    @GetMapping
    public ApiResponse<List<PanelResponseDTO>> getAllPanels() {
        return ApiResponse.success(panelService.getAllPanels());
    }

    @PutMapping("/{id}")
    public ApiResponse<PanelResponseDTO> updatePanel(@PathVariable Long id, @RequestBody PanelRequestDTO request) {
        return ApiResponse.success(panelService.updatePanel(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePanel(@PathVariable Long id) {
        panelService.deletePanel(id);
        return ApiResponse.success(null);
    }
}