/**
 * ==========================================================================
 * __      __ _ __   ___  *    WellProdSim                                  *
 * \ \ /\ / /| '_ \ / __| *    @version 1.0                                 *
 *  \ V  V / | |_) |\__ \ *    @since 2023                                  *
 *   \_/\_/  | .__/ |___/ *                                                 *
 *           | |          *    @author Jairo Serrano                        *
 *           |_|          *    @author Enrique Gonzalez                     *
 * ==========================================================================
 * Social Simulator used to estimate productivity and well-being of peasant *
 * families. It is event oriented, high concurrency, heterogeneous time     *
 * management and emotional reasoning BDI.                                  *
 * ==========================================================================
 */
package org.wpsim.PeasantFamily;

import BESA.BDI.AgentStructuralModel.Agent.AgentBDI;
import BESA.BDI.AgentStructuralModel.GoalBDI;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.StructBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import BESA.Log.ReportBESA;
import org.wpsim.Control.Guards.AliveAgentGuard;
import org.wpsim.Control.Guards.ControlAgentGuard;
import org.wpsim.Control.Guards.DeadAgentGuard;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.PeasantFamilyProfile;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;
import org.wpsim.PeasantFamily.Data.ToControlMessage;
import org.wpsim.PeasantFamily.Goals.L1Survival.DoVitalsGoal;
import org.wpsim.PeasantFamily.Goals.L1Survival.SeekPurposeGoal;
import org.wpsim.PeasantFamily.Goals.L1Survival.SelfEvaluationGoal;
import org.wpsim.PeasantFamily.Goals.L2Obligation.LookForLoanGoal;
import org.wpsim.PeasantFamily.Goals.L2Obligation.PayDebtsGoal;
import org.wpsim.PeasantFamily.Goals.L3Development.*;
import org.wpsim.PeasantFamily.Goals.L4SkillsResources.*;
import org.wpsim.PeasantFamily.Goals.L6Leisure.SpendFamilyTimeGoal;
import org.wpsim.PeasantFamily.Goals.L6Leisure.SpendFriendsTimeGoal;
import org.wpsim.PeasantFamily.Goals.L6Leisure.LeisureActivitiesGoal;
import org.wpsim.PeasantFamily.Goals.L6Leisure.WasteTimeAndResourcesGoal;
import org.wpsim.PeasantFamily.Guards.*;
import org.wpsim.PeasantFamily.Guards.FromBank.FromBankGuard;
import org.wpsim.PeasantFamily.Guards.FromGovernment.FromGovernmentGuard;
import org.wpsim.PeasantFamily.Guards.FromMarket.FromMarketGuard;
import org.wpsim.PeasantFamily.Guards.FromWorld.FromWorldGuard;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.wpsReport;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jairo
 */
@SuppressWarnings("unchecked")
public class PeasantFamilyBDIAgent extends AgentBDI {

    private static final double BDITHRESHOLD = 0;

    private static StructBESA createStruct(StructBESA structBESA) throws ExceptionBESA {
        // Cada comportamiento es un hilo.
        structBESA.addBehavior("PFamilyHeartBeatGuard");
        structBESA.bindGuard("PFamilyHeartBeatGuard", HeartBeatGuard.class);

        structBESA.addBehavior("DefaultPFamilyBehavior");
        structBESA.bindGuard("DefaultPFamilyBehavior", FromControlGuard.class);
        structBESA.bindGuard("DefaultPFamilyBehavior", FromWorldGuard.class);
        structBESA.bindGuard("DefaultPFamilyBehavior", FromBankGuard.class);
        structBESA.bindGuard("DefaultPFamilyBehavior", FromMarketGuard.class);
        structBESA.bindGuard("DefaultPFamilyBehavior", FromGovernmentGuard.class);
        structBESA.bindGuard("DefaultPFamilyBehavior", StatusGuard.class);
        structBESA.bindGuard("DefaultPFamilyBehavior", KillZombieGuard.class);

        return structBESA;
    }

    private static PeasantFamilyBDIAgentBelieves createBelieves(String alias, PeasantFamilyProfile profile) {
        return new PeasantFamilyBDIAgentBelieves(alias, profile);
    }

