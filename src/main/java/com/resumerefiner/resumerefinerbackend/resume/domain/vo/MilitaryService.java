package com.resumerefiner.resumerefinerbackend.resume.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MilitaryService implements ValueObject {

    private static final int PERIOD_MAX = 50;
    private static final int RANK_MAX = 50;
    private static final int NOTES_MAX = 500;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "military_status", nullable = false, length = 30)
    private MilitaryStatus status;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "military_branch", length = 30)
    private MilitaryBranch branch; // optional (해당 없음이면 null 가능)

    @Getter
    @Column(name = "military_period", length = PERIOD_MAX)
    private String period; // optional

    @Getter
    @Column(name = "military_rank", length = RANK_MAX)
    private String rank; // optional

    @Getter
    @Column(name = "military_notes", length = NOTES_MAX)
    private String notes; // optional

    private MilitaryService(
            MilitaryStatus status,
            MilitaryBranch branch,
            String period,
            String rank,
            String notes
    ) {
        this.status = Objects.requireNonNull(status, "MILITARY_STATUS_REQUIRED");
        this.branch = branch;
        this.period = normalizeOptional(period, PERIOD_MAX);
        this.rank = normalizeOptional(rank, RANK_MAX);
        this.notes = normalizeOptional(notes, NOTES_MAX);
    }

    /* ---------------------------
     * Static factories
     * --------------------------- */

    public static MilitaryService of(
            MilitaryStatus status,
            MilitaryBranch branch,
            String period,
            String rank,
            String notes
    ) {
        return new MilitaryService(status, branch, period, rank, notes);
    }

    /** 병역 없음/해당 없음 같은 상태만 표현하고 싶을 때 */
    public static MilitaryService ofStatus(MilitaryStatus status) {
        return new MilitaryService(status, null, null, null, null);
    }

    /* ---------------------------
     * Domain actions
     * --------------------------- */

    public MilitaryService changeStatus(MilitaryStatus status) {
        return new MilitaryService(status, this.branch, this.period, this.rank, this.notes);
    }

    public MilitaryService changeBranch(MilitaryBranch branch) {
        return new MilitaryService(this.status, branch, this.period, this.rank, this.notes);
    }

    public MilitaryService changePeriod(String period) {
        return new MilitaryService(this.status, this.branch, period, this.rank, this.notes);
    }

    public MilitaryService changeRank(String rank) {
        return new MilitaryService(this.status, this.branch, this.period, rank, this.notes);
    }

    public MilitaryService changeNotes(String notes) {
        return new MilitaryService(this.status, this.branch, this.period, this.rank, notes);
    }

    /** 공유/익명화용: 민감정보 제거 */
    public MilitaryService redactDetails() {
        return new MilitaryService(this.status, null, null, null, null);
    }

    /* ---------------------------
     * Validation & normalization
     * --------------------------- */

    private static String normalizeOptional(String raw, int maxLen) {
        if (raw == null) return null;
        String v = raw.trim().replaceAll("\\s+", " ");
        if (v.isEmpty()) return null;
        if (v.length() > maxLen) throw new IllegalArgumentException("VALUE_TOO_LONG");
        return v;
    }

    /* ---------------------------
     * Value semantics
     * --------------------------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MilitaryService that)) return false;
        return status == that.status
                && branch == that.branch
                && Objects.equals(period, that.period)
                && Objects.equals(rank, that.rank)
                && Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, branch, period, rank, notes);
    }
}
