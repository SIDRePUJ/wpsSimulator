package org.wpsim.World.Automata.layer;

/**
 * Interface that holds the contract to build a layer
 */
public interface WorldLayer {
    /**
     * Method that holds the logic to set up the layer
     */
    void setupLayer();

    /**
     * Method to execute the transition function defined without parameters
     */
    void executeLayer();

    /**
     * Method to execute the transition function defined with parameters
     *
     * @param params object that holds the necessary params to execute the transition function
     */
    void executeLayer(LayerExecutionParams params);

    /**
     * Method that holds the dependency layers, util when executing the transition function
     *
     * @param layerName layer name identificator
     * @param layer     reference layer
     */
    void bindLayer(String layerName, WorldLayer layer);

    /**
     * returns a binded layer
     *
     * @param layerName name of the layer to retrieve
     * @return the world layer
     */
    WorldLayer getLayer(String layerName);

}
