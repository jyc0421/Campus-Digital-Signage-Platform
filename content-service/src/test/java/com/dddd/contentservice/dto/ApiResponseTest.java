package com.dddd.contentservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void testConstructorAndGetters() {
        ApiResponse<String> response = new ApiResponse<>(201, "created", "data123");

        assertEquals(201, response.getCode());
        assertEquals("created", response.getMessage());
        assertEquals("data123", response.getData());
    }

    @Test
    void testSetters() {
        ApiResponse<String> response = new ApiResponse<>(0, null, null);
        response.setCode(500);
        response.setMessage("error");
        response.setData("failure");

        assertEquals(500, response.getCode());
        assertEquals("error", response.getMessage());
        assertEquals("failure", response.getData());
    }

    @Test
    void testSuccessFactory() {
        ApiResponse<Integer> success = ApiResponse.success(123);

        assertEquals(200, success.getCode());
        assertEquals("success", success.getMessage());
        assertEquals(123, success.getData());
    }

    @Test
    void testFailFactory() {
        ApiResponse<Void> fail = ApiResponse.fail("bad request");

        assertEquals(400, fail.getCode());
        assertEquals("bad request", fail.getMessage());
        assertNull(fail.getData());
    }
}

