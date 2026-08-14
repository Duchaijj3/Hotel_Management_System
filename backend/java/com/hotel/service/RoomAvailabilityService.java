package com.hotel.service; import com.hotel.dto.AvailableRoomDto; import java.util.*; public interface RoomAvailabilityService { Optional<List<AvailableRoomDto>> find(long id,Integer floor); }
