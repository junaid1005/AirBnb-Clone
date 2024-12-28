package com.projects.airBnbApp.service;

import com.projects.airBnbApp.dto.HotelDto;
import com.projects.airBnbApp.dto.HotelSearchRequest;
import com.projects.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteAllInventories(Room room);

    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
