package com.myorg.tracemed.config;

import com.myorg.tracemed.entity.Role;
import com.myorg.tracemed.entity.Utilisateur;
import com.myorg.tracemed.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

        private final UtilisateurRepository utilisateurRepository;
        private final com.myorg.tracemed.repository.OrganisationRepository organisationRepository;
        private final com.myorg.tracemed.repository.ColisPhysiqueRepository colisPhysiqueRepository;
        private final com.myorg.tracemed.service.EvenementService evenementService;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {
                // 1. Get or Create Organisation
                com.myorg.tracemed.entity.Organisation org = organisationRepository.findAll().stream()
                                .filter(o -> "Clinique Centrale".equals(o.getNom()))
                                .findFirst()
                                .orElseGet(() -> {
                                        com.myorg.tracemed.entity.Organisation newOrg = new com.myorg.tracemed.entity.Organisation();
                                        newOrg.setNom("Clinique Centrale");
                                        newOrg.setTypeOrganisation("CLINIQUE");
                                        return organisationRepository.save(newOrg);
                                });

                // 2. Get or Create Admin
                Utilisateur admin = utilisateurRepository.findByUsername("admin")
                                .orElseGet(() -> {
                                        java.util.Map<String, String> keys = com.myorg.tracemed.util.SignatureUtil
                                                        .generateKeyPair();
                                        Utilisateur newAdmin = Utilisateur.builder()
                                                        .username("admin")
                                                        .password(passwordEncoder.encode("admin123"))
                                                        .role(Role.ADMIN)
                                                        .nomComplet("Administrateur Système")
                                                        .email("admin@tracemed.com")
                                                        .dateCreation(Instant.now())
                                                        .publicKey(keys.get("publicKey"))
                                                        .privateKey(keys.get("privateKey"))
                                                        .organisation(org)
                                                        .build();
                                        return utilisateurRepository.save(newAdmin);
                                });

                // 3. Ensure linking (Fix for existing users)
                if (admin.getOrganisation() == null) {
                        admin.setOrganisation(org);
                        utilisateurRepository.save(admin);
                }

                System.out.println("ADMIN user ready: username=admin");

                // 4. Seed Test Parcel
                seedTestParcel(admin, org);
        }

        private void seedTestParcel(Utilisateur admin, com.myorg.tracemed.entity.Organisation org) {
                if (colisPhysiqueRepository.findByIdentifiantColis("COLIS-TEST-1").isEmpty()) {
                        com.myorg.tracemed.entity.ColisPhysique colis = com.myorg.tracemed.entity.ColisPhysique
                                        .builder()
                                        .identifiantColis("COLIS-TEST-1")
                                        .dateEmballage(Instant.now())
                                        .statut(com.myorg.tracemed.entity.StatutColis.EN_TRANSIT)
                                        .proprietaireActuel(org)
                                        .build();
                        colis = colisPhysiqueRepository.save(colis);

                        // Create Genesis Event
                        com.myorg.tracemed.entity.EvenementColis ev = com.myorg.tracemed.entity.EvenementColis.builder()
                                        .colisPhysique(colis)
                                        .typeEvenement(com.myorg.tracemed.entity.TypeEvenement.RECU)
                                        .sousType("CREATION_INITIALE")
                                        .detailsJson("{\"message\": \"Colis de test créé par DataSeeder\"}")
                                        .realisePar(admin)
                                        .realiseParOrganisation(org)
                                        .build();

                        evenementService.createEventWithTx(ev);
                        System.out.println("TEST PARCEL created: ID=COLIS-TEST-1");
                }

                if (colisPhysiqueRepository.findByIdentifiantColis("COLIS-TEST-2").isEmpty()) {
                        com.myorg.tracemed.entity.ColisPhysique colis2 = com.myorg.tracemed.entity.ColisPhysique
                                        .builder()
                                        .identifiantColis("COLIS-TEST-2")
                                        .dateEmballage(Instant.now())
                                        .statut(com.myorg.tracemed.entity.StatutColis.EN_TRANSIT)
                                        .proprietaireActuel(org)
                                        .build();
                        colis2 = colisPhysiqueRepository.save(colis2);

                        // Create Genesis Event
                        com.myorg.tracemed.entity.EvenementColis ev2 = com.myorg.tracemed.entity.EvenementColis
                                        .builder()
                                        .colisPhysique(colis2)
                                        .typeEvenement(com.myorg.tracemed.entity.TypeEvenement.RECU)
                                        .sousType("CREATION_INITIALE")
                                        .detailsJson("{\"message\": \"Colis de test 2 créé par DataSeeder\"}")
                                        .realisePar(admin)
                                        .realiseParOrganisation(org)
                                        .build();

                        evenementService.createEventWithTx(ev2);
                        System.out.println("TEST PARCEL created: ID=COLIS-TEST-2");
                }
        }
}
