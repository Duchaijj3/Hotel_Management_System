package com.hotel.dao;
import com.hotel.dto.AvailableRoomDto; import java.util.*;
public interface RoomDao { Optional<List<AvailableRoomDto>> findAvailable(long reservationRoomId,Integer floor); }
