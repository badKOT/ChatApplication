package self.project.websocket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import self.project.websocket.dto.MessageDto;
import self.project.websocket.model.Message;

@Mapper
public interface DtoMessageMapper {
    DtoMessageMapper INSTANCE = Mappers.getMapper(DtoMessageMapper.class);

    @Mapping(target = "content", source = "text")
    @Mapping(target = "sender", source = "sender.username")
    @Mapping(target = "type", expression = "java(self.project.websocket.dto.MessageType.CHAT)")
    @Mapping(target = "sent", source = "sent")
    @Mapping(target = "chatId", source = "chat.id")
    MessageDto toDto(Message message);
}
