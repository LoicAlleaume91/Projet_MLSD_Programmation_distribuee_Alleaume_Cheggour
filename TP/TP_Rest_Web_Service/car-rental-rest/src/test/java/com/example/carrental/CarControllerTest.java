package com.example.carrental;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CarControllerTest {
    @Autowired MockMvc mvc;

    @Test
    void helloAndCarList() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(content().string("hello"));
        mvc.perform(get("/cars")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].plateNumber").value("11AA22"));
    }

    @Test
    void findCarOr404() throws Exception {
        mvc.perform(get("/cars/11AA22")).andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Ferrari"))
                .andExpect(jsonPath("$.price").value(100));
        mvc.perform(get("/cars/UNKNOWN")).andExpect(status().isNotFound());
    }

    @Test
    void rentAndReturnCar() throws Exception {
        mvc.perform(put("/cars/AA11BB?rent=true").contentType(MediaType.APPLICATION_JSON)
                .content("{\"begin\":\"11/11/2017\",\"end\":\"1/1/2018\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.rented").value(true));
        mvc.perform(get("/cars")).andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.plateNumber == 'AA11BB')]").isEmpty());
        mvc.perform(put("/cars/AA11BB?rent=true").contentType(MediaType.APPLICATION_JSON)
                .content("{\"begin\":\"11/11/2017\",\"end\":\"1/1/2018\"}"))
                .andExpect(status().isConflict());
        mvc.perform(put("/cars/AA11BB?rent=false")).andExpect(status().isOk())
                .andExpect(jsonPath("$.rented").value(false));
        mvc.perform(put("/cars/AA11BB?rent=false")).andExpect(status().isConflict());
    }

    @Test
    void rejectInvalidDates() throws Exception {
        mvc.perform(put("/cars/BB22CC?rent=true").contentType(MediaType.APPLICATION_JSON)
                .content("{\"begin\":\"31/2/2026\",\"end\":\"1/3/2026\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/cars/BB22CC?rent=true").contentType(MediaType.APPLICATION_JSON)
                .content("{\"begin\":\"2/3/2026\",\"end\":\"1/3/2026\"}"))
                .andExpect(status().isBadRequest());
    }
}
