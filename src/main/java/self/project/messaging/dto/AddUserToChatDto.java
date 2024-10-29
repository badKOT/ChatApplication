package self.project.messaging.dto;

public record AddUserToChatDto(Long userToAddId, Long chatId, Long initiatorId) {}
