package edu.usc.infolab.kien.blockchaingeospatial.eth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.util.ArrayList;

public abstract class ContractHelper {
    private static final Logger logger = LoggerFactory.getLogger(ContractHelper.class);

    private ArrayList<Contract> curatorsContracts = new ArrayList<>();
    private ArrayList<Contract> ownersContracts = new ArrayList<>();
    private ArrayList<Contract> buyersContracts = new ArrayList<>();

    public ContractHelper(String contractAddress) throws IOException {
        prepare(contractAddress);
    }

    public ContractHelper(Web3j web3j, String contractAddress) {
        prepare(web3j, contractAddress);
    }

    public ContractHelper(Web3j web3j, String contractAddress, ContractGasProvider contractGasProvider) {
        prepare(web3j, contractAddress, contractGasProvider);
    }

    /**
     * Prepare {@link Contract} objects for credentials from configuration.
     * @param contractAddress address of Ethereum {@link Contract} smart contract
     */
    public void prepare(String contractAddress) throws IOException {
        Web3j web3j = EthHelper.getWeb3jConnection();
        prepare(web3j, contractAddress, new DefaultGasProvider());
    }

    /**
     * Prepare {@link Contract} objects for credentials from configuration.
     * @param web3j
     * @param contractAddress address of Ethereum {@link Contract} smart contract
     */
    public void prepare(Web3j web3j, String contractAddress) {
        prepare(web3j, contractAddress, new DefaultGasProvider());
    }

    /**
     * Prepare {@link Contract} objects for credentials from configuration.
     * @param web3j
     * @param contractAddress address of Ethereum {@link Contract} smart contract
     */
    public abstract void prepare(Web3j web3j, String contractAddress, ContractGasProvider contractGasProvider);

    /**
     * Check whether all contracts are valid
     * @return whether all contracts are valid
     */
    public boolean isValid() {
        try {
            for (Contract contract: curatorsContracts) {
                if (!contract.isValid()) {
                    return false;
                }
            }

            for (Contract contract: ownersContracts) {
                if (!contract.isValid()) {
                    return false;
                }
            }

            for (Contract contract: buyersContracts) {
                if (!contract.isValid()) {
                    return false;
                }
            }
        } catch (IOException e) {
            logger.error("Error checking validation: ", e);
            return false;
        }

        return true;
    }

    public ArrayList<Contract> getCuratorsContracts() {
        return curatorsContracts;
    }

    public ArrayList<Contract> getOwnersContracts() {
        return ownersContracts;
    }

    public ArrayList<Contract> getBuyersContracts() {
        return buyersContracts;
    }
}
