package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingLast;
import ru.practicum.shareit.booking.model.BookingNext;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM Booking b WHERE b.item.id = :itemId " +
        "AND (b.end >= :startDate AND b.start <= :endDate) AND b.status = :status) THEN true ELSE false END")
    boolean checkBookingTimeOverlap(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("itemId") Long itemId, @Param("status") BookingStatus status);

    @Query("select b " +
        "from Booking as b " +
        "join fetch b.item as i " +
        "join fetch b.booker " +
        "join fetch i.owner " +
        "where b.id = :bookingId")
    Optional<Booking> findByIdWithItemAndOwner(@Param("bookingId") Long bookingId);

    @Query("""
    select b from Booking b
    join fetch b.item i
    join fetch i.owner
    join fetch b.booker boo
    where boo.id = :bookerId
    order by b.start desc
    """)
    List<Booking> getBookingsAllByBookerId(@Param("bookerId") Long bookerId, Pageable pageable);

    @Query("""
    select b from Booking b
    join fetch b.item i
    join fetch i.owner
    join fetch b.booker boo
    where boo.id = :bookerId and (b.start <= :now and b.end >= :now) and b.status = :status
    order by b.start desc
    """)
    List<Booking> getBookingsCurrentByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, @Param("status") BookingStatus status, Pageable pageable);

    @Query("""
    select b from Booking b
    join fetch b.item i
    join fetch i.owner
    join fetch b.booker boo
    where boo.id = :bookerId and b.end < :now and b.status = :status
    order by b.start desc
    """)
    List<Booking> getBookingsPastByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, @Param("status") BookingStatus status, Pageable pageable);

    @Query("""
    select b from Booking b
    join fetch b.item i
    join fetch i.owner
    join fetch b.booker boo
    where boo.id = :bookerId and b.start > :now and b.status = :status
    order by b.start desc
    """)
    List<Booking> getBookingsFutureByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, @Param("status") BookingStatus status, Pageable pageable);

    @Query("""
    select b from Booking b
    join fetch b.item i
    join fetch i.owner
    join fetch b.booker boo
    where boo.id = :bookerId and b.status = :status
    order by b.start desc
    """)
    List<Booking> getBookingsWaitingAndRejectedByBookerId(@Param("bookerId") Long bookerId, @Param("status") BookingStatus status, Pageable pageable);

    @Query("""
    select b from Booking b
    join fetch b.item i
    join fetch i.owner o
    join fetch b.booker
    where o.id = :ownerId
    order by b.start desc
    """)
    List<Booking> getBookingsAllByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    @Query("""
    select b from Booking b
    join fetch b.item i
    join fetch i.owner o
    join fetch b.booker
    where o.id = :ownerId and (b.start <= :now and b.end >= :now) and b.status = :status
    order by b.start desc
    """)
    List<Booking> getBookingsCurrentByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, @Param("status") BookingStatus status, Pageable pageable);

    @Query("""
    select b from Booking b
    join fetch b.item i
    join fetch i.owner o
    join fetch b.booker
    where o.id = :ownerId and b.end < :now and b.status = :status
    order by b.start desc
    """)
    List<Booking> getBookingsPastByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, @Param("status") BookingStatus status, Pageable pageable);

    @Query("""
    select b from Booking b
    join fetch b.item i
    join fetch i.owner o
    join fetch b.booker
    where o.id = :ownerId and b.start > :now and b.status = :status
    order by b.start desc
    """)
    List<Booking> getBookingsFutureByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, @Param("status") BookingStatus status, Pageable pageable);

    @Query("""
    select b from Booking b
    join fetch b.item i
    join fetch i.owner o
    join fetch b.booker
    where o.id = :ownerId and b.status = :status
    order by b.start desc
    """)
    List<Booking> getBookingsWaitingAndRejectedByOwnerId(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status, Pageable pageable);

    @Query("""
    select i.id as id, max(b.start) as lastBooking from Booking b
    join b.item as i
    where i.id in (:ids) and b.start < :now and b.status = :status
    group by i.id
    order by i.id desc
    """)
    List<BookingLast> getBookingsByLast(@Param("ids") List<Long> ids, @Param("now") LocalDateTime now, @Param("status") BookingStatus status);

    @Query("""
    select i.id as id, min(b.start) as nextBooking from Booking b
    join b.item as i
    where i.id in (:ids) and b.start > :now and b.status = :status
    group by i.id
    order by i.id asc
    """)
    List<BookingNext> getBookingsByNext(@Param("ids") List<Long> ids, @Param("now") LocalDateTime now, @Param("status") BookingStatus status);

    @Query("""
    select b from Booking b
    join fetch b.item i
    join fetch b.booker boo
    where boo.id = :userId and i.id = :itemId and b.end < :now and b.status = :status
    """)
    Optional<Booking> getByBookerIdAndItemIdAndStatusAndEndBefore(@Param("userId") Long userId, @Param("itemId") Long itemId, @Param("now") LocalDateTime now, @Param("status") BookingStatus status);
}
