package ru.intech.pechkin.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attr_type")
public class AttributeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "validation")
    private String validation;

    @Column(name = "description")
    private String description;

    @Column(name = "validation_description")
    private String validationDescription;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "attributeType")
//    @JoinColumn(name = "attr_type_id", table = "attr")
    private List<Attribute> attributes;
}
