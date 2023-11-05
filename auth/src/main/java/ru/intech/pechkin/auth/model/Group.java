package ru.intech.pechkin.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "grp")
//@SecondaryTable(name = "grp_type", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id", referencedColumnName = "grp_type_id"))
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "grp_user_id_fkey"))
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "grp_type_id", foreignKey = @ForeignKey(name = "grp_grp_type_id_fkey"))
    private GroupType groupType;
}
