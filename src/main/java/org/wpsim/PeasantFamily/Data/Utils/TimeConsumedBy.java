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
package org.wpsim.PeasantFamily.Data.Utils;

/**
 *
 * @author jairo
 */
public enum TimeConsumedBy {

    Void(1),
    LookForLoanTask(4),
    SelfEvaluationTask(2),
    SearchForHelpAndNecessityTask(6),
    SeekPurposeTask(12),
    GetPriceListTask(2),
    DeforestingLandTask(6),
    DoHealthCareTask(4),
    DoVitalsTask(12),
    AttendReligiousEventsTask(2),
    AttendToLivestockTask(2),
    CheckCropsTask(4),
    HarvestCropsTask(8),
    AskForCollaborationTask(2),
    IrrigateCropsTask(8),
    MaintainHouseTask(2),
    ManagePestsTask(2),
    PlantCropTask(8),
    PrepareLandTask(8),
    ProcessProductsTask(8),
    SellCropTask(8),
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
    CollaborateTask(1),
    LookForCollaborationTask(3),
    ProvideCollaborationTask(2),
    LeisureActivitiesTask(2),
    FindNewsTask(2),
    WasteTimeAndResourcesTask(1),
    PeasantPayDebtsTaks(2),
    peasantOffTask(24),
    SpendFriendsTimeTask(2),
    WorkForOtherTask(8),
    AlternativeWorkTask(8);
    
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
