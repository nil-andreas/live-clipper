package com.nilsson.sentiment;

import com.nilsson.sentiment.entity.ClipEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClipRepository extends CrudRepository<ClipEntity, Long> {
}
