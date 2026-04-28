package com.demo.repositories;

import com.demo.entities.RoomImage;
import org.springframework.data.jpa.repository.*;

import java.util.UUID;

public interface RoomImageRepository extends JpaRepository<RoomImage, UUID> {

}
