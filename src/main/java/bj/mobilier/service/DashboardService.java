package bj.mobilier.service;

import bj.mobilier.model.MatierePremiere;
import bj.mobilier.repository.ChargeFabricationRepository;
import bj.mobilier.repository.ChargeGeneraleRepository;
import bj.mobilier.repository.ConsommationMatiereRepository;
import bj.mobilier.repository.FabricationRepository;
import bj.mobilier.repository.MainOeuvreFabricationRepository;
import bj.mobilier.repository.MatierePremiereRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private static final ZoneId ZONE_BENIN = ZoneId.of("Africa/Porto-Novo");

    private final FabricationRepository fabricationRepository;
    private final ConsommationMatiereRepository consommationMatiereRepository;
    private final MainOeuvreFabricationRepository mainOeuvreFabricationRepository;
    private final ChargeFabricationRepository chargeFabricationRepository;
    private final MatierePremiereRepository matierePremiereRepository;
    private final ChargeGeneraleRepository chargeGeneraleRepository;

    public DashboardService(
            FabricationRepository fabricationRepository,
            ConsommationMatiereRepository consommationMatiereRepository,
            MainOeuvreFabricationRepository mainOeuvreFabricationRepository,
            ChargeFabricationRepository chargeFabricationRepository,
            MatierePremiereRepository matierePremiereRepository,
            ChargeGeneraleRepository chargeGeneraleRepository) {
        this.fabricationRepository = fabricationRepository;
        this.consommationMatiereRepository = consommationMatiereRepository;
        this.mainOeuvreFabricationRepository = mainOeuvreFabricationRepository;
        this.chargeFabricationRepository = chargeFabricationRepository;
        this.matierePremiereRepository = matierePremiereRepository;
        this.chargeGeneraleRepository = chargeGeneraleRepository;
    }

    public IndicateursDashboard calculerIndicateurs() {
        LocalDate aujourdHui = LocalDate.now(ZONE_BENIN);
        LocalDate debutMois = aujourdHui.withDayOfMonth(1);
        LocalDate finMois = aujourdHui.withDayOfMonth(aujourdHui.lengthOfMonth());

        long nbFabricationsTermineesMois = fabricationRepository.countTermineesDansPeriode(debutMois, finMois);
        Long sommeMeubles = fabricationRepository.sumQuantiteProduiteDansPeriode(debutMois, finMois);
        long nbMeublesProduitsMois = sommeMeubles != null ? sommeMeubles : 0L;

        BigDecimal coutMatieresMois = zeroSiNul(consommationMatiereRepository.sumCoutDansPeriode(debutMois, finMois));
        BigDecimal coutMainOeuvreMois = zeroSiNul(mainOeuvreFabricationRepository.sumMontantDansPeriode(debutMois, finMois));
        BigDecimal chargesProductionMois = zeroSiNul(chargeFabricationRepository.sumMontantDansPeriode(debutMois, finMois));
        BigDecimal coutTotalProductionMois = coutMatieresMois.add(coutMainOeuvreMois).add(chargesProductionMois);

        BigDecimal valeurStockActuel = zeroSiNul(matierePremiereRepository.sumValeurStock());
        long nbMatieresStockBas = matierePremiereRepository.countMatieresStockBas();
        BigDecimal chargesGeneralesMois = zeroSiNul(chargeGeneraleRepository.sumMontantDansPeriode(debutMois, finMois));
        List<MatierePremiere> matieresStockBas = matierePremiereRepository.findMatieresStockBas();

        String moisCourant = aujourdHui.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH));

        return new IndicateursDashboard(
                moisCourant,
                nbFabricationsTermineesMois,
                nbMeublesProduitsMois,
                coutMatieresMois,
                coutMainOeuvreMois,
                chargesProductionMois,
                coutTotalProductionMois,
                valeurStockActuel,
                nbMatieresStockBas,
                chargesGeneralesMois,
                matieresStockBas
        );
    }

    private BigDecimal zeroSiNul(BigDecimal valeur) {
        return valeur != null ? valeur : BigDecimal.ZERO;
    }

    public static class IndicateursDashboard {

        private final String moisCourant;
        private final long nbFabricationsTermineesMois;
        private final long nbMeublesProduitsMois;
        private final BigDecimal coutMatieresMois;
        private final BigDecimal coutMainOeuvreMois;
        private final BigDecimal chargesProductionMois;
        private final BigDecimal coutTotalProductionMois;
        private final BigDecimal valeurStockActuel;
        private final long nbMatieresStockBas;
        private final BigDecimal chargesGeneralesMois;
        private final List<MatierePremiere> matieresStockBas;

        public IndicateursDashboard(
                String moisCourant,
                long nbFabricationsTermineesMois,
                long nbMeublesProduitsMois,
                BigDecimal coutMatieresMois,
                BigDecimal coutMainOeuvreMois,
                BigDecimal chargesProductionMois,
                BigDecimal coutTotalProductionMois,
                BigDecimal valeurStockActuel,
                long nbMatieresStockBas,
                BigDecimal chargesGeneralesMois,
                List<MatierePremiere> matieresStockBas) {
            this.moisCourant = moisCourant;
            this.nbFabricationsTermineesMois = nbFabricationsTermineesMois;
            this.nbMeublesProduitsMois = nbMeublesProduitsMois;
            this.coutMatieresMois = coutMatieresMois;
            this.coutMainOeuvreMois = coutMainOeuvreMois;
            this.chargesProductionMois = chargesProductionMois;
            this.coutTotalProductionMois = coutTotalProductionMois;
            this.valeurStockActuel = valeurStockActuel;
            this.nbMatieresStockBas = nbMatieresStockBas;
            this.chargesGeneralesMois = chargesGeneralesMois;
            this.matieresStockBas = matieresStockBas;
        }

        public String getMoisCourant() {
            return moisCourant;
        }

        public long getNbFabricationsTermineesMois() {
            return nbFabricationsTermineesMois;
        }

        public long getNbMeublesProduitsMois() {
            return nbMeublesProduitsMois;
        }

        public BigDecimal getCoutMatieresMois() {
            return coutMatieresMois;
        }

        public BigDecimal getCoutMainOeuvreMois() {
            return coutMainOeuvreMois;
        }

        public BigDecimal getChargesProductionMois() {
            return chargesProductionMois;
        }

        public BigDecimal getCoutTotalProductionMois() {
            return coutTotalProductionMois;
        }

        public BigDecimal getValeurStockActuel() {
            return valeurStockActuel;
        }

        public long getNbMatieresStockBas() {
            return nbMatieresStockBas;
        }

        public BigDecimal getChargesGeneralesMois() {
            return chargesGeneralesMois;
        }

        public List<MatierePremiere> getMatieresStockBas() {
            return matieresStockBas;
        }
    }
}
