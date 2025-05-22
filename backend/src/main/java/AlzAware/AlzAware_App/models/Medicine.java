package AlzAware.AlzAware_App.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medicines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    private String name;

    @NotNull
    private int whichDayParts;  // 1 = öğle, 2 = sabah - akşam, 3 = sabah - öğle - akşam

    @NotNull
    private int usage;  // 1 = tok, 0 = aç

    @NotNull
    private int count;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    private User patient;

    public Medicine(String name, int whichDayParts, int usage, int count, User patient) {
        this.name = name;
        this.whichDayParts = whichDayParts;
        this.usage = usage;
        this.count = count;
        this.patient = patient;
    }

}
