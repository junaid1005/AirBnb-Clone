package com.projects.airBnbApp.controller;

import com.projects.airBnbApp.dto.HotelDto;
import com.projects.airBnbApp.dto.HotelInfoDto;
import com.projects.airBnbApp.dto.HotelSearchRequest;
import com.projects.airBnbApp.service.HotelService;
import com.projects.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest){
        Page<HotelDto> page=inventoryService.searchHotels(hotelSearchRequest);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }
}
