package org.example.accessbasededonnees.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "forms")
public class Forms {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_demand")
    private Long idDemand;

    @Column(name = "type_of_reference")
    private String typeOfReference;

    @Column(name = "requester_name")
    private String requesterName;

    @Column(name = "product_line")
    private String productLine;

    @Column(name = "type_of_article")
    private String typeOfArticle;

    @Column(name = "pricing_validation")
    private String pricingValidation;

    @Column(name = "purchase_validation")
    private String purchaseValidation;

    @Column(name = "ref_affairs_validation")
    private String regulatoryAffairsValidation;

    @Column(name = "product_classification_description")
    private String productClassificationDescription;

    @Column(name = "catalog_price")
    private String catalogPrice;

    @Column(name = "marking")
    private boolean marking;

    @Column(name = "type_of_marking")
    private String typeOfMarking;

    @Column(name = "id_marking")
    private Integer idMarking;

    @Column(name = "email")
    private String email;

    @Column(name = "id_status")
    private String statusId;

    @Column(name =  "product_id")
    private int productId;

    public Forms() {
    }


}
