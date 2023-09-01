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
package org.wpsim.PeasantFamily.Data;

/**
 *
 * @author jairo
 */
public enum TimeConsumedBy {

    LookForLoanTask(4),
    SelfEvaluationTask(2),
    StealingOutOfNecessityTask(6),
    SeekPurposeTask(12),
    GetPriceListTask(2),
    DoHealthCareTask(4),
    DoVitalsTask(12),
    AttendReligiousEventsTask(2),
    AttendToLivestockTask(2),
    CheckCropsTask(2),
    HarvestCropsTask(2),
    IrrigateCropsTask(2),
    MaintainHouseTask(2),
    ManagePestsTask(2),
    PlantCropTask(2),
    PrepareLandTask(2),
    ProcessProductsTask(2),
    SellCropTask(2),
    SellProductsTask(2),
    SpendFamilyTimeTask(2),
    GetTrainingTask(2),
    ObtainALandTask(8),
    ObtainLivestockTask(4),
    ObtainPesticidesTask(4),
    ObtainSeedsTask(4),
    ObtainSuppliestask(4),
    ObtainToolsTask(4),
    ObtainWaterTask(4),
    CommunicateTask(1),
    LookForCollaborationTask(1),
    ProvideCollaborationTask(2),
    LeisureActivitiesTask(2),
    FindNewsTask(2),
    WasteTimeAndResourcesTask(1),
    PeasantPayDebtsTaks(2),
    peasantOffTask(24),
    SpendFriendsTimeTask(2);
    
    private int time;

    private TimeConsumedBy(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
