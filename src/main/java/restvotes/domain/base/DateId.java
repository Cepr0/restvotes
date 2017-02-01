package restvotes.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

/**
 * Base class for entity with {@link LocalDate} id.
 * @author Cepro, 2016-12-10
 */
@MappedSuperclass
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DateId extends BaseEntity<LocalDate> {
    
    @Id
    @Column(columnDefinition = "date default now()", unique = true, nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    public DateId() {
        date = LocalDate.now();
    }
    
    @JsonIgnore
    @Override
    public LocalDate getId() {
        return date;
    }
    
    @Override
    public BaseEntity setId(LocalDate date) {
        this.date = date;
        return this;
    }
    
    public LocalDate getDate() {
        return date;
    }
}
