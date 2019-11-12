package edu.usc.infolab.kien.blockchaingeospatial.eth;

import edu.usc.infolab.kien.blockchaingeospatial.config.AddressConfig;
import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.PrivGeoMarkt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.util.ArrayList;

public class PrivGeoMarktHelper extends ContractHelper {
    private static final Logger logger = LoggerFactory.getLogger(PrivGeoMarktHelper.class);

    public static final String DEFAULT_CONTRACT_ADDRESS = AddressConfig.getAddress(
        AddressConfig.KEY_CONTRACT_PRIV_GEO_MARKT);

    public PrivGeoMarktHelper() throws IOException {
        super(DEFAULT_CONTRACT_ADDRESS);
    }

    public PrivGeoMarktHelper(Web3j web3j) throws IOException {
        super(web3j, DEFAULT_CONTRACT_ADDRESS);
    }

    public PrivGeoMarktHelper(Web3j web3j, String contractAddress) {
        super(web3j, contractAddress);
    }

    public PrivGeoMarktHelper(Web3j web3j, String contractAddress, ContractGasProvider contractGasProvider) {
        super(web3j, contractAddress, contractGasProvider);
    }

    /**
     * Prepare {@link PrivGeoMarkt} objects for credentials from configuration.
     * @param web3j
     * @param contractAddress address of Ethereum {@link PrivGeoMarkt} smart contract
     */
    public void prepare(Web3j web3j, String contractAddress, ContractGasProvider contractGasProvider) {
        //Curator
        {
            Credentials credentials = EthHelper.getCuratorAccount();
            getCuratorsContracts().add(PrivGeoMarkt.load(
                contractAddress,
                web3j,
                credentials,
                contractGasProvider));
        }

        //Owners
        for (Credentials credentials: EthHelper.getOwnerAccounts()) {
            getOwnersContracts().add(PrivGeoMarkt.load(
                contractAddress,
                web3j,
                credentials,
                contractGasProvider));
        }

        //Buyers
        for (Credentials credentials: EthHelper.getBuyerAccounts()) {
            getBuyersContracts().add(PrivGeoMarkt.load(
                contractAddress,
                web3j,
                credentials,
                contractGasProvider));
        }
    }
}
