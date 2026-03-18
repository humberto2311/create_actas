package com.app.juridico.create_actas.domain.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*; // Importación de anotaciones JPA (usando Jakarta)

import java.time.LocalDate;


@Entity
@Table(name = "document_process")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "captura", nullable = false)
    private LocalDate captura;
    
    @Column(name = "names", length = 100, nullable = false)
    private String names;
    
    @Column(name = "last_names", length = 100, nullable = false)
    private String lastNames;
    
    @Column(name = "identity", length = 20,  nullable = false)
    private String identity;

    @Column(name = "nacion", length = 20, nullable = false)
    private String nacion;


    // Delito o tipo de conducta penal
    @Column(name = "conduct", length = 255)
    private String conduct; 
    
    // Número de radicado o expediente
    @Column(name = "radicado",unique = true, length = 30) // Cambiado de Integer a String
    private String radicado;
    
    // Nombre del Fiscal responsable
    @Column(name = "fiscal", length = 100)
    private String fiscal;
    
    // Tipo de audiencia (e.g., imputación, control de garantías)
    @Column(name = "type_audience", length = 50)
    private String typeAudience;
    
    // Descripción de los hechos o fundamentos fácticos
    @Column(name = "fact", columnDefinition = "TEXT") // Usa TEXT para descripciones largas
    private String fact; 
    
    // Nombre del Juzgado
    @Column(name = "juzgado", length = 100)
    private String juzgado;
    
    // Estado del proceso (si/no). Se utiliza 'boolean' para mapear a un tipo de dato lógico (TRUE/FALSE).
    @Column(name = "state", nullable = false) // CAMBIO: Eliminado el 'length'
    private boolean state; // CAMBIO: Tipo de dato a 'boolean' (true = Sí, false = No)
}