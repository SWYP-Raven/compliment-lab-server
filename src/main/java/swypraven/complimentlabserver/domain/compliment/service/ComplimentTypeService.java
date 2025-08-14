package swypraven.complimentlabserver.domain.compliment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.domain.compliment.entity.TypeCompliment;
import swypraven.complimentlabserver.domain.compliment.repository.TypeComplimentRepository;
import swypraven.complimentlabserver.global.exception.complimentType.ComplimentTypeCode;
import swypraven.complimentlabserver.global.exception.complimentType.ComplimentTypeException;

@Service
@RequiredArgsConstructor
public class ComplimentTypeService {
    private final TypeComplimentRepository  typeComplimentRepository;

    public TypeCompliment getType(String type) {
        TypeCompliment typeCompliment = typeComplimentRepository.findByName(type).orElseThrow(
                () ->  new ComplimentTypeException(ComplimentTypeCode.NOT_FOUND)
        );
        return typeCompliment;
    }
}
