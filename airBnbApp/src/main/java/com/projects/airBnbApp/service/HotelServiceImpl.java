package com.projects.airBnbApp.service;

import com.projects.airBnbApp.dto.HotelDto;
import com.projects.airBnbApp.entity.Hotel;
import com.projects.airBnbApp.entity.Room;
import com.projects.airBnbApp.exception.ResourceNotFoundException;
import com.projects.airBnbApp.repository.HotelRepository;
import com.projects.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new Hotel with name : {}",hotelDto.getName());
        Hotel hotel=modelMapper.map(hotelDto,Hotel.class);
        hotel.setActive(false);
        hotel=hotelRepository.save(hotel);
        log.info("Creating a new Hotel with ID : {}",hotel.getId());
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the Hotel with ID: {}",id);
        Hotel hotel=hotelRepository.findById(id)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("Hotel not found with ID : "+id)
                );
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public List<HotelDto> getAllHotels() {
        List<Hotel> hotels=hotelRepository.findAll();
        return hotels
                .stream()
                .map((hotel)->modelMapper.map(hotel,HotelDto.class))
                .toList();
    }

    @Override
    public HotelDto updateHotelById(Long id,HotelDto hotelDto){
        log.info("Updating the Hotel with ID: {}",id);
        Hotel hotel=hotelRepository.findById(id)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("Hotel not found with ID : "+id)
                );
        modelMapper.map(hotelDto,hotel);
        hotel.setId(id);
        hotel=hotelRepository.save(hotel);
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
        log.info("Activating the Hotel with ID: {}",hotelId);
        Hotel hotel=hotelRepository.findById(hotelId)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("Hotel not found with ID : "+hotelId)
                );
        hotel.setActive(true);
        hotelRepository.save(hotel);

        // Create Inventory for all the rooms for this hotel
        // assuming we do it once only
        if(!hotel.getRooms().isEmpty()){
            for (Room room:hotel.getRooms()){
                inventoryService.initializeRoomForAYear(room);
            }
        }

    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        log.info("Deleting the Hotel with ID: {}",id);
        Hotel hotel=hotelRepository.findById(id)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("Hotel not found with ID : "+id)
                );

        //TODO : delete future inventories for this hotel

        for(Room room:hotel.getRooms()){
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }

        hotelRepository.deleteById(id);

    }
}
