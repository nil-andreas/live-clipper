package com.nilsson.sentiment.mapper;

import com.nilsson.sentiment.domain.Clip;
import com.nilsson.sentiment.entity.ClipEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ClipMapper {
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    ClipEntity toEntity(Clip clip);
}
