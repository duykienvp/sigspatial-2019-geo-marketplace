package edu.usc.infolab.kien.blockchaingeospatial.contract.deployer;

public interface ContractDeployer {

    /**
     * Deploy contract and save address to configuration file
     * @throws Exception
     */
    public void deploy() throws Exception;
}