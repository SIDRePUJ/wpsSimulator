package org.wpsim.World.Agent;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.Agent.PeriodicGuardBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import BESA.Util.PeriodicDataBESA;
import org.json.JSONObject;
import org.wpsim.PeasantFamily.Guards.FromWorld.FromWorldGuard;
import org.wpsim.PeasantFamily.Guards.FromWorld.FromWorldMessage;
import org.wpsim.PeasantFamily.Guards.FromWorld.FromWorldMessageType;
import org.wpsim.Viewer.Data.wpsReport;
import org.wpsim.World.Helper.WorldConfiguration;
import org.wpsim.World.Messages.WorldMessage;
import org.wpsim.World.layer.crop.CropLayer;
import org.wpsim.World.layer.crop.cell.CropCell;
import org.wpsim.World.layer.crop.cell.CropCellState;
import org.wpsim.World.layer.disease.DiseaseCellState;

/**
 * BESA world's guard, holds the actions that receive from the peasant agent
 */
public class KillWorldGuard extends GuardBESA {

    /**
     *
     * @param eventBESA
     */
    @Override
    public synchronized void funcExecGuard(EventBESA eventBESA) {
        WorldMessage worldMessage = (WorldMessage) eventBESA.getData();
        System.out.println("Cerrando agent 🚩🚩🚩 " +  this.agent.getAlias());
        //WorldState worldState = (WorldState) this.agent.getState();
        this.agent.shutdownAgent();
    }
}
