package com.dddd.deviceservice.controller;

import com.dddd.deviceservice.dto.*;
import com.dddd.deviceservice.service.PanelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PanelControllerTest {

    @Mock
    private PanelService panelService;

    @InjectMocks
    private PanelController panelController;

    private PanelResponseDTO mockPanel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockPanel = new PanelResponseDTO();
        mockPanel.setId(1L);
        mockPanel.setName("Test Panel");
        mockPanel.setLocation("Room A");
        mockPanel.setIpAddress("192.168.1.1");
        mockPanel.setMacAddress("00:11:22:33:44:55");
        mockPanel.setStatus("ONLINE");
        mockPanel.setLastHeartbeat(null);
    }

    private PanelRequestDTO buildRequest() {
        PanelRequestDTO dto = new PanelRequestDTO();
        dto.setSubscriberId(2L);
        dto.setName("Test Panel");
        dto.setLocation("Room A");
        dto.setIpAddress("192.168.1.1");
        dto.setMacAddress("00:11:22:33:44:55");
        return dto;
    }

    @Test
    void testCreatePanel() {
        PanelRequestDTO request = buildRequest();
        when(panelService.createPanel(request)).thenReturn(mockPanel);

        ApiResponse<PanelResponseDTO> response = panelController.createPanel(request);

        assertEquals(200, response.getCode());
        assertEquals("Test Panel", response.getData().getName());
        verify(panelService, times(1)).createPanel(request);
    }

    @Test
    void testGetPanelById() {
        when(panelService.getPanelById(1L)).thenReturn(mockPanel);

        ApiResponse<PanelResponseDTO> response = panelController.getPanel(1L);

        assertEquals(200, response.getCode());
        assertEquals(1L, response.getData().getId());
        verify(panelService).getPanelById(1L);
    }

    @Test
    void testGetAllPanels() {
        List<PanelResponseDTO> list = List.of(mockPanel);
        when(panelService.getAllPanels()).thenReturn(list);

        ApiResponse<List<PanelResponseDTO>> response = panelController.getAllPanels();

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData().size());
        verify(panelService).getAllPanels();
    }

    @Test
    void testUpdatePanel() {
        PanelRequestDTO request = buildRequest();
        when(panelService.updatePanel(1L, request)).thenReturn(mockPanel);

        ApiResponse<PanelResponseDTO> response = panelController.updatePanel(1L, request);

        assertEquals(200, response.getCode());
        assertEquals("Test Panel", response.getData().getName());
        verify(panelService).updatePanel(1L, request);
    }

    @Test
    void testDeletePanel() {
        doNothing().when(panelService).deletePanel(1L);

        ApiResponse<Void> response = panelController.deletePanel(1L);

        assertEquals(200, response.getCode());
        assertNull(response.getData());
        verify(panelService).deletePanel(1L);
    }
}
