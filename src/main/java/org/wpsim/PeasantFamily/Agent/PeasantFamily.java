/**
 * ==========================================================================
 * __      __ _ __   ___  *    WellProdSim                                  *
 * \ \ /\ / /| '_ \ / __| *    @version 1.0                                 *
 * \ V  V / | |_) |\__ \ *    @since 2023                                  *
 * \_/\_/  | .__/ |___/ *                                                 *
 * | |          *    @author Jairo Serrano                        *
 * |_|          *    @author Enrique Gonzalez                     *
 * ==========================================================================
 * Social Simulator used to estimate productivity and well-being of peasant *
 * families. It is event oriented, high concurrency, heterogeneous time     *
 * management and emotional reasoning BDI.                                  *
 * ==========================================================================
 */
package org.wpsim.PeasantFamily.Agent;

import BESA.BDI.AgentStructuralModel.Agent.AgentBDI;
import BESA.BDI.AgentStructuralModel.GoalBDI;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.PeriodicGuardBESA;
import BESA.Kernel.Agent.StructBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AdmHandlerBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import BESA.Remote.AdmRemoteImpBESA;
import BESA.Remote.Directory.AgRemoteHandlerBESA;
import BESA.Remote.Directory.RemoteAdmHandlerBESA;
import BESA.Remote.RemoteAdmBESA;
import BESA.Util.PeriodicDataBESA;
import org.wpsim.PeasantFamily.Goals.L1Survival.DoVoidGoal;
import org.wpsim.PeasantFamily.Guards.FromCivicAuthority.FromCivicAuthorityTrainingGuard;
import org.wpsim.SimulationControl.Guards.AliveAgentGuard;
import org.wpsim.SimulationControl.Guards.DeadAgentGuard;
import org.wpsim.CivicAuthority.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.PeasantFamilyProfile;
import org.wpsim.PeasantFamily.Goals.L1Survival.DoHealthCareGoal;
import org.wpsim.PeasantFamily.Goals.L2Obligation.LookForLoanGoal;
import org.wpsim.PeasantFamily.Goals.L2Obligation.PayDebtsGoal;
import org.wpsim.PeasantFamily.Guards.FromSimulationControl.ToControlMessage;
import org.wpsim.PeasantFamily.Goals.L1Survival.DoVitalsGoal;
import org.wpsim.PeasantFamily.Goals.L1Survival.SeekPurposeGoal;
import org.wpsim.PeasantFamily.Goals.L3Development.*;
import org.wpsim.PeasantFamily.Goals.L4SkillsResources.*;
import org.wpsim.PeasantFamily.Goals.L5Social.LookForCollaborationGoal;
import org.wpsim.PeasantFamily.Goals.L5Social.ProvideCollaborationGoal;
import org.wpsim.PeasantFamily.Goals.L6Leisure.SpendFamilyTimeGoal;
import org.wpsim.PeasantFamily.Goals.L6Leisure.SpendFriendsTimeGoal;
import org.wpsim.PeasantFamily.Goals.L6Leisure.LeisureActivitiesGoal;
import org.wpsim.PeasantFamily.Goals.L6Leisure.WasteTimeAndResourcesGoal;
import org.wpsim.PeasantFamily.Guards.FromBankOffice.FromBankOfficeGuard;
import org.wpsim.PeasantFamily.Guards.FromSimulationControl.FromSimulationControlGuard;
import org.wpsim.PeasantFamily.Guards.FromCivicAuthority.FromCivicAuthorityGuard;
import org.wpsim.PeasantFamily.Guards.FromMarketPlace.FromMarketPlaceGuard;
import org.wpsim.PeasantFamily.Guards.FromCommunityDynamics.PeasantWorkerContractFinishedGuard;
import org.wpsim.PeasantFamily.Guards.FromCommunityDynamics.SocietyWorkerContractGuard;
import org.wpsim.PeasantFamily.Guards.FromCommunityDynamics.SocietyWorkerContractorGuard;
import org.wpsim.PeasantFamily.Guards.FromAgroEcosystem.FromAgroEcosystemGuard;
import org.wpsim.PeasantFamily.PeriodicGuards.HeartBeatGuard;
import org.wpsim.PeasantFamily.Guards.Status.StatusGuard;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.ViewerLens.Util.wpsReport;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static org.wpsim.WellProdSim.wpsStart.config;
import static org.wpsim.WellProdSim.wpsStart.params;

