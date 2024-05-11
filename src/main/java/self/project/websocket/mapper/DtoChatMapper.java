package self.project.websocket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import self.project.websocket.dto.ChatIdTitleDto;
import self.project.websocket.model.Chat;

@Mapper
public interface DtoChatMapper {

    DtoChatMapper INSTANCE = Mappers.getMapper(DtoChatMapper.class);

    ChatIdTitleDto toDto(Chat chat);
}
