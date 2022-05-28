package top.okfly.auth.infra;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class SeqIdGenerator implements LongIdGenerator {

    private AtomicLong seq = new AtomicLong(0L);

    @Override
    public long generateId() {
        return seq.incrementAndGet();
    }
}