/**
 * @TODO: Patrones de comunicación
 */

/**
 * @author jairo
 */
@SuppressWarnings("unchecked")
public class PeasantFamily extends AgentBDI {

    private static final double BDITHRESHOLD = 0;

    private static StructBESA createStruct(StructBESA structBESA) throws ExceptionBESA {
        // Cada comportamiento es un hilo.
        structBESA.addBehavior("HeartBeatBehavior");
        structBESA.bindGuard("HeartBeatBehavior", HeartBeatGuard.class);

        structBESA.addBehavior("FromWorldBehavior");
        structBESA.bindGuard("FromWorldBehavior", FromAgroEcosystemGuard.class);

        structBESA.addBehavior("SocietyBehavior");
        structBESA.bindGuard("SocietyBehavior", SocietyWorkerContractGuard.class);
        structBESA.bindGuard("SocietyBehavior", SocietyWorkerContractorGuard.class);
        structBESA.bindGuard("SocietyBehavior", PeasantWorkerContractFinishedGuard.class);

        structBESA.addBehavior("FromControlBehavior");
        structBESA.bindGuard("FromControlBehavior", FromSimulationControlGuard.class);

        structBESA.addBehavior("FromBankBehavior");
        structBESA.bindGuard("FromBankBehavior", FromBankOfficeGuard.class);

        structBESA.addBehavior("FromMarketBehavior");
        structBESA.bindGuard("FromMarketBehavior", FromMarketPlaceGuard.class);

        structBESA.addBehavior("FromCivicAuthorityBehavior");
        structBESA.bindGuard("FromCivicAuthorityBehavior", FromCivicAuthorityGuard.class);
        structBESA.bindGuard("FromCivicAuthorityBehavior", FromCivicAuthorityTrainingGuard.class);

        structBESA.addBehavior("StatusBehavior");
        structBESA.bindGuard("StatusBehavior", StatusGuard.class);

        return structBESA;
    }

    private static PeasantFamilyBelieves createBelieves(String alias, PeasantFamilyProfile profile) {
        return new PeasantFamilyBelieves(alias, profile);
    }

