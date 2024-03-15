package ru.intech.pechkin.messenger.infrastructure.service.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GetPageOfUsersByFieldLikeDto {
    private final String value;

    @Size()
    private final int pageNumber;

    @Size(min = 1, max = 50)
    private final int pageSize;
}
