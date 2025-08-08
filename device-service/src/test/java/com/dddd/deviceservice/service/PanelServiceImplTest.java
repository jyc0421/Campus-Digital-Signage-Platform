package com.dddd.deviceservice.service;

import com.dddd.deviceservice.dto.PanelRequestDTO;
import com.dddd.deviceservice.dto.PanelResponseDTO;
import com.dddd.deviceservice.entity.Panel;
import com.dddd.deviceservice.exception.ResourceNotFoundException;
import com.dddd.deviceservice.repository.PanelRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PanelServiceImplTest {

    @Mock
    private PanelRepository panelRepository;

    @InjectMocks
    private PanelServiceImpl panelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Panel mockPanel() {
        Panel panel = new Panel();
        panel.setId(1L);
        panel.setSubscriberId(2L);
        panel.setName("Panel A");
        panel.setLocation("Room 101");
        panel.setIpAddress("192.168.1.10");
        panel.setMacAddress("00:11:22:33:44:55");
        panel.setStatus(Panel.Status.ONLINE);
        panel.setLastHeartbeat(LocalDateTime.now());
        return panel;
    }

    private PanelRequestDTO buildRequestDTO(String name, String location, String ip, String mac) {
        PanelRequestDTO dto = new PanelRequestDTO();
        dto.setSubscriberId(2L);
        dto.setName(name);
        dto.setLocation(location);
        dto.setIpAddress(ip);
        dto.setMacAddress(mac);
        return dto;
    }

    @Test
    void testCreatePanel() {
        PanelRequestDTO request = buildRequestDTO("Panel A", "Room 101", "192.168.1.10", "00:11:22:33:44:55");

        Panel saved = mockPanel();
        saved.setStatus(Panel.Status.OFFLINE); // 初始为 OFFLINE

        when(panelRepository.save(any(Panel.class))).thenReturn(saved);

        PanelResponseDTO response = panelService.createPanel(request);

        assertEquals("Panel A", response.getName());
        assertEquals("Room 101", response.getLocation());
        assertEquals("OFFLINE", response.getStatus());
    }

    @Test
    void testGetPanelByIdSuccess() {
        Panel panel = mockPanel();

        when(panelRepository.findById(1L)).thenReturn(Optional.of(panel));

        PanelResponseDTO result = panelService.getPanelById(1L);

        assertEquals("Panel A", result.getName());
        assertEquals("ONLINE", result.getStatus());
    }

    @Test
    void testGetPanelByIdNotFound() {
        when(panelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            panelService.getPanelById(99L);
        });
    }

    @Test
    void testGetAllPanels() {
        Panel panel = mockPanel();
        when(panelRepository.findAll()).thenReturn(Collections.singletonList(panel));

        List<PanelResponseDTO> result = panelService.getAllPanels();

        assertEquals(1, result.size());
        assertEquals("Panel A", result.get(0).getName());
    }

    @Test
    void testUpdatePanelSuccess() {
        Panel existing = mockPanel();

        PanelRequestDTO request = buildRequestDTO("Updated", "New Room", "10.0.0.1", "AA:BB:CC:DD:EE:FF");

        when(panelRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(panelRepository.save(any(Panel.class))).thenAnswer(inv -> inv.getArgument(0));

        PanelResponseDTO updated = panelService.updatePanel(1L, request);

        assertEquals("Updated", updated.getName());
        assertEquals("New Room", updated.getLocation());
        assertEquals("10.0.0.1", updated.getIpAddress());
    }

    @Test
    void testUpdatePanelNotFound() {
        PanelRequestDTO request = buildRequestDTO("Updated", "New Room", "10.0.0.1", "AA:BB:CC");

        when(panelRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            panelService.updatePanel(404L, request);
        });
    }

    @Test
    void testDeletePanel() {
        doNothing().when(panelRepository).deleteById(1L);

        panelService.deletePanel(1L);

        verify(panelRepository, times(1)).deleteById(1L);
    }
}
