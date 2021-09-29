package org.zerock.guestbook.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.guestbook.dto.GuestbookDTO;
import org.zerock.guestbook.dto.PageRequestDTO;
import org.zerock.guestbook.dto.PageResultDTO;
import org.zerock.guestbook.entity.Guestbook;
import org.zerock.guestbook.entity.QGuestbook;
import org.zerock.guestbook.repository.GuestbookRepository;

import javax.swing.text.StyledEditorKit;
import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor // 의존성 자동 주입
public class GuestbookServiceImpl implements GuestbookService{

    // 반드시 final 선언
    private final GuestbookRepository repository;

    @Override
    public Long regsiter(GuestbookDTO dto) {

        log.info("DTO--------------------------------");
        log.info(dto);
        Guestbook entity = dtoToEntity(dto);
        log.info(entity);
        repository.save(entity);

        return entity.getGno();
    }



    @Override
    public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO) {

        Pageable pageable = requestDTO.getPageable(Sort.by("gno").descending());

        BooleanBuilder booleanBuilder = getSearch(requestDTO); // 검색 조건 처리

        Page<Guestbook> result = repository.findAll(booleanBuilder, pageable); //Querydsl 사용

        Function<Guestbook, GuestbookDTO> fn = (entity -> entityToDto(entity));

        return new PageResultDTO<>(result, fn );
    }


    @Override
    public GuestbookDTO read(Long gno) {

        Optional<Guestbook> result = repository.findById(gno);

        return result.isPresent() ? entityToDto(result.get()) : null;
    }


    @Override
    public void modify(GuestbookDTO dto) {

        Optional<Guestbook> result = repository.findById(dto.getGno());

        if(result.isPresent()){

            Guestbook entity = result.get();

            entity.changeTitle(dto.getTitle());

            entity.changeContent(dto.getContent());

            repository.save(entity);
        }
    }


    @Override
    public void remove(Long gno) {

        repository.deleteById(gno);

    }

    private BooleanBuilder getSearch(PageRequestDTO requestDTO){

        String type = requestDTO.getType();

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // 검색 조건이 없는 경우우
        if(type == null || type.trim().length() == 0)
            return booleanBuilder;

        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword = requestDTO.getKeyword();

        BooleanExpression expression = qGuestbook.gno.gt(0L); // gno > 0 조건만 생성

        booleanBuilder.and(expression);

        // 검색 조건 작성
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        if(type.contains("t"))
            conditionBuilder.or(qGuestbook.title.contains(keyword));
        if(type.contains("c"))
            conditionBuilder.or(qGuestbook.content.contains(keyword));
        if(type.contains("w"))
            conditionBuilder.or(qGuestbook.writer.contains(keyword));

        // 모든 검색 조건 통합
        booleanBuilder.and(conditionBuilder);

        return booleanBuilder;
    }

}
