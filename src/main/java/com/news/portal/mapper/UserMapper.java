package com.news.portal.mapper;

import com.news.portal.dto.RegistrationRequestDto;
import com.news.portal.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "email", target = "email")
    RegistrationRequestDto toDto(UserEntity user);

    UserEntity toEntity(RegistrationRequestDto userDto);

}
