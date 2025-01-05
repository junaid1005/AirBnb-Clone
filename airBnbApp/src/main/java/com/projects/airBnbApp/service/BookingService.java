package com.projects.airBnbApp.service;

import com.projects.airBnbApp.dto.BookingDto;
import com.projects.airBnbApp.dto.BookingRequest;
import com.projects.airBnbApp.dto.GuestDto;

import java.util.List;

public interface BookingService {
    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
