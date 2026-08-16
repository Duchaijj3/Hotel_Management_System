package com.hotel.service.impl;
import com.hotel.dao.RoomDao;import com.hotel.exception.BusinessException;import org.junit.jupiter.api.Test;import java.time.LocalDate;import java.util.*;import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;
class RoomAvailabilityServiceImplTest{
 @Test void invalidDateRangeDoesNotQueryDatabase(){RoomDao d=mock(RoomDao.class);var s=new RoomAvailabilityServiceImpl(d);LocalDate day=LocalDate.of(2026,8,17);assertThrows(BusinessException.class,()->s.search(day,day,null,1,0,null));verifyNoInteractions(d);}
 @Test void emptyAvailabilityIsSupported()throws Exception{RoomDao d=mock(RoomDao.class);LocalDate in=LocalDate.of(2026,8,17),out=in.plusDays(1);when(d.searchAvailable(in,out,null,2,0,null)).thenReturn(List.of());assertTrue(new RoomAvailabilityServiceImpl(d).search(in,out,null,2,0,null).isEmpty());}
}
