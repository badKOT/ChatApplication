package self.project.messaging.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import self.project.messaging.dto.ChatIdTitleDto;
import self.project.messaging.model.Chat;

@Mapper
public interface DtoChatMapper {

    DtoChatMapper INSTANCE = Mappers.getMapper(DtoChatMapper.class);

    ChatIdTitleDto toDto(Chat chat);
}
