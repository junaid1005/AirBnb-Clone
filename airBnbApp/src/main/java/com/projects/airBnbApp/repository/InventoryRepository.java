package com.projects.airBnbApp.repository;

import com.projects.airBnbApp.entity.Hotel;
import com.projects.airBnbApp.entity.Inventory;
import com.projects.airBnbApp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    void deleteByRoom(Room room);

    /*
    * This query is used to fetch unique hotel from inventory for which the condition is satisfied.
    * More over it will only return those hotel that have rooms consistently available throughout the total dateCount.
    *
    * */
    @Query("""
            SELECT DISTINCT i.hotel
            FROM Inventory i
            WHERE i.city=:city
                AND i.date BETWEEN :startDate AND :endDate
                AND i.closed=false
                AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
            GROUP BY i.hotel,i.room
            HAVING COUNT(i.date) = :dateCount
            """)
    Page<Hotel> findHotelsByAvailableInventory(
            @Param("city") String city,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
            );

    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.room.id= :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND i.closed=false
                AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );
}
