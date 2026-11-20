package bj.mobilier.config;

import bj.mobilier.model.CategorieChargeFabrication;
import bj.mobilier.model.CategorieChargeGenerale;
import bj.mobilier.model.CategorieMatiere;
import bj.mobilier.model.Fabrication;
import bj.mobilier.model.MatierePremiere;
import bj.mobilier.model.ModeleMeuble;
import bj.mobilier.model.RoleUtilisateur;
import bj.mobilier.model.UniteMesure;
import bj.mobilier.repository.MatierePremiereRepository;
import bj.mobilier.repository.UtilisateurRepository;
import bj.mobilier.service.AchatMatiereService;
import bj.mobilier.service.ChargeGeneraleService;
import bj.mobilier.service.FabricationService;
import bj.mobilier.service.MatierePremiereService;
import bj.mobilier.service.ModeleMeubleService;
import bj.mobilier.service.ParametresAtelierService;
import bj.mobilier.service.UtilisateurService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InitialisationDonnees implements CommandLineRunner {

    private static final ZoneId ZONE_BENIN = ZoneId.of("Africa/Porto-Novo");

    private final boolean demoMode;
    private final String demoPassword;
    private final UtilisateurRepository utilisateurRepository;
    private final MatierePremiereRepository matierePremiereRepository;
    private final UtilisateurService utilisateurService;
    private final ParametresAtelierService parametresAtelierService;
    private final MatierePremiereService matierePremiereService;
    private final AchatMatiereService achatMatiereService;
    private final ModeleMeubleService modeleMeubleService;
    private final FabricationService fabricationService;
    private final ChargeGeneraleService chargeGeneraleService;

    public InitialisationDonnees(
            @Value("${app.demo.mode:false}") boolean demoMode,
            @Value("${app.demo.password:}") String demoPassword,
            UtilisateurRepository utilisateurRepository,
            MatierePremiereRepository matierePremiereRepository,
            UtilisateurService utilisateurService,
            ParametresAtelierService parametresAtelierService,
            MatierePremiereService matierePremiereService,
            AchatMatiereService achatMatiereService,
            ModeleMeubleService modeleMeubleService,
            FabricationService fabricationService,
            ChargeGeneraleService chargeGeneraleService) {
        this.demoMode = demoMode;
        this.demoPassword = demoPassword;
        this.utilisateurRepository = utilisateurRepository;
        this.matierePremiereRepository = matierePremiereRepository;
        this.utilisateurService = utilisateurService;
        this.parametresAtelierService = parametresAtelierService;
        this.matierePremiereService = matierePremiereService;
        this.achatMatiereService = achatMatiereService;
        this.modeleMeubleService = modeleMeubleService;
        this.fabricationService = fabricationService;
        this.chargeGeneraleService = chargeGeneraleService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        parametresAtelierService.obtenirParametres();

        if (!demoMode || demoPassword == null || demoPassword.isBlank()) {
            return;
        }

        if (utilisateurRepository.count() == 0) {
            utilisateurService.creerUtilisateur(
                    "admin@mobilier.bj",
                    "Administrateur Atelier",
                    demoPassword,
                    RoleUtilisateur.ADMIN
            );
            utilisateurService.creerUtilisateur(
                    "agent@mobilier.bj",
                    "Agent Menuiserie",
                    demoPassword,
                    RoleUtilisateur.AGENT
            );
        }

        if (matierePremiereRepository.count() == 0) {
            initialiserDonneesDemonstration();
        }
    }

    private void initialiserDonneesDemonstration() {
        parametresAtelierService.mettreAJour(
                "Atelier Bois d'Abomey-Calavi",
                "Abomey-Calavi",
                "+229 01 97 00 00 00",
                "Quartier Zopah"
        );

        LocalDate aujourdHui = LocalDate.now(ZONE_BENIN);
        LocalDate debutMois = aujourdHui.withDayOfMonth(1);

        MatierePremiere iroko = matierePremiereService.creer(
                "BOIS-IROKO",
                "Planche de bois Iroko",
                CategorieMatiere.BOIS,
                UniteMesure.PLANCHE,
                new BigDecimal("10")
        );
        MatierePremiere vis = matierePremiereService.creer(
                "QUINC-VIS",
                "Vis à bois 50mm",
                CategorieMatiere.QUINCAILLERIE,
                UniteMesure.BOITE,
                new BigDecimal("5")
        );
        MatierePremiere vernis = matierePremiereService.creer(
                "FIN-VERNIS",
                "Vernis polyuréthane",
                CategorieMatiere.FINITION,
                UniteMesure.LITRE,
                new BigDecimal("4")
        );
        MatierePremiere colle = matierePremiereService.creer(
                "FIN-COLLE",
                "Colle à bois forte",
                CategorieMatiere.FINITION,
                UniteMesure.KILOGRAMME,
                new BigDecimal("3")
        );

        achatMatiereService.enregistrerAchat(iroko.getId(), debutMois, new BigDecimal("20"), new BigDecimal("8000"), "Scierie du Littoral");
        achatMatiereService.enregistrerAchat(iroko.getId(), aujourdHui, new BigDecimal("10"), new BigDecimal("8600"), "Scierie du Littoral");
        achatMatiereService.enregistrerAchat(vis.getId(), debutMois, new BigDecimal("8"), new BigDecimal("2500"), "Quincaillerie Dantokpa");
        achatMatiereService.enregistrerAchat(vernis.getId(), debutMois, new BigDecimal("6"), new BigDecimal("5500"), "Maison de la Peinture");
        achatMatiereService.enregistrerAchat(colle.getId(), debutMois, new BigDecimal("4"), new BigDecimal("3500"), "Quincaillerie Dantokpa");

        ModeleMeuble table = modeleMeubleService.creer(
                "MOD-TAB4",
                "Table à manger 4 places",
                "Table rectangulaire en bois massif Iroko avec finition vernie",
                new BigDecimal("120000")
        );
        ModeleMeuble chaise = modeleMeubleService.creer(
                "MOD-CHAISE",
                "Chaise en bois massif",
                "Chaise simple dossier droit en bois Iroko",
                new BigDecimal("25000")
        );
        modeleMeubleService.creer(
                "MOD-ARMOIRE",
                "Armoire 2 portes",
                "Armoire de rangement avec penderie et étagères",
                new BigDecimal("180000")
        );

        Fabrication fabTerminee = fabricationService.creerFabrication(
                table.getId(),
                2,
                debutMois,
                "Commande client quartier Fidjrossè"
        );
        fabricationService.ajouterConsommation(fabTerminee.getId(), iroko.getId(), new BigDecimal("8"), debutMois);
        fabricationService.ajouterConsommation(fabTerminee.getId(), vis.getId(), new BigDecimal("2"), debutMois);
        fabricationService.ajouterConsommation(fabTerminee.getId(), vernis.getId(), new BigDecimal("2"), aujourdHui);
        fabricationService.ajouterConsommation(fabTerminee.getId(), colle.getId(), new BigDecimal("1.5"), aujourdHui);
        fabricationService.ajouterMainOeuvre(fabTerminee.getId(), "Koffi Mensah", new BigDecimal("4"), "jour(s)", new BigDecimal("24000"), aujourdHui);
        fabricationService.ajouterMainOeuvre(fabTerminee.getId(), "Sèna Dossou", new BigDecimal("3"), "jour(s)", new BigDecimal("15000"), aujourdHui);
        fabricationService.ajouterCharge(fabTerminee.getId(), CategorieChargeFabrication.ELECTRICITE, new BigDecimal("4500"), "Découpe et ponçage électrique", aujourdHui);
        fabricationService.ajouterCharge(fabTerminee.getId(), CategorieChargeFabrication.TRANSPORT, new BigDecimal("5000"), "Livraison client", aujourdHui);
        fabricationService.terminerFabrication(fabTerminee.getId(), aujourdHui, 2);

        Fabrication fabEnCours = fabricationService.creerFabrication(
                chaise.getId(),
                6,
                aujourdHui,
                "Série en assemblage"
        );
        fabricationService.ajouterConsommation(fabEnCours.getId(), iroko.getId(), new BigDecimal("6"), aujourdHui);
        fabricationService.ajouterConsommation(fabEnCours.getId(), vis.getId(), new BigDecimal("2"), aujourdHui);
        fabricationService.ajouterMainOeuvre(fabEnCours.getId(), "Koffi Mensah", new BigDecimal("2"), "jour(s)", new BigDecimal("12000"), aujourdHui);

        chargeGeneraleService.creer(debutMois, CategorieChargeGenerale.LOYER, new BigDecimal("45000"), "Loyer mensuel hangar menuiserie");
        chargeGeneraleService.creer(aujourdHui, CategorieChargeGenerale.ELECTRICITE, new BigDecimal("18000"), "Facture SBEE atelier");
        chargeGeneraleService.creer(aujourdHui, CategorieChargeGenerale.EAU, new BigDecimal("4500"), "Facture SONEB");
    }
}