    private static List<GoalBDI> createGoals() {

        List<GoalBDI> goals = new ArrayList<>();

        //Level 1 Goals: Survival        
        goals.add(DoVoidGoal.buildGoal());
        goals.add(DoVitalsGoal.buildGoal());
        goals.add(SeekPurposeGoal.buildGoal());
        goals.add(DoHealthCareGoal.buildGoal());
        //goals.add(SelfEvaluationGoal.buildGoal());

        //Level 2 Goals: Obligations
        goals.add(LookForLoanGoal.buildGoal());
        goals.add(PayDebtsGoal.buildGoal());

        //Level 3 Goals: Development        
        //goals.add(AttendToLivestockGoal.buildGoal());
        goals.add(AttendReligiousEventsGoal.buildGoal());
        goals.add(CheckCropsGoal.buildGoal());
        goals.add(HarvestCropsGoal.buildGoal());
        goals.add(ManagePestsGoal.buildGoal());
        goals.add(PlantCropGoal.buildGoal());
        goals.add(PrepareLandGoal.buildGoal());
        goals.add(DeforestingLandGoal.buildGoal());
        goals.add(SellCropGoal.buildGoal());
        goals.add(SearchForHelpAndNecessityGoal.buildGoal());
        goals.add(WorkForOtherGoal.buildGoal());

        //goals.add(ProcessProductsGoal.buildGoal());
        //goals.add(SellProductsGoal.buildGoal());
        //goals.add(MaintainHouseGoal.buildGoal());

        //Level 4 Goals: Skills And Resources
        goals.add(GetPriceListGoal.buildGoal());
        goals.add(ObtainALandGoal.buildGoal());
        goals.add(ObtainSeedsGoal.buildGoal());
        goals.add(ObtainToolsGoal.buildGoal());
        goals.add(AlternativeWorkGoal.buildGoal());
        //goals.add(ObtainPesticidesGoal.buildGoal());
        //goals.add(ObtainSuppliesGoal.buildGoal());
        //goals.add(ObtainLivestockGoal.buildGoal());


        if (config.getBooleanProperty("pfagent.trainingEnabled")) {
            goals.add(GetTechAssistanceGoal.buildGoal());
        }

        if (params.irrigation == 1) {
            goals.add(IrrigateCropsGoal.buildGoal());
            goals.add(ObtainWaterGoal.buildGoal());
        }

        //Level 5 Goals: Social
        //goals.add(CommunicateGoal.buildGoal());
        goals.add(LookForCollaborationGoal.buildGoal());
        goals.add(ProvideCollaborationGoal.buildGoal());

        //Level 6 Goals: Leisure
        goals.add(SpendFamilyTimeGoal.buildGoal());
        goals.add(SpendFriendsTimeGoal.buildGoal());
        goals.add(LeisureActivitiesGoal.buildGoal());
        goals.add(WasteTimeAndResourcesGoal.buildGoal());

        return goals;
    }

    /**
     * @param alias
     * @param peasantProfile
     * @throws ExceptionBESA
     */
    public PeasantFamily(String alias, PeasantFamilyProfile peasantProfile) throws ExceptionBESA {
        super(alias, createBelieves(alias, peasantProfile), createGoals(), BDITHRESHOLD, createStruct(new StructBESA()));
        //wpsReport.info("Starting " + alias + " " + peasantProfile.getPeasantKind(), alias);
    }

    /**
     *
     */
    @Override
    public void setupAgentBDI() {
        //wpsReport.debug("Setup " + this.getAlias(), this.getAlias());
        // Internal HeartBeat ping
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) ((StateBDI) this.getState()).getBelieves();
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    getAlias()
            ).sendEvent(
                    new EventBESA(
                            HeartBeatGuard.class.getName(),
                            new PeriodicDataBESA(
                                    config.getLongProperty("control.steptime"),
                                    PeriodicGuardBESA.START_PERIODIC_CALL
                            )
                    )
            );
        } catch (ExceptionBESA ex) {
            System.out.println(ex.getMessage());
        }
        // External Alive Ping
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    config.getControlAgentName()
            ).sendEvent(
                    new EventBESA(
                            AliveAgentGuard.class.getName(),
                            new ToControlMessage(
                                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                                    believes.getCurrentDay()
                            )
                    )
            );
        } catch (ExceptionBESA ex) {
            System.out.println(ex.getMessage());
        }

    }

    /**
     *
     */
    @Override
    public synchronized void shutdownAgentBDI() {
        System.out.println("Shutdown " + this.getAlias());
        // Anuncio de que el agente está muerto
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) ((StateBDI) this.getState()).getBelieves();
        wpsReport.mental(believes.toCSV(), this.getAlias());
        wpsReport.ws(believes.toJson(), believes.getAlias());
        //Eliminar el agente
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    config.getControlAgentName()
            ).sendEvent(
                    new EventBESA(
                            DeadAgentGuard.class.getName(),
                            new ToControlMessage(
                                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                                    believes.getCurrentDay()
                            )
                    )
            );
            String agID = AdmBESA.getInstance().getHandlerByAlias(this.getAlias()).getAgId();
            AdmBESA.getInstance().killAgent(agID, config.getDoubleProperty("control.passwd"));
        } catch (Exception ex) {
            wpsReport.error(ex.getMessage(), believes.getAlias());
        }
    }

}
