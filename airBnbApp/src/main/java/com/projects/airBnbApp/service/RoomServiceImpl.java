package com.projects.airBnbApp.service;

import com.projects.airBnbApp.dto.RoomDto;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    public RoomDto createNewRoom(Long hotelId,RoomDto roomDto) {
        log.info("Creating a new room in hotel with ID: {}",hotelId);
        Hotel hotel=hotelRepository.findById(hotelId)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("Hotel not found with ID : "+hotelId)
                );
        Room room=modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room=roomRepository.save(room);

        // create inventory as soon as room is created and if hotel is active
        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with ID: {}",hotelId);
        Hotel hotel=hotelRepository.findById(hotelId)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("Hotel not found with ID : "+hotelId)
                );

        return hotel.getRooms()
                .stream()
                .map((room)->modelMapper.map(room,RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting room  with ID: {}",roomId);
        Room room=roomRepository.findById(roomId)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("Room not found with ID : "+roomId)
                );
        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting room  with ID: {}",roomId);
        Room room=roomRepository.findById(roomId)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("Room not found with ID : "+roomId)
                );

        //delete all inventory for this room

        inventoryService.deleteAllInventories(room);

        roomRepository.deleteById(roomId);
    }
}
