package self.project.messaging.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import self.project.messaging.dto.ChatIdTitleDto;
import self.project.messaging.model.Chat;

@Mapper
public interface ChatMapper {

    ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);

    ChatIdTitleDto toDto(Chat chat);
}
