package swypraven.complimentlabserver.domain.compliment.sequence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import swypraven.complimentlabserver.domain.compliment.repository.ComplimentRepository;
import swypraven.complimentlabserver.global.exception.archive.ArchiveErrorCode;
import swypraven.complimentlabserver.global.exception.archive.ArchiveException;
import swypraven.complimentlabserver.global.exception.compliment.ComplimentCode;
import swypraven.complimentlabserver.global.exception.compliment.ComplimentException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ComplimentSequenceProviderImpl implements ComplimentSequenceProvider {

    private final LocalDate baseDate;                 // 예: 2025-09-01
    private final ComplimentRepository complimentRepository;

    // lazy 로딩 대상으로 변경
    private volatile int[] baseIds;                   // 모든 compliment_id (정렬)
    private volatile int totalCount;                  // baseIds.length
    private final Map<Integer, int[]> cache = new ConcurrentHashMap<>();

    public ComplimentSequenceProviderImpl(
            @Value("${compliment.base-date:2025-09-01}") String baseDateStr,
            ComplimentRepository complimentRepository
    ) {
        this.baseDate = LocalDate.parse(baseDateStr);
        this.complimentRepository = complimentRepository;
        // ❌ 생성자에서 즉시 로딩/예외 금지
        log.info("[ComplimentSeq] baseDate={}", baseDate);
    }

    @Override
    public int idFor(int seed, LocalDate date) {
        ensureBaseIds();                // 최초 접근 시 로딩
        int[] order = sequenceFor(seed);
        int idx = indexForDate(date);
        return order[idx];
    }

    @Override
    public int[] sequenceFor(int seed) {
        ensureBaseIds();                // 최초 접근 시 로딩
        return cache.computeIfAbsent(seed, s -> {
            int[] arr = Arrays.copyOf(baseIds, baseIds.length);
            Random rng = new Random(seededLong(s));
            for (int i = arr.length - 1; i > 0; i--) {
                int j = rng.nextInt(i + 1);
                int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
            }
            return arr;
        });
    }

    /** 최초 접근 시 마스터 ID 로딩 (비어 있으면 명확한 에러) */
    private synchronized void ensureBaseIds() {
        if (baseIds != null && baseIds.length > 0) return;

        List<Integer> allIds = complimentRepository.findAllIdsSorted();
        if (allIds == null || allIds.isEmpty()) {
            throw new ComplimentException(ComplimentCode.MASTER_EMPTY);
        }

        this.baseIds = allIds.stream().mapToInt(Integer::intValue).toArray();
        this.totalCount = baseIds.length;
        this.cache.clear();
        log.info("[ComplimentSeq] loaded compliments: count={}", totalCount);
    }

    private int indexForDate(LocalDate target) {
        long days = ChronoUnit.DAYS.between(baseDate, target);
        int mod = (int) (days % totalCount);
        if (mod < 0) mod += totalCount; // 과거 날짜 안전 처리
        return mod;
    }

    private long seededLong(int seed) {
        long x = Integer.toUnsignedLong(seed);
        x ^= (x << 21);
        x ^= (x >>> 35);
        x ^= (x << 4);
        return x;
    }

    /** 마스터가 바뀌었을 때 호출 (관리자용) */
    public synchronized void refresh() {
        this.baseIds = null;
        this.totalCount = 0;
        this.cache.clear();
        log.info("[ComplimentSeq] cache cleared; will reload on next call");
    }
}