    private static List<GoalBDI> createGoals() {

        List<GoalBDI> goals = new ArrayList();

        //Level 1 Goals: Survival        
        goals.add(DoVitalsGoal.buildGoal());
        goals.add(SeekPurposeGoal.buildGoal());
        //goals.add(DoHealthCareGoal.buildGoal());
        //goals.add(SelfEvaluationGoal.buildGoal());
        //goals.add(PeasantOffGoal.buildGoal());

        //Level 2 Goals: Obligations
        goals.add(LookForLoanGoal.buildGoal());
        goals.add(PayDebtsGoal.buildGoal());

        //Level 3 Goals: Development        
        //goals.add(AttendToLivestockGoal.buildGoal());
        goals.add(CheckCropsGoal.buildGoal());
        goals.add(HarvestCropsGoal.buildGoal());
        goals.add(IrrigateCropsGoal.buildGoal());
        //goals.add(MaintainHouseGoal.buildGoal());
        goals.add(ManagePestsGoal.buildGoal());
        goals.add(PlantCropGoal.buildGoal());
        goals.add(PrepareLandGoal.buildGoal());
        //goals.add(ProcessProductsGoal.buildGoal());
        goals.add(SellCropGoal.buildGoal());
        //goals.add(SellProductsGoal.buildGoal());
        goals.add(StealingOutOfNecessityGoal.buildGoal());

        //Level 4 Goals: Skills And Resources
        goals.add(GetPriceListGoal.buildGoal());
        //goals.add(GetTrainingGoal.buildGoal());
        goals.add(ObtainALandGoal.buildGoal());
        //goals.add(ObtainLivestockGoal.buildGoal());
        //goals.add(ObtainSeedsGoal.buildGoal());
        //goals.add(ObtainSuppliesGoal.buildGoal());
        //goals.add(ObtainToolsGoal.buildGoal());
        //goals.add(ObtainWaterGoal.buildGoal());
        //goals.add(ObtainPesticidesGoal.buildGoal());

        //Level 5 Goals: Social
        //goals.add(CommunicateGoal.buildGoal());
        //goals.add(LookForCollaborationGoal.buildGoal());
        //goals.add(ProvideCollaborationGoal.buildGoal());

        //Level 6 Goals: Leisure
        goals.add(SpendFamilyTimeGoal.buildGoal());
        goals.add(SpendFriendsTimeGoal.buildGoal());
        goals.add(LeisureActivitiesGoal.buildGoal());
        goals.add(WasteTimeAndResourcesGoal.buildGoal());

        return goals;
    }

    /**
     *
     * @param alias
     * @param peasantProfile
     * @throws ExceptionBESA
     */
    public PeasantFamilyBDIAgent(String alias, PeasantFamilyProfile peasantProfile) throws ExceptionBESA {
        super(alias, createBelieves(alias, peasantProfile), createGoals(), BDITHRESHOLD, createStruct(new StructBESA()));
        wpsReport.info("Starting " + alias + " " + peasantProfile.getPeasantKind(), alias);
    }

    /**
     *
     */
    @Override
    public void setupAgentBDI() {
        // Anuncio de que el agente está disponible
        wpsReport.debug("Setup " + this.getAlias(), this.getAlias());
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) ((StateBDI) this.getState()).getBelieves();
        try {
            AdmBESA adm = AdmBESA.getInstance();
            ToControlMessage toControlMessage = new ToControlMessage(
                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                    believes.getCurrentDay()
            );
            EventBESA eventBesa = new EventBESA(
                    AliveAgentGuard.class.getName(),
                    toControlMessage
            );
            AgHandlerBESA agHandler = adm.getHandlerByAlias(
                    wpsStart.config.getControlAgentName()
            );
            agHandler.sendEvent(eventBesa);
        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
        }
    }

    /**
     *
     */
    @Override
    public void shutdownAgentBDI() {
        wpsReport.debug("Shutdown " + this.getAlias(), this.getAlias());
        // Anuncio de que el agente está muerto
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) ((StateBDI) this.getState()).getBelieves();
        wpsReport.debug(believes.toJson(), this.getAlias());

        //Eliminar la tierra del agente
        try {
            AdmBESA adm = AdmBESA.getInstance();
            EventBESA eventBesa = new EventBESA(KillZombieGuard.class.getName(), null);
            AgHandlerBESA agHandler = adm.getHandlerByAlias(believes.getPeasantProfile().getPeasantFamilyAlias() + "_land");
            agHandler.sendEvent(eventBesa);
        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, "ControlAgentState");
        }
        //Eliminar el agente
        try {
            AdmBESA adm = AdmBESA.getInstance();
            ToControlMessage toControlMessage = new ToControlMessage(
                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                    believes.getCurrentDay()
            );
            EventBESA eventBesa = new EventBESA(
                    DeadAgentGuard.class.getName(),
                    toControlMessage
            );
            AgHandlerBESA agHandler = adm.getHandlerByAlias(
                    wpsStart.config.getControlAgentName()
            );
            agHandler.sendEvent(eventBesa);

            wpsReport.ws(believes.toJson(), believes.getPeasantProfile().getPeasantFamilyAlias());
        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
        }
    }

}
