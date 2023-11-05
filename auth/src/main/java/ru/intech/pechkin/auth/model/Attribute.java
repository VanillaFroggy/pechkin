package ru.intech.pechkin.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "attr")
@Table(name = "attr")
//@SecondaryTable(name = "attr_type", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id", referencedColumnName = "attr_type_id"))
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "is_valid")
    private boolean isValid;

    @Column(name = "is_main_attr")
    private boolean isMainAttr;

    @Column(name = "attr_value")
    private String value;

    @CreatedDate
    @Column(name = "beg_date")
    private Date begDate;

    @Column(name = "end_date")
    private Date endDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "attr_type_id", table = "attr")
    private AttributeType attributeType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", table = "attr")
    private User user;
}
